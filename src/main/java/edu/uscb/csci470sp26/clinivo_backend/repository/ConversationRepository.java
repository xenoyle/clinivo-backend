package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.User;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Get all conversations for a doctor (FULL entities)
    List<Conversation> findByDoctor(User doctor);

    // Get the single conversation for a patient (FULL entity)
    Optional<Conversation> findByPatient(User patient);

    // Check if a conversation already exists between doctor and patient
    Optional<Conversation> findByDoctorAndPatient(User doctor, User patient);
}
