package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdemDAO {
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public static void inserirOrdem(Ordem ordem, Ativo ativo, String nomeAtivo) {  
        float precoMedioAtivo = 0;

        if (ativo == null) {
            AtivoDAO.inserirAtivo(ordem, nomeAtivo); // Inserir o ativo se ele ainda não está na carteira
        } else {
            precoMedioAtivo = ativo.getPrecoMedio(); // Obter preço médio do ativo antes da atuaização
            AtivoDAO.atualizarAtivo(ordem, ativo); // Atualizar o ativo se ele já está na carteira
        }
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Database.getInstance().getConnection();

            if (ordem.getTipo().equals("Compra")) {
                String sql = "INSERT INTO Ordem (dataOrdem, quantidade, preco, tipo, tickerAtivo) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(5, ordem.getTickerAtivo());
            } else {
                String sql = "INSERT INTO Ordem (dataOrdem, quantidade, preco, tipo, custo, tickerAtivo) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setFloat(5, ordem.getQuantidade() * precoMedioAtivo);
                preparedStatement.setString(6, ordem.getTickerAtivo());
            }          
            preparedStatement.setString(1, sdf.format(ordem.getDataOrdem()));
            preparedStatement.setFloat(2, ordem.getQuantidade());
            preparedStatement.setFloat(3, ordem.getPreco());
            preparedStatement.setString(4, ordem.getTipo());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir informações no banco de dados: " + e.getMessage());

        } finally {
            encerrarRecursos(null, preparedStatement);
        }
    }

    public static void listarOrdens(String tickerAtivo) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Ordem ordem;

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
                float custo = resultSet.getFloat("custo");

                if (tipo.equals("Compra")) {
                    ordem = new Ordem(data, quantidade, preco, tipo, tickerAtivo);
                } else {
                    ordem = new OrdemVenda(data, quantidade, preco, custo, tickerAtivo);
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
