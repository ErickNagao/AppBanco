package service;

import model.Account;

public class AuthService {
    private Bank bank;

    public AuthService(Bank bank) {
        this.bank = bank;
    }

    public Account register(String agency, String client, double initialDeposit, double limit, String type, String password) {
        if (client == null || !client.matches(".*\\p{L}.*")) {
            throw new IllegalArgumentException("Nome do cliente inválido. Deve conter letras e não ser apenas números.");
        }
        return bank.createAccount(agency, client, initialDeposit, limit, type, password);
    }

    public Account login(String agency, int accountNumber, String password) {
        Account acc = bank.find(accountNumber);
        if (acc == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }
        if (agency == null || !agency.equals(acc.getAgency())) {
            throw new IllegalArgumentException("Agência incorreta.");
        }
        if (!acc.checkPassword(password)) {
            throw new IllegalArgumentException("Senha incorreta.");
        }
        return acc;
    }
}
