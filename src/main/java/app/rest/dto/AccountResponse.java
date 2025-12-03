package app.rest.dto;

import model.Account;
import model.AccountType;

public class AccountResponse {
    public int accountNumber;
    public String agency;
    public String client;
    public double balance;
    public double limit;
    public AccountType type;

    public AccountResponse() {}

    public AccountResponse(Account a) {
        if (a == null) return;
        this.accountNumber = a.getAccountNumber();
        this.agency = a.getAgency();
        this.client = a.getClient();
        this.balance = a.getBalance();
        this.limit = a.getLimit();
        this.type = a.getType();
    }
}