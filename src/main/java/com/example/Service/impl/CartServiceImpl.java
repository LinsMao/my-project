package com.example.Service.impl;

import com.example.DTO.CartAddRequest;
import com.example.Entity.Cart;
import com.example.Entity.Product;
import com.example.Mapper.CartMapper;
import com.example.Mapper.ProductMapper;
import com.example.Service.CartService;
import com.example.VO.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    //  添加商品到购物车
    @Override
    @Transactional
    public void addToCart(Long userId, CartAddRequest request) {
        // 校验商品是否存在且已上架
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        //  检查库存是否足够
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足，当前库存：" + product.getStock());
        }


        // 检查购物车中是否已存在该商品
        Cart existingCart = cartMapper.findByUserIdAndProductId(userId, request.getProductId());

        if (existingCart == null) {
            // 购物车中没有该商品
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
            cart.setSelected(true); // 默认选中
            cartMapper.insert(cart);
        } else {
            // 购物车中已存在该商品
            int newQuantity = existingCart.getQuantity() + request.getQuantity();
            // 再次校验累加后的数量是否超过库存
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("库存不足，当前库存：" + product.getStock() + "，购物车已有 " + existingCart.getQuantity() + " 件");
            }
            existingCart.setQuantity(newQuantity);
            cartMapper.updateQuantity(existingCart);

        }
    }

    // 获取购物车列表
    @Override
    public List<CartVO> getCartList(Long userId) {
        return cartMapper.selectCartListWithProduct(userId);
    }

    // 更新购物车选中状态
    @Override
    @Transactional
    public void updateSelect(Long cartId, Long userId, Integer selected) {
        // 查询购物车项是否存在且属于该用户
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new RuntimeException("购物车项不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        // 更新选中状态
        cart.setSelected(selected==1);
        cartMapper.updateSelect(cart);
    }
}
