package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserListVO {
    private Long total;
    private List<UserItem> list;

    @Data
    public static class UserItem {
        private Long id;
        private String nickname;
        private String avatar;
        private Integer gender;        // 0-未知 1-男 2-女
        private String phone;
        private Integer status;        // 0-禁用 1-正常
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;
    }
}
