package model;

public enum AccountType {
    CORRENTE("Corrente"),
    POUPANCA("Poupança"),
    SALARIO("Salário");

    private final String label;

    AccountType(String label) { this.label = label; }

    @Override
    public String toString() { return label; }

    public static AccountType fromCode(String code) {
        if (code == null) return CORRENTE;
        String c = code.trim();
        if (c.equals("1") || c.equalsIgnoreCase("corrente")) return CORRENTE;
        if (c.equals("2") || c.equalsIgnoreCase("poupança") || c.equalsIgnoreCase("poupanca")) return POUPANCA;
        if (c.equals("3") || c.equalsIgnoreCase("salário") || c.equalsIgnoreCase("salario")) return SALARIO;
        return CORRENTE;
    }
}
