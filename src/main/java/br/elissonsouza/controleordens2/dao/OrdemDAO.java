package br.elissonsouza.controleordens2.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.elissonsouza.controleordens2.Database;
import br.elissonsouza.controleordens2.model.Ativo;
import br.elissonsouza.controleordens2.model.Ordem;
import br.elissonsouza.controleordens2.model.OrdemVenda;

public class OrdemDAO {
    private static final String SELECT_ORDENS_ANO = 
        "SELECT * FROM Ordem WHERE tickerAtivo = ? AND strftime('%Y', dataOrdem) = ? LIMIT 1";
    private static final String SELECT_ORDENS_MES =
        "SELECT * FROM Ordem WHERE tickerAtivo = ? AND strftime('%Y', dataOrdem) = ? AND strftime('%m', dataOrdem) = ? ORDER BY dataOrdem";
    private static final String SELECT_ORDENS = 
        "SELECT * FROM Ordem WHERE tickerAtivo = ? ORDER BY dataOrdem";
    private static final String INSERT_ORDEM_COMPRA =
        "INSERT INTO Ordem (dataOrdem, quantidade, preco, taxa, tipo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_ORDEM_VENDA =
        "INSERT INTO Ordem (dataOrdem, quantidade, preco, taxa, tipo, custo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static void inserirOrdem(Ordem ordem, Ativo ativo, String nomeAtivo) {  
        BigDecimal precoMedioAtivo = BigDecimal.ZERO;

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

            if (ordem instanceof OrdemVenda) {    
                preparedStatement = connection.prepareStatement(INSERT_ORDEM_VENDA);
                preparedStatement.setBigDecimal(6, ordem.getQuantidade().multiply(precoMedioAtivo));
                preparedStatement.setString(7, ordem.getTickerAtivo());

            } else {
                preparedStatement = connection.prepareStatement(INSERT_ORDEM_COMPRA);
                preparedStatement.setString(6, ordem.getTickerAtivo());                        
            }     

            preparedStatement.setString(1, ordem.getDataOrdem().format(formatter));
            preparedStatement.setBigDecimal(2, ordem.getQuantidade());
            preparedStatement.setBigDecimal(3, ordem.getPreco());
            preparedStatement.setBigDecimal(4, ordem.getTaxa());
            preparedStatement.setString(5, ordem.getTipo());
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
                        lerResultSet(resultSet, tickerAtivo);
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
            BigDecimal totalVendas = BigDecimal.ZERO;
            BigDecimal custoTotal = BigDecimal.ZERO;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDENS_MES)) {       
                preparedStatement.setString(1, tickerAtivo.toUpperCase());
                preparedStatement.setString(2, ano);
                preparedStatement.setString(3, String.format("%02d", mes));
                
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.isBeforeFirst()) { // Verifica se existem linhas no resultado da busca
                        System.out.println("\n" + meses[mes-1] + " ===================");
                    } 

                    while (resultSet.next()) {   
                        
                        Ordem ordem = lerResultSet(resultSet, tickerAtivo);

                        if (ordem instanceof OrdemVenda) {
                            OrdemVenda ordemVenda = (OrdemVenda) ordem;
                            totalVendas = totalVendas.add(ordem.getQuantidade().multiply(ordem.getPreco()));
                            custoTotal = custoTotal.add(ordemVenda.getCusto());
                        }
                    }

                    if (totalVendas.compareTo(BigDecimal.ZERO) > 0) { // Verifica se houve vendas no mês
                        System.out.println("___________________________");
                        System.out.println("\nApuração das vendas do mês:");
                        System.out.println("- Total das vendas: R$ " + totalVendas);
                        System.out.println("- Custo total: R$ " + custoTotal);
                    }
                }
            }
        }
    }

    private static Ordem lerResultSet(ResultSet resultSet, String tickerAtivo) throws SQLException {
        BigDecimal quantidade = resultSet.getBigDecimal("quantidade");
        BigDecimal preco = resultSet.getBigDecimal("preco");
        BigDecimal taxa = resultSet.getBigDecimal("taxa");
        BigDecimal custo =  resultSet.getBigDecimal("custo");
        String tipo = resultSet.getString("tipo");
        LocalDate dataOrdem = LocalDate.parse(resultSet.getString("dataOrdem"), formatter);

        Ordem ordem = criarOrdem(tipo, dataOrdem, quantidade, preco, taxa, custo, tickerAtivo);
        System.out.println(ordem);

        return ordem;
    }

    public static Ordem criarOrdem(String tipo, LocalDate dataOrdem, BigDecimal quantidade, BigDecimal preco, BigDecimal taxa, BigDecimal custo, String tickerAtivo) {
        if (tipo.equals("Compra")) {
            return new Ordem(dataOrdem, quantidade, preco, taxa, tipo, tickerAtivo);
        } else {
            return new OrdemVenda(dataOrdem, quantidade, preco, taxa, custo, tickerAtivo);
        }
    }
}
