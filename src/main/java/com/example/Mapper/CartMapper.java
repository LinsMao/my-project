package com.example.Mapper;

import com.example.Entity.Cart;
import com.example.VO.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface CartMapper {

    // 根据用户ID和商品ID查询购物车
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // 插入
    int insert(Cart cart);

    // 更新
    int updateQuantity(Cart cart);

    // 查询
    List<CartVO> selectCartListWithProduct(@Param("userId") Long userId);
}
