package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private Integer status;          // 1-正常 0-禁用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String openid;
    private String sessionKey;
    private String unionid;
    private String nickname;
    private String avatar;
    private Integer gender;           // 0-未知 1-男 2-女
    private String phone;
    private LocalDateTime lastLoginAt;
}