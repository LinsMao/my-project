package com.example.Service.impl;

import com.example.Entity.AiChatMessage;
import com.example.Mapper.AiChatMapper;
import com.example.Service.AiChatService;
import com.example.VO.ChatMessageVO;
import com.example.VO.ChatResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiChatMapper aiChatMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.volcengine.api-key}")
    private String apiKey;

    @Value("${ai.volcengine.model:doubao-pro-4k}")
    private String model;

    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/coding/v3/chat/completions";

    @Override
    public ChatResponseVO chat(Long userId, String message, String sessionId) {
        // 生成会话ID
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        //  保存用户消息
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setUserId(userId);
        userMessage.setRole("user");
        userMessage.setContent(message);
        userMessage.setSessionId(sessionId);
        userMessage.setCreatedAt(LocalDateTime.now());
        aiChatMapper.insertMessage(userMessage);

        // 获取历史消息
        List<AiChatMessage> history = aiChatMapper.getMessagesByUserAndSession(userId, sessionId, 10);
        Collections.reverse(history);  // 按时间正序

        // 调用API
        String aiReply = callVolcengineAPI(history, message);

        // 保存AI回复
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(aiReply);
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        aiChatMapper.insertMessage(assistantMessage);

        // 返回结果
        return new ChatResponseVO(aiReply, assistantMessage.getId(), sessionId);
    }

    @Override
    public List<ChatMessageVO> getChatHistory(Long userId, String sessionId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }

        List<AiChatMessage> messages;
        if (sessionId != null && !sessionId.isEmpty()) {
            messages = aiChatMapper.getMessagesByUserAndSession(userId, sessionId, limit);
        } else {
            messages = aiChatMapper.getRecentMessagesByUser(userId, limit);
        }

        Collections.reverse(messages);  // 按时间正序

        return messages.stream()
                .map(msg -> new ChatMessageVO(msg.getId(), msg.getRole(), msg.getContent(), msg.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 调用火山方舟API（兼容OpenAI格式）
     */
    private String callVolcengineAPI(List<AiChatMessage> history, String currentMessage) {
        try {
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统提示
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个生鲜商城的智能助手，可以帮助用户推荐商品、解答问题。请用友好、专业的语气回答。");
            messages.add(systemMsg);

            // 添加历史消息
            for (AiChatMessage msg : history) {
                Map<String, String> historyMsg = new HashMap<>();
                historyMsg.put("role", msg.getRole());
                historyMsg.put("content", msg.getContent());
                messages.add(historyMsg);
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, Map.class);

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                    String content = (String) messageObj.get("content");
                    
                    // 确保返回内容不为空
                    if (content != null && !content.trim().isEmpty()) {
                        return content;
                    }
                }
            }

            return "抱歉，我现在无法回答，请稍后再试。";

        } catch (org.springframework.web.client.ResourceAccessException e) {
            // 超时异常
            e.printStackTrace();
            return "抱歉，AI响应超时，请稍后再试。";
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // HTTP 4xx 错误
            e.printStackTrace();
            return "抱歉，请求失败：" + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            // HTTP 5xx 错误
            e.printStackTrace();
            return "抱歉，服务器错误：" + e.getStatusCode();
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
            return "抱歉，服务出现异常：" + e.getMessage();
        }
    }
}
