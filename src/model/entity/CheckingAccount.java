package model.entity;

import model.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckingAccount extends Account {
    private BigDecimal overdraftLimit;

    private static final BigDecimal MONTHLY_FEE =
            BigDecimal.valueOf(12.0).setScale(2, RoundingMode.HALF_EVEN);

    private static final BigDecimal MINIMUM_BALANCE_FOR_WAIVER = BigDecimal.valueOf(1_000)
                                                        .setScale(2, RoundingMode.HALF_EVEN);

    public CheckingAccount(String agency, User holder) {
        super(agency, holder);
        this.overdraftLimit = BigDecimal.valueOf(500.0).setScale(2, RoundingMode.HALF_EVEN);
    }

    public CheckingAccount(String agency, User holder, BigDecimal overdraftLimit) {
        super(agency, holder);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(BigDecimal amount, String description) {
        super.validateAmount(amount);
        super.validateActiveAccount();

        BigDecimal previousBalance = this.balance;
        BigDecimal totalAvailable = this.balance.add(overdraftLimit);

        if (amount.compareTo(totalAvailable) > 0) {
            throw new IllegalStateException(
                    "Insufficient funds. Available balance: " + totalAvailable
            );
        }

        this.balance = balance.subtract(amount);

        registerTransaction(
                TransactionType.WITHDRAW,
                amount,
                previousBalance,
                this,
                null,
                description == null ? "Withdraw performed" : description
        );
    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        if (this.balance.compareTo(MINIMUM_BALANCE_FOR_WAIVER) < 0) {
            return CheckingAccount.MONTHLY_FEE;
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        if (this.overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Overdraft limit must be greater than zero.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getTotalAvailableBalance() {
        return this.balance.add(this.overdraftLimit);
    }
}
