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
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            String sql = "SELECT cpf, nome, saldo " + "FROM users WHERE cpf = ? ";
            ps = connection.prepareStatement(sql);
            ps.setString(1, cpf);

            rs = ps.executeQuery();
            while (rs.next()){
                System.out.println("============= Dados do cliente =============");
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("CPF: " + rs.getString("cpf"));
                System.out.println("Saldo: R$" + rs.getDouble("saldo"));
                System.out.println("============================================");

            }
            return true;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        } finally {
            try {
                rs.close();
                connection.close();
                ps.close();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    // Todo: ver porque esse método não está funcionando
    private static void consultaSaldo() {
        System.out.print("Insira o CPF do cliente a ser pesquisado: ");
        sc.skip("\\R?");
        String cpf = sc.nextLine();
        if (!pesquisarCliente(cpf)) {
            System.err.println("CPF inválido ou cliente não cadastrado.");
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
            sc.skip("\\R?");
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
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String sql = "INSERT INTO users (nome, cpf, saldo) VALUES (?,?,?)";
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
