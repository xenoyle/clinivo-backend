package edu.uscb.csci470sp26.clinivo_backend.controller;

import org.springframework.web.bind.annotation.*;

import edu.uscb.csci470sp26.clinivo_backend.dto.ConversationResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.service.ConversationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    // Constructor injection
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    //  Create conversation
    @PostMapping
    public ConversationResponse createConversation(@RequestBody List<Long> participantIds) {

        Conversation conversation = conversationService.createConversation(participantIds);

        return new ConversationResponse(conversation.getId());
    }

    //  Get user conversations
    @GetMapping("/user/{userId}")
    public List<ConversationResponse> getUserConversations(@PathVariable Long userId) {

        return conversationService.getUserConversations(userId)
                .stream()
                .map(c -> new ConversationResponse(c.getId()))
                .collect(Collectors.toList());
    }
}
