package edu.uscb.csci470sp26.clinivo_backend.service;

import org.springframework.stereotype.Service;

import edu.uscb.csci470sp26.clinivo_backend.dto.UserRequest;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //  Create
    public User createUser(UserRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword()); // ⚠️ hash later
        user.setRole(User.Role.valueOf(request.getRole()));

        return userRepository.save(user);
    }

    //  Get all
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //  Get by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //  Update
    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setRole(User.Role.valueOf(request.getRole()));

        return userRepository.save(user);
    }

    //  Delete
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}