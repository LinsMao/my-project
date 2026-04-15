package com.example.VO;

import lombok.Data;

@Data
public class ReviewStatsVO {
    private Long totalCount;
    private Double averageRating;
    private Double goodRate;
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;
}
