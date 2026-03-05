package com.example.Mapper;

import com.example.Entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrdersMapper {

    // 插入订单
    int insert(Orders order);

    // 根据ID查询订单
    Orders selectById(@Param("id") Long id);

    // 根据订单号查询订单
    Orders selectByOrderNo(@Param("orderNo") String orderNo);

    // 查询用户订单列表（可按状态筛选）
    List<Orders> selectByUserId(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);

    // 更新订单支付状态
    int updatePaymentStatus(Orders order);

    // 更新订单状态
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus, @Param("cancelTime") java.time.LocalDateTime cancelTime);
}
