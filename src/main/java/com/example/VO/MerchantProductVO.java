package com.example.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MerchantProductVO {
    private Long id;
    private String name;
    private String mainImage;
    private Integer categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer soldCount;
    private Integer status;
    private Integer isRecommended;
    private LocalDateTime createTime;
}
