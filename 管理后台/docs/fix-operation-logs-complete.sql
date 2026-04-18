-- ============================================
-- 完整修复操作日志表结构
-- 1. 修改 admin_id 字段允许为 NULL
-- 2. 检查并修复其他可能的字段问题
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;

-- 查看当前表结构
SELECT '=== 修改前的表结构 ===' as info;
DESCRIBE operation_logs;

-- 查看当前数据
SELECT '=== 当前日志数据 ===' as info;
SELECT id, admin_id, username, action, module, method, url, created_at 
FROM operation_logs 
ORDER BY created_at DESC 
LIMIT 20;

-- ============================================
-- 关键修复：修改 admin_id 字段允许为 NULL
-- ============================================
SELECT '=== 开始修改 admin_id 字段 ===' as info;

ALTER TABLE `operation_logs` 
MODIFY COLUMN `admin_id` INT UNSIGNED DEFAULT NULL COMMENT '管理员 ID';

-- ============================================
-- 检查 duration 字段类型
-- Java 中是 Long，数据库中是 INT，需要确认是否有溢出问题
-- ============================================
SELECT '=== 检查 duration 字段 ===' as info;

-- 查看当前 duration 字段的最大和最小值
SELECT 
    MIN(duration) as min_duration,
    MAX(duration) as max_duration,
    AVG(duration) as avg_duration
FROM operation_logs;

-- ============================================
-- 查看修改后的表结构
-- ============================================
SELECT '=== 修改后的表结构 ===' as info;
DESCRIBE operation_logs;

-- ============================================
-- 测试插入一条 admin_id 为 NULL 的日志
-- ============================================
SELECT '=== 测试插入 admin_id 为 NULL 的日志 ===' as info;

INSERT INTO `operation_logs` (
    `admin_id`, 
    `username`, 
    `action`, 
    `module`, 
    `method`, 
    `url`, 
    `ip`, 
    `user_agent`, 
    `request_data`, 
    `response_code`, 
    `response_data`, 
    `duration`, 
    `created_at`
) VALUES (
    NULL, 
    'test_user', 
    'TEST', 
    '测试模块', 
    'GET', 
    '/api/admin/test', 
    '127.0.0.1', 
    'TestBrowser', 
    '{}', 
    200, 
    '{"success":true}', 
    100, 
    NOW()
);

-- 查看插入结果
SELECT '=== 测试插入结果 ===' as info;
SELECT * FROM operation_logs WHERE action = 'TEST';

-- 清理测试数据
DELETE FROM operation_logs WHERE action = 'TEST';

SELECT '=== 操作日志表修复完成！admin_id 现在允许为 NULL。 ===' as info;
