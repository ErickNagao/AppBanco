package model;

public class CheckingAccount extends Account {
    public CheckingAccount(int accountNumber, String agency, String client, double initialDeposit, double limit, model.AccountType type, String password) {
        super(accountNumber, agency, client, initialDeposit, limit, type, password);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount > balance) return false;
        if (limit >= 0 && amount > limit) return false;
        balance -= amount;
        return true;
    }
}
