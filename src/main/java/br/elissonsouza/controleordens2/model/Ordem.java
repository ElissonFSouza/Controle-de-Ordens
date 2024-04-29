package br.elissonsouza.controleordens2.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.elissonsouza.controleordens2.App;

public class Ordem {
    private final LocalDate dataOrdem;
    private final BigDecimal quantidade;
    private final BigDecimal preco;
    private final BigDecimal taxa;
    private final String tipo;
    private final String tickerAtivo;

    public Ordem(LocalDate data, BigDecimal quantidade, BigDecimal preco, BigDecimal taxa, String tipo, String tickerAtivo) {
        this.dataOrdem = data;
        this.quantidade = quantidade;
        this.preco = preco;
        this.taxa = taxa;
        this.tipo = tipo;
        this.tickerAtivo = tickerAtivo.toUpperCase();
    }
        
    public LocalDate getDataOrdem() {
        return dataOrdem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTickerAtivo() {
        return tickerAtivo;
    }

    @Override
    public String toString() {
        return "\nData da ordem: " + dataOrdem.format(App.formatter) + "\nTipo: " + tipo +"\nQuantidade: " + tickerAtivo + " " + quantidade
               + "\nPreço: R$ " + preco + "\nTaxa: " + tickerAtivo + " " + taxa + "\nTotal da ordem: R$ " + quantidade.multiply(preco);
    }
}
