package com.example.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders {
    private Long id;                   // 订单ID
    private String orderNo;             // 订单号
    private Long userId;                // 用户ID
    private String userName;            // 用户名（关联查询）
    private Long merchantId;            // 商户ID
    private String merchantName;        // 商户名称（快照）
    private BigDecimal totalAmount;      // 订单总金额
    private BigDecimal payAmount;        // 实付金额
    private BigDecimal freightAmount;    // 运费金额
    private Integer payType;             // 支付方式：1-微信支付
    private Integer payStatus;           // 支付状态：0-待支付，1-已支付，2-支付失败
    private Integer orderStatus;         // 订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-售后中
    private String receiverName;         // 收货人姓名
    private String receiverPhone;        // 收货人电话
    private String receiverAddress;      // 完整收货地址
    private String remark;               // 用户备注
    private String deliveryCompany;      // 物流公司
    private String deliveryNo;           // 物流单号
    private LocalDateTime payTime;       // 支付时间
    private LocalDateTime deliveryTime;  // 发货时间
    private LocalDateTime receiveTime;   // 收货时间
    private LocalDateTime cancelTime;    // 取消时间
    private LocalDateTime createTime;    // 创建时间
    private LocalDateTime updateTime;    // 更新时间
}
