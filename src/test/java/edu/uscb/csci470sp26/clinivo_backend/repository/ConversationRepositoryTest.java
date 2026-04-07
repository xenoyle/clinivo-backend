package edu.uscb.csci470sp26.clinivo_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.uscb.csci470sp26.clinivo_backend.model.*;

@DataJpaTest
public class ConversationRepositoryTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserConversations() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123");
        user.setRole(User.Role.PATIENT);
        user = userRepository.save(user);

        Conversation conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);

        participantRepository.save(participant);

        List<ConversationParticipant> results =
                participantRepository.findByUserId(user.getId());

        assertEquals(1, results.size());
        assertEquals(conversation.getId(), results.get(0).getConversation().getId());
    }
}
