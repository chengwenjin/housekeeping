-- ====================================
-- ⚡ 快速修复 - 操作日志 400 错误
-- ====================================
-- 数据库：jiazheng_miniapp
-- 执行方式：在 MySQL 客户端中直接运行此脚本
-- ====================================

USE jiazheng_miniapp;

-- ====================================
-- 第 1 步：添加 username 字段（如果不存在）
-- ====================================
ALTER TABLE `operation_logs` 
ADD COLUMN IF NOT EXISTS `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;

-- ====================================
-- 第 2 步：添加索引（如果不存在）
-- ====================================
ALTER TABLE `operation_logs` 
ADD INDEX IF NOT EXISTS `idx_username` (`username`);

-- ====================================
-- 第 3 步：验证表结构
-- ====================================
DESC `operation_logs`;

-- ====================================
-- 第 4 步：测试查询
-- ====================================
SELECT '测试查询:' AS message;
SELECT COUNT(*) AS total_logs FROM operation_logs WHERE username IS NOT NULL;

SELECT '=====================================' AS '';
SELECT '✓ 修复完成！请重启后端服务' AS message;
SELECT '=====================================' AS '';
