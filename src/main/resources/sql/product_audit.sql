-- 商品审核记录表
CREATE TABLE IF NOT EXISTS product_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审核记录ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    audit_status TINYINT NOT NULL COMMENT '审核状态：1-通过，3-拒绝',
    audit_reason VARCHAR(500) COMMENT '审核意见/拒绝原因',
    auditor_id BIGINT NOT NULL COMMENT '审核人ID（管理员ID）',
    auditor_name VARCHAR(50) COMMENT '审核人姓名',
    audit_time DATETIME NOT NULL COMMENT '审核时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_audit_time (audit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品审核记录表';
