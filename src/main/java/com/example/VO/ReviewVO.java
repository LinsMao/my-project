package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewVO {
    private Long id;
    private Long productId;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String content;
    private Integer isAnonymous;
    private LocalDateTime createTime;
}
