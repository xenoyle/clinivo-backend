package edu.uscb.csci470sp26.clinivo_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

@SpringBootApplication
public class ClinivoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinivoBackendApplication.class, args);
		
		
	}
		
	@Bean
    public CommandLineRunner dataChecker(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByRole(User.Role.DOCTOR).isEmpty()) {
                User doctor = new User();
                doctor.setFirstName("John");
                doctor.setLastName("Doe");
                doctor.setEmail("doctor@test.com");
                doctor.setPassword("password"); // test data, hash for real application
                doctor.setPhoneNumber("1234567890");
                doctor.setRole(User.Role.DOCTOR);
                userRepository.save(doctor);
            }
        };
    }
	

}
