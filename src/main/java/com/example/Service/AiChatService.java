package com.example.Service;

import com.example.VO.ChatMessageVO;
import com.example.VO.ChatResponseVO;

import java.util.List;

public interface AiChatService {
    
    /**
     * 发送消息并获取AI回复
     * @param userId 用户ID
     * @param message 用户消息
     * @param sessionId 会话ID（可选）
     * @return AI回复
     */
    ChatResponseVO chat(Long userId, String message, String sessionId);
    
    /**
     * 获取用户的聊天历史
     * @param userId 用户ID
     * @param sessionId 会话ID（可选）
     * @param limit 限制数量
     * @return 消息列表
     */
    List<ChatMessageVO> getChatHistory(Long userId, String sessionId, Integer limit);
}
