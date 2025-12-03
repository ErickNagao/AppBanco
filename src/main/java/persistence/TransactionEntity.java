package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime timestamp;
    private String type;
    private double amount;
    private Integer fromAccount;
    private Integer toAccount;
    private double balanceAfter;
    private String description;

    public TransactionEntity() {}

    public TransactionEntity(LocalDateTime timestamp, String type, double amount, Integer fromAccount, Integer toAccount, double balanceAfter, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Integer getFromAccount() { return fromAccount; }
    public void setFromAccount(Integer fromAccount) { this.fromAccount = fromAccount; }
    public Integer getToAccount() { return toAccount; }
    public void setToAccount(Integer toAccount) { this.toAccount = toAccount; }
    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
