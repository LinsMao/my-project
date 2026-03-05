package com.example.Mapper;

import com.example.Entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    // 插入订单项
    int insert(OrderItem orderItem);

    // 批量插入订单项
    int batchInsert(@Param("list") List<OrderItem> list);

    // 根据订单ID查询订单项列表
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
}
