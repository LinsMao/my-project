package com.example.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecentMerchantVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private LocalDateTime createTime;
}
