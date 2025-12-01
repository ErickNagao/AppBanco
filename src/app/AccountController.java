package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import model.Account;
import model.Transaction;
import service.Bank;
import util.CsvExporter;
import util.OperationResult;

public class AccountController {
    private final Bank bank;

    public AccountController(Bank bank) {
        this.bank = bank;
    }

    public void showMenu(Scanner sc, Account logged) {
        while (true) {
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
            System.out.print("Escolha uma opção: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1": deposit(sc, logged); break;
                    case "2": withdraw(sc, logged); break;
                    case "3": changeLimit(sc, logged); break;
                    case "4": transfer(sc, logged); break;
                    case "5": exportCsv(); break;
                    case "6": listAccounts(); break;
                    case "7": viewBalance(logged); break;
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

    private void deposit(Scanner sc, Account logged) {
        double v = InputUtils.readDoublePositive(sc, "Valor: ");
        OperationResult res = bank.deposit(logged.getAccountNumber(), v);
        System.out.println(res.getMessage());
    }

    private void withdraw(Scanner sc, Account logged) {
        while (true) {
            double v = InputUtils.readDoublePositive(sc, "Valor: ");
            String pwd;
            while (true) {
                pwd = InputUtils.readPassword(sc, "Senha: ");
                if (logged.checkPassword(pwd)) break;
                System.out.println("Senha inválida.");
            }
            OperationResult res = bank.withdraw(logged.getAccountNumber(), v, pwd);
            if (res.isSuccess()) { System.out.println(res.getMessage()); break; }
            if ("INSUFFICIENT_FUNDS".equals(res.getCode()) || "LIMIT_EXCEEDED".equals(res.getCode())) {
                System.out.println(res.getMessage());
                System.out.print("Deseja informar outro valor? (s/n): ");
                String yn = sc.nextLine().trim().toLowerCase();
                if (yn.equals("s") || yn.equals("y")) continue; else break;
            } else if ("INVALID_PASSWORD".equals(res.getCode())) {
                System.out.println("Senha inválida.");
                continue;
            } else { System.out.println(res.getMessage()); break; }
        }
    }

    private void changeLimit(Scanner sc, Account logged) {
        double lim = InputUtils.readDoubleNonNegative(sc, "Novo limite: ");
        String pwd;
        while (true) {
            pwd = InputUtils.readPassword(sc, "Senha: ");
            if (logged.checkPassword(pwd)) break;
            System.out.println("Senha inválida.");
        }
        OperationResult res = bank.changeLimit(logged.getAccountNumber(), lim, pwd);
        System.out.println(res.getMessage());
    }

    private void transfer(Scanner sc, Account logged) {
        int to;
        while (true) {
            to = InputUtils.readAccountNumber(sc, "Conta destino: ");
            if (to == logged.getAccountNumber()) { System.out.println("Conta destino igual à conta de origem (não permitido)."); continue; }
            if (bank.find(to) == null) { System.out.println("Conta destino não encontrada."); continue; }
            break;
        }
        while (true) {
            double v = InputUtils.readDoublePositive(sc, "Valor: ");
            String pwd;
            while (true) {
                pwd = InputUtils.readPassword(sc, "Senha: ");
                if (logged.checkPassword(pwd)) break;
                System.out.println("Senha inválida.");
            }
            OperationResult res = bank.transfer(logged.getAccountNumber(), to, v, pwd);
            if (res.isSuccess()) { System.out.println(res.getMessage()); break; }
            if ("VALIDATION_ERRORS".equals(res.getCode()) && !res.getDetails().isEmpty()) {
                System.out.println("Foram encontrados os seguintes problemas:");
                for (String d : res.getDetails()) System.out.println(" - " + d);
                System.out.print("Deseja corrigir o valor e tentar novamente? (s/n): ");
                String yn = sc.nextLine().trim().toLowerCase();
                if (yn.equals("s") || yn.equals("y")) continue; else { System.out.println(res.getMessage()); break; }
            } else { System.out.println(res.getMessage()); break; }
        }
    }

    private void exportCsv() {
        String path = "transactions.csv";
        List<Transaction> tx = bank.getTransactions();
        try {
            CsvExporter.exportTransactions(tx, path);
            System.out.println("Exportado para: " + path + " (substituído se já existia)");
        } catch (IOException e) {
            System.out.println("Falha na exportação: " + e.getMessage());
        }
    }

    private void listAccounts() {
        for (Account a : bank.listAccounts()) System.out.println(a.summary());
    }

    private void viewBalance(Account logged) {
        System.out.printf("Saldo atual da conta %d: R$ %.2f%n", logged.getAccountNumber(), logged.getBalance());
    }
}
