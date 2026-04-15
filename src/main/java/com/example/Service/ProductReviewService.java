package com.example.Service;

import com.example.DTO.review.AddReviewRequest;
import com.example.DTO.review.ReviewListRequest;
import com.example.VO.ReviewStatsVO;
import com.example.VO.ReviewVO;

import java.util.List;
import java.util.Map;

public interface ProductReviewService {
    
    /**
     * 提交评论
     */
    void addReview(Long userId, AddReviewRequest request);
    
    /**
     * 检查订单是否可以评论
     */
    boolean canReview(Long userId, Long orderId);
    
    /**
     * 获取商品评论列表（用户端）
     */
    Map<String, Object> getProductReviews(Long productId, Integer rating, Integer page, Integer size);
    
    /**
     * 获取商品评论统计
     */
    ReviewStatsVO getReviewStats(Long productId);
    
    /**
     * 获取所有评论列表（管理端）
     */
    Map<String, Object> getAllReviews(ReviewListRequest request);
    
    /**
     * 更新评论状态（管理端）
     */
    void updateReviewStatus(Long id, Integer status);
    
    /**
     * 删除评论（管理端）
     */
    void deleteReview(Long id);
    
    /**
     * 商家查看自己商品的评论列表
     */
    Map<String, Object> getMerchantProductReviews(Long merchantId, Long productId, Integer rating, Integer page, Integer size);
    
    /**
     * 商家更新评论状态
     */
    void updateReviewStatusByMerchant(Long merchantId, Long reviewId, Integer status);
}
