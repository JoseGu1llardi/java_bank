package application.services;

import application.repositories.UserRepository;
import domain.entity.User;
import domain.exception.UserNotFoundException;

/**
 * Service class for managing user-related operations.
 * This class provides methods for creating new users while ensuring data consistency.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String cpf, String email) {
        if (userRepository.findByCPF(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF already exists");
        }

        User user = new User(name, cpf, email);
        userRepository.save(user);
        return user;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
