package model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        if (fee.compareTo(BigDecimal.ZERO) < 0 ||
                fee.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            throw new IllegalArgumentException("Rate return must be between 0% and 10%");
        }
    }

    @Override
    public void withdraw(BigDecimal amount, String description) {

    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        return null;
    }
}
