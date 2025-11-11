package model.entity;

import model.valueObject.CPF;
import model.valueObject.Email;

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

    public CPF getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
