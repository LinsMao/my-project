package com.example.Service;

import com.example.Entity.Product;

import java.util.List;

public interface RecommendationService {
    
    /**
     * 获取推荐商品列表
     * @param userId 用户ID（可能为null）
     * @param limit 返回数量
     * @return 推荐商品列表
     */
    List<Product> getRecommendedProducts(Long userId, Integer limit);
    
    /**
     * 记录用户浏览商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void recordView(Long userId, Long productId);
    
    /**
     * 记录用户购买商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void recordPurchase(Long userId, Long productId);
}
