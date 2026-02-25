package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.CartAddRequest;
import com.example.VO.CartVO;
import com.example.Service.CartService;
import com.example.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            cartService.addToCart(userId, request);
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 获取购物车列表
    @GetMapping("/list")
    public ApiResponse<List<CartVO>> getCartList(HttpServletRequest request) {
        // 获取用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        List<CartVO> list = cartService.getCartList(userId);
        return ApiResponse.success(list);
    }


    // 更新购物车选中状态
    @PutMapping("/item/{id}/select")
    public ApiResponse<?> updateSelect(@PathVariable Long id, @RequestParam Integer selected, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }
        try {
            cartService.updateSelect(id, userId, selected);
            return ApiResponse.success();
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
