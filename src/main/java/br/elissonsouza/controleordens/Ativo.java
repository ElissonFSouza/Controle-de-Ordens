package br.elissonsouza.controleordens;

public class Ativo {
    private final String ticker;
    private final String nome;
    private float quantidade;
    private float precoMedio;
    private float totalAtual;   

    public Ativo(String ticker, String nome, float quantidade, float precoMedio, float totalAtual) {
        this.ticker = ticker;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoMedio = precoMedio;
        this.totalAtual = totalAtual;
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

    public float getTotalAtual() {
        return totalAtual;
    }

    @Override
    public String toString() {
        return "\n======== " + ticker + " ========" + "\nNome: " + nome + "\nQuantidade: " + quantidade + "\nPreço médio: R$ " + precoMedio + "\nTotal atual: R$ " + totalAtual;
    }
}
