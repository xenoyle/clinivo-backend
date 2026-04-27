package edu.uscb.csci470sp26.clinivo_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.uscb.csci470sp26.clinivo_backend.model.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ConversationRepositoryTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserConversations() {

    	User doctor = new User();
    	doctor.setFirstName("Test");
    	doctor.setLastName("User");
    	doctor.setEmail("doctor_" + System.currentTimeMillis() + "@test.com");
    	doctor.setPassword("123");
    	doctor.setPhoneNumber("1112223333"); 
    	doctor.setRole(User.Role.DOCTOR);
    	doctor = userRepository.save(doctor);
    	
    	User patient = new User();
    	patient.setFirstName("Test");
    	patient.setLastName("User");
    	patient.setEmail("patient_" + System.currentTimeMillis() + "@test.com");
    	patient.setPassword("123");
    	patient.setPhoneNumber("1112223333"); 
    	patient.setRole(User.Role.PATIENT);
    	patient = userRepository.save(patient);

        Conversation conversation = new Conversation();
        conversation.setDoctor(doctor);
        conversation.setPatient(patient);
        conversation = conversationRepository.save(conversation);

        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(patient);

        participantRepository.save(participant);

        List<ConversationParticipant> results =
                participantRepository.findByUserId(patient.getId());

        assertEquals(1, results.size());
        assertEquals(conversation.getId(), results.get(0).getConversation().getId());
    }
}
