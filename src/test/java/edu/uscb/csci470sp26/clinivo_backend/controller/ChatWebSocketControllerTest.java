package edu.uscb.csci470sp26.clinivo_backend.controller;

import edu.uscb.csci470sp26.clinivo_backend.dto.ChatMessage;
import edu.uscb.csci470sp26.clinivo_backend.dto.MessageResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.service.ConversationService;
import edu.uscb.csci470sp26.clinivo_backend.service.MessageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatWebSocketControllerTest {

    private SimpMessagingTemplate messagingTemplate;
    private MessageService messageService;
    private ConversationService conversationService;
    private ChatWebSocketController controller;

    @BeforeEach
    void setup() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        messageService = mock(MessageService.class);
        conversationService = mock(ConversationService.class);

        controller = new ChatWebSocketController(
                messagingTemplate,
                messageService,
                conversationService
        );
    }

    @Test
    void testSendMessageBroadcastsToDoctorAndPatient() {
        // Incoming WebSocket payload
        ChatMessage incoming = new ChatMessage();
        incoming.setConversationId(10L);
        incoming.setSenderId(1L);
        incoming.setContent("Hello");

        // Mock conversation participants
        User doctor = new User();
        doctor.setId(1L);

        User patient = new User();
        patient.setId(2L);

        Conversation conv = new Conversation();
        conv.setDoctor(doctor);
        conv.setPatient(patient);

        // Mock saved message
        Message saved = new Message();
        saved.setId(100L);
        saved.setSender(doctor);
        saved.setContent("Hello");
        saved.setConversation(conv);

        when(messageService.sendMessage(10L, 1L, "Hello"))
                .thenReturn(saved);

        controller.sendMessage(incoming);

        // Verify broadcast to doctor
        verify(messagingTemplate).convertAndSend(
                eq("/topic/messages/1"),
                any(MessageResponse.class)
        );

        // Verify broadcast to patient
        verify(messagingTemplate).convertAndSend(
                eq("/topic/messages/2"),
                any(MessageResponse.class)
        );
    }
}
