import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner entrada = new Scanner(System.in);

        int menu = 0;
        boolean concluido;

        Ativo ativo;
        String nomeAtivo;

        do {           
            concluido = false;
            do {
                try {
                    exibirMenu();                    
                    menu = entrada.nextInt();
                    concluido = true;
                } catch (InputMismatchException e) {
                    System.out.println("\nDigite o número correspondente à opção desejada.");
                    entrada.next();
                }
            } while (!concluido);

            entrada.nextLine(); // Consumir nova linha pendente

            switch (menu) {
                case 1:
                    cadastrarOrdem("Compra", entrada);
                    break;

                case 2:
                    cadastrarOrdem("Venda", entrada);
                    break;

                case 3:
                    Carteira.imprimirAtivos();
                    break;

                case 4:
                    concluido = false;
                    do {
                        System.out.print("Nome do ativo: ");
                        nomeAtivo = entrada.nextLine().trim();
                        
                        if (!nomeAtivo.isEmpty()) {
                            ativo = Carteira.pesquisarAtivo(nomeAtivo);
                            if (ativo == null) {
                                System.out.println("\nAtivo não encontrado.");
                            } else {
                                System.out.println(ativo);
                                ativo.imprimirOrdens();
                            }
                            concluido = true;
                        } else {
                            System.out.println("O nome do ativo precisa ser inserido.");
                        }
                    } while (!concluido);
                    break;

                case 5:
                    System.out.println("\nSistema encerrado.");
            
                default:
                    System.out.println("\nOpção inválida.");
                    System.out.println("Digite o número correspondente à opção desejada.");
            }            
        } while (menu != 5);  
        
        entrada.close();
    }

    static void cadastrarOrdem(String tipo, Scanner entrada) {
        boolean concluido;

        Ativo ativo;
        String nomeAtivo;

        Ordem ordem;
        Date dataOrdem = null;
        float quantidade = 0;
        float preco = 0;  

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n====== Cadastrar ordem de " + tipo.toLowerCase() + " ======");

        concluido = false;
        do {
            System.out.print("Nome do ativo: ");
            nomeAtivo = entrada.nextLine().trim();

            if (!nomeAtivo.isEmpty()) {
                if (Carteira.pesquisarAtivo(nomeAtivo) == null) {
                    if (tipo.equals("Venda")) {
                        System.out.println("\nO ativo informado não está em carteira.");
                        break;
                    } else {
                        ativo = new Ativo(nomeAtivo);
                        Carteira.adicionarAtivo(ativo);
                    }                    
                }
                concluido = true;
            } else {
                System.out.println("O nome do ativo precisa ser inserido.");
            }
        } while (!concluido);

        concluido = false;
        do {
            try {
                System.out.print("Data da " + tipo.toLowerCase() + ": ");
                dataOrdem = sdf.parse(entrada.nextLine());
                concluido = true;
            } catch (ParseException e) {
                System.out.println("A data precisa ser inserida no formato DD/MM/AAAA.");
            }
        } while (!concluido);

        concluido = false;
        do {
            try {
                System.out.print("Quantidade: ");
                quantidade = Float.parseFloat(entrada.nextLine());
                concluido = true;
            } catch (NumberFormatException e) {
                System.out.println("Insira a quantidade no formato correto.");
            }
        } while (!concluido);

        concluido = false;
        do {
            try {
                System.out.print("Preço: ");
                preco = Float.parseFloat(entrada.nextLine());
                concluido = true;
            } catch (NumberFormatException e) {
                System.out.println("Insira o preço no formato correto.");
            }
        } while (!concluido);

        ordem = new Ordem(dataOrdem, quantidade, preco, tipo);
        Carteira.adicionarOrdem(nomeAtivo, ordem);
    }

    static void exibirMenu() {
        System.out.println("\n========== Menu Principal ==========");
        System.out.println("\n1 - Cadastrar ordem de compra");
        System.out.println("2 - Cadastrar ordem de venda");
        System.out.println("3 - Exibir lista de ativos");
        System.out.println("4 - Exibir informações de um ativo");
        System.out.println("5 - Encerrar sistema");
        System.out.print("> ");
    }
}
