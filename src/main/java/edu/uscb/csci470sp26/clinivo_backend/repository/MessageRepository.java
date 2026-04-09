package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Fetch all messages in a conversation, sorted oldest → newest
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
