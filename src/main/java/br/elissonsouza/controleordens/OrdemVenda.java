package br.elissonsouza.controleordens;

import java.util.Date;

public class OrdemVenda extends Ordem {
    private final float custo;

    public OrdemVenda(Date data, float quantidade, float preco, float precoMedio, String tickerAtivo) {
        super(data, quantidade, preco, "Venda", tickerAtivo);
        this.custo = quantidade * precoMedio;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCusto: R$ " + custo;
    }
}
