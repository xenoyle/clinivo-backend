package edu.uscb.csci470sp26.clinivo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uscb.csci470sp26.clinivo_backend.model.MessageRead;

public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    List<MessageRead> findByUserIdAndIsReadFalse(Long userId);

    List<MessageRead> findByMessageId(Long messageId);
    
    Optional<MessageRead> findByMessageIdAndUserId(Long messageId, Long userId);

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}
