package edu.uscb.csci470sp26.clinivo_backend.controller;

import org.springframework.web.bind.annotation.*;

import edu.uscb.csci470sp26.clinivo_backend.dto.MessageRequest;
import edu.uscb.csci470sp26.clinivo_backend.dto.MessageResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.service.MessageService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Send a message
    @PostMapping
    public MessageResponse sendMessage(@RequestBody MessageRequest request) {

        Message message = messageService.sendMessage(
                request.getConversationId(),
                request.getSenderId(),
                request.getContent()
        );

        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getCreatedAt()
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
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
