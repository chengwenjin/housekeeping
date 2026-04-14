-- ====================================
-- 操作日志表快速修复脚本
-- ====================================
-- 用途：检查并修复 operation_logs 表结构
-- 执行前请确保已停止后端服务
-- ====================================

USE your_database_name;  -- 请替换为实际数据库名

-- 步骤 1: 检查 username 字段是否存在
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'your_database_name'  -- 请替换为实际数据库名
  AND TABLE_NAME = 'operation_logs' 
  AND COLUMN_NAME = 'username';

-- 如果上述查询无结果，说明需要添加 username 字段

-- 步骤 2: 添加 username 字段（如果不存在）
ALTER TABLE `operation_logs` 
ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;

-- 步骤 3: 添加索引
ALTER TABLE `operation_logs` 
ADD INDEX `idx_username` (`username`);

-- 步骤 4: 验证表结构
DESC `operation_logs`;

-- ====================================
-- 使用说明：
-- 1. 将 'your_database_name' 替换为实际数据库名
-- 2. 在 MySQL 客户端中执行此脚本
-- 3. 查看步骤 4 的输出，确认 username 字段已添加
-- 4. 重启后端服务
-- ====================================
