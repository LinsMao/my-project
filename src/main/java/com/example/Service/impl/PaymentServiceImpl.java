package com.example.Service.impl;

import com.example.Entity.Orders;
import com.example.Mapper.OrdersMapper;
import com.example.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @Transactional
    public void mockPayment(String orderNo, Long userId) {
        // 1. 查询订单
        Orders order = ordersMapper.selectByOrderNo(orderNo);
        
        // 2. 验证订单
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        
        if (order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }
        
        // 3. 更新订单状态
        order.setPayStatus(1); // 已支付
        order.setOrderStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now()); // 支付时间
        
        ordersMapper.updatePaymentStatus(order);
    }
}
