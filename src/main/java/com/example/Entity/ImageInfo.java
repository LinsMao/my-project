package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片信息表实体类
 * 对应表：image_info
 */
@Data
public class ImageInfo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 图片文件名（如1435904.jpeg）
     */
    private String fileName;

    /**
     * 图片类型：GOODS-商品图，BANNER-轮播图，AVATAR-用户头像
     */
    private String type;

    /**
     * 关联业务ID（比如商品图关联goods_info.id，头像关联user_info.id）
     */
    private Long relatedId;

    /**
     * 存储目录标识（可选，对应本地文件夹：goods/banner/avatar）
     */
    private String storageDir;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}