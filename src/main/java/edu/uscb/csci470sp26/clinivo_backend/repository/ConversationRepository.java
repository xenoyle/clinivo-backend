package edu.uscb.csci470sp26.clinivo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
