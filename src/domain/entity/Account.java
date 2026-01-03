package domain.entity;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

public abstract class Account {
    protected String number;
    protected String branchCode;
    protected BigDecimal balance;
    protected User holder;
    protected LocalDateTime createdAt;
    protected boolean isActive;
    protected String accountCode;

    protected static final SecureRandom random = new SecureRandom();

    public Account(String branchCode, User holder) {
        this.number = generateAccountNumber();
        this.branchCode = branchCode;
        this.holder = holder;
        this.balance  = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.accountCode = generateAccountCode();
    }

    public void deposit(BigDecimal amount,  String description) {
        validateActiveAccount();
        validateAmount(amount);

        this.balance = this.balance.add(amount);
    }

    public abstract void withdraw(BigDecimal amount, String description);

    public abstract BigDecimal calculateMonthlyFee();

    /**
     * Transfers amount to destination if validations pass
     */
    public void transfer(BigDecimal amount, Account destinationAccount) {
        if (destinationAccount == null || !destinationAccount.isActive) {
            throw new IllegalArgumentException("Invalid destination account.");
        }

        validateActiveAccount();
        validateAmount(amount);
        validateBalance(amount);

        this.balance = this.balance.subtract(amount);
        destinationAccount.balance = destinationAccount.balance.add(amount);
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

    protected String generateAccountNumber() {
        return String.format("%08d", random.nextInt(100_000_000));
    }

    protected String generateAccountCode() {
        return getBranchCode() + "-" + getNumber();
    }

    public String getNumber() {
        return number;
    }

    public String getBranchCode() {
        return branchCode;
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

    public void disable() {
        this.isActive = false;
    }

    public void enable() {
        this.isActive = true;
    }
}
