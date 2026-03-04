package com.example.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long id;                // 订单项ID
    private Long orderId;            // 订单ID
    private Long productId;          // 商品ID
    private String productName;      // 商品名称（快照）
    private String productImage;     // 商品主图（快照）
    private String unit;             // 单位（快照）
    private BigDecimal price;        // 购买时的单价（快照）
    private Integer quantity;        // 购买数量
    private BigDecimal totalAmount;  // 小计金额
    private LocalDateTime createTime; // 创建时间
}
