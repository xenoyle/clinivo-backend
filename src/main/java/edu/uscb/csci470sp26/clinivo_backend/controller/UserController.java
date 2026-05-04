package edu.uscb.csci470sp26.clinivo_backend.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import edu.uscb.csci470sp26.clinivo_backend.dto.UserRequest;
import edu.uscb.csci470sp26.clinivo_backend.dto.LoginRequest;
import edu.uscb.csci470sp26.clinivo_backend.dto.UserResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //  CREATE user
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {

        User user = userService.createUser(request);

        return mapToResponse(user);
    }

    //  GET all users
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  GET only patients (for doctors to list and start conversations)
    @GetMapping("/patients")
    public List<UserResponse> getPatients() {
        return userService.getPatients()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  GET user by ID
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);

        return mapToResponse(user);
    }

    //  UPDATE user
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserRequest request) {

        User updatedUser = userService.updateUser(id, request);

        return mapToResponse(updatedUser);
    }

    //  DELETE user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    
    // LOGIN user
    @PostMapping("/login")
    public UserResponse loginUser(@RequestBody LoginRequest request) {
        User user = userService.authenticateUser(request.getEmail(), request.getPassword());
        return mapToResponse(user);
    }

    //  Helper mapper
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().name()
        );
    }
}
