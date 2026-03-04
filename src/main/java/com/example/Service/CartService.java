package com.example.Service;
import com.example.DTO.CartAddRequest;
import com.example.VO.CartVO;

import java.util.List;


public interface CartService {

        // 添加商品到购物车
        void addToCart(Long userId, CartAddRequest request);

        // 获取购物车列表
        List<CartVO> getCartList(Long userId);

        // 获取选中的购物车列表
        List<CartVO> getSelectedCartList(Long userId);

        // 更新选中状态
        void updateSelect(Long cartId, Long userId, Integer selected);

        // 更新购物车商品数量
        void updateQuantity(Long cartId, Long userId, Integer quantity);

        // 全选/取消全选
        void updateSelectAll(Long userId, Integer selected);

        // 删除购物车项
        void deleteCartItem(Long cartId, Long userId);

        // 批量删除选中的购物车项
        int deleteSelected(Long userId);

}
