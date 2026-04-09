package com.example.DTO.product;

import lombok.Data;

@Data
public class ProductAuditRequest {
    private Long productId;         // 商品ID
    private Integer auditStatus;    // 审核状态：1-通过，3-拒绝
    private String auditReason;     // 审核意见/拒绝原因
}
