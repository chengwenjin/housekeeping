@echo off
chcp 65001
cd /d D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin
mysql -u root --default-character-set=utf8mb4 < "F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\init.sql"
pause
