package com.example.VO;

import com.example.Entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;                    // 订单ID
    private String orderNo;             // 订单号
    private Long merchantId;            // 商户ID
    private String merchantName;        // 商户名称
    private Integer orderStatus;        // 订单状态
    private BigDecimal payAmount;       // 实付金额
    private LocalDateTime createTime;   // 创建时间
    private List<OrderItem> items;      // 订单项列表
}
