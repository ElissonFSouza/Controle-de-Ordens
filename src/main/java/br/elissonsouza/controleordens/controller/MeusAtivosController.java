package br.elissonsouza.controleordens.controller;

import br.elissonsouza.controleordens.App;
import br.elissonsouza.controleordens.dao.AtivoDAO;
import br.elissonsouza.controleordens.model.Ativo;

import java.math.BigDecimal;

import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class MeusAtivosController {
    @FXML
    private Button btnVoltar;

    @FXML
    private Label lblTotalGasto;

    @FXML
    private TableView<Ativo> tvListaAtivos;

    @FXML
    private TableColumn<Ativo, String> tcNome;

    @FXML
    private TableColumn<Ativo, BigDecimal> tcQuantidade;

    @FXML
    private TableColumn<Ativo, BigDecimal> tcPrecoMedio;

    @FXML
    private TableColumn<Ativo, BigDecimal> tcTotal;

    @FXML
    private TableColumn<Ativo, BigDecimal> tcSaldoVendas;

    @FXML
    public void initialize() {
        // Define a fábrica de linhas da TableView para ajustar a altura da linha
        tvListaAtivos.setRowFactory(new Callback<TableView<Ativo>, TableRow<Ativo>>() {
            @Override
            public TableRow<Ativo> call(TableView<Ativo> tableView) {
                final TableRow<Ativo> row = new TableRow<>();
                row.setPrefHeight(40);
                return row;
            }
        });
        
        tcNome.setCellValueFactory(cellData -> cellData.getValue().nomeProperty().concat(" (").concat(cellData.getValue().tickerProperty()).concat(")"));
        tcQuantidade.setCellValueFactory(cellData -> cellData.getValue().quantidadeProperty());
        tcPrecoMedio.setCellValueFactory(cellData -> cellData.getValue().precoMedioProperty());
        tcTotal.setCellValueFactory(cellData -> cellData.getValue().valorTotalProperty());
        tcSaldoVendas.setCellValueFactory(cellData -> cellData.getValue().saldoVendasProperty());

        carregarAtivos();

        lblTotalGasto.setText(App.getTotalGasto().toString().replace(".", ","));
    }

    private void carregarAtivos() {
        // Chama o método AtivoDAO.listarAtivos para obter a lista de ativos
        ObservableList<Ativo> listaAtivos = AtivoDAO.listarAtivos();
        
        // Define os dados da TableView com a lista de ativos
        tvListaAtivos.setItems(listaAtivos);
    }

    @FXML
    private void voltar() {
        App.setRoot("TelaInicial");
    }
}