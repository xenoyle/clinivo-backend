package edu.uscb.csci470sp26.clinivo_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import edu.uscb.csci470sp26.clinivo_backend.dto.ChatMessage;
import edu.uscb.csci470sp26.clinivo_backend.dto.MessageResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.service.ConversationService;
import edu.uscb.csci470sp26.clinivo_backend.service.MessageService;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ConversationService conversationService;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   MessageService messageService,
                                   ConversationService conversationService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.conversationService = conversationService;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {

        // Save message to DB
        Message saved = messageService.sendMessage(
                chatMessage.getConversationId(),
                chatMessage.getSenderId(),
                chatMessage.getContent()
        );

        Conversation conv = saved.getConversation();

        // Build response DTO
        MessageResponse response = new MessageResponse(
                saved.getId(),
                saved.getSender().getId(),
                saved.getContent(),
                saved.getCreatedAt(),
                conv.getId()
        );

        Long doctorId = conv.getDoctor().getId();
        Long patientId = conv.getPatient().getId();

        // Always send to doctor
        messagingTemplate.convertAndSend(
                "/topic/messages/" + doctorId,
                response
        );

        // Only send to patient if they are NOT the doctor
        // Prevents duplicate messages when sender == patient
        if (!doctorId.equals(patientId)) {
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + patientId,
                    response
            );
        }
    }
}
