package model.entity;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String number;
    protected String agency;
    protected BigDecimal balance;
    protected User holder;
    protected LocalDateTime createdAt;
    protected List<Transaction> transactions;
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

    protected  String generateAccountNumber() {
        return String.format("%08d", random.nextInt(100_000_000));
    }
}
