package br.elissonsouza.controleordens;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import br.elissonsouza.controleordens.controller.AvisoController;
import br.elissonsouza.controleordens.model.Ativo;

public class App extends Application {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static Scene scene;

    private static Ativo ativo;
    private static String tickerAtivo;
    private static String nomeAtivo = null;
    private static String tipoOrdem;
    private static BigDecimal totalGasto;

    public static Ativo getAtivo() {
        return ativo;
    }

    public static void setAtivo(Ativo ativo) {
        App.ativo = ativo;
    }

    public static String getTickerAtivo() {
        return tickerAtivo;
    }

    public static void setTickerAtivo(String tickerAtivo) {
        App.tickerAtivo = tickerAtivo;
    }

    public static String getNomeAtivo() {
        return nomeAtivo;
    }

    public static void setNomeAtivo(String nomeAtivo) {
        App.nomeAtivo = nomeAtivo;
    }

    public static String getTipoOrdem() {
        return tipoOrdem;
    }

    public static void setTipoOrdem(String tipoOrdem) {
        App.tipoOrdem = tipoOrdem;
    }

    public static BigDecimal getTotalGasto() {
        return totalGasto;
    }

    public static void setTotalGasto(BigDecimal totalGasto) {
        App.totalGasto = totalGasto;
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("TelaInicial"));

        stage.setTitle("Controle de Ordens");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            System.err.println("Erro ao carregar nova janela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void mostrarAviso(String mensagem, Window janelaPrincipal) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        //stage.setTitle("Aviso");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/Aviso.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            AvisoController avisoController = loader.getController();
            avisoController.setMensagem(mensagem);

            stage.setOnCloseRequest(event -> stage.close()); // Fechar a janela de aviso

            // Calcula a posição central da janela de aviso em relação à janela principal
            double posX = janelaPrincipal.getX() + janelaPrincipal.getWidth() / 2;
            double posY = janelaPrincipal.getY() + janelaPrincipal.getHeight() / 2;
            
            // Define a posição da janela de aviso
            stage.setX(posX - 200);
            stage.setY(posY - 125);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}