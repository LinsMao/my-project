package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantOrderItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private String unit;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
}
