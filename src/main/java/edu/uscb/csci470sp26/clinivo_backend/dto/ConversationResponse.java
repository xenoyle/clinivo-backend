package edu.uscb.csci470sp26.clinivo_backend.dto;

public class ConversationResponse {

    private Long id;

    public ConversationResponse() {}

    public ConversationResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
