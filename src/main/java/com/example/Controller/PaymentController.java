package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Service.PaymentService;
import com.example.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 模拟支付
     */
    @PostMapping("/mock/{orderNo}")
    public ApiResponse<?> mockPayment(@PathVariable String orderNo, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            paymentService.mockPayment(orderNo, userId);
            return ApiResponse.success("支付成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 获取当前用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return null;
        }
        return JwtUtils.getUserIdFromToken(token);
    }
}
