package com.example.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {
    private Long id;                // ID
    private String username;         // 用户名
    private String password;         // 密码
    private String email;            // 邮箱
    private String phone;            // 联系电话
    private String nickname;         // 昵称
    private String avatar;           // 头像URL
    private Integer role;            // 角色：1-商家, 2-管理员
    private Integer status;          // 状态：0-禁用, 1-启用
    private LocalDateTime lastLoginAt; // 最后登录时间
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
