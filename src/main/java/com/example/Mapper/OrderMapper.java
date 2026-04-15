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
    
    // Dashboard 统计方法
    Integer countTodayOrders(@Param("merchantId") Long merchantId);
    Integer countYesterdayOrders(@Param("merchantId") Long merchantId);
    Double sumTodaySales(@Param("merchantId") Long merchantId);
    Double sumYesterdaySales(@Param("merchantId") Long merchantId);
    Integer countPendingOrders(@Param("merchantId") Long merchantId);
    List<Orders> selectRecentOrders(@Param("merchantId") Long merchantId, @Param("limit") Integer limit);
    
    // 平台统计方法
    Integer countPlatformTodayOrders();
    Integer countPlatformYesterdayOrders();
    Double sumPlatformTodaySales();
    Double sumPlatformYesterdaySales();
    Integer countPlatformPendingOrders();
    
    // 管理员订单管理
    List<Orders> selectAdminOrders(@Param("orderNo") String orderNo,
                                   @Param("merchantName") String merchantName,
                                   @Param("userPhone") String userPhone,
                                   @Param("orderStatus") Integer orderStatus,
                                   @Param("startTime") String startTime,
                                   @Param("endTime") String endTime,
                                   @Param("offset") int offset,
                                   @Param("size") int size);
    
    int countAdminOrders(@Param("orderNo") String orderNo,
                        @Param("merchantName") String merchantName,
                        @Param("userPhone") String userPhone,
                        @Param("orderStatus") Integer orderStatus,
                        @Param("startTime") String startTime,
                        @Param("endTime") String endTime);
    
    // 根据订单号查询订单（管理员用，不限制商家）
    Orders selectByOrderNoForAdmin(@Param("orderNo") String orderNo);
    
    // 根据订单号查询订单
    Orders selectByOrderNo(@Param("orderNo") String orderNo);
    
    // 根据订单ID查询订单
    Orders selectById(@Param("id") Long id);
    
    // 更新订单评论状态
    void updateReviewStatus(@Param("id") Long id, @Param("isReviewed") Integer isReviewed);
}
