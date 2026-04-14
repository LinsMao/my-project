package com.example.DTO.merchant;

import lombok.Data;

@Data
public class MerchantProductListRequest {
    private Long merchantId;
    private String name;
    private Integer categoryId;
    private Integer status;
    private Integer page = 1;
    private Integer size = 12;
    private String sortField;  // 排序字段：price, stock, soldCount
    private String sortOrder;  // 排序方向：asc, desc
    private String startTime;  // 创建时间起始
    private String endTime;    // 创建时间截止
}
