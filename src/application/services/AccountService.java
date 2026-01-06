package application.services;

import application.repositories.AccountRepository;
import application.repositories.UserRepository;
import domain.model.Account;
import domain.model.CheckingAccount;
import domain.model.SavingsAccount;
import domain.model.User;
import domain.exception.AccountNotFoundException;
import domain.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class responsible for handling operations related to financial accounts.
 * It provides methods for creating, retrieving, deactivating, and searching accounts
 * associated with users.
 */
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createCheckingAccount(String agency, String userId) {
        User holder = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Account newAccount = new CheckingAccount(agency, holder);
        accountRepository.save(newAccount);
        return newAccount;
    }

    public Account createSavingAccount(String agency, String userId) {
        User holder = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Account newAccount =  new SavingsAccount(agency, holder);
        accountRepository.save(newAccount);
        return newAccount;
    }

    public Account getAccount(String accountCode) {
        return accountRepository.getByCode(accountCode).orElseThrow(AccountNotFoundException::new);
    }

    public List<Account> searchForUserAccounts(String userId) {
        return accountRepository.getByHolder(userId).stream().toList();
    }

    public void deactivateAccount(String accountCode) {
        Account account = accountRepository.getByCode(accountCode).orElseThrow(AccountNotFoundException::new);

        // Throws if an account has nonzero balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("It is not possible to deactivate account with balance.");
        } else if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new  IllegalArgumentException("Account cannot be deactivated with a negative balance.");
        }

        account.disable();
        accountRepository.save(account);
    }
}
