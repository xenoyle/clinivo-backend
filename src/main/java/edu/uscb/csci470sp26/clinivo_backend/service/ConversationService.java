package edu.uscb.csci470sp26.clinivo_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ConversationService(ConversationRepository conversationRepository,
                               UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    //Create or return existing conversation between doctor and patient
    public Conversation getOrCreateConversation(Long doctorId, Long patientId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (doctor.getRole() != User.Role.DOCTOR) {
            throw new RuntimeException("doctorId does not belong to a provider");
        }

        if (patient.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("patientId does not belong to a patient");
        }

        return conversationRepository.findByDoctorAndPatient(doctor, patient)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setDoctor(doctor);
                    newConv.setPatient(patient);
                    Conversation saved = conversationRepository.save(newConv);

                    //Notify doctor in real time
                    messagingTemplate.convertAndSend(
                        "/topic/conversations/" + doctorId,
                        saved
                    );

                    return saved;
                });
    }

    //Get all conversations for a doctor
    public java.util.List<Conversation> getDoctorConversations(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return conversationRepository.findByDoctor(doctor);
    }

    public Conversation getPatientConversation(Long patientId) {

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("patientId does not belong to a patient");
        }

        // 1. Check if a conversation ALREADY exists for this patient
        java.util.Optional<Conversation> existingConv = conversationRepository.findByPatient(patient);
        if (existingConv.isPresent()) {
            return existingConv.get();
        }

        // 2. If no conversation exists, find the first available doctor dynamically
        User doctor = userRepository.findByRole(User.Role.DOCTOR).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No doctors found in the system"));

        // 3. Create and save the new conversation
        Conversation newConv = new Conversation();
        newConv.setDoctor(doctor);
        newConv.setPatient(patient);
        Conversation saved = conversationRepository.save(newConv);

        // Notify doctor in real time
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + doctor.getId(),
            saved
        );

        return saved;
    }
    
}
