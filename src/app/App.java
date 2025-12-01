package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import model.Account;
import model.Transaction;
import service.AuthService;
import service.Bank;
import util.CsvExporter;
import util.OperationResult;

public class App {
    private static Bank bank = new Bank();
    private static AuthService auth = new AuthService(bank);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            showInitialMenu();
            System.out.print("Escolha uma opção: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": doRegister(sc); break;
                case "2": doLogin(sc); break;
                case "0": System.out.println("Saindo..."); sc.close(); return;
                default: System.out.println("Opção inválida");
            }
            System.out.println();
        }
    }

    private static void showInitialMenu() {
        System.out.println("=== Bem-vindo ao Banco ===");
        System.out.println("1 - Cadastrar nova conta");
        System.out.println("2 - Login (usar nº da conta)");
        System.out.println("0 - Sair");
    }

    private static void doRegister(Scanner sc) {
        try {
            String agency;
            while (true) {
                System.out.print("Agência (somente números): ");
                agency = sc.nextLine().trim();
                if (agency.matches("\\d+")) break;
                System.out.println("Agência inválida. Informe apenas números.");
            }
            String client;
            while (true) {
                System.out.print("Cliente: ");
                client = sc.nextLine().trim();
                if (client.matches(".*\\p{L}.*")) break;
                System.out.println("Nome inválido. Informe um nome que contenha letras e não seja apenas números.");
            }
            double init;
            while (true) {
                System.out.print("Depósito inicial: ");
                String s = sc.nextLine().trim().replace(',', '.');
                try {
                    init = Double.parseDouble(s);
                    if (init < 0) {
                        System.out.println("Valor inválido. O depósito inicial deve ser >= 0.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Informe um número (ex: 1000.50).") ;
                }
            }
            System.out.println("Tipos disponíveis: 1) Corrente  2) Poupança  3) Salário");
            String type = "Corrente";
            while (true) {
                System.out.print("Escolha o tipo (1-3): ");
                String t = sc.nextLine().trim();
                if (t.equals("1")) { type = "Corrente"; break; }
                if (t.equals("2")) { type = "Poupança"; break; }
                if (t.equals("3")) { type = "Salário"; break; }
                System.out.println("Opção inválida. Escolha 1, 2 ou 3.");
            }
            double limit;
            while (true) {
                System.out.print("Limite: ");
                String s = sc.nextLine().trim().replace(',', '.');
                try {
                    limit = Double.parseDouble(s);
                    if (limit < 0) {
                        System.out.println("Valor inválido. O limite não pode ser negativo.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Informe um número (ex: 500.00).");
                }
            }
            System.out.print("Senha (simples): ");
            String password = sc.nextLine();
            Account acc = auth.register(agency, client, init, limit, type, password);
            System.out.println("Conta criada com sucesso! Nº: " + acc.getAccountNumber());
            System.out.println("Retornando ao menu inicial...");
        } catch (Exception e) {
            System.out.println("Falha no cadastro: " + e.getMessage());
        }
    }

    private static void doLogin(Scanner sc) {
        try {
            String agency;
            while (true) {
                System.out.print("Agência (somente números): ");
                agency = sc.nextLine().trim();
                if (agency.matches("\\d+")) break;
                System.out.println("Agência inválida. Informe apenas números.");
            }
            int accNum;
            while (true) {
                System.out.print("Digite o número da conta: ");
                String s = sc.nextLine().trim();
                try {
                    accNum = Integer.parseInt(s);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Número de conta inválido. Informe apenas dígitos.");
                }
            }
            System.out.print("Senha: ");
            String pwd = sc.nextLine();
            Account acc = auth.login(agency, accNum, pwd);
            System.out.println("\nLogin bem-sucedido. Bem-vindo, " + acc.getClient());
            bankMenu(sc, acc);
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getMessage());
        }
    }

    private static void bankMenu(Scanner sc, Account logged) {
        while (true) {
            showBankMenu(logged);
            System.out.print("Escolha uma opção: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1": depositLogged(sc, logged); break;
                    case "2": withdrawLogged(sc, logged); break;
                    case "3": changeLimitLogged(sc, logged); break;
                    case "4": transferLogged(sc, logged); break;
                    case "5": exportCsv(sc); break;
                    case "6": listAccounts(); break;
                    case "7": viewBalanceLogged(logged); break;
                    case "9": System.out.println("Logout..."); return;
                    case "0": System.out.println("Saindo..."); System.exit(0); break;
                    default: System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void showBankMenu(Account logged) {
        System.out.println("=== Menu da Conta (logado: " + logged.getAccountNumber() + ") ===");
        System.out.println("1 - Depósito");
        System.out.println("2 - Saque");
        System.out.println("3 - Alterar limite");
        System.out.println("4 - Transferência");
        System.out.println("5 - Exportar histórico (CSV)");
        System.out.println("6 - Listar contas");
        System.out.println("7 - Ver saldo atual");
        System.out.println("9 - Logout");
        System.out.println("0 - Sair do aplicativo");
    }

    private static void depositLogged(Scanner sc, Account logged) {
        while (true) {
            System.out.print("Valor: ");
            String s = sc.nextLine().trim().replace(',', '.');
            double v;
            try {
                v = Double.parseDouble(s);
                if (v <= 0) { System.out.println("Valor inválido. Informe um número maior que zero."); continue; }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Informe um número (ex: 1000.50)." );
                continue;
            }
            OperationResult res = bank.deposit(logged.getAccountNumber(), v);
            System.out.println(res.getMessage());
            break;
        }
    }

    private static void withdrawLogged(Scanner sc, Account logged) {
        while (true) {
            double v;
            while (true) {
                System.out.print("Valor: ");
                String s = sc.nextLine().trim().replace(',', '.');
                try {
                    v = Double.parseDouble(s);
                    if (v <= 0) { System.out.println("Valor inválido. Informe um número maior que zero."); continue; }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Informe um número (ex: 250.00)." );
                }
            }
            String pwd;
            while (true) {
                System.out.print("Senha: ");
                pwd = sc.nextLine();
                if (logged.checkPassword(pwd)) break;
                System.out.println("Senha inválida.");
            }
            OperationResult res = bank.withdraw(logged.getAccountNumber(), v, pwd);
            if (res.isSuccess()) {
                System.out.println(res.getMessage());
                break;
            }
            if ("INSUFFICIENT_FUNDS".equals(res.getCode()) || "LIMIT_EXCEEDED".equals(res.getCode())) {
                System.out.println(res.getMessage());
                System.out.print("Deseja informar outro valor? (s/n): ");
                String yn = sc.nextLine().trim().toLowerCase();
                if (yn.equals("s") || yn.equals("y")) continue;
                else break;
            } else if ("INVALID_PASSWORD".equals(res.getCode())) {
                System.out.println("Senha inválida.");
                continue;
            } else {
                System.out.println(res.getMessage());
                break;
            }
        }
    }

    private static void changeLimitLogged(Scanner sc, Account logged) {
        while (true) {
            System.out.print("Novo limite: ");
            String s = sc.nextLine().trim().replace(',', '.');
            double lim;
            try {
                lim = Double.parseDouble(s);
                if (lim < 0) { System.out.println("Valor inválido. O limite não pode ser negativo."); continue; }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Informe um número (ex: 500.00)." );
                continue;
            }
            String pwd;
            while (true) {
                System.out.print("Senha: ");
                pwd = sc.nextLine();
                if (logged.checkPassword(pwd)) break;
                System.out.println("Senha inválida.");
            }
            OperationResult res = bank.changeLimit(logged.getAccountNumber(), lim, pwd);
            System.out.println(res.getMessage());
            break;
        }
    }

    private static void transferLogged(Scanner sc, Account logged) {
        int to;
        while (true) {
            System.out.print("Conta destino: ");
            String s = sc.nextLine().trim();
            if (!s.matches("\\d+")) {
                System.out.println("Número de conta inválido. Informe apenas dígitos.");
                continue;
            }
            to = Integer.parseInt(s);
            Account dest = bank.find(to);
            if (dest == null) {
                System.out.println("Conta destino não encontrada.");
                continue;
            }
            if (to == logged.getAccountNumber()) {
                System.out.println("Conta destino igual à conta de origem (não permitido).");
                continue;
            }
            break;
        }
        while (true) {
            double v;
            while (true) {
                System.out.print("Valor: ");
                String s = sc.nextLine().trim().replace(',', '.');
                try {
                    v = Double.parseDouble(s);
                    if (v <= 0) { System.out.println("Valor inválido. Informe um número maior que zero."); continue; }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Informe um número (ex: 250.00).");
                }
            }
            String pwd;
            while (true) {
                System.out.print("Senha: ");
                pwd = sc.nextLine();
                if (logged.checkPassword(pwd)) break;
                System.out.println("Senha inválida.");
            }

            OperationResult res = bank.transfer(logged.getAccountNumber(), to, v, pwd);
            if (res.isSuccess()) {
                System.out.println(res.getMessage());
                break;
            }
            if ("VALIDATION_ERRORS".equals(res.getCode()) && !res.getDetails().isEmpty()) {
                System.out.println("Foram encontrados os seguintes problemas:");
                for (String d : res.getDetails()) System.out.println(" - " + d);
                System.out.print("Deseja corrigir o valor e tentar novamente? (s/n): ");
                String yn = sc.nextLine().trim().toLowerCase();
                if (yn.equals("s") || yn.equals("y")) {
                    continue;
                } else {
                    System.out.println(res.getMessage());
                    break;
                }
            } else {
                System.out.println(res.getMessage());
                break;
            }
        }
    }

    private static void exportCsv(Scanner sc) {
        String path = "transactions.csv";
        List<Transaction> tx = bank.getTransactions();
        try {
            CsvExporter.exportTransactions(tx, path);
            System.out.println("Exportado para: " + path + " (substituído se já existia)");
        } catch (IOException e) {
            System.out.println("Falha na exportação: " + e.getMessage());
        }
    }

    private static void listAccounts() {
        for (Account a : bank.listAccounts()) {
            System.out.println(a.summary());
        }
    }

    private static void viewBalanceLogged(Account logged) {
        System.out.printf("Saldo atual da conta %d: R$ %.2f%n", logged.getAccountNumber(), logged.getBalance());
    }
}
