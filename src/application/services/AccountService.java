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
import java.util.Optional;

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

    /**
     * Creates a new checking account for a user with the given branch and user ID.
     */
    public Account createCheckingAccount(String branch, String userId) {
        User holder = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Account newAccount = new CheckingAccount(branch, holder);
        accountRepository.save(newAccount);
        return newAccount;
    }

    /**
     * Creates a new savings account for a user with the specified branch and user ID.
     * The method ensures that the user exists before creating the account.
     */
    public Account createSavingAccount(String branch, String userId) {
        User holder = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Account newAccount =  new SavingsAccount(branch, holder);
        accountRepository.save(newAccount);
        return newAccount;
    }

    /**
     * Retrieves an account based on its unique account code.
     * If the account is not found, an AccountNotFoundException is thrown.
     */
    public Account getAccount(String accountCode) {
        return accountRepository.getByCode(accountCode).orElseThrow(AccountNotFoundException::new);
    }

    /**
     * Searches for and retrieves a list of user accounts associated with the specified user ID.
     */
    public List<Account> searchForUserAccounts(String userId) {
        return accountRepository.getByHolder(userId).stream().toList();
    }

    /**
     * Deactivates an account associated with the provided account code.
     * The account must have a zero balance to be deactivated. If the account balance is
     * greater than or less than zero, an exception will be thrown.
     */
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
