package com.example.DTO;

import lombok.Data;
import java.util.List;

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
    
    /**
     * 商品列表（立即购买时使用，为空则从购物车读取）
     */
    private List<OrderItemRequest> items;
    
    /**
     * 订单商品项
     */
    @Data
    public static class OrderItemRequest {
        /**
         * 商品ID
         */
        private Long productId;
        
        /**
         * 购买数量
         */
        private Integer quantity;
    }
}
