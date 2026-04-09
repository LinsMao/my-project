package com.example.VO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品审核记录VO
 */
@Data
public class ProductAuditVO {
    private Long id;                    // 审核记录ID
    private Long productId;             // 商品ID
    private String productName;         // 商品名称
    private String productImage;        // 商品图片
    private BigDecimal productPrice;    // 商品价格
    private Integer productStock;       // 商品库存
    private String categoryName;        // 商品分类
    private Long merchantId;            // 商家ID
    private String merchantName;        // 商家名称
    private Integer auditStatus;        // 审核状态：1-通过，3-拒绝
    private String auditReason;         // 审核意见/拒绝原因
    private Long auditorId;             // 审核人ID
    private String auditorName;         // 审核人姓名
    private LocalDateTime auditTime;    // 审核时间
    private LocalDateTime submitTime;   // 提交时间（商品创建时间）
    private LocalDateTime createTime;   // 创建时间
}
