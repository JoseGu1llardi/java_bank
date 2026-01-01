package domain.exception;

public class EmailUnchangedException extends RuntimeException {
    public EmailUnchangedException(String message) {
        super(message);
    }
}
