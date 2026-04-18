@echo off
chcp 65001
setlocal enabledelayedexpansion

set "MYSQL_PATH=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe"
set "SQL_FILE=%~dp0docs\generate-reviews-for-orders.sql"
set "DB_NAME=jiazheng_miniapp_new"

echo ============================================
echo 修复评价数据中文乱码问题
echo ============================================
echo.

echo [1/3] 清空旧的评价数据...
"%MYSQL_PATH%" -u root --default-character-set=utf8mb4 %DB_NAME% -e "SET NAMES utf8mb4; DELETE FROM reviews WHERE id > 0;"

if %errorlevel% neq 0 (
    echo ❌ 清空数据失败！
    pause
    exit /b 1
)
echo ✅ 已清空旧评价数据
echo.

echo [2/3] 重新生成评价数据...

"%MYSQL_PATH%" -u root --default-character-set=utf8mb4 --init-command="SET NAMES utf8mb4;" %DB_NAME% < "%SQL_FILE%"

if %errorlevel% neq 0 (
    echo ❌ 执行 SQL 失败！
    pause
    exit /b 1
)
echo ✅ 评价数据已生成
echo.

echo [3/3] 验证数据...
"%MYSQL_PATH%" -u root --default-character-set=utf8mb4 %DB_NAME% -e "SET NAMES utf8mb4; SELECT id, order_id, rating, LEFT(content, 30) as content_preview FROM reviews ORDER BY id DESC LIMIT 10;"

echo.
echo ============================================
echo ✅ 完成！
echo ============================================
pause
