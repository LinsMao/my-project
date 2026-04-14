package com.example.DTO.admin;

import lombok.Data;

@Data
public class AdminOrderListRequest {
    private String orderNo;          // 订单号
    private String merchantName;     // 商家名称
    private String userPhone;        // 用户手机号
    private Integer orderStatus;     // 订单状态
    private String startTime;        // 开始时间
    private String endTime;          // 结束时间
    private Integer page;            // 页码
    private Integer size;            // 每页数量
}
