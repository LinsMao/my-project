package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String subtitle;
    private String description;          // 商品详情（副文本）
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;                 // 主图（不拼URL）
    private Integer soldCount;
    private Integer stock;
    private String unit;
    private List<String> tagList;         // 标签：推荐、热销、新品
    private String originPlace;
    private String brand;
    private Integer shelfLife;             // 保质期（天）
    private Integer deliveryType;          // 配送方式：1普通 2冷链
    private Integer minPurchase;           // 最小购买量
    private Integer maxPurchase;           // 最大购买量（可为null）
}