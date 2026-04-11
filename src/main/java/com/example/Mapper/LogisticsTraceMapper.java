package com.example.Mapper;

import com.example.Entity.LogisticsTrace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogisticsTraceMapper {
    
    // 批量插入物流轨迹
    int batchInsert(@Param("list") List<LogisticsTrace> list);
    
    // 根据订单号查询物流轨迹（按时间倒序）
    List<LogisticsTrace> selectByOrderNo(@Param("orderNo") String orderNo);
}
