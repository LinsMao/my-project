package com.example.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminProductVO {
    private Long id;
    private String name;
    private String mainImage;
    private Integer categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer soldCount;
    private Integer viewCount;
    private Integer status;          // 0-下架，1-在售，2-待审核
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
