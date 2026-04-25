package com.example.VO;

public class ChatResponseVO {
    private String reply;  // AI的回复内容
    private Long messageId;  // 消息ID
    private String sessionId;  // 会话ID

    public ChatResponseVO() {
    }

    public ChatResponseVO(String reply, Long messageId, String sessionId) {
        this.reply = reply;
        this.messageId = messageId;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
