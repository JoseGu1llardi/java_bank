package domain.entity;

import domain.enums.TransactionType;
import domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SavingsAccount extends Account {
    private static final BigDecimal RATE_RETURN_STANDARD = BigDecimal.valueOf(0.005);

    private BigDecimal rateReturn;
    private final LocalDate anniversaryDate;
    private LocalDate lastIncome;

    public SavingsAccount(String agency, User holder) {
        super(agency, holder);
        this.anniversaryDate = LocalDate.now();
    }

    public SavingsAccount(String agency, User holder, BigDecimal rateReturn) {
        super(agency, holder);
        validateRateReturn(rateReturn);
        this.rateReturn = rateReturn;
        this.anniversaryDate = LocalDate.now();
        this.lastIncome = LocalDate.now();
    }

    private void validateRateReturn(BigDecimal fee) {
        // Validates rate return is within allowed bounds
        if (fee.compareTo(BigDecimal.ZERO) < 0 ||
                fee.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            throw new IllegalArgumentException("Rate return must be between 0% and 10%");
        }
    }

    /**
     * Withdraws amount if an account active and has sufficient funds
     */
    @Override
    public void withdraw(BigDecimal amount, String description) {
        validateAmount(amount);
        validateActiveAccount();

        BigDecimal previousBalance = getBalance();

        if (amount.compareTo(this.balance) > 0) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds. Available %.2f", this.balance)
            );
        }

        this.balance = balance.subtract(amount);
    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        return BigDecimal.ZERO;
    }

    /**
     * Verifies if the current date is not before the account anniversary date
     * and ensures the income was not already applied in the anniversary date.
     */
    private boolean canApplyYield() {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextAnniversaryDate = calculateNextAnniversary();

        return !currentDate.isBefore(nextAnniversaryDate) &&
                !lastIncome.isAfter(nextAnniversaryDate.minusDays(1));
    }

    /**
     * Calculates the next account anniversary date
     * @return LocalDate
     */
    private LocalDate calculateNextAnniversary() {
        LocalDate now = LocalDate.now();
        int anniversaryDay = anniversaryDate.getDayOfMonth();

        LocalDate candidate = LocalDate.of(
                now.getYear(),
                now.getMonth(),
                // ensures that the day chosen for the next birthday is never
                // greater than the number of days in the current month.
                Math.min(anniversaryDay, now.getMonth().length(now.isLeapYear()))
        );

        // if the anniversary of this month has passed, take the next month
        if (candidate.isBefore(now)) {
            candidate = candidate.plusMonths(1);
        }

        return candidate;
    }

    /**
     * Calculates the yield value
     */
    private BigDecimal calculateYield() {
        return this.balance.multiply(RATE_RETURN_STANDARD);
    }


    /**
     * Calculate how many days are left for the next yield
     */
    private long daysForTheNextYield() {
        return ChronoUnit.DAYS.between(LocalDate.now(), calculateNextAnniversary());
    }

    public BigDecimal getRateReturn() {
        return rateReturn;
    }

    public LocalDate getAnniversaryDate() {
        return anniversaryDate;
    }

    public LocalDate getLastIncome() {
        return lastIncome;
    }

    /**
     * Return the estimated value for the next yield
     */
    public BigDecimal getEstimatedYield() {
        return calculateYield();
    }

    @Override
    public String toString() {
        return String.format(
                "SavingAccount[agency=%s, number=%s, balance=$ %.2f, +" +
                        "rateReturn=%.2f%%, nextYield=%d days]",
                agency, number, balance,
                rateReturn.multiply(BigDecimal.valueOf(100)),
                daysForTheNextYield()
        );
    }
}
