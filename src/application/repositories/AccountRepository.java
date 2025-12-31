package application.repositories;

import domain.entity.Account;

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
        String key = account.getAgency() + "-" + account.getNumber();
        accounts.put(key, account);
    }

    public Optional<Account> getByKey(String agency, String number) {
        String key = agency + "-" + number;
        return Optional.ofNullable(accounts.get(key));
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
        String key = agency + "-" + number;
        accounts.remove(key);
    }
}
