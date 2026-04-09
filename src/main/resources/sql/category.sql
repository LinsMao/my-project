-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序，数字越小越靠前',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `product_count` INT DEFAULT 0 COMMENT '商品数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 插入初始分类数据
INSERT INTO `category` (`name`, `sort_order`, `status`) VALUES
('水果', 1, 1),
('蔬菜', 2, 1),
('肉类', 3, 1),
('海鲜', 4, 1),
('粮油', 5, 1),
('乳品', 6, 1);
