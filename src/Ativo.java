import java.util.ArrayList;

public class Ativo {
    private final String nome;
    private float quantidade;
    private float precoMedio;
    private float totalAtual;
    private final ArrayList<Ordem> listaOrdens = new ArrayList<>();

    public Ativo(String nome) {
        this.nome = nome.toUpperCase();
        this.quantidade = 0;
        this.totalAtual = 0;        
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

    public void imprimirOrdens() {
        System.out.println("\n===> Lista de Ordens");
        for(Ordem ordem : listaOrdens) {
            System.out.println(ordem);
        }
    }

    public void novaOrdem(Ordem ordem) {
        listaOrdens.add(ordem);
        quantidade += ordem.getQuantidade();
        totalAtual += ordem.getTotal();        
        precoMedio = totalAtual / quantidade;
    }

    public void novaOrdemVenda(Ordem ordem) {
        OrdemVenda ordemVenda = new OrdemVenda(ordem.getData(), ordem.getQuantidade(), ordem.getPreco(), precoMedio);
        listaOrdens.add(ordemVenda);
        quantidade -= ordem.getQuantidade();
        totalAtual -= ordem.getQuantidade() * precoMedio;      
    }

    @Override
    public String toString() {
        return "\n======== " + nome + " ========" + "\nQuantidade: " + quantidade + "\nPreço médio: R$ " + precoMedio + "\nTotal atual: R$ " + totalAtual;
    }
}
