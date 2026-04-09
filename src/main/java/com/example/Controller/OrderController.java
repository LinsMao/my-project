package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.CreateOrderRequest;
import com.example.Service.OrderService;
import com.example.Utils.JwtUtils;
import com.example.VO.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（支持拆单）
     */
    @PostMapping("/create")
    public ApiResponse<?> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        // 验证地址ID
        if (request.getAddressId() == null) {
            return ApiResponse.error("请选择收货地址");
        }

        try {
            List<String> orderNos = orderService.createOrder(userId, request.getAddressId(), request.getRemark());
            
            // 返回订单号列表和订单数量
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("orderNos", orderNos);
            result.put("count", orderNos.size());
            
            String message = orderNos.size() > 1 
                ? "订单创建成功，共创建" + orderNos.size() + "个订单" 
                : "订单创建成功";
            
            return ApiResponse.success(message, result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取订单列表
     * @param orderStatus 订单状态（可选）：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消
     */
    @GetMapping("/list")
    public ApiResponse<List<OrderVO>> getOrderList(@RequestParam(required = false) Integer orderStatus, HttpServletRequest httpRequest) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            List<OrderVO> orderList = orderService.getOrderList(userId, orderStatus);
            return ApiResponse.success(orderList);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderNo}")
    public ApiResponse<?> cancelOrder(@PathVariable String orderNo, HttpServletRequest httpRequest) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            orderService.cancelOrder(orderNo, userId);
            return ApiResponse.success("订单已取消");
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
