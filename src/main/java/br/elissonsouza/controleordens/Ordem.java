package br.elissonsouza.controleordens;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ordem {
    private final Date dataOrdem;
    private final float quantidade;
    private final float preco;
    private final String tipo;
    private final String tickerAtivo;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Ordem(Date data, float quantidade, float preco, String tipo, String tickerAtivo) {
        this.dataOrdem = data;
        this.quantidade = quantidade;
        this.preco = preco;
        this.tipo = tipo;
        this.tickerAtivo = tickerAtivo;
    }
        
    public Date getDataOrdem() {
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
        return "\nData da ordem: " + sdf.format(dataOrdem) + "\nTipo: " + tipo + "\nQuantidade: " + quantidade + "\nPreço: R$ " + preco + "\nTotal da ordem: R$ " + quantidade * preco;
    }
}
