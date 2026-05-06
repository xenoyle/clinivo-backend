package edu.uscb.csci470sp26.clinivo_backend.repository;



import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.uscb.csci470sp26.clinivo_backend.model.*;
import edu.uscb.csci470sp26.clinivo_backend.service.EncryptionService;
import jakarta.transaction.Transactional;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void testFindByConversationId() {

    	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    	
    	User doctor = new User();
    	doctor.setFirstName("Test");
    	doctor.setLastName("User");
    	doctor.setEmail("doctor_" + System.currentTimeMillis() + "@test.com");
    	doctor.setPassword(passwordEncoder.encode("123"));
    	doctor.setPhoneNumber("1112223333"); 
    	doctor.setRole(User.Role.DOCTOR);
    	doctor = userRepository.save(doctor);
    	
    	User patient = new User();
    	patient.setFirstName("Test");
    	patient.setLastName("User");
    	patient.setEmail("patient_" + System.currentTimeMillis() + "@test.com");
    	patient.setPassword(passwordEncoder.encode("123"));
    	patient.setPhoneNumber("1112223333"); 
    	patient.setRole(User.Role.PATIENT);
    	patient = userRepository.save(patient);

        Conversation conversation = new Conversation();
        conversation.setDoctor(doctor);
        conversation.setPatient(patient);
        conversation = conversationRepository.save(conversation);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(patient);
        // Encrypt the content before storing (simulating what MessageService does)
        String plainContent = "Hello!";
        message.setContent(encryptionService.encrypt(plainContent));

        messageRepository.save(message);

        List<Message> messages =
                messageRepository.findByConversationOrderByCreatedAtAsc(conversation);

        assertEquals(1, messages.size());
        // Content is stored encrypted in DB, so we need to decrypt to verify
        String decryptedContent = encryptionService.decrypt(messages.get(0).getContent());
        assertEquals("Hello!", decryptedContent);
    }
}
