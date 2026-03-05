package com.example.Service;

import com.example.VO.OrderVO;

import java.util.List;

public interface OrderService {

    // 创建订单，返回订单号
    String createOrder(Long userId, Long addressId, String remark);

    // 获取用户订单列表（可按状态筛选）
    List<OrderVO> getOrderList(Long userId, Integer orderStatus);

    // 取消订单
    void cancelOrder(String orderNo, Long userId);
}
