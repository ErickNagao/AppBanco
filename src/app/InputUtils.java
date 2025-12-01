package app;

import java.util.Scanner;

public class InputUtils {
    public static String readAgency(Scanner sc) {
        while (true) {
            System.out.print("Agência (somente números): ");
            String agency = sc.nextLine().trim();
            if (agency.matches("\\d+")) return agency;
            System.out.println("Agência inválida. Informe apenas números.");
        }
    }

    public static String readClientName(Scanner sc) {
        while (true) {
            System.out.print("Cliente: ");
            String client = sc.nextLine().trim();
            if (client.matches(".*\\p{L}.*")) return client;
            System.out.println("Nome inválido. Informe um nome que contenha letras e não seja apenas números.");
        }
    }

    public static int readAccountNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.matches("\\d+")) { System.out.println("Número de conta inválido. Informe apenas dígitos."); continue; }
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { System.out.println("Número inválido."); }
        }
    }

    public static double readDoublePositive(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().replace(',', '.');
            try {
                double v = Double.parseDouble(s);
                if (v <= 0) { System.out.println("Valor inválido. Informe um número maior que zero."); continue; }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Informe um número (ex: 250.00).");
            }
        }
    }

    public static double readDoubleNonNegative(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().replace(',', '.');
            try {
                double v = Double.parseDouble(s);
                if (v < 0) { System.out.println("Valor inválido. Informe um número >= 0."); continue; }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Informe um número (ex: 500.00).");
            }
        }
    }

    public static String readPassword(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }
}
