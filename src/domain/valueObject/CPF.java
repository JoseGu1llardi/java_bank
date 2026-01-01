package domain.valueObject;

public record CPF(String value) {

    /**
     * Normalizes and validates CPF; throws on invalid input
     */
    public CPF(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("CPF must not be null or empty.");
        }

        // removes everything that is not number
        String validCPF = value.replaceAll("\\D", "");

        if (validCPF.length() != 11) {
            throw new IllegalArgumentException("Invalid CPF: must contain 11 digits.");
        }

        if (validCPF.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("Invalid CPF: repeated digits are not allowed.");
        }

        this.value = validCPF;
    }
}
