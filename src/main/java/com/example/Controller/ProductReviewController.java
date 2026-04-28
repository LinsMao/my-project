package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.review.AddReviewRequest;
import com.example.DTO.review.ReviewListRequest;
import com.example.Service.ProductReviewService;
import com.example.Utils.JwtUtils;
import com.example.VO.ReviewStatsVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    /**
     * 提交评论
     */
    @PostMapping
    public ApiResponse<?> addReview(@RequestBody AddReviewRequest request, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            productReviewService.addReview(userId, request);
            return ApiResponse.success("评论成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查订单是否可以评论
     */
    @GetMapping("/can-review/{orderId}")
    public ApiResponse<Boolean> canReview(@PathVariable Long orderId, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            boolean canReview = productReviewService.canReview(userId, orderId);
            return ApiResponse.success(canReview);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取商品评论列表（用户端）
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Map<String, Object> result = productReviewService.getProductReviews(productId, rating, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取商品评论统计
     */
    @GetMapping("/product/{productId}/stats")
    public ApiResponse<ReviewStatsVO> getReviewStats(@PathVariable Long productId) {
        try {
            ReviewStatsVO stats = productReviewService.getReviewStats(productId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有评论列表（管理端）
     */
    @GetMapping("/admin/list")
    public ApiResponse<Map<String, Object>> getAllReviews(ReviewListRequest request) {
        try {
            Map<String, Object> result = productReviewService.getAllReviews(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新评论状态（管理端）
     */
    @PutMapping("/admin/{id}/status")
    public ApiResponse<?> updateReviewStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            productReviewService.updateReviewStatus(id, status);
            return ApiResponse.success("状态更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除评论（管理端）
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<?> deleteReview(@PathVariable Long id) {
        try {
            productReviewService.deleteReview(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 商家查看自己商品的评论列表
     */
    @GetMapping("/merchant/product/{productId}")
    public ApiResponse<Map<String, Object>> getMerchantProductReviews(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        
        Long merchantId = getCurrentMerchantId(httpRequest);
        if (merchantId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            Map<String, Object> result = productReviewService.getMerchantProductReviews(merchantId, productId, rating, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 商家隐藏/显示评论
     */
    @PutMapping("/merchant/{id}/status")
    public ApiResponse<?> updateReviewStatusByMerchant(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest httpRequest) {
        Long merchantId = getCurrentMerchantId(httpRequest);
        if (merchantId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            productReviewService.updateReviewStatusByMerchant(merchantId, id, status);
            return ApiResponse.success("状态更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 商家回复评论
     */
    @PutMapping("/merchant/{id}/reply")
    public ApiResponse<?> replyReview(@PathVariable Long id, @RequestParam String replyContent, HttpServletRequest httpRequest) {
        Long merchantId = getCurrentMerchantId(httpRequest);
        if (merchantId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            productReviewService.replyReview(merchantId, id, replyContent);
            return ApiResponse.success("回复成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 获取当前商家ID
    private Long getCurrentMerchantId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return null;
        }
        return JwtUtils.getUserIdFromToken(token);
    }

    // 获取当前用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return null;
        }
        return JwtUtils.getUserIdFromToken(token);
    }
}
