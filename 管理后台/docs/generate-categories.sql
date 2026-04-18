-- ============================================
-- 服务分类数据生成脚本
-- 生成时间：2026-04-18
-- 功能：插入分类数据并与需求表关联
-- ============================================

-- 设置字符集（防止中文乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 第一步：清空分类表（如果有旧数据）
-- ============================================
TRUNCATE TABLE `categories`;

-- ============================================
-- 第二步：插入分类数据（ID 与需求表 category_id 对应）
-- ============================================

-- 一级分类（id 1-8，与需求表 category_id 对应）
INSERT INTO `categories` (`id`, `name`, `icon`, `parent_id`, `level`, `sort_order`, `is_hot`, `description`, `status`) VALUES
-- id=1: 保洁（需求数 69）
(1, '保洁', '🧹', 0, 1, 100, 1, '家庭日常保洁、深度清洁等服务', 1),
-- id=2: 烹饪（需求数 29）
(2, '烹饪', '🍳', 0, 1, 90, 1, '家常菜、营养餐、月子餐等烹饪服务', 1),
-- id=3: 育儿嫂（需求数 23）
(3, '育儿嫂', '👶', 0, 1, 80, 1, '新生儿护理、婴幼儿照料等服务', 1),
-- id=4: 老人照护（需求数 26）
(4, '老人照护', '🧓', 0, 1, 70, 1, '老人陪护、康复护理等服务', 1),
-- id=5: 搬运（需求数 38）
(5, '搬运', '📦', 0, 1, 60, 0, '搬家、家具搬运、重物搬运等服务', 1),
-- id=6: 宠物服务（需求数 37）
(6, '宠物服务', '🐾', 0, 1, 50, 0, '宠物喂养、遛狗、宠物美容等服务', 1),
-- id=7: 维修服务（需求数 40）
(7, '维修服务', '🔧', 0, 1, 40, 0, '家电维修、水电维修、管道疏通等服务', 1),
-- id=8: 家电清洗（需求数 38）
(8, '家电清洗', '🧼', 0, 1, 30, 0, '空调清洗、油烟机清洗、洗衣机清洗等服务', 1);

-- ============================================
-- 第三步：更新需求数量（与需求表实际数据同步）
-- ============================================

-- 根据需求表统计，更新每个分类的需求数量
UPDATE `categories` SET `demand_count` = (
    SELECT COUNT(*) FROM `demands` WHERE `demands`.`category_id` = `categories`.`id`
) WHERE `id` BETWEEN 1 AND 8;

-- ============================================
-- 第四步：验证结果
-- ============================================

SELECT '服务分类数据生成完成！' as info;

SELECT 
    c.id,
    c.name,
    c.icon,
    c.demand_count,
    c.is_hot,
    c.status
FROM `categories` c
ORDER BY c.id;

-- 验证需求表与分类表的关联是否正确
SELECT 
    c.id as category_id,
    c.name as category_name,
    COUNT(d.id) as demand_count,
    c.demand_count as stored_count
FROM `categories` c
LEFT JOIN `demands` d ON c.id = d.category_id
GROUP BY c.id, c.name, c.demand_count
ORDER BY c.id;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
