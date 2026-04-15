package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductReview {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String content;
    private Integer isAnonymous;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
