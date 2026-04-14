package com.example.Service;

import com.example.DTO.merchant.MerchantOrderListRequest;
import com.example.Entity.LogisticsTrace;
import com.example.VO.DashboardStatsVO;
import com.example.VO.DashboardTodosVO;
import com.example.VO.MerchantOrderVO;
import com.example.VO.OrderVO;
import com.example.VO.RecentOrderVO;

import java.util.List;
import java.util.Map;

public interface OrderService {

    // 创建订单，返回订单号列表（支持拆单）
    List<String> createOrder(Long userId, Long addressId, String remark, List<com.example.DTO.CreateOrderRequest.OrderItemRequest> items);

    // 获取用户订单列表（可按状态筛选）
    List<OrderVO> getOrderList(Long userId, Integer orderStatus);

    // 获取订单详情
    OrderVO getOrderDetail(String orderNo, Long userId);

    // 取消订单
    void cancelOrder(String orderNo, Long userId);
    
    // 确认收货
    void confirmReceipt(String orderNo, Long userId);
    
    // 获取商家订单列表
    Map<String, Object> getMerchantOrders(MerchantOrderListRequest request);
    
    // 获取商家订单详情
    MerchantOrderVO getMerchantOrderDetail(String orderNo, Long merchantId);
    
    // 商家发货
    void deliverOrder(String orderNo, Long merchantId, String deliveryCompany, String deliveryNo);
    
    // 获取物流轨迹
    List<LogisticsTrace> getLogisticsTrace(String orderNo, Long userId);
    
    // Dashboard 相关方法
    DashboardStatsVO getMerchantDashboardStats(Long merchantId);
    DashboardTodosVO getMerchantDashboardTodos(Long merchantId);
    List<RecentOrderVO> getRecentOrders(Long merchantId, Integer limit);
}
