package com.example.Service;
import com.example.DTO.CartAddRequest;
import com.example.VO.CartVO;

import java.util.List;


public interface CartService {

        // 添加商品到购物车
        void addToCart(Long userId, CartAddRequest request);

        // 获取购物车列表
        List<CartVO> getCartList(Long userId);

}
