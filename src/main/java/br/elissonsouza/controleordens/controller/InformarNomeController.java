package br.elissonsouza.controleordens.controller;

import java.net.URL;
import java.util.ResourceBundle;

import br.elissonsouza.controleordens.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class InformarNomeController implements Initializable {

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnProximo;

    @FXML
    private TextField tfNome;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        btnProximo.disableProperty().bind(
            tfNome.textProperty().isEmpty()
        );
    }

    @FXML
    private void inserirNome() {
        String nomeAtivo = tfNome.getText();

        if (nomeAtivo != null) {
            App.setNomeAtivo(nomeAtivo);
            App.setRoot("CadastrarOrdem");
        }
    }

    @FXML
    private void voltarPagina() {
        App.setRoot("InformarTicker");
    }

}
