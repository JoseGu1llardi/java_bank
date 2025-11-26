package model.entity;

import model.enums.TransactionType;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String number;
    protected String agency;
    protected BigDecimal balance;
    protected User holder;
    protected LocalDateTime createdAt;
    protected List<Transaction> transactions;
    protected boolean isActive;

    protected static final SecureRandom random = new SecureRandom();

    public Account(String agency, User holder) {
        this.number = generateAccountNumber();
        this.agency = agency;
        this.holder = holder;
        this.balance  = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
        this.isActive = true;
    }

    public void deposit(BigDecimal amount, String description) {
        validateAmount(amount);
        validateActiveAccount();

        BigDecimal previousBalance = this.balance;
        this.balance = this.balance.add(amount);

        registerTransaction(TransactionType.DEPOSIT, amount, previousBalance,
                null, this, description);
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The amount must be greater than zero.");
        }
    }

    protected void validateActiveAccount() {
        if (!isActive) {
            throw new IllegalStateException("The account is not active.");
        }
    }

    public void registerTransaction(TransactionType type,
                                    BigDecimal amount,
                                    BigDecimal previousBalance,
                                    Account origin,
                                    Account destination,
                                    String description) {

        Transaction transaction = new Transaction(type, amount, previousBalance, origin, destination, description);
        transactions.add(transaction);
    }

    protected String generateAccountNumber() {
        return String.format("%08d", random.nextInt(100_000_000));
    }
}
