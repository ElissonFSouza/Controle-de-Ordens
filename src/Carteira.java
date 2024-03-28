import java.util.ArrayList;

public class Carteira {
    private static final ArrayList<Ativo> listaAtivos = new ArrayList<>();

    public static ArrayList<Ativo> getListaAtivos() {
        return listaAtivos;
    }

    public static void adicionarAtivo(Ativo ativo) {
        listaAtivos.add(ativo);
    }  

    public static Ativo pesquisarAtivo(String nomeAtivo) {
        for (Ativo ativo : listaAtivos) {
            if (ativo.getNome().equals(nomeAtivo.toUpperCase())) {
                return ativo;
            }
        }
        return null;
    }  

    public static void adicionarOrdem(String nomeAtivo, Ordem ordem) {
        for (Ativo ativo : listaAtivos) {
            if (ativo.getNome().equals(nomeAtivo.toUpperCase())) {
                if (ordem.getTipo().equals("Compra")) {
                    ativo.novaOrdem(ordem);
                } else {
                    ativo.novaOrdemVenda(ordem);
                }
                break;
            }
        }
    }

    public static void imprimirAtivos() {
        if (getListaAtivos().isEmpty()) {
            System.out.println("\nNão há ativos na carteira.");
        } else {
            System.out.println("\n====================================");
            for(Ativo ativo : listaAtivos) {
                System.out.println(ativo);
            }
        }
    }
}
