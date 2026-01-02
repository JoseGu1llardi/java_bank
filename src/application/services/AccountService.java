package application.services;

import application.repositories.AccountRepository;
import application.repositories.UserRepository;
import domain.entity.Account;
import domain.entity.CheckingAccount;
import domain.entity.SavingsAccount;
import domain.entity.User;
import domain.exception.AccountNotFoundException;
import domain.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.List;

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

    public Account getAccount(String agency, String number) {
        return accountRepository.getByKey(agency, number).orElseThrow(AccountNotFoundException::new);
    }

    public List<Account> searchForUserAccounts(String userId) {
        return accountRepository.getByHolder(userId).stream().toList();
    }

    public void deactivateAccount(String agency, String number) {
        Account account = accountRepository.getByKey(agency, number).orElseThrow(AccountNotFoundException::new);

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("It is not possible to deactivate account with balance.");
        } else if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new  IllegalArgumentException("It is not possible to deactivate account with negative balance, " +
                    "you must pay first.");
        }

        account.disable();
        accountRepository.save(account);
    }
}
