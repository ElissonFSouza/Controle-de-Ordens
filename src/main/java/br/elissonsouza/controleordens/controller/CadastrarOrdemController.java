package br.elissonsouza.controleordens.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import br.elissonsouza.controleordens.App;
import br.elissonsouza.controleordens.dao.OrdemDAO;
import br.elissonsouza.controleordens.model.Ordem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;

public class CadastrarOrdemController implements Initializable {

    @FXML
    private Button btnCadastrarOrdem;

    @FXML
    private Button btnCancelar;

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblNomeAtivo;

    @FXML
    private TextField tfQuantidade;

    @FXML
    private TextField tfPreco;

    @FXML
    private TextField tfTaxa;

    @FXML
    private TextField tfTotal;

    @FXML
    private DatePicker dpData;

    BigDecimal quantidade = BigDecimal.ZERO;
    BigDecimal preco = BigDecimal.ZERO;
    BigDecimal taxa = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO.setScale(2);
    BigDecimal precoMedio = BigDecimal.ZERO;
    LocalDate dataOrdem;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        lblTitulo.setText("Cadastrar ordem de " + App.getTipoOrdem().toLowerCase());

        // Bind para desabilitar o botão de cadastrar ordem se algum campo (exceto "Taxa") estiver vazio
        btnCadastrarOrdem.disableProperty().bind(
            tfQuantidade.textProperty().isEmpty()
            .or(tfPreco.textProperty().isEmpty())
            .or(dpData.valueProperty().isNull())
        );

        // Aplica o filtro para aceitar apenas valores numéricos nos TextFields
        applyNumericFilter(tfQuantidade);
        applyNumericFilter(tfPreco);
        applyNumericFilter(tfTaxa);

        // Adiciona um listener para atualizar tfTotal quando tfQuantidade ou tfPreco forem alterados
        tfQuantidade.textProperty().addListener((observable, oldValue, newValue) -> updateTotal());
        tfPreco.textProperty().addListener((observable, oldValue, newValue) -> updateTotal());

        // Adiciona um evento de clique ao campo de texto do DatePicker para abrir o calendário
        dpData.getEditor().setOnMouseClicked(this::abrirCalendario);

        lblNomeAtivo.setText(App.getNomeAtivo() + " (" + App.getTickerAtivo().toUpperCase() + ")");

        tfTotal.setText("R$ " + total.toString().replace(".", ","));
    }

    @FXML
    private void abrirCalendario(MouseEvent event) {
        dpData.show();
    }

    private void applyNumericFilter(TextField textField) {
        // Define um UnaryOperator para filtrar os caracteres digitados
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            // Verifica se o texto inserido contém apenas dígitos numéricos e no máximo uma vírgula
            if (text.matches("[0-9]*") || (text.equals(",") && !textField.getText().contains(","))) {
                return change;
            }
            return null; // Rejeita a alteração se o caractere não for um dígito numérico
        };

        // Cria um TextFormatter com o UnaryOperator definido acima
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);

        // Aplica o TextFormatter ao TextField
        textField.setTextFormatter(textFormatter);
    }

    private void updateTotal() {
        if (tfQuantidade.getText().isEmpty()) {
            quantidade = BigDecimal.ZERO;
        } else {
            quantidade = new BigDecimal(tfQuantidade.getText().replace(",", "."));
        }

        if (tfPreco.getText().isEmpty()) {
            preco = BigDecimal.ZERO;
        } else {
            preco = new BigDecimal(tfPreco.getText().replace(",", "."));
        }

        total = quantidade.multiply(preco).setScale(2, RoundingMode.HALF_UP);
        tfTotal.setText("R$ " + total.toString().replace(".", ","));          
    }

    @FXML
    private void cadastrarOrdem() {
        if (App.getTipoOrdem().equals("Venda") && quantidade.compareTo(App.getAtivo().getQuantidade()) > 0) {
            App.mostrarAviso("Você não possui quantidade suficiente para esta venda", tfQuantidade.getScene().getWindow());
            
        } else {
            if (App.getAtivo() != null) {
                precoMedio = App.getAtivo().getPrecoMedio();
            }
    
            if (!tfTaxa.getText().isEmpty()) {
                taxa = new BigDecimal(tfTaxa.getText().replace(',', '.'));
            }        
    
            dataOrdem = dpData.getValue();
    
            Ordem ordem = OrdemDAO.criarOrdem(App.getTipoOrdem(), dataOrdem, quantidade, preco, taxa, quantidade.multiply(precoMedio), App.getTickerAtivo());        
            OrdemDAO.inserirOrdem(ordem, App.getAtivo(), App.getNomeAtivo());
            
            App.mostrarAviso("Ordem cadastrada com sucesso", btnCadastrarOrdem.getScene().getWindow());

            App.setRoot("TelaInicial");
        }
    }

    @FXML
    private void cancelar() {
        App.setRoot("TelaInicial");
    }

}
