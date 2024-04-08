package br.elissonsouza.controleordens;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database INSTANCE = null;
    private Connection connection = null;

    private Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:controle-ordens/src/main/resources/dados.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String sql = FileUtils.loadTextFile("controle-ordens/src/main/resources/inicializacao.sql");
            statement.executeUpdate(sql);

        } catch (Exception e) {
            System.err.println("Houve um erro ao criar o banco de dados: " + e.getMessage());
        }
    }

    public static Database getInstance() {
        if (INSTANCE == null) INSTANCE = new Database();
        return INSTANCE;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() {
        try {
            this.connection.close();
                        
        } catch (SQLException e) {
            System.err.println("Houve um erro ao encerrar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}
