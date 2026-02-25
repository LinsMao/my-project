package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.CartAddRequest;
import com.example.Service.CartService;
import com.example.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ApiResponse<?> addToCart(@RequestBody CartAddRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.unauthorized("未登录");
        }

        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return ApiResponse.unauthorized("token无效");
        }
        //获取用户ID
        Long userId = JwtUtils.getUserIdFromToken(token);

        try {
            cartService.addToCart(userId, request);
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
