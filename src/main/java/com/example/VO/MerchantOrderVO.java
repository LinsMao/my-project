package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MerchantOrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private String merchantName;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer orderStatus;
    private String deliveryCompany;
    private String deliveryNo;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private String remark;
    
    // 订单项列表
    private List<MerchantOrderItemVO> items;
}
