package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminListVO {
    private Long total;              // 总条数
    private List<AdminItem> list;    // 商家列表

    @Data
    public static class AdminItem {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String avatar;
        private String shopName;     // 店铺名称（使用nickname字段）
        private Integer productCount; // 商品数量
        private Integer status;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;
    }
}
