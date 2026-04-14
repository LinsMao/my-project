package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecentOrderVO {
    private Long id;
    private String orderNo;
    private String userName;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;
}
