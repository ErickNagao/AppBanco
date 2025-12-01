package app;

import java.util.Scanner;

import model.Account;
import service.AuthService;
import service.Bank;

public class App {
    public static void main(String[] args) {
        Bank bank = new Bank();
        AuthService auth = new AuthService(bank);
        AuthController authCtrl = new AuthController(bank, auth);
        AccountController accCtrl = new AccountController(bank);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("=== Bem-vindo ao Banco ===");
            System.out.println("1 - Cadastrar nova conta");
            System.out.println("2 - Login (usar nº da conta)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    try { authCtrl.register(sc); } catch (Exception e) { System.out.println("Falha no cadastro: " + e.getMessage()); }
                    break;
                case "2":
                    try {
                        Account acc = authCtrl.login(sc);
                        accCtrl.showMenu(sc, acc);
                    } catch (Exception e) {
                        System.out.println("Erro no login: " + e.getMessage());
                    }
                    break;
                case "0": System.out.println("Saindo..."); sc.close(); return;
                default: System.out.println("Opção inválida");
            }
            System.out.println();
        }
    }
}
