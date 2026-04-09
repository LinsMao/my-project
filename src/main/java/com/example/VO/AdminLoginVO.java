package com.example.VO;


import lombok.Data;

@Data
public class AdminLoginVO {
    private String token;          // JWT token
    private AdminInfo adminInfo;   // 管理员信息

    @Data
    public static class AdminInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private Integer role;      // 1-商家, 2-管理员
    }
}
