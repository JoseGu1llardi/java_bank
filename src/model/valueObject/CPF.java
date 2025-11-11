package model.valueObject;

import java.util.Objects;

public final class CPF {
    private final String value;

    public CPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("CPF must not be null or empty.");
        }

        // removes everything that is not number
        String validCPF = cpf.replaceAll("\\D", "");

        if (validCPF.length() != 11) {
            throw new IllegalArgumentException("Invalid CPF: must contain 11 digits.");
        }

        if (validCPF.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("Invalid CPF: repeated digits are not allowed.");
        }

        this.value = validCPF;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CPF{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
