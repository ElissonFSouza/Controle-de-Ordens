package br.elissonsouza.controleordens;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ordem {
    private final LocalDate dataOrdem;
    private final float quantidade;
    private final float preco;
    private final String tipo;
    private final String tickerAtivo;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Ordem(LocalDate data, float quantidade, float preco, String tipo, String tickerAtivo) {
        this.dataOrdem = data;
        this.quantidade = quantidade;
        this.preco = preco;
        this.tipo = tipo;
        this.tickerAtivo = tickerAtivo.toUpperCase();
    }
        
    public LocalDate getDataOrdem() {
        return dataOrdem;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public float getPreco() {
        return preco;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTickerAtivo() {
        return tickerAtivo;
    }

    @Override
    public String toString() {
        return "\nData da ordem: " + dataOrdem.format(formatter) + "\nTipo: " + tipo + "\nQuantidade: " + quantidade + "\nPreço: R$ " + preco + "\nTotal da ordem: R$ " + quantidade * preco;
    }
}
