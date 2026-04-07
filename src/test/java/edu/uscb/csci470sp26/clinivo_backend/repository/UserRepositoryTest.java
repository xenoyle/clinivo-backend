package edu.uscb.csci470sp26.clinivo_backend.repository;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.uscb.csci470sp26.clinivo_backend.model.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPassword("123");
        user.setRole(User.Role.PATIENT);

        user = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
    }

    @Test
    public void testSave() {

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@test.com");
        user.setPassword("123");
        user.setRole(User.Role.PROVIDER);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Jane", savedUser.getFirstName());
    }

    @Test
    public void testDeleteById() {

        User user = new User();
        user.setFirstName("Delete");
        user.setLastName("Me");
        user.setEmail("delete@test.com");
        user.setPassword("123");
        user.setRole(User.Role.PATIENT);

        user = userRepository.save(user);
        Long userId = user.getId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
    }
}
