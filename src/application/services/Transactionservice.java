package application.services;

import application.repositories.AccountRepository;
import application.repositories.TransactionRepository;
import domain.enums.TransactionType;
import domain.exception.AccountNotFoundException;
import domain.model.Account;
import domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * Transfers funds between accounts; persists origin transaction
     */
    public void transfer(String originAccountCode, String destinationAccountCode, BigDecimal amount) {
        Account originAccount = accountRepository.getByCode(originAccountCode).
                orElseThrow(AccountNotFoundException::new);

        Account destinationAccount = accountRepository.getByCode(destinationAccountCode)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal originPreviousBalance = originAccount.getBalance();
        BigDecimal destinationPreviousBalance = destinationAccount.getBalance();

        originAccount.transfer(destinationAccount, amount);

        Transaction sentTransaction = new Transaction(
                TransactionType.TRANSFER_SENT,
                amount,
                originPreviousBalance,
                originAccountCode,
                destinationAccountCode,
                "Transfer sent"
        );

        Transaction receivedTransaction = new Transaction(
                TransactionType.TRANSFER_RECEIVED,
                amount,
                destinationPreviousBalance,
                originAccountCode,
                destinationAccountCode,
                "Transfer received"
        );

        transactionRepository.save(sentTransaction);
        transactionRepository.save(receivedTransaction);

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);
    }


    /**
     * Gets complete transaction history for an account
     */
    public List<Transaction> getStatement(String accountCode) {
        return transactionRepository.getByAccountCode(accountCode);
    }

    /**
     * Gets transaction history for a specific period
     */
    public List<Transaction> getStatement(String accountCode, LocalDate startDateTime, LocalDate endDateTime) {
        return transactionRepository.searchForAccountAndDate(accountCode, startDateTime, endDateTime);
    }

    /**
     * Gets the most recent N transaction
     */
    public List<Transaction> getRecentStatement(String accountCode, int limit) {
        return transactionRepository.fetchLatest(accountCode, limit);
    }
}
