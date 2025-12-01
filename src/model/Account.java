package model;

import java.text.DecimalFormat;

public abstract class Account {
    protected int accountNumber;
    protected String agency;
    protected String client;
    protected double balance;
    protected double limit;
    protected AccountType type;
    protected String password;

    public Account(int accountNumber, String agency, String client, double initialDeposit, double limit, AccountType type, String password) {
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.client = client;
        this.balance = initialDeposit;
        this.limit = limit;
        this.type = type;
        this.password = password;
    }

    public int getAccountNumber() { return accountNumber; }
    public String getAgency() { return agency; }
    public String getClient() { return client; }
    public double getBalance() { return balance; }
    public double getLimit() { return limit; }
    public AccountType getType() { return type; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Valor de depósito deve ser positivo");
        balance += amount;
    }

    public void setLimit(double newLimit) {
        this.limit = newLimit;
    }

    public boolean checkPassword(String candidate) {
        if (password == null) return false;
        return password.equals(candidate);
    }

    public String summary() {
        DecimalFormat df = new DecimalFormat("#.00");
        return "Conta: " + accountNumber + " | Agencia: " + agency + " | Cliente: " + client + " | Tipo: " + type + " | Saldo: R$ " + df.format(balance) + " | Limite: R$ " + df.format(limit);
    }

    public abstract boolean withdraw(double amount);
}
