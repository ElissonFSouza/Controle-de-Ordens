package br.elissonsouza.controleordens2.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.elissonsouza.controleordens2.App;

public class OrdemVenda extends Ordem {
    private final BigDecimal custo;

    public OrdemVenda(LocalDate data, BigDecimal quantidade, BigDecimal preco, BigDecimal taxa, BigDecimal custo, String tickerAtivo) {
        super(data, quantidade, preco, taxa, "Venda", tickerAtivo);
        this.custo = custo;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    @Override
    public String toString() {
        return "\nData da ordem: " + super.getDataOrdem().format(App.formatter) + "\nTipo: " + super.getTipo()
               + "\nQuantidade: " + super.getTickerAtivo() + " " + super.getQuantidade()
               + "\nPreço: R$ " + super.getPreco() + "\nTaxa: R$ " + super.getTaxa()
               + "\nTotal da ordem: R$ " + super.getQuantidade().multiply(super.getPreco()) + "\nCusto: R$ " + custo;
    }
}
