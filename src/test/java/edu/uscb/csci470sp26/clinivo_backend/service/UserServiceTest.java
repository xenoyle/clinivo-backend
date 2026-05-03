package edu.uscb.csci470sp26.clinivo_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.uscb.csci470sp26.clinivo_backend.dto.UserRequest;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void testCreateUserWithPasswordHashing() {
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setPhoneNumber("1234567890");
        request.setPassword("plainPassword123");
        request.setRole("PATIENT");

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPhoneNumber("1234567890");
        user.setPassword(passwordEncoder.encode("plainPassword123"));
        user.setRole(User.Role.PATIENT);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());
        assertTrue(passwordEncoder.matches("plainPassword123", result.getPassword()));
        assertNotEquals("plainPassword123", result.getPassword());
    }

    @Test
    public void testUpdateUserWithPasswordHashing() {
        Long userId = 1L;
        UserRequest request = new UserRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane@test.com");
        request.setPhoneNumber("0987654321");
        request.setPassword("newPassword456");
        request.setRole("DOCTOR");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setEmail("john@test.com");
        existingUser.setPhoneNumber("1234567890");
        existingUser.setPassword(passwordEncoder.encode("oldPassword123"));
        existingUser.setRole(User.Role.PATIENT);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane@test.com");
        updatedUser.setPhoneNumber("0987654321");
        updatedUser.setPassword(passwordEncoder.encode("newPassword456"));
        updatedUser.setRole(User.Role.DOCTOR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(userId, request);

        assertNotNull(result);
        assertEquals("jane@test.com", result.getEmail());
        assertTrue(passwordEncoder.matches("newPassword456", result.getPassword()));
    }

    @Test
    public void testAuthenticateUserWithCorrectPassword() {
        String email = "john@test.com";
        String plainPassword = "correctPassword123";
        String hashedPassword = passwordEncoder.encode(plainPassword);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.authenticateUser(email, plainPassword);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testAuthenticateUserWithIncorrectPassword() {
        String email = "john@test.com";
        String correctPassword = "correctPassword123";
        String wrongPassword = "wrongPassword456";
        String hashedPassword = passwordEncoder.encode(correctPassword);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser(email, wrongPassword);
        }, "Should throw RuntimeException for invalid password");
    }

    @Test
    public void testAuthenticateUserWithNonexistentEmail() {
        String email = "nonexistent@test.com";
        String password = "anyPassword123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser(email, password);
        }, "Should throw RuntimeException for invalid email");
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("john@test.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    public void testGetUserByIdNotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        }, "Should throw RuntimeException when user not found");
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
