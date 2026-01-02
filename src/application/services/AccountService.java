package application.services;

import application.repositories.AccountRepository;
import application.repositories.UserRepository;
import domain.entity.Account;
import domain.entity.CheckingAccount;
import domain.entity.SavingsAccount;
import domain.entity.User;
import domain.exception.UserNotFoundException;

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
}
