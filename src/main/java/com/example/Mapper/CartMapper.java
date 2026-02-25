package com.example.Mapper;

import com.example.Entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface CartMapper {

    // 根据用户ID和商品ID查询购物车
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // 插入
    int insert(Cart cart);

    // 更新
    int updateQuantity(Cart cart);
}
