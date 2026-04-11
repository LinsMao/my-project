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
    private BigDecimal totalAmount;     // 订单总金额
    private BigDecimal freightAmount;   // 运费
    private String receiverName;        // 收货人
    private String receiverPhone;       // 收货电话
    private String receiverAddress;     // 收货地址
    private String remark;              // 备注
    private String deliveryCompany;     // 物流公司
    private String deliveryNo;          // 物流单号
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime payTime;      // 支付时间
    private LocalDateTime deliveryTime; // 发货时间
    private List<OrderItem> items;      // 订单项列表
}
