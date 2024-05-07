package br.elissonsouza.controleordens.model;

import java.math.BigDecimal;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Ativo {
    private final StringProperty ticker;
    private final StringProperty nome;
    private final ObjectProperty<BigDecimal> quantidade;
    private final ObjectProperty<BigDecimal> precoMedio; 
    private final ObjectProperty<BigDecimal> saldoVendas;
    private final ObjectProperty<BigDecimal> totalComprado;
    private final ObjectProperty<BigDecimal> valorTotal;

    public Ativo(String ticker, String nome, BigDecimal quantidade, BigDecimal precoMedio, BigDecimal saldoVendas, BigDecimal totalComprado) {
        this.ticker = new SimpleStringProperty(ticker);
        this.nome = new SimpleStringProperty(nome);
        this.quantidade = new SimpleObjectProperty<>(quantidade);
        this.precoMedio = new SimpleObjectProperty<>(precoMedio);
        this.saldoVendas = new SimpleObjectProperty<>(saldoVendas);
        this.totalComprado = new SimpleObjectProperty<>(totalComprado);
        this.valorTotal = new SimpleObjectProperty<>(this.quantidade.get().multiply(this.precoMedio.get()));
    }

    public StringProperty tickerProperty() {
        return ticker;
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public ObjectProperty<BigDecimal> quantidadeProperty() {
        return quantidade;
    }

    public ObjectProperty<BigDecimal> precoMedioProperty() {
        return precoMedio;
    }

    public ObjectProperty<BigDecimal> saldoVendasProperty() {
        return saldoVendas;
    }

    public ObjectProperty<BigDecimal> totalCompradoProperty() {
        return totalComprado;
    }

    public ObjectProperty<BigDecimal> valorTotalProperty() {
        return valorTotal;
    }

    public String getTicker() {
        return ticker.get();
    }

    public String getNome() {
        return nome.get();
    }

    public BigDecimal getQuantidade() {
        return quantidade.get();
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio.get();
    }

    public BigDecimal getSaldoVendas() {
        return saldoVendas.get();
    }

    public BigDecimal getTotalComprado() {
        return totalComprado.get();
    }

    public BigDecimal getValorTotal() {
        return valorTotal.get();
    }

    // @Override
    // public String toString() {
    //     return "\n========= " + ticker + " =========" + "\nNome: " + nome + "\nQuantidade: "+ quantidade
    //     + "\nPreço médio: R$ " + precoMedio + "\nTotal atual: R$ " + quantidade.multiply(precoMedio) + "\nSaldo de vendas: R$ " + saldoVendas;
    // }
}
