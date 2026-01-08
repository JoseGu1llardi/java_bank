package application.repositories;

import domain.model.Account;
import domain.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {
    private Map<String, Account> accounts;

    public AccountRepository() {
        accounts = new HashMap<>();
    }

    public void save(Account account) {
        String accountCode = account.getAccountCode();
        accounts.put(accountCode, account);
    }

    public Optional<Account> getByCode(String accountCode) {
        return Optional.ofNullable(accounts.get(accountCode));
    }

    /**
     * Returns accounts filtered by holder's identifier
     */
    public Collection<Account> getByHolder(String userId) {
        return accounts.values().stream()
                .filter(account -> account.getHolder().getId().equals(userId))
                .toList();
    }

    public Collection<Account> getAll() {
        return accounts.values();
    }

    public void remove(String agency, String number) {
        String accountCode = agency + "-" + number;
        accounts.remove(accountCode);
    }
}
