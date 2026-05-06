package edu.uscb.csci470sp26.clinivo_backend.controller;

import org.springframework.web.bind.annotation.*;

import edu.uscb.csci470sp26.clinivo_backend.dto.MessageRequest;
import edu.uscb.csci470sp26.clinivo_backend.dto.MessageResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.service.EncryptionService;
import edu.uscb.csci470sp26.clinivo_backend.service.MessageService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final EncryptionService encryptionService;

    public MessageController(MessageService messageService, EncryptionService encryptionService) {
        this.messageService = messageService;
        this.encryptionService = encryptionService;
    }

    // Send a message
    @PostMapping
    public MessageResponse sendMessage(@RequestBody MessageRequest request) {

        Message message = messageService.sendMessage(
                request.getConversationId(),
                request.getSenderId(),
                request.getContent()
        );

        // Decrypt the content for the response
        String decryptedContent = encryptionService.decrypt(message.getContent());

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                decryptedContent,
                message.getCreatedAt(),
                message.getConversation().getId()
        );
    }

    // Get messages in a conversation
    @GetMapping("/conversation/{conversationId}")
    public List<MessageResponse> getMessages(@PathVariable Long conversationId) {

        return messageService.getMessages(conversationId)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getSender().getId(),
                        m.getContent(),  // Already decrypted by MessageService
                        m.getCreatedAt(),
                        m.getConversation().getId()
                ))
                .collect(Collectors.toList());
    }
    
    @PostMapping("/read")
    public void markAsRead(@RequestParam Long conversationId, @RequestParam Long userId) {
		messageService.markMessagesAsRead(conversationId, userId);
	}
    
    @GetMapping("/unread/{userId}")
    public int getUnreadCount(@PathVariable Long userId) {
        return messageService.getUnreadCount(userId);
    }
}

