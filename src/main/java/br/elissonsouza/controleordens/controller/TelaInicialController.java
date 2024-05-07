package br.elissonsouza.controleordens.controller;

import java.net.URL;
import java.util.ResourceBundle;

import br.elissonsouza.controleordens.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TelaInicialController implements Initializable {

    @FXML
    private TextField dummyTextField; // Campo de texto vazio

    @FXML
    private Button btnCadastrarVenda;

    @FXML
    private Button btnCadastrarCompra;

    @FXML
    private Button btnMinhaCarteira;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //dummyTextField.requestFocus();
    }

    @FXML
    private void cadastrarCompra() {
        App.setTipoOrdem("Compra");
        App.setRoot("InformarTicker");
    }

    @FXML
    private void cadastrarVenda() {
        App.setTipoOrdem("Venda");
        App.setRoot("InformarTicker");
    }

    @FXML
    private void listarAtivos() {
        App.setRoot("MeusAtivos");
    }
}