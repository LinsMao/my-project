package com.example.Service.impl;

import com.example.DTO.review.AddReviewRequest;
import com.example.DTO.review.ReviewListRequest;
import com.example.Entity.Orders;
import com.example.Entity.ProductReview;
import com.example.Entity.User;
import com.example.Mapper.OrderMapper;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.ProductReviewMapper;
import com.example.Mapper.UserMapper;
import com.example.Service.ProductReviewService;
import com.example.VO.ReviewStatsVO;
import com.example.VO.ReviewVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public void addReview(Long userId, AddReviewRequest request) {
        // 1. 验证订单是否存在且属于当前用户
        Orders order = orderMapper.selectByOrderNo(request.getOrderNo());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权评论此订单");
        }

        // 2. 验证订单状态（必须是已完成）
        if (order.getOrderStatus() != 3) {
            throw new RuntimeException("订单未完成，无法评论");
        }

        // 3. 检查是否已经评论过
        ProductReview existingReview = productReviewMapper.selectByOrderAndProduct(
            order.getId(), request.getProductId()
        );
        if (existingReview != null) {
            throw new RuntimeException("该商品已评论过");
        }

        // 4. 验证评论内容长度
        if (request.getContent() == null || request.getContent().trim().length() < 5) {
            throw new RuntimeException("评论内容不能少于5个字符");
        }
        if (request.getContent().length() > 500) {
            throw new RuntimeException("评论内容不能超过500个字符");
        }

        // 5. 获取用户信息
        User user = userMapper.findById(userId);
        String userName = user != null && user.getNickname() != null ? user.getNickname() : "用户" + userId;

        // 6. 创建评论
        ProductReview review = new ProductReview();
        review.setOrderId(order.getId());
        review.setProductId(request.getProductId());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setRating(request.getRating());
        review.setContent(request.getContent().trim());
        review.setIsAnonymous(request.getIsAnonymous() != null && request.getIsAnonymous() ? 1 : 0);
        review.setStatus(1); // 默认显示

        productReviewMapper.insert(review);

        // 7. 更新订单的评论状态
        orderMapper.updateReviewStatus(order.getId(), 1);
    }

    @Override
    public boolean canReview(Long userId, Long orderId) {
        // 1. 查询订单
        Orders order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }

        // 2. 订单必须是已完成状态
        if (order.getOrderStatus() != 3) {
            return false;
        }

        // 3. 检查是否已评论
        // 注意：这里需要检查订单中的所有商品是否都已评论
        // 简化处理：如果订单标记为已评论，则不能再评论
        return order.getIsReviewed() == null || order.getIsReviewed() == 0;
    }

    @Override
    public Map<String, Object> getProductReviews(Long productId, Integer rating, Integer page, Integer size) {
        int offset = (page - 1) * size;
        
        List<ProductReview> reviews = productReviewMapper.selectByProductId(productId, rating, offset, size);
        int total = productReviewMapper.countByProductId(productId, rating);

        // 转换为VO
        List<ReviewVO> reviewVOs = reviews.stream().map(review -> {
            ReviewVO vo = new ReviewVO();
            BeanUtils.copyProperties(review, vo);
            
            // 如果是匿名评论，隐藏用户名
            if (review.getIsAnonymous() == 1) {
                vo.setUserName("匿名用户");
            }
            
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", reviewVOs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));

        return result;
    }

    @Override
    public ReviewStatsVO getReviewStats(Long productId) {
        ReviewStatsVO stats = new ReviewStatsVO();
        
        // 总评论数
        int totalCount = productReviewMapper.countByProductId(productId, null);
        stats.setTotalCount((long) totalCount);
        
        // 平均评分
        Double avgRating = productReviewMapper.getAverageRating(productId);
        stats.setAverageRating(avgRating != null ? avgRating : 0.0);
        
        // 各星级数量
        stats.setFiveStarCount(productReviewMapper.countByRating(productId, 5));
        stats.setFourStarCount(productReviewMapper.countByRating(productId, 4));
        stats.setThreeStarCount(productReviewMapper.countByRating(productId, 3));
        stats.setTwoStarCount(productReviewMapper.countByRating(productId, 2));
        stats.setOneStarCount(productReviewMapper.countByRating(productId, 1));
        
        return stats;
    }

    @Override
    public Map<String, Object> getAllReviews(ReviewListRequest request) {
        int offset = (request.getPage() - 1) * request.getSize();
        
        List<ProductReview> reviews = productReviewMapper.selectAll(
            request.getProductId(),
            request.getRating(),
            request.getUserName(),
            request.getStartTime(),
            request.getEndTime(),
            offset,
            request.getSize()
        );
        
        int total = productReviewMapper.countAll(
            request.getProductId(),
            request.getRating(),
            request.getUserName(),
            request.getStartTime(),
            request.getEndTime()
        );

        // 转换为VO
        List<ReviewVO> reviewVOs = reviews.stream().map(review -> {
            ReviewVO vo = new ReviewVO();
            BeanUtils.copyProperties(review, vo);
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", reviewVOs);
        result.put("total", total);
        result.put("page", request.getPage());
        result.put("size", request.getSize());
        result.put("totalPages", (int) Math.ceil((double) total / request.getSize()));

        return result;
    }

    @Override
    public void updateReviewStatus(Long id, Integer status) {
        ProductReview review = productReviewMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }
        
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值无效");
        }
        
        productReviewMapper.updateStatus(id, status);
    }

    @Override
    public void deleteReview(Long id) {
        ProductReview review = productReviewMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }
        
        productReviewMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getMerchantProductReviews(Long merchantId, Long productId, Integer rating, Integer page, Integer size) {
        // 验证商品是否属于该商家
        com.example.Entity.Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权查看该商品评论");
        }

        // 查询评论列表（包括隐藏的）
        int offset = (page - 1) * size;
        List<ProductReview> reviews = productReviewMapper.selectByProductId(productId, rating, offset, size);
        int total = productReviewMapper.countByProductId(productId, rating);

        // 转换为VO
        List<ReviewVO> reviewVOs = reviews.stream().map(review -> {
            ReviewVO vo = new ReviewVO();
            BeanUtils.copyProperties(review, vo);
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", reviewVOs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));

        return result;
    }

    @Override
    public void updateReviewStatusByMerchant(Long merchantId, Long reviewId, Integer status) {
        // 查询评论
        ProductReview review = productReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评论不存在");
        }

        // 验证商品是否属于该商家
        com.example.Entity.Product product = productMapper.selectById(review.getProductId());
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该评论");
        }

        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值无效");
        }

        productReviewMapper.updateStatus(reviewId, status);
    }
}
