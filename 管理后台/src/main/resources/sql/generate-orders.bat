@echo off
chcp 65001 >nul
echo ========================================
echo 开始生成 400 条订单测试数据
echo ========================================
echo.

REM MySQL 配置
set MYSQL_USER=root
set MYSQL_PASS=
set MYSQL_DB=jiazheng_miniapp
set SQL_FILE=F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\generate_orders.sql

echo 正在执行 SQL 脚本...
echo.

REM 尝试使用 Laragon 的 MySQL
REM 尝试使用 Laragon 的 MySQL
"D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe" -u%MYSQL_USER% %MYSQL_DB% < "%SQL_FILE%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 订单数据生成成功！
    echo ========================================
    echo.
    
    echo 订单统计：
    "D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe" -u%MYSQL_USER% %MYSQL_DB% -e "SELECT COUNT(*) AS 总订单数 FROM orders;"
) else (
    echo.
    echo ========================================
    echo 订单数据生成失败！请检查 MySQL 是否运行。
    echo ========================================
)

echo.
pause
