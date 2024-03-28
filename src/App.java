import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner entradaMenu = new Scanner(System.in);
        Scanner entradaString = new Scanner(System.in);

        String aux;
        int menu = 0;
        boolean concluido;

        Ativo ativo;
        String nomeAtivo;

        do {           
            concluido = false;
            do {
                try {
                    exibirMenu();                    
                    aux = entradaMenu.nextLine();
                    menu = Integer.parseInt(aux);
                    concluido = true;
                } catch (NumberFormatException e) {
                    System.out.println("\nDigite o valor numérico correspondente à opção desejada.");
                }
            } while (!concluido);

            switch (menu) {
                case 1:
                    cadastrarOrdem("Compra");
                    break;

                case 2:
                    cadastrarOrdem("Venda");
                    break;

                case 3:
                    Carteira.imprimirAtivos();
                    break;

                case 4:
                    concluido = false;
                    do {
                        System.out.print("Nome do ativo: ");
                        nomeAtivo = entradaString.nextLine();
                        
                        if (!nomeAtivo.toLowerCase().equals("")) {
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
            
                default:
                    System.out.println("\nOpção inválida.");
            }            
        } while (menu != 5);        
    }

    static void cadastrarOrdem(String tipo) {
        boolean concluido;

        Ativo ativo;
        String nomeAtivo;

        Ordem ordem;
        Date dataOrdem = null;
        float quantidade = 0;
        float preco = 0;  

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Scanner entrada = new Scanner(System.in);
        Scanner entradaString = new Scanner(System.in);  

        System.out.println("\n====== Cadastrar ordem de " + tipo.toLowerCase() + " ======");

        concluido = false;
        do {
            System.out.print("Nome do ativo: ");
            nomeAtivo = entradaString.nextLine();

            if (!nomeAtivo.equals("")) {
                if (Carteira.pesquisarAtivo(nomeAtivo) == null) {
                    if (tipo.equals("Venda")) {
                        System.out.println("\nO ativo informado não está em carteira.");
                        return;
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
                dataOrdem = sdf.parse(entradaString.nextLine());
                concluido = true;
            } catch (ParseException e) {
                System.out.println("A data precisa ser inserida no formato DD/MM/AAAA.");
            }
        } while (!concluido);

        concluido = false;
        do {
            try {
                System.out.print("Quantidade: ");
                quantidade = entrada.nextFloat();
                concluido = true;
            } catch (NumberFormatException e) {
                System.out.println("\nInsira a quantidade no formato correto.");
            }
        } while (!concluido);

        concluido = false;
        do {
            try {
                System.out.print("Preço: ");
                preco = entrada.nextFloat();
                concluido = true;
            } catch (NumberFormatException e) {
                System.out.println("\nInsira o preço no formato correto.");
            }
        } while (!concluido);

        ordem = new Ordem(dataOrdem, quantidade, preco, tipo);
        Carteira.adicionarOrdem(nomeAtivo, ordem);
    }

    static void exibirMenu() {
        System.out.println("\n====================================");
        System.out.println("\n1 - Cadastrar ordem de compra");
        System.out.println("2 - Cadastrar ordem de venda");
        System.out.println("3 - Exibir lista de ativos");
        System.out.println("4 - Exibir informações de um ativo");
        System.out.print("> ");
    }
}
