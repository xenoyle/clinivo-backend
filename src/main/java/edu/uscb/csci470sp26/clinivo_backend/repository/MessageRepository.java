package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	
	List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

}
