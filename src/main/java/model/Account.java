package model;

import java.text.DecimalFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
        if (candidate == null) return false;
        if (password == null) return false;
        try {
            if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
                org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                return enc.matches(candidate, password);
            }
        } catch (NoClassDefFoundError | Exception ex) {
        }
        return password.equals(candidate);
    }

    @JsonIgnore
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String summary() {
        DecimalFormat df = new DecimalFormat("#.00");
        return "Conta: " + accountNumber + " | Agencia: " + agency + " | Cliente: " + client + " | Tipo: " + type + " | Saldo: R$ " + df.format(balance) + " | Limite: R$ " + df.format(limit);
    }

    public abstract boolean withdraw(double amount);
}
