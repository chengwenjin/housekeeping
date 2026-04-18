@echo off
chcp 65001
set "MYSQL_PATH=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe"
set "SQL_FILE=%~dp0generate-orders-20-users.sql"
set "DB_NAME=jiazheng_miniapp_new"

echo ============================================
echo 正在执行订单数据生成脚本...
echo ============================================
echo.

"%MYSQL_PATH%" -u root --default-character-set=utf8mb4 %DB_NAME% < "%SQL_FILE%"

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo ✅ 订单数据生成成功！
    echo ============================================
) else (
    echo.
    echo ============================================
    echo ❌ 执行失败，错误码: %errorlevel%
    echo ============================================
)

echo.
pause
