package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProductInteraction {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer viewCount;
    private LocalDateTime lastViewTime;
    private Integer isPurchased;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
