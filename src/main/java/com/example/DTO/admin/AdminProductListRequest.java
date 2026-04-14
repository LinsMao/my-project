package com.example.DTO.admin;

import lombok.Data;

@Data
public class AdminProductListRequest {
    private String productName;      // 商品名称
    private String merchantName;     // 商家名称
    private Integer categoryId;      // 分类ID
    private Integer status;          // 状态：0-下架，1-在售，2-待审核
    private String startTime;        // 开始时间
    private String endTime;          // 结束时间
    private String sortField;        // 排序字段
    private String sortOrder;        // 排序方式：asc/desc
    private Integer page;            // 页码
    private Integer size;            // 每页数量
}
