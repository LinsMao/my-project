package com.example.DTO.user;

import lombok.Data;

@Data
public class UserListRequest {
    private String nickname;    // 昵称（模糊搜索）
    private String phone;       // 手机号（模糊搜索）
    private Integer status;     // 状态：0-禁用, 1-正常
    private Integer pageNum;    // 页码
    private Integer pageSize;   // 每页数量

    public UserListRequest() {
        this.pageNum = 1;
        this.pageSize = 10;
    }
}
