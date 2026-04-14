package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotProductVO {
    private Long id;
    private String name;
    private String mainImage;
    private Integer soldCount;
    private BigDecimal price;
}
