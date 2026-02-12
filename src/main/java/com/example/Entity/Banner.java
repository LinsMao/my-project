package com.example.Entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体类
 */
@Data
public class Banner {
    private Long id;                    // 主键ID
    private String imageName;           // 图片文件名
    private String imagePath;           // 图片存储路径（如：banner-images/）
    private String title;               // 轮播图标题
    private String subtitle;            // 轮播图副标题
    private String price;               // 价格描述
    private String linkUrl;             // 点击跳转链接
    private Integer sortOrder;          // 排序
    private Integer status;             // 状态：0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}