package com.example.Service;
import com.example.DTO.CartAddRequest;


public interface CartService {

        // 添加商品到购物车
        void addToCart(Long userId, CartAddRequest request);

}
