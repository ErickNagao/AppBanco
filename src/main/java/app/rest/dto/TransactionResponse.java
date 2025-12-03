package app.rest.dto;

import model.Transaction;
import java.time.format.DateTimeFormatter;

public class TransactionResponse {
    public String timestamp;
    public String type;
    public double amount;
    public Integer fromAccount;
    public Integer toAccount;
    public double balanceAfter;
    public String description;

    public TransactionResponse() {}

    public TransactionResponse(Transaction t) {
        if (t == null) return;
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = t.getTimestamp() == null ? null : t.getTimestamp().format(f);
        this.type = t.getType();
        this.amount = t.getAmount();
        this.fromAccount = t.getFromAccount();
        this.toAccount = t.getToAccount();
        this.balanceAfter = t.getBalanceAfter();
        this.description = t.getDescription();
    }
}