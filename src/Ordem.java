import java.text.SimpleDateFormat;
import java.util.Date;

public class Ordem {
    private final Date data;
    private final float quantidade;
    private final float preco;
    private final float total;
    private final String tipo;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Ordem(Date data, float quantidade, float preco, String tipo) {
        this.data = data;
        this.quantidade = quantidade;
        this.preco = preco;
        this.total = preco * quantidade;
        this.tipo = tipo;
    }
        
    public Date getData() {
        return data;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public float getPreco() {
        return preco;
    }

    public float getTotal() {
        return total;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "\nData da ordem: " + sdf.format(data) + "\nTipo: " + tipo + "\nQuantidade: " + quantidade + "\nPreço: R$ " + preco + "\nValor total: R$ " + total;
    }
}
