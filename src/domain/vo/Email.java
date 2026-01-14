package domain.vo;

import java.util.regex.Pattern;

public record Email(String address) {
    private static final String EMAIL_REGEX =
            "^(?i)[a-z0-9._%+-]+@(?:[a-z0-9-]+\\.)+[a-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Compact constructor for validation
    public Email {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        address = address.toLowerCase().trim();

        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Invalid email: " + address);
        }
    }

    // Factory method alternative
    public static Email of(String address) {
        return new Email(address);
    }

    public String getUser() {
        return address.substring(0, address.indexOf('@'));
    }

    public String getDomain() {
        return address.substring(address.indexOf('@') + 1);
    }
}
