package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.uscb.csci470sp26.clinivo_backend.model.ConversationParticipant;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    @Query("SELECT cp FROM ConversationParticipant cp WHERE cp.user.id = :userId")
    List<ConversationParticipant> findByUserId(@Param("userId") Long userId);

    @Query("SELECT cp FROM ConversationParticipant cp WHERE cp.conversation.id = :conversationId")
    List<ConversationParticipant> findByConversationId(@Param("conversationId") Long conversationId);
}
