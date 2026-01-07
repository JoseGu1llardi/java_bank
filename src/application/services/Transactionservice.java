package application.services;

import application.repositories.AccountRepository;
import application.repositories.TransactionRepository;
import domain.enums.TransactionType;
import domain.exception.AccountNotFoundException;
import domain.model.Account;
import domain.model.Transaction;

import java.math.BigDecimal;

public class Transactionservice {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Transactionservice(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void deposit(String accountCode, BigDecimal amount) {
        Account account = accountRepository.getByCode(accountCode).orElseThrow(AccountNotFoundException::new);

        BigDecimal previousBalance = account.getBalance();

        account.deposit(amount);

        // Register the transaction in the separate repository
        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                previousBalance,
                null,
                accountCode,
                "Deposit"
        );

        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    /**
     * Implements withdrawal; persists transactional state
     */
    public void withdraw(String accountCode, BigDecimal amount) {
        Account account = accountRepository.getByCode(accountCode).orElseThrow(AccountNotFoundException::new);

        BigDecimal previousBalance = account.getBalance();

        account.withdraw(amount);

        Transaction transaction = new Transaction(
                TransactionType.WITHDRAW,
                amount,
                previousBalance,
                accountCode,
                null,
                "Withdraw"
        );

        transactionRepository.save(transaction);
        accountRepository.save(account);
    }
}
