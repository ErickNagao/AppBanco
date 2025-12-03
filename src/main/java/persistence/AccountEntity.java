package persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    private Integer accountNumber;
    private String agency;
    private String client;
    private double balance;
    private double limitValue;
    private String type;
    private String password;

    public AccountEntity() {}

    public AccountEntity(Integer accountNumber, String agency, String client, double balance, double limitValue, String type, String password) {
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.client = client;
        this.balance = balance;
        this.limitValue = limitValue;
        this.type = type;
        this.password = password;
    }

    public Integer getAccountNumber() { return accountNumber; }
    public void setAccountNumber(Integer accountNumber) { this.accountNumber = accountNumber; }
    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }
    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getLimitValue() { return limitValue; }
    public void setLimitValue(double limitValue) { this.limitValue = limitValue; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
