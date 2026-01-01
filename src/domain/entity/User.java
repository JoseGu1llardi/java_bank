package domain.entity;

import domain.exception.EmailUnchangedException;
import domain.valueObject.CPF;
import domain.valueObject.Email;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private CPF cpf;
    private Email email;
    private LocalDateTime createdAt;

    public User(String name, String cpf, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.cpf = new CPF(cpf);
        this.email = Email.of(email);
        this.createdAt = LocalDateTime.now();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf.toString();
    }

    public String getEmail() {
        return email.toString();
    }

    public void changeEmail(String newEmail) {
        Email newValue = Email.of(newEmail);

        if (this.email.equals(newValue)) {
            throw new EmailUnchangedException("The new email is equal to the current one.");
        }
        this.email = newValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
