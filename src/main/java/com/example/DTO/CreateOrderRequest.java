package com.example.DTO;

import lombok.Data;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 收货地址ID
     */
    private Long addressId;
    
    /**
     * 订单备注（可选）
     */
    private String remark;
}
