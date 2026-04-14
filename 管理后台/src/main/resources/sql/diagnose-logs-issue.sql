-- ====================================
-- 操作日志接口问题诊断脚本
-- ====================================
-- 数据库：jiazheng_miniapp
-- 执行前请确保后端服务已启动
-- ====================================

USE jiazheng_miniapp;

-- ====================================
-- 诊断 1: 检查 operation_logs 表是否存在
-- ====================================
SELECT '【诊断 1】检查表是否存在' AS step;
SELECT TABLE_NAME, TABLE_COMMENT 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'jiazheng_miniapp' 
  AND TABLE_NAME = 'operation_logs';

-- ====================================
-- 诊断 2: 检查 username 字段是否存在
-- ====================================
SELECT '【诊断 2】检查 username 字段' AS step;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT, COLUMN_KEY
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'jiazheng_miniapp' 
  AND TABLE_NAME = 'operation_logs' 
ORDER BY ORDINAL_POSITION;

-- ====================================
-- 诊断 3: 检查是否有测试数据
-- ====================================
SELECT '【诊断 3】检查测试数据' AS step;
SELECT COUNT(*) AS total_records FROM operation_logs;

-- 如果有数据，显示最新 5 条
SELECT id, admin_id, username, action, module, created_at 
FROM operation_logs 
ORDER BY created_at DESC 
LIMIT 5;

-- ====================================
-- 诊断 4: 模拟查询（测试 SQL 是否报错）
-- ====================================
SELECT '【诊断 4】模拟后端查询 SQL' AS step;

-- 基础查询（应该成功）
SELECT COUNT(*) AS base_count FROM operation_logs;

-- 带 username 的查询（如果字段不存在会报错）
SELECT COUNT(*) AS with_username_count 
FROM operation_logs 
WHERE username IS NOT NULL;

-- ====================================
-- 修复建议
-- ====================================
SELECT '=====================================' AS '';
SELECT '诊断完成！请查看以上结果' AS message;
SELECT '=====================================' AS '';

-- 如果 username 字段不存在，执行以下 SQL：
SELECT '如果 username 字段不存在，请执行:' AS message;
SELECT 'ALTER TABLE operation_logs ADD COLUMN username VARCHAR(64) DEFAULT NULL COMMENT ''操作人员用户名'' AFTER admin_id;' AS sql_statement;
SELECT 'ALTER TABLE operation_logs ADD INDEX idx_username (username);' AS sql_statement;

SELECT '=====================================' AS '';
SELECT '完整表结构' AS message;
DESC operation_logs;
