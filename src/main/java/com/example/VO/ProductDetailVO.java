package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImage;
    private String image;  // 小程序使用，与mainImage相同
    private Integer categoryId;
    private Long merchantId;
    private String merchantName;  // 商家名称
    private Integer stock;
    private Integer soldCount;
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
    private List<String> tagList;  // 标签列表
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
