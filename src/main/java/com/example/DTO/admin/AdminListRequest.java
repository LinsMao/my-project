package com.example.DTO.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminListRequest {
    
    private String username;  // 商家名称（模糊查询）
    
    private String phone;     // 联系电话（模糊查询）
    
    private Integer status;   // 状态：0-禁用, 1-启用, null-全部
    
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum;  // 当前页
    
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer pageSize; // 每页条数
}
