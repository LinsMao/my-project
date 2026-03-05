package com.example.Service;

public interface PaymentService {

    /**
     * 模拟支付
     * @param orderNo 订单号
     * @param userId 用户ID
     */
    void mockPayment(String orderNo, Long userId);
}
