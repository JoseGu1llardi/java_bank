package domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Account " + accountNumber + " not found.");
    }
}
