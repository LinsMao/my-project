package com.example.DTO.ai;

public class ChatRequest {
    private String message;
    private String sessionId;  // 可选，用于多轮对话

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
