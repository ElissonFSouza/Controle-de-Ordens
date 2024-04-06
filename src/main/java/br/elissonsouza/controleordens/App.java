package br.elissonsouza.controleordens;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    private static Scanner entrada = new Scanner(System.in);
    
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

        String tickerAtivo = lerTickerOuNome("Ticker");

        Ativo ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);

        if (tipo.equals("Venda") && (ativo == null || ativo.getQuantidade() == 0)) {
            System.out.println("\nVocê não possui o ativo informado.");
            return;                                 
        }

        String nomeAtivo = null;
        float precoMedio = 0;

        if (ativo == null) {
            nomeAtivo = lerTickerOuNome("Nome");   
        } else {
            precoMedio = ativo.getPrecoMedio();
        }

        LocalDate dataOrdem = lerDataOrdem(tipo);   

        float quantidade = lerQuantidade();
        if (tipo.equals("Venda") && quantidade > ativo.getQuantidade()) {
            System.out.println("\nNão é possível vender uma quantidade maior do que a quantidade em posse.");
            return;
        }

        float preco = lerPreco();
        
        Ordem ordem = OrdemDAO.criarOrdem(tipo, dataOrdem, quantidade, preco, quantidade * precoMedio, tickerAtivo);        
        OrdemDAO.inserirOrdem(ordem, ativo, nomeAtivo);
    }

    private static void listarOrdensAtivo() {
        System.out.println("");

        String tickerOuNome = lerTickerOuNome("Ticker ou nome");

        Ativo ativo = AtivoDAO.pesquisarAtivo(tickerOuNome);

        if (ativo == null) {
            System.out.println("\nAtivo não encontrado.");
        } else {
            System.out.println(ativo);
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
                    OrdemDAO.listarOrdens(tickerAtivo);
                    break;

                case OPCAO_FILTRAR_MES:
                    String ano = lerAno();                                       
                    OrdemDAO.listarOrdensAno(tickerAtivo, ano);
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
                    exibirMenuBuscarOrdens();
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
   
    private static void exibirMenuBuscarOrdens() {
        System.out.println("\n==== Selecionar modo de busca ====");
        System.out.println("1 - Listar todas as ordens");
        System.out.println("2 - Filtrar ordens por ano");
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

    private static LocalDate lerDataOrdem(String tipo) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataOrdem = null;

        boolean concluido = false;
        do {
            System.out.print("Data da " + tipo.toLowerCase() + " (dd/mm/aaaa): ");

            dataOrdem = LocalDate.parse(entrada.nextLine(), formatter);
            
            concluido = true;

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
                System.out.print("Preço: R$ ");
                preco = Float.parseFloat(entrada.nextLine());
                concluido = true;
            } catch (NumberFormatException e) {
                System.err.println("Insira o preço no formato correto.");
            }
        } while (!concluido);

        return preco;
    }

    private static String lerAno() {
        String anoString = null;

        boolean concluido = false;
        do {
            try {
                System.out.print("\nAno: ");
                int ano = entrada.nextInt();

                anoString = String.format("%04d", ano);
                concluido = true;

            } catch (NumberFormatException e) {
                System.err.println("Insira o ano no formato correto.");
            }

        } while (!concluido);

        return anoString;
    }
}