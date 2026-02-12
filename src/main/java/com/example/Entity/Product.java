package com.example.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;
    private String name;
    private String subtitle;
    private String description;

    private BigDecimal price;
    private BigDecimal originalPrice;

    private String mainImage;

    private Integer categoryId;
    private Long merchantId;

    private Integer stock;
    private Integer soldCount;
    private Integer viewCount;

    private String unit;
    private BigDecimal weight;
    private Integer shelfLife;
    private String originPlace;
    private String brand;

    private Integer deliveryType;

    private Integer minPurchase;
    private Integer maxPurchase;

    private Integer isRecommended;
    private Integer isHot;
    private Integer isNew;

    private Integer status;
    private Integer sortOrder;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
