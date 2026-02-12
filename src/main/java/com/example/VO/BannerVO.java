package com.example.VO;

import lombok.Data;

@Data
public class BannerVO {
    private String image;       // 完整的图片URL
    private String title;       // 标题
    private String subtitle;    // 副标题
    private String price;       // 价格
    private String linkUrl;     // 跳转链接
}
