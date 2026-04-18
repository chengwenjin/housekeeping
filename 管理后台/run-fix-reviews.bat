@echo off
chcp 65001

set "MYSQL_PATH=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe"
set "SQL_FILE=F:/AiCoding/traeWork/管理后台/fix-reviews.sql"
set "DB_NAME=jiazheng_miniapp_new"

echo ============================================
echo 修复评价数据中文乱码
echo ============================================
echo.

"%MYSQL_PATH%" -u root --default-character-set=utf8mb4 %DB_NAME% -e "source %SQL_FILE%"

echo.
echo 完成！按任意键退出...
pause >nul
