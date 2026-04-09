package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductAudit {
    private Long id;                    // 审核记录ID
    private Long productId;             // 商品ID
    private Long merchantId;            // 商家ID
    private Integer auditStatus;        // 审核状态：1-通过，3-拒绝
    private String auditReason;         // 审核意见/拒绝原因
    private Long auditorId;             // 审核人ID（管理员ID）
    private String auditorName;         // 审核人姓名
    private LocalDateTime auditTime;    // 审核时间
    private LocalDateTime createTime;   // 创建时间
}
