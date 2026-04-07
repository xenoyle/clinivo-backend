package edu.uscb.csci470sp26.clinivo_backend.dto;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private Long conversationId; // ⭐ ADDED

    public MessageResponse() {}

    public MessageResponse(Long id, Long senderId, String content, LocalDateTime createdAt, Long conversationId) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
        this.conversationId = conversationId; // ⭐ ADDED
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getConversationId() { // ⭐ ADDED
        return conversationId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setConversationId(Long conversationId) { // ⭐ ADDED
        this.conversationId = conversationId;
    }
}
