package br.elissonsouza.controleordens2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AvisoController {

    @FXML
    private Label lblMensagem;

    @FXML
    private Button btnOK;

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) btnOK.getScene().getWindow();
        stage.close();
    }
    
    // Método para definir a mensagem a ser exibida no aviso
    public void setMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
}
