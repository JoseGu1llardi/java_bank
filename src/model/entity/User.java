package model.entity;

import model.valueObject.CPF;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private CPF cpf;
    private String email;
    private LocalDateTime createdAt;

    public User(String name, String cpf, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.cpf = new CPF(cpf);
    }
}
