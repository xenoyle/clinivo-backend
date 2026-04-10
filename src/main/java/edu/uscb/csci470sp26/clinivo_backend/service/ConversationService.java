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

    //Automatically create a conversation for a patient if missing
    public Conversation getPatientConversation(Long patientId) {

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("patientId does not belong to a patient");
        }

        //Hardcoded doctor for now (can be dynamic later)
        Long doctorId = 2L;

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

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
}
