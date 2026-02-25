package com.example.VO;

import lombok.Data;

@Data
public class LoginVO {
    private String token;          // JWT token
    private Boolean isNewUser;     // 是否新用户（true表示需要完善资料）
    private UserInfo userInfo;     // 用户信息（老用户返回，新用户可空）

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
        private Integer gender;
    }
}