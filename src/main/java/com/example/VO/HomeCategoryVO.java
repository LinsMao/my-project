package com.example.VO;

import lombok.Data;

/**
 * 首页分类展示VO
 */
@Data
public class HomeCategoryVO {
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Integer isHot;
    private Integer productCount;
}
