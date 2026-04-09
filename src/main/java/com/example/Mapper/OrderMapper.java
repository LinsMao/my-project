package com.example.Mapper;

import com.example.Entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    // 查询商家订单列表（通过订单项关联商品）
    List<Orders> selectMerchantOrders(@Param("merchantId") Long merchantId,
                                      @Param("orderStatus") Integer orderStatus,
                                      @Param("orderNo") String orderNo,
                                      @Param("phone") String phone,
                                      @Param("startTime") String startTime,
                                      @Param("endTime") String endTime,
                                      @Param("offset") int offset,
                                      @Param("size") int size);
    
    // 统计商家订单总数
    int countMerchantOrders(@Param("merchantId") Long merchantId,
                           @Param("orderStatus") Integer orderStatus,
                           @Param("orderNo") String orderNo,
                           @Param("phone") String phone,
                           @Param("startTime") String startTime,
                           @Param("endTime") String endTime);
    
    // 根据订单号和商家ID查询订单（验证权限）
    Orders selectByOrderNoAndMerchantId(@Param("orderNo") String orderNo,
                                        @Param("merchantId") Long merchantId);
    
    // 更新订单发货信息
    int updateDeliveryInfo(@Param("orderNo") String orderNo,
                          @Param("deliveryCompany") String deliveryCompany,
                          @Param("deliveryNo") String deliveryNo);
}
