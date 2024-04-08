package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrdemDAO {
    private static final String SELECT_ORDENS_ANO = 
        "SELECT * FROM Ordem WHERE tickerAtivo = ? AND strftime('%Y', dataOrdem) = ? LIMIT 1";
    private static final String SELECT_ORDENS_MES =
        "SELECT * FROM Ordem WHERE tickerAtivo = ? AND strftime('%Y', dataOrdem) = ? AND strftime('%m', dataOrdem) = ? ORDER BY dataOrdem";
    private static final String SELECT_ORDENS = 
        "SELECT * FROM Ordem WHERE tickerAtivo = ? ORDER BY dataOrdem";
    private static final String INSERT_ORDEM_COMPRA =
        "INSERT INTO Ordem (dataOrdem, quantidade, preco, tipo, tickerAtivo) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ORDEM_VENDA =
        "INSERT INTO Ordem (dataOrdem, quantidade, preco, tipo, custo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?)";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static void inserirOrdem(Ordem ordem, Ativo ativo, String nomeAtivo) {  
        float precoMedioAtivo = 0;

        if (ativo == null) {
            AtivoDAO.inserirAtivo(ordem, nomeAtivo); // Inserir o ativo se ele ainda não está na carteira
        } else {
            precoMedioAtivo = ativo.getPrecoMedio(); // Obter preço médio do ativo antes da atualização
            AtivoDAO.atualizarAtivo(ordem, ativo); // Atualizar o ativo se ele já está na carteira
        }
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Database.getInstance().getConnection();

            if (ordem.getTipo().equals("Compra")) {
                preparedStatement = connection.prepareStatement(INSERT_ORDEM_COMPRA);
                preparedStatement.setString(5, ordem.getTickerAtivo());
                                
            } else {
                preparedStatement = connection.prepareStatement(INSERT_ORDEM_VENDA);
                preparedStatement.setFloat(5, ordem.getQuantidade() * precoMedioAtivo);
                preparedStatement.setString(6, ordem.getTickerAtivo());                               
            }     

            preparedStatement.setString(1, ordem.getDataOrdem().format(formatter));
            preparedStatement.setFloat(2, ordem.getQuantidade());
            preparedStatement.setFloat(3, ordem.getPreco());
            preparedStatement.setString(4, ordem.getTipo());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir informações no banco de dados: " + e.getMessage());

        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.err.println("\nHouve um erro ao encerrar a consulta: " + e.getMessage());
                }
            }
        }
    }

    public static void listarOrdens(String tickerAtivo) {
        Connection connection = null;

        try {
            connection = Database.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDENS)) {
                preparedStatement.setString(1, tickerAtivo.toUpperCase());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("\n===== Lista de Ordens =====");
                
                    while (resultSet.next()) {    
                        float quantidade = resultSet.getFloat("quantidade");
                        float preco = resultSet.getFloat("preco");
                        String tipo = resultSet.getString("tipo");
                        float custo = resultSet.getFloat("custo");
                        LocalDate data = LocalDate.parse(resultSet.getString("dataOrdem"), formatter); 
        
                        Ordem ordem = criarOrdem(tipo, data, quantidade, preco, custo, tickerAtivo);                
                        System.out.println(ordem);
                    }
                }
            }           
                
        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao listar as ordens: " + e.getMessage());
        }
    }

    public static void listarOrdensAno(String tickerAtivo, String ano) {
        try {
            Connection connection = Database.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDENS_ANO)) {
                preparedStatement.setString(1, tickerAtivo.toUpperCase());
                preparedStatement.setString(2, ano);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("\nNão foram encontradas ordens em " + ano + ".");
                    } else {
                        System.out.println("\n===== Lista de Ordens =====");
                        listarOrdensMeses(connection, tickerAtivo, ano);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao listar as ordens: " + e.getMessage());
        }
    }
    
    public static void listarOrdensMeses(Connection connection, String tickerAtivo, String ano) throws SQLException {
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};            

        for (int mes = 1; mes <= 12; mes++) {  
            float totalVendas = 0;
            float custoTotal = 0;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDENS_MES)) {       
                preparedStatement.setString(1, tickerAtivo.toUpperCase());
                preparedStatement.setString(2, ano);
                preparedStatement.setString(3, String.format("%02d", mes));
                
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.isBeforeFirst()) { // Verifica se existem linhas no resultado da busca
                        System.out.println("\n" + meses[mes-1] + " ===================");
                    } 

                    while (resultSet.next()) {    
                        float quantidade = resultSet.getFloat("quantidade");
                        float preco = resultSet.getFloat("preco");
                        String tipo = resultSet.getString("tipo");
                        float custo = resultSet.getFloat("custo");
                        LocalDate dataOrdem = LocalDate.parse(resultSet.getString("dataOrdem"), formatter);
        
                        Ordem ordem = criarOrdem(tipo, dataOrdem, quantidade, preco, custo, tickerAtivo);
                        System.out.println(ordem);

                        if (tipo.equals("Venda")) {
                            totalVendas += quantidade * preco;
                            custoTotal += custo;
                        }
                    }

                    if (totalVendas > 0) { // Verifica se houve vendas no mês
                        System.out.println("___________________________");
                        System.out.println("\nApuração das vendas do mês:");
                        System.out.println("- Total das vendas: R$ " + totalVendas);
                        System.out.println("- Custo total: R$ " + custoTotal);
                    }
                }
            }
        }
    }

    public static Ordem criarOrdem(String tipo, LocalDate dataOrdem, float quantidade, float preco, float custo, String tickerAtivo) {
        if (tipo.equals("Compra")) {
            return new Ordem(dataOrdem, quantidade, preco, tipo, tickerAtivo);
        } else {
            return new OrdemVenda(dataOrdem, quantidade, preco, custo, tickerAtivo);
        }
    }
}
