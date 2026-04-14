@echo off
chcp 65001 >nul
echo ====================================
echo 批量生成测试数据
echo - 300 个用户
echo - 400 个需求
echo ====================================
echo.

set MYSQL_PATH=D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe
set DB_USER=root
set DB_PASS=
set DB_NAME=jiazheng_miniapp
set SQL_FILE=%~dp0src\main\resources\sql\generate-test-data.sql

echo [步骤 1] 检查 SQL 文件是否存在...
if not exist "%SQL_FILE%" (
    echo ✗ SQL 文件不存在：%SQL_FILE%
    pause
    exit /b 1
)
echo ✓ SQL 文件存在
echo.

echo [步骤 2] 执行 SQL 脚本，这可能需要 30-60 秒...
"%MYSQL_PATH%" -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%SQL_FILE%"
if errorlevel 1 (
    echo ✗ 执行失败，请检查错误信息
    pause
    exit /b 1
)
echo.

echo ====================================
echo ✓ 测试数据生成完成！
echo ====================================
echo.
echo 生成的数据：
echo - 用户数量：300
echo - 需求数量：400
echo.
echo 可以在管理后台查看这些数据
echo.
pause
