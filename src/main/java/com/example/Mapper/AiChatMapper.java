package com.example.Mapper;

import com.example.Entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiChatMapper {
    
    // 插入消息
    void insertMessage(AiChatMessage message);
    
    // 根据用户ID和会话ID获取历史消息
    List<AiChatMessage> getMessagesByUserAndSession(
        @Param("userId") Long userId, 
        @Param("sessionId") String sessionId,
        @Param("limit") Integer limit
    );
    
    // 根据用户ID获取最近的消息
    List<AiChatMessage> getRecentMessagesByUser(
        @Param("userId") Long userId,
        @Param("limit") Integer limit
    );
}
