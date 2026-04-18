@echo off
chcp 65001 >nul

set MYSQL=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe
set DB=jiazheng_miniapp_new
set SQL_FILE=F:\AiCoding\traeWork\管理后台\docs\fix-reviews-v2.sql

echo ============================================
echo 修复评价数据中文乱码
echo ============================================
echo.

echo 正在执行 SQL 脚本...
"%MYSQL%" -u root --default-character-set=utf8mb4 %DB% < "%SQL_FILE%"

echo.
echo ============================================
echo 完成！
echo ============================================
pause
