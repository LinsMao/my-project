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

    // 获取选中的购物车列表
    @GetMapping("/selected")
    public ApiResponse<List<CartVO>> getSelectedCartList(HttpServletRequest request) {
        // 获取用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        List<CartVO> list = cartService.getSelectedCartList(userId);
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

    // 更新购物车商品数量
    @PutMapping("/item/{id}/quantity")
    public ApiResponse<?> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }
        try {
            cartService.updateQuantity(id, userId, quantity);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 全选/取消全选
    @PutMapping("/selectAll")
    public ApiResponse<?> updateSelectAll(@RequestParam Integer selected, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }
        try {
            cartService.updateSelectAll(userId, selected);
            return ApiResponse.success("操作成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 删除购物车项
    @DeleteMapping("/item/{id}")
    public ApiResponse<?> deleteCartItem(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }
        try {
            cartService.deleteCartItem(id, userId);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 批量删除选中的购物车项
    @DeleteMapping("/selected")
    public ApiResponse<?> deleteSelected(HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }
        try {
            int count = cartService.deleteSelected(userId);
            return ApiResponse.success("成功删除" + count + "件商品");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    // 获取当前用户ID方法
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
