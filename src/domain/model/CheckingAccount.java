package domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CheckingAccount extends Account {
    private BigDecimal overdraftLimit;

    private static final BigDecimal MONTHLY_FEE =
            BigDecimal.valueOf(12.0).setScale(2, RoundingMode.HALF_EVEN);

    private static final BigDecimal MINIMUM_BALANCE_FOR_WAIVER = BigDecimal.valueOf(1_000)
                                                        .setScale(2, RoundingMode.HALF_EVEN);

    public CheckingAccount(String branch, User holder) {
        super(branch, holder);
        this.overdraftLimit = BigDecimal.valueOf(500.0).setScale(2, RoundingMode.HALF_EVEN);
    }

    public CheckingAccount(String branch, User holder, BigDecimal overdraftLimit) {
        super(branch, holder);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Withdraws amount if funds are enough
     */
    @Override
    public void withdraw(BigDecimal amount) {
        super.validateAmount(amount);
        super.validateActiveAccount();

        BigDecimal totalAvailable = this.balance.add(overdraftLimit);

        if (amount.compareTo(totalAvailable) > 0) {
            throw new IllegalStateException(
                    "Insufficient funds. Available balance: " + totalAvailable
            );
        }

        this.balance = balance.subtract(amount);
    }

    /**
     * Applies monthly fee, if any, to account balance
     */
    public Optional<BigDecimal> applyFee() {
        BigDecimal feeAmount = this.calculateMonthlyFee();

        if (feeAmount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        this.balance = balance.subtract(feeAmount);
        return Optional.of(feeAmount);
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
