package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.ai.ChatRequest;
import com.example.Service.AiChatService;
import com.example.VO.ChatMessageVO;
import com.example.VO.ChatResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 发送消息并获取AI回复
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponseVO> chat(@RequestHeader("user-id") Long userId, @RequestBody ChatRequest request) {

        // 参数校验
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ApiResponse.error(400, "消息内容不能为空");
        }

        ChatResponseVO response = aiChatService.chat(userId,  request.getMessage(), request.getSessionId()
        );

        return ApiResponse.success(response);
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/history")
    public ApiResponse<List<ChatMessageVO>> getHistory(
            @RequestHeader("user-id") Long userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "50") Integer limit) {

        List<ChatMessageVO> history = aiChatService.getChatHistory(userId, sessionId, limit);
        return ApiResponse.success(history);
    }
}
