package edu.uscb.csci470sp26.clinivo_backend.service;

import org.springframework.stereotype.Service;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    // Create or return existing conversation between doctor and patient
    public Conversation getOrCreateConversation(Long doctorId, Long patientId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Ensure roles are correct
        if (doctor.getRole() != User.Role.PROVIDER) {
            throw new RuntimeException("doctorId does not belong to a provider");
        }

        if (patient.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("patientId does not belong to a patient");
        }

        // Check if conversation already exists
        return conversationRepository.findByDoctorAndPatient(doctor, patient)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setDoctor(doctor);
                    newConv.setPatient(patient);
                    return conversationRepository.save(newConv);
                });
    }

    // Get all conversations for a doctor (list)
    public java.util.List<Conversation> getDoctorConversations(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return conversationRepository.findByDoctor(doctor);
    }

//    // ⭐ Get the single conversation for a doctor
//    public Conversation getDoctorConversation(Long doctorId) {
//        User doctor = userRepository.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//
//        return conversationRepository.findByDoctor(doctor)
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Conversation not found"));
//    }

    // Get the single conversation for a patient
    public Conversation getPatientConversation(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return conversationRepository.findByPatient(patient)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }
}
