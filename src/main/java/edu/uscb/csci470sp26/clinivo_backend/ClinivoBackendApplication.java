package edu.uscb.csci470sp26.clinivo_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

@SpringBootApplication
public class ClinivoBackendApplication {
    

	public static void main(String[] args) {
		SpringApplication.run(ClinivoBackendApplication.class, args);
	}
		
    @Bean
    public CommandLineRunner defaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByRole(User.Role.DOCTOR).isEmpty()) {
                User doctor = new User();
                doctor.setFirstName("John");
                doctor.setLastName("Doe");
                doctor.setEmail("doctor@test.com");
                doctor.setPassword(passwordEncoder.encode("password"));
                doctor.setPhoneNumber("1234567890");
                doctor.setRole(User.Role.DOCTOR);
                userRepository.save(doctor);
            }
            
            if (userRepository.findByRole(User.Role.PATIENT).isEmpty()) {
                User patient = new User();
                patient.setFirstName("Tony");
                patient.setLastName("Stark");
                patient.setEmail("patient@test.com");
                patient.setPassword(passwordEncoder.encode("password"));
                patient.setPhoneNumber("1234567890");
                patient.setRole(User.Role.PATIENT);
                userRepository.save(patient);
            }
        };
    }
	

}
