package br.elissonsouza.controleordens;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    static Scanner entrada = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        final int OPCAO_CADASTRAR_COMPRA = 1;
        final int OPCAO_CADASTRAR_VENDA = 2;
        final int OPCAO_LISTAR_ATIVOS = 3;
        final int OPCAO_EXIBIR_ORDENS_ATIVO = 4;
        final int OPCAO_ENCERRAR_SISTEMA = 5;

        int opcao;

        do {
            opcao = escolherOpcaoMenu(1);

            entrada.nextLine(); // Consumir nova linha pendente

            switch (opcao) {
                case OPCAO_CADASTRAR_COMPRA:
                    cadastrarOrdem("Compra");
                    break;

                case OPCAO_CADASTRAR_VENDA:
                    cadastrarOrdem("Venda");
                    break;

                case OPCAO_LISTAR_ATIVOS:
                    AtivoDAO.listarAtivos();
                    break;

                case OPCAO_EXIBIR_ORDENS_ATIVO:
                    listarOrdensAtivo();
                    break;

                case OPCAO_ENCERRAR_SISTEMA:
                    encerrarSistema();
                    break;
            
                default:
                    System.out.println("\nOpção inválida.");
                    System.out.println("Digite o número correspondente à opção desejada.");
            } 
                    
        } while (opcao != OPCAO_ENCERRAR_SISTEMA);        
    }
    
    private static void cadastrarOrdem(String tipo) {       
        System.out.println("\n==== Cadastrar ordem de " + tipo.toLowerCase() + " ====");

        String tickerAtivo = lerTickerOuNome("Ticker ou nome");

        Ativo ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);

        if (tipo.equals("Venda") && (ativo == null || ativo.getQuantidade() == 0)) {
            System.out.println("\nVocê não possui o ativo informado.");
            return;                                 
        }

        String nomeAtivo = null;
        if (ativo == null) nomeAtivo = lerTickerOuNome("Nome");        

        Date dataOrdem = lerDataOrdem(tipo);   

        float quantidade = lerQuantidade();
        if (tipo.equals("Venda") && quantidade > ativo.getQuantidade()) {
            System.out.println("\nNão é possível vender uma quantidade maior do que a quantidade em posse.");
            return;
        }

        float preco = lerPreco();
        
        Ordem ordem;
        if (tipo.equals("Compra")) {
            ordem = new Ordem(dataOrdem, quantidade, preco, tipo, tickerAtivo);
        } else {
            ordem = new OrdemVenda(dataOrdem, quantidade, preco, quantidade * ativo.getPrecoMedio(), tickerAtivo);
        }
        
        OrdemDAO.inserirOrdem(ordem, ativo, nomeAtivo);
    }

    private static void listarOrdensAtivo() {
        System.out.println("");

        String tickerAtivo = lerTickerOuNome("Ticker");

        Ativo ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);

        if (ativo == null) {
            System.out.println("\nAtivo não encontrado.");
        } else {
            apresentarOpcoesBusca(ativo);            
        }
    }

    private static void apresentarOpcoesBusca(Ativo ativo) {
        final int OPCAO_VER_TUDO = 1;
        final int OPCAO_FILTRAR_MES = 2;
        final int OPCAO_CANCELAR = 3;

        String tickerAtivo = ativo.getTicker();

        int opcao;

        do {
            opcao = escolherOpcaoMenu(2);

            entrada.nextLine(); // Consumir nova linha pendente

            switch (opcao) {
                case OPCAO_VER_TUDO:
                    System.out.println(ativo);
                    OrdemDAO.listarOrdens(tickerAtivo, null);
                    break;

                case OPCAO_FILTRAR_MES:
                    String mes = lerMes();
                    System.out.println(ativo);                    
                    OrdemDAO.listarOrdens(tickerAtivo, mes);
                    break;

                case OPCAO_CANCELAR:                    
                    break;
            
                default:
                    System.out.println("\nOpção inválida.");
                    System.out.println("Digite o número correspondente à opção desejada.");
            } 
                    
        } while (opcao < 1 || opcao > 3);
    }

    private static int escolherOpcaoMenu(int menu) {
        int opcao = 0;

        boolean concluido = false;
        do {
            try {
                if (menu == 1) {
                    exibirMenuPrincipal();
                } else if (menu == 2) {
                    exibirMenuBuscaOrdens();
                }   

                opcao = entrada.nextInt();
                concluido = true;

            } catch (InputMismatchException e) {
                System.err.println("\nInsira o número correspondente à opção desejada.");
                entrada.next(); // Consumir nova linha pendente
            }
        } while (!concluido);

        return opcao;
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n========== Menu Principal ==========");
        System.out.println("1 - Cadastrar ordem de compra");
        System.out.println("2 - Cadastrar ordem de venda");
        System.out.println("3 - Exibir lista de ativos");
        System.out.println("4 - Exibir ordens de um ativo");
        System.out.println("5 - Encerrar sistema");
        System.out.print("> ");
    }
   
    private static void exibirMenuBuscaOrdens() {
        System.out.println("\n==== Selecionar modo de busca ====");
        System.out.println("1 - Ver todas as ordens");
        System.out.println("2 - Filtrar ordens por mês");
        System.out.println("3 - Cancelar");
        System.out.print("> ");
    }

    private static void encerrarSistema() {
        entrada.close();
        Database.getInstance().closeConnection();                    
        System.out.println("\nSistema encerrado.\n");
    }

    private static String lerTickerOuNome(String str) {
        String tickerOuNome;

        boolean concluido = false;
        do {
            System.out.print(str + " do ativo: ");
            tickerOuNome = entrada.nextLine().trim();            

            if (!tickerOuNome.isEmpty()) {
                concluido = true;
            } else {
                System.out.println(str + " do ativo precisa ser inserido.");
            }
            
        } while (!concluido);

        return tickerOuNome;
    }

    private static Date lerDataOrdem(String tipo) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataOrdem = null;

        boolean concluido = false;
        do {
            try {
                System.out.print("Data da " + tipo.toLowerCase() + " (dd/mm/aaaa): ");
                dataOrdem = sdf.parse(entrada.nextLine());
                concluido = true;
            } catch (ParseException e) {
                System.err.println("A data precisa ser inserida no formato dd/mm/aaaa.");
            }
        } while (!concluido);

        return dataOrdem;
    }

    private static float lerQuantidade() {
        float quantidade = 0;

        boolean concluido = false;
        do {
            try {
                System.out.print("Quantidade: ");
                quantidade = Float.parseFloat(entrada.nextLine());
                concluido = true;
            } catch (NumberFormatException e) {
                System.err.println("Insira a quantidade no formato correto.");
            }
        } while (!concluido);

        return quantidade;
    }

    private static float lerPreco() {
        float preco = 0;

        boolean concluido = false;
        do {
            try {
                System.out.print("Preço: ");
                preco = Float.parseFloat(entrada.nextLine());
                concluido = true;
            } catch (NumberFormatException e) {
                System.err.println("Insira o preço no formato correto.");
            }
        } while (!concluido);

        return preco;
    }

    private static String lerMes() {
        String mesString = null;

        boolean concluido = false;
        do {
            try {
                System.out.print("Mês: ");
                int mes = entrada.nextInt();

                if (mes >= 1 && mes <= 12) {
                    mesString = String.format("%02d", mes);
                    concluido = true;
                } else {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {
                System.err.println("Insira o número corespondente ao mês desejado.");
            }

        } while (!concluido);

        return mesString;
    }
}