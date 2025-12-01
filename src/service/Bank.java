package service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Account;
import model.CheckingAccount;
import model.Transaction;
import util.OperationResult;

public class Bank {
    private Map<Integer, Account> accounts = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextAccountNumber = 1001;

    public synchronized Account createAccount(String agency, String client, double initialDeposit, double limit, model.AccountType type, String password) {
        int accNum = nextAccountNumber++;
        Account acc = new CheckingAccount(accNum, agency, client, initialDeposit, limit, type, password);
        accounts.put(accNum, acc);
        transactions.add(new Transaction(LocalDateTime.now(), "CREATE", initialDeposit, null, accNum, acc.getBalance(), "Criação de conta"));
        return acc;
    }

    public Account find(int accountNumber) {
        return accounts.get(accountNumber);
    }

    public OperationResult deposit(int accountNumber, double amount) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) return OperationResult.fail("ACCOUNT_NOT_FOUND", "Conta não encontrada.");
        if (amount <= 0) return OperationResult.fail("INVALID_AMOUNT", "Valor de depósito deve ser maior que zero.");
        acc.deposit(amount);
        transactions.add(new Transaction(LocalDateTime.now(), "DEPOSIT", amount, null, accountNumber, acc.getBalance(), "Depósito"));
        return OperationResult.ok(String.format("Depósito realizado. Saldo atual: %.2f", acc.getBalance()));
    }

    public OperationResult withdraw(int accountNumber, double amount, String password) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) return OperationResult.fail("ACCOUNT_NOT_FOUND", "Conta não encontrada.");
        if (!acc.checkPassword(password)) return OperationResult.fail("INVALID_PASSWORD", "Senha inválida.");
        if (amount <= 0) return OperationResult.fail("INVALID_AMOUNT", "Valor de saque deve ser maior que zero.");
        if (acc.getLimit() >= 0 && amount > acc.getLimit()) return OperationResult.fail("LIMIT_EXCEEDED", "Valor acima do limite por operação.");
        if (amount > acc.getBalance()) return OperationResult.fail("INSUFFICIENT_FUNDS", "Saldo insuficiente.");
        boolean ok = acc.withdraw(amount);
        if (ok) {
            transactions.add(new Transaction(LocalDateTime.now(), "WITHDRAW", amount, accountNumber, null, acc.getBalance(), "Saque"));
            return OperationResult.ok(String.format("Saque realizado. Saldo atual: %.2f", acc.getBalance()));
        }
        return OperationResult.fail("UNKNOWN", "Falha ao realizar saque.");
    }

    public OperationResult changeLimit(int accountNumber, double newLimit, String password) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) return OperationResult.fail("ACCOUNT_NOT_FOUND", "Conta não encontrada.");
        if (!acc.checkPassword(password)) return OperationResult.fail("INVALID_PASSWORD", "Senha inválida.");
        if (newLimit < 0) return OperationResult.fail("INVALID_LIMIT", "Limite não pode ser negativo.");
        acc.setLimit(newLimit);
        transactions.add(new Transaction(LocalDateTime.now(), "LIMIT_CHANGE", 0.0, accountNumber, null, acc.getBalance(), "Alteração de limite"));
        return OperationResult.ok(String.format("Limite atualizado para: %.2f", newLimit));
    }

    public OperationResult transfer(int fromAccount, int toAccount, double amount, String password) {
        if (fromAccount == toAccount) return OperationResult.fail("SAME_ACCOUNT", "Conta de destino igual à conta de origem (não permitido).");
        Account aFrom = accounts.get(fromAccount);
        Account aTo = accounts.get(toAccount);
        if (aFrom == null) return OperationResult.fail("FROM_NOT_FOUND", "Conta de origem não encontrada.");
        if (aTo == null) return OperationResult.fail("TO_NOT_FOUND", "Conta destino não encontrada.");
        if (!aFrom.checkPassword(password)) return OperationResult.fail("INVALID_PASSWORD", "Senha inválida.");

        List<String> details = new ArrayList<>();
        if (amount <= 0) details.add("Valor de transferência deve ser maior que zero.");
        LocalTime now = LocalTime.now();
        if (now.getHour() >= 0 && now.getHour() < 6) {
            double cap = 1000.0;
            if (amount > cap) details.add("Transferências acima de 1000.00 não são permitidas entre 00:00 e 06:00.");
        }
        if (aFrom.getLimit() >= 0 && amount > aFrom.getLimit()) details.add("Valor acima do limite por operação.");
        if (amount > aFrom.getBalance()) details.add("Saldo insuficiente.");

        if (!details.isEmpty()) {
            return OperationResult.fail("VALIDATION_ERRORS", "Erros de validação na transferência.", details);
        }

        if (!aFrom.withdraw(amount)) return OperationResult.fail("WITHDRAW_FAILED", "Falha ao debitar da conta de origem.");
        aTo.deposit(amount);
        transactions.add(new Transaction(LocalDateTime.now(), "TRANSFER", amount, fromAccount, toAccount, aFrom.getBalance(), "Transferência"));
        transactions.add(new Transaction(LocalDateTime.now(), "TRANSFER_IN", amount, fromAccount, toAccount, aTo.getBalance(), "Transferência recebida"));
        return OperationResult.ok(String.format("Transferência realizada. Saldo atual: %.2f", aFrom.getBalance()));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(accounts.values());
    }
}
