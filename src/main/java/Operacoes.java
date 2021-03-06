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

    private static void pesquisarCliente(){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        System.out.print("Insira o CPF do cliente a ser pesquisado: ");
        sc.skip("\\R?");
        String cpf = sc.nextLine();

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
    }
    private static double consultaSaldo(String cpf) {
        double saldo = 0;
        ResultSet rs = null;

        Connection connection = null;
        String sqlConsulta = "select saldo " + "from users where cpf = ? ";
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            PreparedStatement ps = connection.prepareStatement(sqlConsulta);
            ps.setString(1, cpf);
            rs = ps.executeQuery();

            while (rs.next()){
                saldo = rs.getDouble("saldo");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return saldo;
    }

    private static void realizarSaque(String cpf, double quantia){
        Connection connection;
        ResultSet rs = null;
        double saldo = consultaSaldo(cpf);

        String sqlUpdate = "UPDATE users SET saldo = ? WHERE cpf = ?";

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            PreparedStatement ps = connection.prepareStatement(sqlUpdate);
            ps.setString(2, cpf);
            ps.setDouble(1, saldo-quantia);
            ps.executeUpdate();

            ps.close();
            connection.close();
            System.out.println("Saque realizado com sucesso.");


        } catch (SQLException e){
            System.err.println("Erro ao realizar saque.");
            System.out.println(e.getMessage());
        }
    }

    private static void realizarDeposito(String cpf, double quantia){
        Connection connection;
        ResultSet rs = null;
        double saldo = consultaSaldo(cpf);
        String sqlUpdate = "UPDATE users SET saldo = ? WHERE cpf = ?";

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/users.db");
            PreparedStatement ps = connection.prepareStatement(sqlUpdate);
            ps.setString(2, cpf);
            ps.setDouble(1, saldo+quantia);
            ps.executeUpdate();
            ps.close();
            connection.close();
            System.out.println("Dep??sito realizado com sucesso.");
        } catch (SQLException e){
            System.err.println("Erro ao realizar dep??sito.");
            System.out.println(e.getMessage());
        }

    }
    protected static void menu(){
        int opcao;
        do {
            System.out.println("======= MENU =======");
            System.out.println("Op????es dispon??veis: ");
            System.out.println("1 - Cadastrar Cliente");
            System.out.println("2 - Consulta Saldo");
            System.out.println("3 - Realizar saque");
            System.out.println("4 - Realizar dep??sito");
            System.out.println("5 - Sair do programa.");
            System.out.print("Insira a op????o desejada: ");
            sc.skip("\\R?");
            opcao = sc.nextInt();

            switch (opcao){
                case 1:
                    cadastrarCliente();
                    break;
                case 2:
                    pesquisarCliente();
                    break;
                case 3:
                    System.out.print("Insira o CPF do cliente: ");
                    sc.skip("\\R?");
                    String cpf1 = sc.nextLine();
                    System.out.print("Insira a quantia a ser retirada: R$");
                    sc.skip("\\R?");
                    double quantia_retirada = sc.nextDouble();
                    realizarSaque(cpf1, quantia_retirada);
                    break;
                case 4:
                    System.out.print("Insira o CPF do cliente: ");
                    sc.skip("\\R?");
                    String cpf2 = sc.nextLine();
                    System.out.print("Insira a quantia a ser depositada: R$");
                    sc.skip("\\R?");
                    double quantia_depositada = sc.nextDouble();
                    realizarDeposito(cpf2, quantia_depositada);
                    break;
                default:
                    System.err.println("Op????o inv??lida. Tente novamente.");
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
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setDouble(3, saldo);
            ps.executeUpdate();
            System.out.println("Cliente cadastrado com sucesso!");
        } catch(SQLException e){
            System.out.println("N??o foi poss??vel cadastrar o usu??rio pelo seguinte motivo: ");
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
