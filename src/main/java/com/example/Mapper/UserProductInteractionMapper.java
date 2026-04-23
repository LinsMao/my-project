package com.example.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProductInteractionMapper {
    
    /**
     * 插入或更新浏览记录
     */
    void insertOrUpdateView(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 标记为已购买
     */
    void markAsPurchased(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 获取用户购买过的商品ID列表
     */
    List<Long> selectPurchasedProductIds(@Param("userId") Long userId);
    
    /**
     * 获取用户浏览过的商品ID列表
     */
    List<Long> selectViewedProductIds(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 获取用户浏览过的商品分类ID列表
     */
    List<Integer> selectViewedCategoryIds(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 查找购买过相同商品的其他用户
     */
    List<Long> selectSimilarUsers(@Param("userId") Long userId, 
                                   @Param("productIds") List<Long> productIds, 
                                   @Param("limit") Integer limit);
    
    /**
     * 获取相似用户购买的其他商品
     */
    List<Long> selectSimilarUsersPurchasedProducts(@Param("userIds") List<Long> userIds,
                                                     @Param("excludeIds") List<Long> excludeIds,
                                                     @Param("limit") Integer limit);
}
