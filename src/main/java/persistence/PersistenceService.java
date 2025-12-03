package persistence;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import model.Account;
import model.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersistenceService {
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PersistenceService(AccountRepository accountRepo, TransactionRepository txRepo) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
    }

    @Transactional
    public void saveOrUpdateAccount(Account acc) {
        if (acc == null) return;
        String raw = acc.getPassword();
        String hashed = null;
        if (raw != null) {
            if (raw.startsWith("$2a$") || raw.startsWith("$2b$") || raw.startsWith("$2y$")) hashed = raw;
            else hashed = passwordEncoder.encode(raw);
        }
        persistence.AccountEntity e = new persistence.AccountEntity(
                acc.getAccountNumber(), acc.getAgency(), acc.getClient(), acc.getBalance(), acc.getLimit(), acc.getType().name(), hashed
        );
        accountRepo.save(e);
        if (hashed != null) {
            acc.setPassword(hashed);
        }
    }

    @Transactional
    public void saveTransaction(Transaction tx) {
        if (tx == null) return;
        TransactionEntity e = new TransactionEntity(tx.getTimestamp(), tx.getType(), tx.getAmount(), tx.getFromAccount(), tx.getToAccount(), tx.getBalanceAfter(), tx.getDescription());
        txRepo.save(e);
    }

    public List<TransactionEntity> listTransactions() {
        return txRepo.findAll().stream().collect(Collectors.toList());
    }

    public List<AccountEntity> listAccounts() {
        return accountRepo.findAll();
    }

    public Account loadAccountDomain(Integer accountNumber) {
        return accountRepo.findById(accountNumber).map(e -> {
            model.AccountType t;
            try { t = model.AccountType.valueOf(e.getType()); } catch (Exception ex) { t = model.AccountType.CORRENTE; }
            return new model.CheckingAccount(e.getAccountNumber(), e.getAgency(), e.getClient(), e.getBalance(), e.getLimitValue(), t, e.getPassword());
        }).orElse(null);
    }

    @Transactional
    public void deleteAccount(Integer accountNumber) {
        if (accountNumber == null) return;
        accountRepo.deleteById(accountNumber);
    }
}
