package br.elissonsouza.controleordens;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        final int OPCAO_CADASTRAR_COMPRA = 1;
        final int OPCAO_CADASTRAR_VENDA = 2;
        final int OPCAO_LISTAR_ATIVOS = 3;
        final int OPCAO_EXIBIR_INFO_ATIVO = 4;
        final int OPCAO_ENCERRAR_SISTEMA = 5;

        Scanner entrada = new Scanner(System.in);
        int menu = 0;

        do {
            boolean concluido = false;
            do {
                try {
                    exibirMenu();                    
                    menu = entrada.nextInt();
                    concluido = true;

                } catch (InputMismatchException e) {
                    System.err.println("\nDigite o número correspondente à opção desejada.");
                    entrada.next(); // Consumir nova linha pendente
                }
            } while (!concluido);

            entrada.nextLine(); // Consumir nova linha pendente

            switch (menu) {
                case OPCAO_CADASTRAR_COMPRA:
                    cadastrarOrdem("Compra", entrada);
                    break;

                case OPCAO_CADASTRAR_VENDA:
                    cadastrarOrdem("Venda", entrada);
                    break;

                case OPCAO_LISTAR_ATIVOS:
                    AtivoDAO.listarAtivos();
                    break;

                case OPCAO_EXIBIR_INFO_ATIVO:
                    exibirOrdensAtivo(entrada);
                    break;

                case OPCAO_ENCERRAR_SISTEMA:
                    encerrarSistema(entrada);
                    break;
            
                default:
                    System.out.println("\nOpção inválida.");
                    System.out.println("Digite o número correspondente à opção desejada.");
            } 
                    
        } while (menu != OPCAO_ENCERRAR_SISTEMA);        
    }
    
    private static void cadastrarOrdem(String tipo, Scanner entrada) {       
        System.out.println("\n====== Cadastrar ordem de " + tipo.toLowerCase() + " ======");

        String tickerAtivo = lerTickerAtivo(entrada);

        Ativo ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);
        if (tipo.equals("Venda") && ativo == null) {
            System.out.println("\nVocê não possui o ativo informado.");
            return;                                 
        }

        String nomeAtivo = lerNomeAtivo(entrada);

        Date dataOrdem = lerDataOrdem(entrada, tipo);   

        float quantidade = lerQuantidade(entrada);

        float preco = lerPreco(entrada);
        
        Ordem ordem;
        if (tipo.equals("Compra")) {
            ordem = new Ordem(dataOrdem, quantidade, preco, tipo, tickerAtivo);
        } else {
            ordem = new OrdemVenda(dataOrdem, quantidade, preco, ativo.getPrecoMedio(), tickerAtivo);
        }
        
        OrdemDAO.inserirOrdem(ordem, ativo, nomeAtivo);
    }

    private static void exibirOrdensAtivo(Scanner entrada) {
        Ativo ativo;
        String tickerAtivo;
        
        Boolean concluido = false;
        do {
            System.out.print("Ticker do ativo: ");
            tickerAtivo = entrada.nextLine().trim();
            
            if (!tickerAtivo.isEmpty()) {
                ativo = AtivoDAO.pesquisarAtivo(tickerAtivo);

                if (ativo == null) System.out.println("\nAtivo não encontrado.");
                else {
                    System.out.println(ativo);
                    OrdemDAO.listarOrdens(ativo);
                }
                concluido = true;

            } else {
                System.out.println("O ticker do ativo precisa ser inserido.");
            }      
                                
        } while (!concluido);
    }

    private static void exibirMenu() {
        System.out.println("\n========== Menu Principal ==========");
        System.out.println("\n1 - Cadastrar ordem de compra");
        System.out.println("2 - Cadastrar ordem de venda");
        System.out.println("3 - Exibir lista de ativos");
        System.out.println("4 - Exibir ordens de um ativo");
        System.out.println("5 - Encerrar sistema");
        System.out.print("> ");
    }
   
    private static void encerrarSistema(Scanner entrada) {
        entrada.close();
        Database.getInstance().closeConnection();                    
        System.out.println("\nSistema encerrado.\n");
    }

    private static String lerTickerAtivo(Scanner entrada) {
        String tickerAtivo;

        boolean concluido = false;
        do {
            System.out.print("Ticker do ativo: ");
            tickerAtivo = entrada.nextLine().trim().toUpperCase();            

            if (!tickerAtivo.isEmpty()) {
                concluido = true;
            } else {
                System.out.println("O ticker do ativo precisa ser inserido.");
            }
            
        } while (!concluido);

        return tickerAtivo;
    }

    private static String lerNomeAtivo(Scanner entrada) {
        String nomeAtivo;

        boolean concluido = false;
        do {
            System.out.print("Nome do ativo: ");
            nomeAtivo = entrada.nextLine().trim();

            if (!nomeAtivo.isEmpty()) {
                concluido = true;
            } else {
                System.out.println("O nome do ativo precisa ser inserido.");
            }

        } while (!concluido);

        return nomeAtivo;
    }

    private static Date lerDataOrdem(Scanner entrada, String tipo) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataOrdem = null;

        boolean concluido = false;
        do {
            try {
                System.out.print("Data da " + tipo.toLowerCase() + ": ");
                dataOrdem = sdf.parse(entrada.nextLine());
                concluido = true;
            } catch (ParseException e) {
                System.err.println("A data precisa ser inserida no formato DD/MM/AAAA.");
            }
        } while (!concluido);

        return dataOrdem;
    }

    private static float lerQuantidade(Scanner entrada) {
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

    private static float lerPreco(Scanner entrada) {
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
}