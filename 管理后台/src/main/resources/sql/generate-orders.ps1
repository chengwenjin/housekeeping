# ========================================
# 生成 400 条订单测试数据 - PowerShell 脚本
# 执行时间：2026-03-28
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "开始生成 400 条订单测试数据" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# MySQL 配置
$mysqlPath = "C:\Program Files\Laragon\bin\mysql\mysql-8.0.30-winx64\bin\mysql.exe"
$database = "jiazheng_miniapp"
$username = "root"
$password = ""  # 空密码
$sqlFile = "F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\generate_orders.sql"

# 检查 MySQL 路径是否存在
if (!(Test-Path $mysqlPath)) {
    Write-Host "错误：未找到 MySQL，请检查路径：$mysqlPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "如果您使用的是 Laragon，请确认 MySQL 安装路径。" -ForegroundColor Yellow
    Write-Host "如果使用的是其他 MySQL 版本，请修改此脚本中的 mysqlPath 变量。" -ForegroundColor Yellow
    exit 1
}

# 检查 SQL 文件是否存在
if (!(Test-Path $sqlFile)) {
    Write-Host "错误：未找到 SQL 文件：$sqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "MySQL 路径：$mysqlPath" -ForegroundColor Green
Write-Host "数据库名称：$database" -ForegroundColor Green
Write-Host "SQL 文件：$sqlFile" -ForegroundColor Green
Write-Host ""

# 提示用户确认
Write-Host "警告：此操作将向 orders 表插入 400 条数据！" -ForegroundColor Yellow
Write-Host "如果 orders 表已有数据，新数据将会追加。" -ForegroundColor Yellow
Write-Host ""
$response = Read-Host "是否继续？(Y/N)"

if ($response -ne "Y" -and $response -ne "y") {
    Write-Host "操作已取消。" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "正在执行 SQL 脚本..." -ForegroundColor Cyan

# 构建 MySQL 命令
if ($password -eq "") {
    $mysqlArgs = "-u$username", "-D$database", "-e", "source `"$sqlFile`""
} else {
    $mysqlArgs = "-u$username", "-p$password", "-D$database", "-e", "source `"$sqlFile`""
}

try {
    # 执行 SQL 脚本
    $output = & $mysqlPath @mysqlArgs 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "订单数据生成成功！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        
        # 显示输出
        if ($output) {
            Write-Host "执行结果：" -ForegroundColor Cyan
            Write-Host $output -ForegroundColor White
        }
        
        # 查询统计数据
        Write-Host ""
        Write-Host "订单数据统计：" -ForegroundColor Cyan
        
        $statsArgs = "-u$username", "-D$database", "-e", @"
SELECT 
    COUNT(*) AS '总订单数',
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS '待服务',
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS '服务中',
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS '已完成',
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS '已评价',
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS '已取消',
    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS '售后',
    ROUND(AVG(total_amount), 2) AS '平均金额',
    ROUND(SUM(total_amount), 2) AS '总金额'
FROM orders;
"@
        
        & $mysqlPath @statsArgs
        
        Write-Host ""
        Write-Host "最新 10 条订单：" -ForegroundColor Cyan
        
        $latestArgs = "-u$username", "-D$database", "-e", @"
SELECT 
    id,
    order_no AS '订单编号',
    title AS '标题',
    status AS '状态',
    total_amount AS '金额',
    publisher_id AS '发布者',
    taker_id AS '服务者',
    created_at AS '创建时间'
FROM orders
ORDER BY id DESC
LIMIT 10;
"@
        
        & $mysqlPath @latestArgs
        
    } else {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Red
        Write-Host "订单数据生成失败！" -ForegroundColor Red
        Write-Host "========================================" -ForegroundColor Red
        Write-Host ""
        Write-Host "错误信息：" -ForegroundColor Red
        Write-Host $output -ForegroundColor Red
    }
} catch {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "发生异常！" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "异常信息：$_" -ForegroundColor Red
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
Read-Host > $null
