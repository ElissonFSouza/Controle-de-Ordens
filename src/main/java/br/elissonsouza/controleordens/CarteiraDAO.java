package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarteiraDAO {
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static void inserirAtivo(Ordem ordem, String nomeAtivo) {        
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

    private static void atualizarAtivo(Ordem ordem, Ativo ativo) {
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

    public static void inserirOrdem(Ordem ordem, Ativo ativo, String nomeAtivo) {  
        if (ativo == null) {
            inserirAtivo(ordem, nomeAtivo);
        } else {
            atualizarAtivo(ordem, ativo);
        }
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Database.getInstance().getConnection();

            if (ordem.getTipo().equals("Compra")) {
                String sql = "INSERT INTO Ordem (dataOrdem, quantidade, preco, total, tipo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(6, ordem.getTickerAtivo());
            } else {
                String sql = "INSERT INTO Ordem (dataOrdem, quantidade, preco, total, tipo, custo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setFloat(6, ordem.getQuantidade() * ativo.getPrecoMedio());
                preparedStatement.setString(7, ordem.getTickerAtivo());
            }          
            preparedStatement.setString(1, sdf.format(ordem.getDataOrdem()));
            preparedStatement.setFloat(2, ordem.getQuantidade());
            preparedStatement.setFloat(3, ordem.getPreco());
            preparedStatement.setFloat(4, ordem.getTotal());
            preparedStatement.setString(5, ordem.getTipo());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir informações no banco de dados.");
            e.printStackTrace();

        } finally {
            encerrarRecursos(null, preparedStatement);
        }
    }

    public static void listarOrdens(Ativo ativo) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Ordem ordem;
        String tickerAtivo = ativo.getTicker();

        try {
            connection = Database.getInstance().getConnection();
            String sql = "SELECT * FROM Ordem WHERE tickerAtivo = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tickerAtivo);
            resultSet = preparedStatement.executeQuery();
            
            System.out.println("\n===> Lista de Ordens");

            while (resultSet.next()) {
                Date data = null;
                try {
                    data = sdf.parse(resultSet.getString("dataOrdem"));
                } catch (ParseException e) {
                    System.err.println("\nHouve um erro ao coletar a data da ordem: " + e.getMessage());
                }
                float quantidade = resultSet.getFloat("quantidade");
                float preco = resultSet.getFloat("preco");
                String tipo = resultSet.getString("tipo");

                if (tipo.equals("Compra")) {
                    ordem = new Ordem(data, quantidade, preco, tipo, tickerAtivo);
                } else {
                    ordem = new OrdemVenda(data, quantidade, preco, ativo.getPrecoMedio(), tickerAtivo);
                }
                
                System.out.println(ordem);
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
