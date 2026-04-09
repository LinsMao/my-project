package com.example.DTO.merchant;

import lombok.Data;

@Data
public class MerchantOrderListRequest {
    private Long merchantId;
    private Integer orderStatus;
    private String orderNo;
    private String phone;
    private String startTime;
    private String endTime;
    private Integer page = 1;
    private Integer size = 10;
}
