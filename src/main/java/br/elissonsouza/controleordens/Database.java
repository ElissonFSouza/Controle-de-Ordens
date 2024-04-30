package br.elissonsouza.controleordens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database INSTANCE = null;
    private Connection connection = null;

    private Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:Database.db");

            String sql = carregarArquivoDeRecurso("inicializacao.sql");
            
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }            

        } catch (Exception e) {
            System.err.println("Houve um erro ao criar o banco de dados: " + e.getMessage());
        }
    }

    private static String carregarArquivoDeRecurso(String nomeArquivo) {
        // Obter o ClassLoader da classe atual
        ClassLoader classLoader = Database.class.getClassLoader();
    
        // Carregar o arquivo de recurso como um InputStream
        try (InputStream inputStream = classLoader.getResourceAsStream(nomeArquivo)) {
            // Se o InputStream não for nulo (ou seja, se o recurso for encontrado)
            if (inputStream != null) {
                // Cria um InputStreamReader para ler o conteúdo do InputStream
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                // Usa um BufferedReader para ler o conteúdo linha por linha
                BufferedReader reader = new BufferedReader(streamReader);

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                // Lê cada linha do arquivo e adiciona ao StringBuilder
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                return stringBuilder.toString();

            } else {
                System.err.println("Arquivo de recurso não encontrado: " + nomeArquivo);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
