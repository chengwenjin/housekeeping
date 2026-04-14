-- 修复 admins 表缺失的字段
-- 执行时间：2026-03-27

-- 添加 phone 字段（手机号）
ALTER TABLE `admins` 
ADD COLUMN `phone` varchar(20) DEFAULT NULL COMMENT '手机号' AFTER `avatar`;

-- 添加 email 字段（邮箱）
ALTER TABLE `admins` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 插入默认管理员账号（用户名：admin, 密码：admin123）
-- 注意：实际生产中密码需要加密存储
INSERT INTO `admins` (`username`, `password`, `real_name`, `avatar`, `phone`, `email`, `role_id`, `status`, `created_at`, `updated_at`) 
VALUES ('admin', 'admin123', '系统管理员', '', '', '', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `username` = `username`;
