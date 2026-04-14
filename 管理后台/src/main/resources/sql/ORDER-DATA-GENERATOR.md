# 订单测试数据生成指南

## 📋 功能说明

本工具用于批量生成 400 条订单测试数据，包含以下特点：

### 数据特性
- ✅ **订单编号**: 唯一且有序（格式：ORD+ 时间戳 + 序号）
- ✅ **关联数据**: 随机关联需求和用户（1-300 号用户）
- ✅ **服务分类**: 5 种分类随机分配（保洁、烹饪、育儿嫂、老人照护等）
- ✅ **价格范围**: 50-500 元，随机时长 2-10 小时
- ✅ **订单状态**: 6 种状态按合理比例分布
  - 待服务：15%
  - 服务中：20%
  - 已完成：40%
  - 已评价：15%
  - 已取消：7%
  - 退款/售后：3%
- ✅ **支付状态**: 90% 已支付，10% 未支付
- ✅ **地址信息**: 深圳市 7 个区随机分配
- ✅ **时间逻辑**: 服务时间、支付时间、完成时间等符合业务逻辑

---

## 🚀 使用方法

### 方法一：使用 PowerShell 脚本（推荐）

#### 步骤 1: 打开 PowerShell
在文件资源管理器中右键 → "在终端中打开" 或直接运行 PowerShell

#### 步骤 2: 导航到 SQL 目录
```powershell
cd F:\AiCoding\家政小程序\管理后台\src\main\resources\sql
```

#### 步骤 3: 执行脚本
```powershell
.\generate-orders.ps1
```

#### 步骤 4: 确认执行
输入 `Y` 确认生成数据

---

### 方法二：直接使用 MySQL 命令

#### 步骤 1: 打开命令行
```bash
# Windows CMD 或 PowerShell
cd C:\Program Files\Laragon\bin\mysql\mysql-8.0.30-winx64\bin
```

#### 步骤 2: 执行 SQL 脚本
```bash
mysql -u root -D jiazheng_miniapp < "F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\generate_orders.sql"
```

或使用 Laragon 的 MySQL：
```bash
mysql -u root jiazheng_miniapp < "F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\generate_orders.sql"
```

---

## 📊 验证数据

### 方式 1: 查看统计信息
执行后会自动显示订单统计：
```sql
SELECT 
    COUNT(*) AS 总订单数,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS 待服务,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS 服务中,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS 已完成,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS 已评价,
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS 已取消,
    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS 售后
FROM orders;
```

### 方式 2: 前端页面查看
1. 访问管理后台：http://localhost:3003
2. 登录系统（admin / admin123）
3. 进入 "订单管理" → "订单列表"
4. 查看分页和数据

### 方式 3: 数据库查询
```sql
-- 查看最新 10 条订单
SELECT * FROM orders ORDER BY id DESC LIMIT 10;

-- 查看订单总数
SELECT COUNT(*) FROM orders;

-- 查看状态分布
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status;
```

---

## ⚠️ 注意事项

### 1. 数据追加
- 脚本会**追加**数据而不是清空现有数据
- 如果需要重新生成，请先手动清空：
  ```sql
  TRUNCATE TABLE orders;
  ```

### 2. 外键依赖
- 订单会关联 `demand_id` (1-400)
- 订单会关联 `publisher_id` 和 `taker_id` (1-300)
- 确保用户表和需求表有足够的测试数据

### 3. 执行时间
- 生成 400 条数据大约需要 5-10 秒
- 执行过程中请勿中断

### 4. 路径配置
如果 MySQL 不在默认路径，请修改 PowerShell 脚本中的 `$mysqlPath`：
```powershell
$mysqlPath = "你的 MySQL 路径\mysql.exe"
```

---

## 🔧 自定义配置

### 修改数据量
编辑 `generate_orders.sql`，修改循环次数：
```sql
WHILE i <= 400 DO  -- 改为需要的数量
```

### 修改状态分布
编辑 `generate_orders.sql`，调整状态概率：
```sql
SET status = CASE 
    WHEN RAND() < 0.15 THEN 1  -- 待服务 15%
    WHEN RAND() < 0.35 THEN 2  -- 服务中 20%
    WHEN RAND() < 0.75 THEN 3  -- 已完成 40%
    WHEN RAND() < 0.90 THEN 4  -- 已评价 15%
    WHEN RAND() < 0.97 THEN 5  -- 已取消 7%
    ELSE 6                      -- 售后 3%
END;
```

### 修改价格范围
编辑 `generate_orders.sql`：
```sql
SET actual_price = ROUND(50 + RAND() * 450, 2);  -- 50-500 元
```

---

## 📝 文件清单

```
管理后台/src/main/resources/sql/
├── generate_orders.sql      # SQL 存储过程脚本
├── generate-orders.ps1      # PowerShell 执行脚本
└── README.md               # 本文档
```

---

## 🐛 常见问题

### 问题 1: 提示找不到 MySQL
**解决方案**: 
- 检查 Laragon 是否启动
- 确认 MySQL 服务是否运行
- 修改脚本中的 MySQL 路径

### 问题 2: 权限不足
**解决方案**:
```sql
-- 使用 root 用户执行
mysql -u root -p jiazheng_miniapp < generate_orders.sql
```

### 问题 3: 外键约束错误
**原因**: 关联的用户 ID 或需求 ID 不存在

**解决方案**:
1. 先插入用户和需求测试数据
2. 或临时禁用外键检查（脚本中已处理）

### 问题 4: 数据重复
**原因**: 多次执行脚本

**解决方案**:
- 每次执行前清空表：`TRUNCATE TABLE orders;`
- 或接受数据追加（用于压力测试）

---

## 📞 技术支持

如遇到问题，请检查：
1. MySQL 服务是否正常运行
2. 数据库 `jiazheng_miniapp` 是否存在
3. `orders` 表结构是否正确
4. 是否有足够的权限执行 INSERT 操作

---

## ✅ 执行成功标志

看到以下输出表示成功：
```
========================================
订单数据生成成功！
========================================

订单数据统计：
+----------+--------+--------+--------+--------+--------+--------+------------+----------+
| 总订单数 | 待服务 | 服务中 | 已完成 | 已评价 | 已取消 | 售后   | 平均金额   | 总金额   |
+----------+--------+--------+--------+--------+--------+--------+------------+----------+
|      400 |     60 |     80 |    160 |     60 |     28 |     12 |     250.50 |  100200  |
+----------+--------+--------+--------+--------+--------+--------+------------+----------+
```

---

**最后更新**: 2026-03-28  
**维护人员**: 开发团队
