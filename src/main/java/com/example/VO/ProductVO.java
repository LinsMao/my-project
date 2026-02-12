package com.example.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {

//    private Long id;
//    private String name;
//    private String subtitle;
//
//    private BigDecimal price;
//    private BigDecimal originalPrice;
//    private Integer soldCount;
//
//    private String image;   // 这里只放 imageName，不拼完整 URL

    private Long id;
    private String name;
    private String subtitle;

    private BigDecimal price;
    private BigDecimal originalPrice;

    private Integer soldCount;
    private String unit;

    /** 商品主图（不拼 URL） */
    private String image;

    /** 首页标签 */
    private List<String> tagList;
}
