package com.example.DTO.review;

import lombok.Data;

@Data
public class ReviewListRequest {
    private Long productId;
    private Integer rating;
    private String userName;
    private String startTime;
    private String endTime;
    private Integer page = 1;
    private Integer size = 10;
}
