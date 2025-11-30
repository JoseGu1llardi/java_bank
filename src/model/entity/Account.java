package model.entity;

import model.enums.TransactionType;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    protected String number;
    protected String agency;
    protected BigDecimal balance;
    protected User holder;
    protected LocalDateTime createdAt;
    protected final List<Transaction> transactions;
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

    public void deposit(BigDecimal amount,  String description) {
        validateActiveAccount();
        validateAmount(amount);

        BigDecimal previousBalance = this.balance;
        this.balance = this.balance.add(amount);

        registerTransaction(
                TransactionType.DEPOSIT,
                amount,
                previousBalance,
                null,
                this,
                description == null ? "Deposit performed." : description
        );
    }

    public abstract void withdraw(BigDecimal amount, String description);

    public abstract BigDecimal calculateMonthlyFee();

    public void transfer(BigDecimal amount, Account destinationAccount) {
        if (destinationAccount == null || !destinationAccount.isActive) {
            throw new IllegalArgumentException("Invalid destination account.");
        }

        validateActiveAccount();
        validateAmount(amount);
        validateBalance(amount);

        BigDecimal previousBalanceOrigin = this.balance;
        BigDecimal previousBalanceDestination = destinationAccount.balance;

        this.balance = this.balance.subtract(amount);
        destinationAccount.balance = destinationAccount.balance.add(amount);

        this.registerTransaction(
                TransactionType.TRANSFER_SENT,
                amount,
                previousBalanceOrigin,
                this,
                destinationAccount,
                "Transfer made to " + destinationAccount.number
        );

        destinationAccount.registerTransaction(
                TransactionType.TRANSFER_RECEIVED,
                amount,
                previousBalanceDestination,
                this,
                destinationAccount,
                "Transfer received from " + this.number
        );
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount must be greater than zero.");
        }
    }

    protected void validateActiveAccount() {
        if (!isActive()) {
            throw new IllegalStateException("The account is not active.");
        }
    }

    protected void validateBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0)  {
            throw new IllegalArgumentException(
                    "Insufficient funds. The balance must be greater than or equal to the requested amount."
            );
        }
    }

    public void registerTransaction(TransactionType type,
                                    BigDecimal amount,
                                    BigDecimal previousBalance,
                                    Account origin,
                                    Account destination,
                                    String description ) {

        if ((type == TransactionType.TRANSFER_SENT || type == TransactionType.TRANSFER_RECEIVED)
        && (origin == null || destination == null)) {
            throw new IllegalArgumentException("Transfers must contain both origin and destination accounts.");
        }

        Transaction transaction = new Transaction(type, amount, previousBalance, origin, destination, description);
        transactions.add(transaction);
    }

    protected String generateAccountNumber() {
        return String.format("%08d", random.nextInt(100_000_000));
    }

    public String getNumber() {
        return number;
    }

    public String getAgency() {
        return agency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public User getHolder() {
        return holder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void disable() {
        this.isActive = false;
    }
    public void enable() {
        this.isActive = true;
    }
}
