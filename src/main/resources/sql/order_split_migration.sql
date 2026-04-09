-- 订单拆单功能数据库迁移脚本
-- 功能：支持按商户拆分订单

-- 1. 为 orders 表添加商户相关字段
ALTER TABLE orders 
ADD COLUMN merchant_id BIGINT COMMENT '商户ID',
ADD COLUMN merchant_name VARCHAR(100) COMMENT '商户名称（快照）';

-- 2. 添加索引提升查询性能
ALTER TABLE orders ADD INDEX idx_merchant_id (merchant_id);

-- 3. 为历史订单设置 merchant_id（如果有历史数据）
-- 注意：如果一个订单包含多个商户的商品，只取第一个商户
UPDATE orders o
INNER JOIN (
    SELECT oi.order_id, MAX(p.merchant_id) as merchant_id
    FROM order_item oi
    INNER JOIN product p ON oi.product_id = p.id
    GROUP BY oi.order_id
) AS first_merchant ON o.id = first_merchant.order_id
SET o.merchant_id = first_merchant.merchant_id
WHERE o.merchant_id IS NULL;

-- 4. 为历史订单设置真实的商户名称（从admin表的nickname字段获取）
UPDATE orders o
INNER JOIN admin a ON o.merchant_id = a.id
SET o.merchant_name = a.nickname
WHERE a.role = 1 AND o.merchant_name IS NULL;

-- 5. 验证数据
SELECT 
    COUNT(*) as total_orders,
    COUNT(merchant_id) as orders_with_merchant,
    COUNT(*) - COUNT(merchant_id) as orders_without_merchant
FROM orders;
