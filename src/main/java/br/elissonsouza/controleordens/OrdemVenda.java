package br.elissonsouza.controleordens;

import java.time.LocalDate;

public class OrdemVenda extends Ordem {
    private final float custo;

    public OrdemVenda(LocalDate data, float quantidade, float preco, float custo, String tickerAtivo) {
        super(data, quantidade, preco, "Venda", tickerAtivo);
        this.custo = custo;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCusto: R$ " + custo;
    }
}
