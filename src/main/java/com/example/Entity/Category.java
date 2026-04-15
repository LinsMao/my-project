package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private String icon;              // 分类图标URL
    private String description;       // 分类描述
    private Integer isHot;            // 是否热销：1-是，0-否
    private Integer isShowHome;       // 是否在首页显示：1-显示，0-不显示
    private Integer sortOrder;
    private Integer status;
    private Integer productCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
