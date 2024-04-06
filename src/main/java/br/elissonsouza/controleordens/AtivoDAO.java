package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AtivoDAO {
    private static final String INSERT_ATIVO =
        "INSERT INTO Ativo (ticker, nome, quantidade, precoMedio, saldoVendas) VALUES (?, ?, ?, ?, 0)";
    private static final String UPDATE_ATIVO =
        "UPDATE Ativo SET quantidade = ?, precoMedio = ?, saldoVendas = ? WHERE ticker = ?";
    private static final String PESQUISAR_ATIVO =
        "SELECT * FROM Ativo WHERE ticker = ? OR LOWER(Nome) = LOWER(?)";
    private static final String LISTAR_ATIVOS =
        "SELECT * FROM Ativo";

    public static void inserirAtivo(Ordem ordem, String nomeAtivo) {        
        Connection connection = null;

        try {
            connection = Database.getInstance().getConnection();
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATIVO)) {
                preparedStatement.setString(1, ordem.getTickerAtivo());
                preparedStatement.setString(2, nomeAtivo);
                preparedStatement.setFloat(3, ordem.getQuantidade());
                preparedStatement.setFloat(4, ordem.getPreco());
                preparedStatement.executeUpdate();
            }         

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir as informações no banco de dados: " + e.getMessage());
        }
    }

    public static void atualizarAtivo(Ordem ordem, Ativo ativo) {
        // Obter dados atuais do ativo
        float quantidadeAtivo = ativo.getQuantidade();        
        float precoMedioAtivo = ativo.getPrecoMedio();
        float saldoVendasAtivo = ativo.getSaldoVendas();
        float totalAtivo = quantidadeAtivo * precoMedioAtivo;
        // Obter dados da ordem atual
        float quantidadeOrdem = ordem.getQuantidade();
        float precoOrdem = ordem.getPreco();
        float totalOrdem = quantidadeOrdem * precoOrdem;
        
        // Atualizar dados do ativo de acordo com o tipo de ordem
        if (ordem.getTipo().equals("Compra")) { 
            quantidadeAtivo += quantidadeOrdem;
            precoMedioAtivo = (totalAtivo + totalOrdem) / quantidadeAtivo;
        } else {
            quantidadeAtivo -= quantidadeOrdem;            
            saldoVendasAtivo += totalOrdem - (quantidadeOrdem * precoMedioAtivo);
            if (quantidadeAtivo == 0) precoMedioAtivo = 0;
        }        

        Connection connection = null;

        try {
            connection = Database.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATIVO)) {
                preparedStatement.setFloat(1, quantidadeAtivo);
                preparedStatement.setFloat(2, precoMedioAtivo);
                preparedStatement.setFloat(3, saldoVendasAtivo);
                preparedStatement.setString(4, ativo.getTicker());
                preparedStatement.executeUpdate();
            }            

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir as informações no banco de dados: " + e.getMessage());
        }
    }

    public static Ativo pesquisarAtivo(String tickerOuNome) {
        Ativo ativo = null;

        Connection connection = null;
        
        try {
            connection = Database.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(PESQUISAR_ATIVO)) {
                preparedStatement.setString(1, tickerOuNome.toUpperCase());
                preparedStatement.setString(2, tickerOuNome);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String ticker = resultSet.getString("ticker");
                        String nome = resultSet.getString("nome");
                        float quantidade = resultSet.getFloat("quantidade");
                        float precoMedio = resultSet.getFloat("precoMedio");
                        float saldoVendas = resultSet.getFloat("saldoVendas");
        
                        ativo = new Ativo(ticker, nome, quantidade, precoMedio, saldoVendas);
                    }
                }   
            }            

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao pesquisar o ativo: " + e.getMessage());
        }

        return ativo;
    }

    public static void listarAtivos() {
        Connection connection = null;

        try {
            connection = Database.getInstance().getConnection();  

            try (PreparedStatement preparedStatement = connection.prepareStatement(LISTAR_ATIVOS)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) { // Verifica se existem linhas no resultado da busca
                        System.out.println("\nNão há ativos na carteira.");
        
                    } else {
                        System.out.println("\n========= Lista de ativos ==========");
        
                        while (resultSet.next()) {
                            String ticker = resultSet.getString("ticker");
                            String nome = resultSet.getString("nome");
                            float quantidade = resultSet.getFloat("quantidade");
                            float precoMedio = resultSet.getFloat("precoMedio");
                            float saldoVendas = resultSet.getFloat("saldoVendas");
        
                            Ativo ativo = new Ativo(ticker, nome, quantidade, precoMedio, saldoVendas);
                            System.out.println(ativo);
                        }
                    }
                }
            }            

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao listar os ativos: " + e.getMessage());
        }
    }
}