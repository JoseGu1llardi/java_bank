package model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsAccount extends Account {
    private static final BigDecimal RATE_RETURN = BigDecimal.valueOf(0.005);

    private final LocalDate anniversaryDate;

    public SavingsAccount(String agency, User holder) {
        super(agency, holder);
        this.anniversaryDate = LocalDate.now();
    }

    @Override
    public void withdraw(BigDecimal amount, String description) {

    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        return null;
    }
}
