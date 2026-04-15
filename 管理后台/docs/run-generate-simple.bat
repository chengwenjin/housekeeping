@echo off
chcp 65001 >nul
D:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe -uroot jiazheng_miniapp < f:\generate-data.sql
pause
