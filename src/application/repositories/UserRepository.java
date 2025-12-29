package application.repositories;

import domain.entity.User;
import domain.valueObject.CPF;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository class for managing {@code User} entities in memory.
 * Provides methods for saving, finding, verifying existence,
 * retrieving all, and removing users.
 * <p>
 * This class is implemented using a {@code HashMap} for storage.
 */
public class UserRepository {
    private Map<String, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByCPF(CPF cpf) {
        return users.values().stream()
                .filter(user -> user.getCpf().equals(cpf))
                .findFirst();
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public boolean existsById(String id) {
        return users.containsKey(id);
    }

    public void remove(User user) {
        users.remove(user.getId());
    }
}
