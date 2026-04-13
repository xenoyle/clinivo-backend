package edu.uscb.csci470sp26.clinivo_backend.controller;

import org.springframework.web.bind.annotation.*;

import edu.uscb.csci470sp26.clinivo_backend.dto.ConversationResponse;
import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.service.ConversationService;

import java.util.List;

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

    // ⭐ Return FULL conversations for doctor (includes patient names)
    @GetMapping("/doctor/{doctorId}")
    public List<Conversation> getDoctorConversations(@PathVariable Long doctorId) {
        return conversationService.getDoctorConversations(doctorId);
    }

    // ⭐ Return FULL conversation for patient (includes doctor info)
    @GetMapping("/patient/{patientId}")
    public Conversation getPatientConversation(@PathVariable Long patientId) {
        return conversationService.getPatientConversation(patientId);
    }
}
