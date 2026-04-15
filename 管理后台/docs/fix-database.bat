@echo off
chcp 65001 >nul
echo ====================================
echo 操作日志表结构检查与修复
echo 数据库：jiazheng_miniapp
echo ====================================
echo.

set MYSQL_PATH=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe
set DB_USER=root
set DB_PASS=
set DB_NAME=jiazheng_miniapp

echo [步骤 1] 检查 operation_logs 表是否存在...
"%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT COUNT(*) AS table_exists FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '%DB_NAME%' AND TABLE_NAME = 'operation_logs';"
if errorlevel 1 (
    echo ✗ 无法连接数据库或表不存在
    echo 请确保:
    echo 1. MySQL 服务已启动
    echo 2. 数据库 %DB_NAME% 已创建
    pause
    exit /b 1
)
echo ✓ 表存在
echo.

echo [步骤 2] 检查 username 字段是否存在...
"%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT COUNT(*) AS column_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '%DB_NAME%' AND TABLE_NAME = 'operation_logs' AND COLUMN_NAME = 'username';" > temp_check.txt
findstr /c:"0" temp_check.txt >nul
if not errorlevel 1 (
    echo ✗ username 字段不存在，正在添加...
    "%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "ALTER TABLE `operation_logs` ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;"
    if errorlevel 1 (
        echo ✗ 添加字段失败
        del temp_check.txt
        pause
        exit /b 1
    )
    echo ✓ username 字段添加成功
) else (
    echo ✓ username 字段已存在
)
del temp_check.txt
echo.

echo [步骤 3] 添加 idx_username 索引...
"%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "ALTER TABLE `operation_logs` ADD INDEX `idx_username` (`username`);" 2>nul
if errorlevel 1 (
    echo 索引可能已存在，跳过
) else (
    echo ✓ 索引添加成功
)
echo.

echo [步骤 4] 验证表结构...
"%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "DESC operation_logs;"
echo.

echo ====================================
echo ✓ 数据库修复完成！
echo ====================================
echo.
echo 下一步操作：
echo 1. 重启后端服务（停止当前服务，重新启动）
echo 2. 访问 http://localhost:8080/api/admin/logs?page=1^&pageSize=10 测试
echo.
pause
