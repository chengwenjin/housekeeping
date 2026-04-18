-- ============================================
-- 修复 operation_logs 表的 admin_id 字段约束
-- 允许 admin_id 为 NULL，这样即使是匿名操作（如登录）也能记录日志
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;

-- 查看当前表结构
DESCRIBE operation_logs;

-- 修改 admin_id 字段，允许为 NULL
ALTER TABLE `operation_logs` 
MODIFY COLUMN `admin_id` INT UNSIGNED DEFAULT NULL COMMENT '管理员 ID';

-- 查看修改后的表结构
DESCRIBE operation_logs;

SELECT '操作日志表修改完成！admin_id 现在允许为 NULL。' as info;
