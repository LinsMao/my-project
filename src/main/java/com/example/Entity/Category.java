package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private Integer sortOrder;
    private Integer status;
    private Integer productCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
