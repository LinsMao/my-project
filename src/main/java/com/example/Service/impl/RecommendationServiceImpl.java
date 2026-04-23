package com.example.Service.impl;

import com.example.Entity.Product;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.UserProductInteractionMapper;
import com.example.Service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private UserProductInteractionMapper interactionMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getRecommendedProducts(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 如果用户未登录或无历史行为，返回热度推荐
        if (userId == null) {
            return getHotProducts(limit);
        }

        // 获取用户购买过的商品（用于过滤）
        List<Long> purchasedProductIds = interactionMapper.selectPurchasedProductIds(userId);

        // 获取用户浏览过的商品
        List<Long> viewedProductIds = interactionMapper.selectViewedProductIds(userId, 20);

        // 如果用户没有浏览历史，返回热度推荐
        if (viewedProductIds == null || viewedProductIds.isEmpty()) {
            return getHotProducts(limit);
        }

        // 个性化推荐：混合策略
        List<Product> recommendations = new ArrayList<>();

        // 1. 基于内容的推荐（同类目商品）- 70%
        int contentBasedCount = (int) (limit * 0.7);
        List<Product> contentBased = getContentBasedRecommendations(userId, purchasedProductIds, contentBasedCount);
        System.out.println("=== 推荐调试 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("基于内容推荐数量: " + contentBased.size());
        if (!contentBased.isEmpty()) {
            System.out.println("推荐商品: " + contentBased.stream().map(Product::getName).collect(Collectors.joining(", ")));
        }
        recommendations.addAll(contentBased);

        // 2. 协同过滤推荐（买了还买）- 15%
        int collaborativeCount = (int) (limit * 0.15);
        List<Product> collaborative = getCollaborativeRecommendations(userId, purchasedProductIds, collaborativeCount);
        System.out.println("协同过滤推荐数量: " + collaborative.size());
        recommendations.addAll(collaborative);

        // 3. 热度推荐（补充）- 15%
        int hotCount = limit - recommendations.size();
        if (hotCount > 0) {
            List<Product> hot = getHotProducts(hotCount);
            // 过滤掉已推荐的商品
            Set<Long> recommendedIds = recommendations.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            hot = hot.stream()
                    .filter(p -> !recommendedIds.contains(p.getId()))
                    .collect(Collectors.toList());
            System.out.println("热度推荐数量: " + hot.size());
            recommendations.addAll(hot);
        }
        
        System.out.println("总推荐数量: " + recommendations.size());
        System.out.println("===============");

        // 去重并限制数量
        return recommendations.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 基于内容的推荐（同类目商品）
     */
    private List<Product> getContentBasedRecommendations(Long userId, List<Long> excludeIds, int limit) {
        // 获取用户浏览过的商品分类
        List<Integer> categoryIds = interactionMapper.selectViewedCategoryIds(userId, 5);
        
        System.out.println("=== 基于内容推荐调试 ===");
        System.out.println("查询到的分类ID: " + categoryIds);
        
        if (categoryIds == null || categoryIds.isEmpty()) {
            System.out.println("没有找到浏览过的分类");
            return new ArrayList<>();
        }

        // 查询相同分类的商品
        List<Product> products = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            List<Product> categoryProducts = productMapper.selectByCategoryId(categoryId.longValue());
            System.out.println("分类 " + categoryId + " 的商品数量: " + (categoryProducts != null ? categoryProducts.size() : 0));
            if (categoryProducts != null) {
                products.addAll(categoryProducts);
            }
        }
        
        System.out.println("查询到的总商品数: " + products.size());

        // 过滤已购买的商品，按销量排序
        List<Product> filtered = products.stream()
                .filter(p -> {
                    boolean hasStatus = p.getStatus() != null && p.getStatus() == 1;
                    if (!hasStatus) {
                        System.out.println("商品 " + p.getName() + " 状态不符: status=" + p.getStatus());
                    }
                    return hasStatus;
                })
                .filter(p -> excludeIds == null || !excludeIds.contains(p.getId()))
                .sorted((a, b) -> {
                    int soldCompare = Integer.compare(
                            b.getSoldCount() != null ? b.getSoldCount() : 0,
                            a.getSoldCount() != null ? a.getSoldCount() : 0
                    );
                    if (soldCompare != 0) return soldCompare;
                    return Long.compare(b.getId(), a.getId());
                })
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
        
        System.out.println("过滤后的商品数: " + filtered.size());
        System.out.println("=======================");
        
        return filtered;
    }

    /**
     * 协同过滤推荐（买了还买）
     */
    private List<Product> getCollaborativeRecommendations(Long userId, List<Long> excludeIds, int limit) {
        // 获取用户购买过的商品
        List<Long> purchasedProductIds = interactionMapper.selectPurchasedProductIds(userId);
        
        if (purchasedProductIds == null || purchasedProductIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查找购买过相同商品的其他用户
        List<Long> similarUsers = interactionMapper.selectSimilarUsers(userId, purchasedProductIds, 50);
        
        if (similarUsers == null || similarUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取这些用户购买的其他商品
        List<Long> recommendedProductIds = interactionMapper.selectSimilarUsersPurchasedProducts(
                similarUsers, excludeIds, limit
        );

        if (recommendedProductIds == null || recommendedProductIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询商品详情
        return recommendedProductIds.stream()
                .map(productMapper::selectById)
                .filter(Objects::nonNull)
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 热度推荐（销量 + 评分）
     */
    private List<Product> getHotProducts(int limit) {
        // 查询热销商品
        List<Product> hotProducts = productMapper.selectHotProducts(null, limit * 2);
        
        if (hotProducts == null || hotProducts.isEmpty()) {
            // 如果没有热销数据，返回最新商品
            return productMapper.selectHomeProductPage(0, limit);
        }

        // 按综合得分排序：销量 * 0.7 + 浏览量 * 0.3
        return hotProducts.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .sorted((a, b) -> {
                    double scoreA = (a.getSoldCount() != null ? a.getSoldCount() : 0) * 0.7 +
                                   (a.getViewCount() != null ? a.getViewCount() : 0) * 0.3;
                    double scoreB = (b.getSoldCount() != null ? b.getSoldCount() : 0) * 0.7 +
                                   (b.getViewCount() != null ? b.getViewCount() : 0) * 0.3;
                    return Double.compare(scoreB, scoreA);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void recordView(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return;
        }
        interactionMapper.insertOrUpdateView(userId, productId);
    }

    @Override
    public void recordPurchase(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return;
        }
        interactionMapper.markAsPurchased(userId, productId);
    }
}
