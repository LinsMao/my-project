package com.example.DTO.category;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
