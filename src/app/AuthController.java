package app;

import java.util.Scanner;

import model.Account;
import service.AuthService;

public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    public Account register(Scanner sc) {
        String agency = InputUtils.readAgency(sc);
        String client = InputUtils.readClientName(sc);
        double init = InputUtils.readDoubleNonNegative(sc, "Depósito inicial: ");
        System.out.println("Tipos disponíveis: 1) Corrente  2) Poupança  3) Salário");
        model.AccountType type = null;
        while (true) {
            System.out.print("Escolha o tipo (1-3): ");
            String t = sc.nextLine().trim();
            type = model.AccountType.fromCode(t);
            if (type != null) break;
            System.out.println("Opção inválida. Escolha 1, 2 ou 3.");
        }
        double limit = InputUtils.readDoubleNonNegative(sc, "Limite: ");
        System.out.print("Senha (simples): ");
        String password = sc.nextLine();
        Account acc = auth.register(agency, client, init, limit, type, password);
        System.out.println("Conta criada com sucesso! Nº: " + acc.getAccountNumber());
        return acc;
    }

    public Account login(Scanner sc) {
        String agency = InputUtils.readAgency(sc);
        int accNum = InputUtils.readAccountNumber(sc, "Digite o número da conta: ");
        System.out.print("Senha: ");
        String pwd = sc.nextLine();
        Account acc = auth.login(agency, accNum, pwd);
        System.out.println("\nLogin bem-sucedido. Bem-vindo, " + acc.getClient());
        return acc;
    }
}
