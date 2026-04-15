package com.example.DTO.review;

import lombok.Data;

@Data
public class AddReviewRequest {
    private String orderNo;  // 改为订单号
    private Long productId;
    private Integer rating;
    private String content;
    private Boolean isAnonymous;  // 改为Boolean类型
}
