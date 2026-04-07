package edu.uscb.csci470sp26.clinivo_backend.repository;



import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.uscb.csci470sp26.clinivo_backend.model.*;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Test
    public void testFindByConversationId() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123");
        user.setRole(User.Role.PATIENT);
        user = userRepository.save(user);

        Conversation conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(user);
        message.setContent("Hello!");

        messageRepository.save(message);

        List<Message> messages =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        assertEquals(1, messages.size());
        assertEquals("Hello!", messages.get(0).getContent());
    }
}