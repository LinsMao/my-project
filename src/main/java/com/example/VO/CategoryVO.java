package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Integer isHot;
    private Integer isShowHome;
    private Integer sortOrder;
    private Integer status;
    private Integer productCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
