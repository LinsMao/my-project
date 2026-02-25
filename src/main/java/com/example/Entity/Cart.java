package com.example.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Boolean selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
