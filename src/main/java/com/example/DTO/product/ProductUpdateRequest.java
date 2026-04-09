package com.example.DTO.product;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private Long id;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImage;
    private Integer categoryId;
    private Integer stock;
    private String unit;
    private BigDecimal weight;
    private Integer shelfLife;
    private String originPlace;
    private String brand;
    private Integer deliveryType;
    private Integer isRecommended;
    private Integer isHot;
    private Integer isNew;
    private Integer status;
}
