package domain.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    private BigDecimal currentBalance;
    private  BigDecimal requestedAmount;

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(BigDecimal currentBalance, BigDecimal requestedAmount) {
        super(String.format("Insufficient balance. Balance $ %.2f, Requested amount $ %.2f.%n",
                currentBalance, requestedAmount));

        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}
