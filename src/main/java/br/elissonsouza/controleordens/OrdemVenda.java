package br.elissonsouza.controleordens;

import java.util.Date;

public class OrdemVenda extends Ordem {
    private final float custo;

    public OrdemVenda(Date data, float quantidade, float preco, float custo, String tickerAtivo) {
        super(data, quantidade, preco, "Venda", tickerAtivo);
        this.custo = custo;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCusto: R$ " + custo;
    }
}
