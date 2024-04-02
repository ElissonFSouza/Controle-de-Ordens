package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AtivoDAO {
    public static void inserirAtivo(Ordem ordem, String nomeAtivo) {        
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Database.getInstance().getConnection();
            String sql = "INSERT INTO Ativo (ticker, nome, quantidade, precoMedio, totalAtual) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ordem.getTickerAtivo());
            preparedStatement.setString(2, nomeAtivo);
            preparedStatement.setFloat(3, ordem.getQuantidade());
            preparedStatement.setFloat(4, ordem.getPreco());
            preparedStatement.setFloat(5, ordem.getTotal());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir as informações no banco de dados.");
            e.printStackTrace();

        } finally {
            encerrarRecursos(null, preparedStatement);
        }
    }

    public static void atualizarAtivo(Ordem ordem, Ativo ativo) {
        float quantidade;
        float totalAtual;
        float precoMedio;
        
        if (ordem.getTipo().equals("Compra")) {
            quantidade = ativo.getQuantidade() + ordem.getQuantidade();
            totalAtual = ativo.getTotalAtual() + ordem.getTotal();      
            precoMedio = totalAtual / quantidade;
        } else {
            quantidade = ativo.getQuantidade() - ordem.getQuantidade();
            totalAtual = ativo.getTotalAtual() - ordem.getQuantidade() * ativo.getPrecoMedio();
            precoMedio = ativo.getPrecoMedio();
        }        

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Database.getInstance().getConnection();
            String sql = "UPDATE Ativo SET quantidade = ?, precoMedio = ?, totalAtual = ? WHERE ticker = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setFloat(1, quantidade);
            preparedStatement.setFloat(2, precoMedio);
            preparedStatement.setFloat(3, totalAtual);
            preparedStatement.setString(4, ativo.getTicker());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir as informações no banco de dados.");
            e.printStackTrace();

        } finally {
            encerrarRecursos(null, preparedStatement);
        }
    }

    public static Ativo pesquisarAtivo(String tickerAtivo) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Ativo ativo = null;

        try {
            connection = Database.getInstance().getConnection();
            String sql = "SELECT * FROM Ativo WHERE ticker = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tickerAtivo.toUpperCase());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String ticker = resultSet.getString("ticker");
                String nome = resultSet.getString("nome");
                float quantidade = resultSet.getFloat("quantidade");
                float precoMedio = resultSet.getFloat("precoMedio");
                float totalAtual = resultSet.getFloat("totalAtual");

                ativo = new Ativo(ticker, nome, quantidade, precoMedio, totalAtual);
            }

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao pesquisar o ativo: " + e.getMessage());

        } finally {
            encerrarRecursos(resultSet, preparedStatement);
        }

        return ativo;
    }

    public static void listarAtivos() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Database.getInstance().getConnection();            
            String sql = "SELECT * FROM Ativo";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            if (!resultSet.isBeforeFirst()) { // Verifica se existem linhas na tabela
                System.out.println("\nNão há ativos na carteira.");

            } else {
                System.out.println("\n========= Lista de ativos ==========");

                while (resultSet.next()) {
                    String ticker = resultSet.getString("ticker");
                    String nome = resultSet.getString("nome");
                    float quantidade = resultSet.getFloat("quantidade");
                    float precoMedio = resultSet.getFloat("precoMedio");
                    float totalAtual = resultSet.getFloat("totalAtual");

                    Ativo ativo = new Ativo(ticker, nome, quantidade, precoMedio, totalAtual);
                    System.out.println(ativo);
                }
            }

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao listar os ativos: " + e.getMessage());

        } finally {
            encerrarRecursos(resultSet, preparedStatement);
        }
    }

    private static void encerrarRecursos(ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("\nHouve um erro ao encerrar o resultado da consulta: " + e.getMessage());
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.err.println("\nHouve um erro ao encerrar a consulta: " + e.getMessage());
            }
        }

        // if (connection != null) {
        //     try {
        //         connection.close();
        //     } catch (SQLException e) {
        //         System.err.println("\nHouve um erro ao encerrar conexão com o banco de dados: " + e.getMessage());
        //     }
        // }
    }
}
