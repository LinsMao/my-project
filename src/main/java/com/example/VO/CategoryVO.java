package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private Integer productCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
