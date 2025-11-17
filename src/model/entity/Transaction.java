package model.entity;

import model.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal previousBalance;
    private final BigDecimal balanceAfter;
    private final LocalDateTime dateTime;
    private final String originAccount;
    private final String destinationAccount;
    private final String description;
    private String status;
    private final String authenticationCode;

    public Transaction(TransactionType type, BigDecimal amount, BigDecimal previousBalance,
                       BigDecimal balanceAfter, String originAccount,
                       String destinationAccount, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.previousBalance = previousBalance.setScale(2, RoundingMode.HALF_EVEN);
        this.balanceAfter = calculateBalanceAfter();
        this.dateTime = LocalDateTime.now();
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.description = description;
        this.authenticationCode = generateAuthenticationCode();
    }

    private BigDecimal calculateBalanceAfter() {
        return switch (type) {
            case DEPOSIT, TRANSFER_RECEIVED -> this.previousBalance.add(this.amount);
            case WITHDRAW, TRANSFER_SENT, BILL_PAYMENT, PIX_PAYMENT, TED, DOC, FEE ->
                previousBalance.subtract(this.amount);
            default -> previousBalance;
        };
    }

    private String generateAuthenticationCode() {
        return String.format("%s-%06d",
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                ThreadLocalRandom.current().nextInt(100000, 1_000_000));
    }
}
