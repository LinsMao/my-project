package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminOrderItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private String unit;
    private BigDecimal totalAmount;
}
