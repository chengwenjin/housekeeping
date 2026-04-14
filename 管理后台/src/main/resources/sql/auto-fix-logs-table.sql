-- ====================================
-- 操作日志表自动修复脚本
-- ====================================
-- 用途：自动检测并修复 operation_logs 表
-- 特点：如果字段已存在则跳过，避免重复执行报错
-- ====================================

-- 使用数据库（请修改为实际数据库名）
-- USE jiazheng;

-- ====================================
-- 步骤 1: 检查并添加 username 字段
-- ====================================

DELIMITER $$

-- 创建存储过程来检查并添加字段
CREATE PROCEDURE IF NOT EXISTS add_username_if_not_exists()
BEGIN
    -- 声明变量
    DECLARE column_count INT DEFAULT 0;
    
    -- 检查 username 字段是否存在
    SELECT COUNT(*) INTO column_count
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'operation_logs'
      AND COLUMN_NAME = 'username';
    
    -- 如果不存在，则添加
    IF column_count = 0 THEN
        ALTER TABLE `operation_logs` 
        ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;
        
        SELECT '✓ 成功添加 username 字段' AS message;
    ELSE
        SELECT '✓ username 字段已存在，跳过' AS message;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL add_username_if_not_exists();

-- 删除存储过程（清理）
DROP PROCEDURE IF EXISTS add_username_if_not_exists;

-- ====================================
-- 步骤 2: 检查并添加索引
-- ====================================

DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS add_username_index_if_not_exists()
BEGIN
    DECLARE index_count INT DEFAULT 0;
    
    -- 检查 idx_username 索引是否存在
    SELECT COUNT(*) INTO index_count
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'operation_logs'
      AND INDEX_NAME = 'idx_username';
    
    -- 如果不存在，则添加
    IF index_count = 0 THEN
        ALTER TABLE `operation_logs` 
        ADD INDEX `idx_username` (`username`);
        
        SELECT '✓ 成功添加 idx_username 索引' AS message;
    ELSE
        SELECT '✓ idx_username 索引已存在，跳过' AS message;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL add_username_index_if_not_exists();

-- 删除存储过程（清理）
DROP PROCEDURE IF EXISTS add_username_index_if_not_exists;

-- ====================================
-- 步骤 3: 显示最终表结构
-- ====================================

SELECT '===== 当前 operation_logs 表结构 =====' AS message;
DESC `operation_logs`;

SELECT '===== 所有索引 =====' AS message;
SHOW INDEX FROM `operation_logs`;

-- ====================================
-- 完成提示
-- ====================================

SELECT '====================================' AS '';
SELECT '✓ 操作日志表结构检查与修复完成！' AS message;
SELECT '====================================' AS '';
SELECT '下一步：重启后端服务并测试接口' AS message;
SELECT '====================================' AS '';
