package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminOrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private Long merchantId;
    private String merchantName;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer orderStatus;
    private Integer paymentMethod;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private String deliveryCompany;
    private String deliveryNo;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime finishTime;
    private List<AdminOrderItemVO> items;
}
