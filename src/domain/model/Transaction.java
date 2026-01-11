package domain.model;

import domain.enums.TransactionStatus;
import domain.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal previousBalance;
    private final BigDecimal balanceAfter;
    private final LocalDate dateTime;

    private final String accountOwnerCode;
    private final String counterpartyAccountCode;
    private final String description;
    private TransactionStatus status;
    private final String authenticationCode;

    /**
     * Creates transaction; sets amount, status, and authentication
     */
    public Transaction(TransactionType type, BigDecimal amount,
                       BigDecimal previousBalance, String accountOwnerCode,
                       String counterpartyAccountCode, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.previousBalance = previousBalance.setScale(2, RoundingMode.HALF_EVEN);
        this.balanceAfter = calculateBalanceAfter();
        this.dateTime = LocalDate.now();
        this.accountOwnerCode = accountOwnerCode;
        this.counterpartyAccountCode = counterpartyAccountCode;
        this.description = description != null ? description : "Transaction made.";
        this.status = TransactionStatus.PENDING;
        this.authenticationCode = generateAuthenticationCode();
    }

    private BigDecimal calculateBalanceAfter() {
        return switch (type) {
            case DEPOSIT, TRANSFER_RECEIVED -> this.previousBalance.add(this.amount);
            case WITHDRAW, TRANSFER_SENT, BILL_PAYMENT, PIX_PAYMENT, TED, DOC, FEE ->
                previousBalance.subtract(this.amount);
            default -> this.previousBalance;
        };
    }

    private String generateAuthenticationCode() {
        return String.format("%s-%06d",
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                ThreadLocalRandom.current().nextInt(100000, 1_000_000));
    }

    public void confirm() {
        if (this.status == TransactionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled transaction.");
        }
        if (this.status == TransactionStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot confirm a confirmed transaction.");
        }
        this.status = TransactionStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == TransactionStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a confirmed transaction.");
        }
        if (this.status == TransactionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a confirmed transaction.");
        }
        this.status = TransactionStatus.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public String getAccountOwnerCode() {
        return accountOwnerCode;
    }

    public String getCounterpartyAccountCode() {
        return counterpartyAccountCode;
    }

    public String getDescription() {
        return description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    @Override
    public String toString() {
        return String.format(
                "Transaction[ID: %s | %s | Amount: $ %.2f | Date: %s | Status: %s]",
                id.substring(0, 8), // shows only part of the ID
                type.getDescription(),
                amount,
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                status.getDescription()
        );
    }

    /**
     * Returns detailed multi‑line string representation of transaction
     */
    public String toDetailedString() {
        return String.format(
                """
                Transaction Details:
                ID: %s
                Type: %s
                Amount: $ %.2f
                Previous Balance: $ %.2f
                Balance After: $ %.2f
                Date: %s
                Origin: %s
                Destination: %s
                Status: %s
                Auth Code: %s
                Description: %s
                """,
                id,
                type.getDescription(),
                amount,
                previousBalance,
                balanceAfter,
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                accountOwnerCode != null ? accountOwnerCode : "N/A",
                counterpartyAccountCode != null ? counterpartyAccountCode : "N/A",
                status.getDescription(),
                authenticationCode,
                description
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(authenticationCode, that.authenticationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authenticationCode);
    }
}