package br.elissonsouza.controleordens;

public class Ativo {
    private final String ticker;
    private final String nome;
    private float quantidade;
    private float precoMedio; 
    private float saldoVendas;

    public Ativo(String ticker, String nome, float quantidade, float precoMedio, float saldoVendas) {
        this.ticker = ticker;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoMedio = precoMedio;
        this.saldoVendas = saldoVendas;
    }

    public String getTicker() {
        return ticker;
    }

    public String getNome() {
        return nome;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public float getPrecoMedio() {
        return precoMedio;
    }

    public float getSaldoVendas() {
        return saldoVendas;
    }

    @Override
    public String toString() {
        return "\n========= " + ticker + " =========" + "\nNome: " + nome + "\nQuantidade: "+ quantidade
        + "\nPreço médio: R$ " + precoMedio + "\nTotal atual: R$ " + quantidade * precoMedio + "\nSaldo de vendas: R$ " + saldoVendas;
    }
}
