package app;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import persistence.PersistenceService;
import persistence.AccountEntity;
import service.Bank;
import model.CheckingAccount;
import model.AccountType;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private final PersistenceService persistenceService;
    private final Bank bank;

    public AppStartupRunner(PersistenceService persistenceService, Bank bank) {
        this.persistenceService = persistenceService;
        this.bank = bank;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (AccountEntity e : persistenceService.listAccounts()) {
            AccountType type = AccountType.valueOf(e.getType());
            CheckingAccount acc = new CheckingAccount(e.getAccountNumber(), e.getAgency(), e.getClient(), e.getBalance(), e.getLimitValue(), type, e.getPassword());
            bank.addExistingAccount(acc);
        }
    }
}
