package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDateTime timestamp;
    private String type;
    private double amount;
    private Integer fromAccount;
    private Integer toAccount;
    private double balanceAfter;
    private String description;

    public Transaction(LocalDateTime timestamp, String type, double amount, Integer fromAccount, Integer toAccount, double balanceAfter, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Integer getFromAccount() { return fromAccount; }
    public Integer getToAccount() { return toAccount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }

    public String toCsvLine() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ts = timestamp.format(f);
        return String.format(java.util.Locale.US, "%s,%s,%.2f,%s,%s,%.2f,%s", ts, type, amount,
                fromAccount == null ? "" : fromAccount.toString(),
                toAccount == null ? "" : toAccount.toString(),
                balanceAfter, description == null ? "" : description.replace(',', ' '));
    }
}
