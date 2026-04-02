package edu.uscb.csci470sp26.clinivo_backend.service;

import org.springframework.stereotype.Service;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.ConversationParticipant;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationParticipantRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    //  Constructor injection
    public ConversationService(ConversationRepository conversationRepository,
                               ConversationParticipantRepository participantRepository,
                               UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    //  Create conversation
    public Conversation createConversation(List<Long> userIds) {

        Conversation conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ConversationParticipant participant = new ConversationParticipant();
            participant.setConversation(conversation);
            participant.setUser(user);

            participantRepository.save(participant);
        }

        return conversation;
    }

    //  Get conversations for a user
    public List<Conversation> getUserConversations(Long userId) {
        return participantRepository.findByUserId(userId)
                .stream()
                .map(ConversationParticipant::getConversation)
                .collect(Collectors.toList());
    }
}
