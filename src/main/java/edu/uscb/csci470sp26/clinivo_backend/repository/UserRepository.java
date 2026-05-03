package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	
	List<User> findByRole(User.Role role);
	
	public default List<User> findPatients() {
		return findByRole(User.Role.PATIENT);
	}
}



