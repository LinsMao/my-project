package com.example.VO;

import lombok.Data;

@Data
public class AdminProductStatisticsVO {
    private Integer totalProducts;      // 总商品数
    private Integer onSaleProducts;     // 在售商品数
    private Integer offSaleProducts;    // 下架商品数
    private Integer pendingProducts;    // 待审核商品数
}
