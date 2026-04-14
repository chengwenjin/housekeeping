-- 操作日志表结构升级脚本
-- 执行时间：2026-03-26
-- 说明：添加 username 字段用于记录操作人员用户名

-- 添加 username 字段
ALTER TABLE `operation_logs` 
ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;

-- 添加 username 索引
ALTER TABLE `operation_logs` 
ADD INDEX `idx_username` (`username`);
