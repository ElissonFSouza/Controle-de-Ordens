package br.elissonsouza.controleordens2.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import br.elissonsouza.controleordens2.App;
import br.elissonsouza.controleordens2.dao.AtivoDAO;
import br.elissonsouza.controleordens2.model.Ativo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class InformarTickerController implements Initializable {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnProximo;

    @FXML
    private TextField tfTicker;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        btnProximo.disableProperty().bind(
            tfTicker.textProperty().isEmpty()
        );
    }

    @FXML
    private void buscarAtivo() {
        String tickerAtivo = tfTicker.getText();

        if (tickerAtivo != null) {
            Ativo ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);
            App.setAtivo(ativo);

            if (App.getTipoOrdem().equals("Venda")
                && (ativo == null || ativo.getQuantidade().compareTo(BigDecimal.ZERO) == 0)) {
                    
                App.mostrarAviso("Você não possui este ativo", tfTicker.getScene().getWindow());  
                                          
            } else {
                App.setTickerAtivo(tickerAtivo);

                if (ativo == null) {              
                    App.setRoot("InformarNome");
                } else {
                    App.setNomeAtivo(ativo.getNome());
                    App.setRoot("CadastrarOrdem");
                }
            }            
        }
    }

    @FXML
    private void voltarPagina() {
        App.setRoot("TelaInicial");
    }
}
