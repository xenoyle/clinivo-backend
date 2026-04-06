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

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    // Create or return existing conversation between doctor and patient
    @PostMapping("/{doctorId}/{patientId}")
    public ConversationResponse createOrGetConversation(
            @PathVariable Long doctorId,
            @PathVariable Long patientId) {

        Conversation conversation =
                conversationService.getOrCreateConversation(doctorId, patientId);

        return new ConversationResponse(conversation.getId());
    }

    // Get all conversations for a doctor
    @GetMapping("/doctor/{doctorId}")
    public List<ConversationResponse> getDoctorConversations(@PathVariable Long doctorId) {

        return conversationService.getDoctorConversations(doctorId)
                .stream()
                .map(c -> new ConversationResponse(c.getId()))
                .collect(Collectors.toList());
    }

    // Get the single conversation for a patient
    @GetMapping("/patient/{patientId}")
    public ConversationResponse getPatientConversation(@PathVariable Long patientId) {

        Conversation conversation = conversationService.getPatientConversation(patientId);

        return new ConversationResponse(conversation.getId());
    }
}
