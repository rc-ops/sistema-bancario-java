import java.sql.*;
import java.util.Scanner;

public class Operacoes extends Cliente{
    static Scanner sc = new Scanner(System.in);

    private static void cadastrarCliente(){
        System.out.println("====== CADASTRO DE CLIENTE ======");

        do {
            try {
                System.out.print("Insira o nome do cliente: ");
                sc.skip("\\R?");
                String nome = sc.nextLine();
                System.out.print("Insira o CPF: ");
                sc.skip("\\R?");
                String cpf = sc.nextLine();
                System.out.print("Insira o saldo: ");
                double saldo = sc.nextDouble();

                connectDatabase(nome, cpf, saldo);


            } catch (Exception e) {
                System.err.println(("Ocorreu o seguinte erro: " + e.getMessage()));
                System.out.println("Tentando novamente...");
                cadastrarCliente();
            }
            System.out.print("Deseja cadastrar outro cliente? [S/N]: ");
            sc.skip("\\R?");
            String resposta = sc.nextLine();
            if (resposta.equalsIgnoreCase("n")){
                break;
            }
        } while(true);
    }

    private static boolean pesquisarCliente(String cpf){
        if (clientes.size() == 0){
            System.err.println("Não há nenhum cliente cadastrado no momento.");
            return false;
        } else{
            return clientes.contains(cpf);
        }
    }

    // Todo: ver porque esse método não está funcionando
    private static void consultaSaldo(){
        System.out.print("Insira o CPF do cliente a ser pesquisado: ");
        sc.skip("\\R?");
        String cpf = sc.nextLine();
        if (pesquisarCliente(cpf)){
            System.out.println("Informações encontradas do cliente com CPF " + cpf);
            System.out.println(clientes.get(clientes.indexOf(cpf)));
        } else{
            System.err.println("CPF inválido ou cliente não cadastrado no sistema.");
        }
    }

    protected static void menu(){
        int opcao;
        do {
            System.out.println("====== MENU ======");
            System.out.println("Opções disponíveis: ");
            System.out.println("1 - Cadastrar Cliente");
            System.out.println("2 - Consulta Saldo");
            System.out.println("3 - Realizar saque");
            System.out.println("4 - Realizar deposito.");
            System.out.println("5 - Sair do programa.");
            System.out.print("Insira a opção desejada: ");
            opcao = sc.nextInt();

            switch (opcao){
                case 1:
                    cadastrarCliente();
                    break;
                case 2:
                    consultaSaldo();
                    break;
            }


        } while (opcao != 5);
    }

    private static void connectDatabase(String nome, String cpf, double saldo){
        Connection connection = null;
        try{
            // Todo: resolver o erro No suitable driver found for jdbc:sqlite:/sistema/database/users.db
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String sql = "INSERT INTO users(nome, cpf, saldo) VALUES(?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nome);
            pstmt.setString(2, cpf);
            pstmt.setDouble(3, saldo);
            pstmt.executeUpdate();
            System.out.println("Cliente cadastrado com sucesso!");

        } catch(SQLException e){
            System.out.println("Não foi possível cadastrar o usuário pelo seguinte motivo: ");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e){
                System.err.println(e.getMessage());
            }
        }
    }
}
