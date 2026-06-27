// Sistema de Controle de Estoque e Vendas
// Versao: 1.1
// Historico de versoes:
//   1.0 - Versao inicial entregue pela equipe antiga.
//   1.1 - Quitacao de divida tecnica (Unidade 2):
//         D1.: senha do admin externalem arquivo .env.
//         D16: unificacao da regra de desconto.
//         D13 e D22: constantes nomeadas no lugar de numeros magicos.
//         D5: encapsulamento da classe Produto.
//         D25: extracao da autenticacao de administrador para um metodo dedicado.
//
// Autores: Rodrigo Ferreira da Nobrega, Jorge do Amaral Neto.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;   // (nao usado)

public class Estoque {
    private static final String SENHA_ADMIN = carregarSenhaAdmin();

    private static final double LIMITE_PARA_DESCONTO = 100.0;
    private static final double PERCENTUAL_DESCONTO = 0.10;

    private static final String OPCAO_CADASTRAR = "1";
    private static final String OPCAO_VENDER = "2";
    private static final String OPCAO_LISTAR = "3";
    private static final String OPCAO_ESTOQUE_BAIXO = "4";
    private static final String OPCAO_ADMIN = "5";
    private static final String OPCAO_SAIR = "0";

    static ArrayList<Produto> produtos = new ArrayList<>();
    static ArrayList<String> hist = new ArrayList<>();  // historico

    static class Produto {
        private String nome;
        private double preco;
        private int qtd;

        Produto(String nome, double preco, int qtd) {
            this.nome = nome;
            this.preco = preco;
            this.qtd = qtd;
        }

        String getNome() {
            return nome;
        }

        double getPreco() {
            return preco;
        }

        int getQtd() {
            return qtd;
        }

        void setQtd(int qtd) {
            this.qtd = qtd;
        }
    }

    // funcao que adiciona produto
    static void add(String n, double p, int q) {
        Produto prod = new Produto(n, p, q);
        produtos.add(prod);
        hist.add(n);
        System.out.println("Produto adicionado!");
    }

    static double vender(String nome, int quantidade) {
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            if (p.getNome().equals(nome)) {
                if (p.getQtd() >= quantidade) {
                    p.setQtd(p.getQtd() - quantidade);
                    double total = aplicarDesconto(p.getPreco() * quantidade);
                    System.out.println("Venda realizada. Total: " + total);
                    return total;
                } else {
                    System.out.println("Estoque insuficiente");
                    return 0;
                }
            }
        }
        System.out.println("Produto nao encontrado");
        return 0;
    }

    static double aplicarDesconto(double total) {
        if (total > LIMITE_PARA_DESCONTO) {
            return total - total * PERCENTUAL_DESCONTO;
        }
        return total;
    }

    static double calcular_total(double preco, int quantidade) {
        return aplicarDesconto(preco * quantidade);
    }

    static void listar() {
        System.out.println("=== PRODUTOS ===");
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            System.out.println(p.getNome() + " - R$" + p.getPreco()
                    + " - qtd: " + p.getQtd());
        }
    }

    static void relatorio_estoque_baixo() {
        System.out.println("=== ESTOQUE BAIXO ===");
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            if (p.getQtd() < 5) {   // estoque baixo
                System.out.println(p.getNome() + " esta com estoque baixo ("
                        + p.getQtd() + ")");
            }
        }
    }

    // funcao antiga, nao usamos mais
    // static void exportar() {
    //     try {
    //         FileWriter f = new FileWriter("dados.txt");
    //         for (int i = 0; i < produtos.size(); i++) {
    //             f.write(produtos.get(i).nome + "\n");
    //         }
    //         f.close();
    //     } catch (Exception e) {}
    // }

    static void relatorio_vendas() {
        // TODO: implementar de verdade
    }

    static boolean autenticarAdmin(String senha) {
        return SENHA_ADMIN.equals(senha);
    }

    private static String carregarSenhaAdmin() {
        final String chave = "ESTOQUE_ADMIN_SENHA";

        try (BufferedReader br = new BufferedReader(new FileReader(".env"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                int separador = linha.indexOf('=');
                if (separador < 0) {
                    continue;
                }
                String nomeVariavel = linha.substring(0, separador).trim();
                String valor = linha.substring(separador + 1).trim();
                if (nomeVariavel.equals(chave)) {
                    return valor;
                }
            }
        } catch (IOException e) {
            // .env ausente ou ilegivel: cai nos fallbacks abaixo.
        }

        String doAmbiente = System.getenv(chave);
        if (doAmbiente != null && !doAmbiente.isEmpty()) {
            return doAmbiente;
        }

        return "admin-dev";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1-Cadastrar  2-Vender  3-Listar  4-Estoque baixo  5-Admin  0-Sair");
            System.out.print("Opcao: ");
            String op = sc.next();
            if (op.equals(OPCAO_CADASTRAR)) {
                System.out.print("Nome: ");
                String n = sc.next();
                System.out.print("Preco: ");
                double p = Double.parseDouble(sc.next());   // quebra se digitar texto
                System.out.print("Qtd: ");
                int q = Integer.parseInt(sc.next());        // quebra se digitar texto
                add(n, p, q);
            } else if (op.equals(OPCAO_VENDER)) {
                System.out.print("Nome do produto: ");
                String n = sc.next();
                System.out.print("Quantidade: ");
                int q = Integer.parseInt(sc.next());
                vender(n, q);
            } else if (op.equals(OPCAO_LISTAR)) {
                listar();
            } else if (op.equals(OPCAO_ESTOQUE_BAIXO)) {
                relatorio_estoque_baixo();
            } else if (op.equals(OPCAO_ADMIN)) {
                System.out.print("Senha: ");
                String s = sc.next();
                if (autenticarAdmin(s)) {
                    System.out.println("Acesso liberado");
                } else {
                    System.out.println("Senha errada");
                }
            } else if (op.equals(OPCAO_SAIR)) {
                break;
            } else {
                System.out.println("Opcao invalida");
            }
        }
    }
}
