package br.elissonsouza.controleordens.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.elissonsouza.controleordens.Database;
import br.elissonsouza.controleordens.model.Ativo;
import br.elissonsouza.controleordens.model.Ordem;
import br.elissonsouza.controleordens.model.OrdemVenda;

public class AtivoDAO {
    private static final String INSERT_ATIVO =
        "INSERT INTO Ativo (ticker, nome, quantidade, precoMedio, saldoVendas, totalComprado) VALUES (?, ?, ?, ?, 0, ?)";
    private static final String UPDATE_ATIVO =
        "UPDATE Ativo SET quantidade = ?, precoMedio = ?, saldoVendas = ?, totalComprado = ? WHERE ticker = ?";
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
                preparedStatement.setBigDecimal(3, ordem.getQuantidade().subtract(ordem.getTaxa()));
                preparedStatement.setBigDecimal(4, ordem.getPreco());
                preparedStatement.setBigDecimal(5, ordem.getQuantidade());
                preparedStatement.executeUpdate();
            }         

        } catch (SQLException e) {
            System.err.println("\nHouve um erro ao inserir as informações no banco de dados: " + e.getMessage());
        }
    }

    public static void atualizarAtivo(Ordem ordem, Ativo ativo) {
        // Obter dados atuais do ativo
        BigDecimal qtdAtivo = ativo.getQuantidade();        
        BigDecimal precoMedio = ativo.getPrecoMedio();
        BigDecimal saldoVendas = ativo.getSaldoVendas();
        BigDecimal totalComprado = ativo.getTotalComprado();
        // Obter dados da ordem atual
        BigDecimal qtdOrdem = ordem.getQuantidade();
        BigDecimal precoOrdem = ordem.getPreco();
        BigDecimal taxaOrdem = ordem.getTaxa();
        BigDecimal totalOrdem = qtdOrdem.multiply(precoOrdem);
        
        // Atualizar dados do ativo de acordo com o tipo de ordem
        if (ordem instanceof OrdemVenda) { 
            qtdAtivo = qtdAtivo.subtract(qtdOrdem);            
            saldoVendas = saldoVendas.add(totalOrdem.subtract(qtdOrdem.multiply(precoMedio)).subtract(taxaOrdem));
            totalComprado = totalComprado.subtract(qtdOrdem);
            if (qtdAtivo.compareTo(BigDecimal.ZERO) == 0) precoMedio = BigDecimal.ZERO;            

        } else {
            precoMedio = totalComprado.multiply(precoMedio).add(totalOrdem).divide(totalComprado.add(qtdOrdem), 6, RoundingMode.HALF_UP);
            qtdAtivo = qtdAtivo.add(qtdOrdem.subtract(taxaOrdem));
            totalComprado = totalComprado.add(qtdOrdem);
        }        

        Connection connection = null;

        try {
            connection = Database.getInstance().getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATIVO)) {
                preparedStatement.setBigDecimal(1, qtdAtivo);
                preparedStatement.setBigDecimal(2, precoMedio);
                preparedStatement.setBigDecimal(3, saldoVendas);
                preparedStatement.setBigDecimal(4, totalComprado);
                preparedStatement.setString(5, ativo.getTicker());
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
                        BigDecimal quantidade = resultSet.getBigDecimal("quantidade");
                        BigDecimal precoMedio = resultSet.getBigDecimal("precoMedio");
                        BigDecimal saldoVendas = resultSet.getBigDecimal("saldoVendas");
                        BigDecimal totalComprado = resultSet.getBigDecimal("totalComprado");
        
                        ativo = new Ativo(ticker, nome, quantidade, precoMedio, saldoVendas, totalComprado);
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
                            BigDecimal quantidade = resultSet.getBigDecimal("quantidade");
                            BigDecimal precoMedio = resultSet.getBigDecimal("precoMedio");
                            BigDecimal saldoVendas = resultSet.getBigDecimal("saldoVendas");
                            BigDecimal totalComprado = resultSet.getBigDecimal("totalComprado");
        
                            Ativo ativo = new Ativo(ticker, nome, quantidade, precoMedio, saldoVendas, totalComprado);
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