package br.elissonsouza.controleordens2.model;

import java.math.BigDecimal;

public class Ativo {
    private final String ticker;
    private final String nome;
    private BigDecimal quantidade;
    private BigDecimal precoMedio; 
    private BigDecimal saldoVendas;
    private BigDecimal totalGasto;

    public Ativo(String ticker, String nome, BigDecimal quantidade, BigDecimal precoMedio, BigDecimal saldoVendas, BigDecimal totalGasto) {
        this.ticker = ticker;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoMedio = precoMedio;
        this.saldoVendas = saldoVendas;
        this.totalGasto = totalGasto;
    }

    public String getTicker() {
        return ticker;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }

    public BigDecimal getSaldoVendas() {
        return saldoVendas;
    }

    public BigDecimal getTotalGasto() {
        return totalGasto;
    }

    @Override
    public String toString() {
        return "\n========= " + ticker + " =========" + "\nNome: " + nome + "\nQuantidade: "+ quantidade
        + "\nPreço médio: R$ " + precoMedio + "\nTotal atual: R$ " + quantidade.multiply(precoMedio) + "\nSaldo de vendas: R$ " + saldoVendas;
    }
}
