package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.ConversationParticipant;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
	
	List<ConversationParticipant> findByUserId(Long userId);
	
	List<ConversationParticipant> findByConversationId(Long conversationId);

}
