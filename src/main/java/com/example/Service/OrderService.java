package com.example.Service;

import com.example.DTO.merchant.MerchantOrderListRequest;
import com.example.VO.MerchantOrderVO;
import com.example.VO.OrderVO;

import java.util.List;
import java.util.Map;

public interface OrderService {

    // 创建订单，返回订单号列表（支持拆单）
    List<String> createOrder(Long userId, Long addressId, String remark);

    // 获取用户订单列表（可按状态筛选）
    List<OrderVO> getOrderList(Long userId, Integer orderStatus);

    // 取消订单
    void cancelOrder(String orderNo, Long userId);
    
    // 获取商家订单列表
    Map<String, Object> getMerchantOrders(MerchantOrderListRequest request);
    
    // 获取商家订单详情
    MerchantOrderVO getMerchantOrderDetail(String orderNo, Long merchantId);
    
    // 商家发货
    void deliverOrder(String orderNo, Long merchantId, String deliveryCompany, String deliveryNo);
}
