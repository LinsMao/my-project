package com.example.Mapper;

import com.example.Entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductReviewMapper {
    
    void insert(ProductReview review);
    
    ProductReview selectById(Long id);
    
    ProductReview selectByOrderAndProduct(@Param("orderId") Long orderId, @Param("productId") Long productId);
    
    List<ProductReview> selectByProductId(@Param("productId") Long productId, 
                                          @Param("rating") Integer rating,
                                          @Param("offset") Integer offset, 
                                          @Param("size") Integer size);
    
    int countByProductId(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    List<ProductReview> selectAll(@Param("productId") Long productId,
                                   @Param("rating") Integer rating,
                                   @Param("userName") String userName,
                                   @Param("startTime") String startTime,
                                   @Param("endTime") String endTime,
                                   @Param("offset") Integer offset,
                                   @Param("size") Integer size);
    
    int countAll(@Param("productId") Long productId,
                 @Param("rating") Integer rating,
                 @Param("userName") String userName,
                 @Param("startTime") String startTime,
                 @Param("endTime") String endTime);
    
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    void deleteById(Long id);
    
    Double getAverageRating(Long productId);
    
    Long countByRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    void updateMerchantReply(@Param("id") Long id, @Param("merchantReply") String merchantReply);
}
