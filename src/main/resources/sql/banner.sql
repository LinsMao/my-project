-- 轮播图表
CREATE TABLE IF NOT EXISTS banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    image_name VARCHAR(200) NOT NULL COMMENT '图片文件名',
    image_path VARCHAR(200) NOT NULL COMMENT '图片存储路径',
    title VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    subtitle VARCHAR(200) COMMENT '轮播图副标题',
    price VARCHAR(50) COMMENT '价格描述',
    link_url VARCHAR(500) COMMENT '点击跳转链接',
    sort_order INT DEFAULT 0 COMMENT '排序顺序（数字越小越靠前）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 插入示例数据
INSERT INTO banner (image_name, image_path, title, subtitle, price, link_url, sort_order, status) VALUES
('banner1.jpg', 'https://via.placeholder.com/750x400/1a1a1a/ffffff?text=', '鲜活海鲜', '当季新鲜 冷链配送', '39.9', '/pages/category/index?id=4', 0, 1),
('banner2.jpg', 'https://via.placeholder.com/750x400/333333/ffffff?text=', '进口水果', '全球直采 超过初选', '25.8', '/pages/category/index?id=1', 1, 1),
('banner3.jpg', 'https://via.placeholder.com/750x400/666666/ffffff?text=', '有机蔬菜专区', '产地直送 新鲜直达', '12.8', '/pages/category/index?id=2', 2, 1),
('banner4.jpg', 'https://via.placeholder.com/750x400/999999/ffffff?text=', '精品肉类', '冷鲜保鲜 肉质鲜嫩', '28.8', '/pages/category/index?id=3', 3, 0);
