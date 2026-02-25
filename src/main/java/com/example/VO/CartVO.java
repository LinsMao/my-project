package com.example.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartVO {
    private Long id;                // 购物车项ID
    private Long productId;          // 商品ID
    private String productName;      // 商品名称
    private String productImage;     // 商品主图（完整URL）
    private BigDecimal price;        // 商品当前售价
    private String unit;             // 商品单位
    private Integer quantity;        // 购物车数量
    private Integer selected;        // 是否选中（0/1）
    private LocalDateTime createTime; // 加入时间
}
