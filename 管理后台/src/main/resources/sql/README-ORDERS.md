# 📦 订单测试数据生成工具

## ✅ 已生成的文件

### 1. SQL 脚本文件
- **generate_orders.sql** - 完整版存储过程脚本（推荐）
- **generate-orders-simple.sql** - 简化版脚本
- **generate-orders.bat** - Windows 批处理执行脚本
- **generate-orders.ps1** - PowerShell 执行脚本

### 2. 文档文件
- **ORDER-DATA-GENERATOR.md** - 详细使用指南

---

## 🚀 快速执行方法

### 方法一：使用批处理文件（最简单）

1. 双击运行：
   ```
   F:\AiCoding\家政小程序\管理后台\src\main\resources\sql\generate-orders.bat
   ```

2. 等待执行完成
3. 查看统计结果

---

### 方法二：使用 Laragon 终端

1. 打开 Laragon 终端
2. 登录 MySQL：
   ```bash
   mysql -u root jiazheng_miniapp
   ```

3. 复制粘贴以下内容执行：
   ```sql
   source F:/AiCoding/家政小程序/管理后台/src/main/resources/sql/generate_orders.sql
   ```

---

### 方法三：手动执行 SQL

1. 打开任意 MySQL 客户端工具（如 Navicat、DBeaver、Workbench）
2. 连接到数据库 `jiazheng_miniapp`
3. 打开文件：`generate_orders.sql`
4. 执行全部 SQL 语句

---

## 📊 数据说明

### 生成数据量
- **订单总数**: 400 条

### 数据分布

#### 订单状态分布
| 状态 | 代码 | 比例 | 数量 |
|------|------|------|------|
| 待服务 | 1 | 15% | ~60 条 |
| 服务中 | 2 | 20% | ~80 条 |
| 已完成 | 3 | 40% | ~160 条 |
| 已评价 | 4 | 15% | ~60 条 |
| 已取消 | 5 | 7% | ~28 条 |
| 退款/售后 | 6 | 3% | ~12 条 |

#### 支付状态
- **已支付**: 90% (~360 条)
- **未支付**: 10% (~40 条)

#### 价格范围
- **单价**: 50-500 元
- **时长**: 2-10 小时
- **总金额**: 100-5000 元

#### 地域分布
- **省份**: 广东省
- **城市**: 深圳市
- **区县**: 南山区、福田区、罗湖区、宝安区、龙岗区、龙华区、光明区（随机）

#### 服务分类
1. 日常保洁
2. 深度保洁
3. 做饭阿姨
4. 育儿嫂
5. 老人照护

---

## 🔍 验证数据

### SQL 查询验证

```sql
-- 1. 查看总订单数
SELECT COUNT(*) FROM orders;

-- 2. 查看状态分布
SELECT 
    status,
    CASE status
        WHEN 1 THEN '待服务'
        WHEN 2 THEN '服务中'
        WHEN 3 THEN '已完成'
        WHEN 4 THEN '已评价'
        WHEN 5 THEN '已取消'
        WHEN 6 THEN '退款/售后'
    END AS 状态名称，
    COUNT(*) AS 数量
FROM orders
GROUP BY status;

-- 3. 查看最新 10 条订单
SELECT 
    id,
    order_no,
    title,
    status,
    total_amount,
    publisher_id,
    taker_id,
    created_at
FROM orders
ORDER BY id DESC
LIMIT 10;

-- 4. 查看统计数据
SELECT 
    COUNT(*) AS 总订单数，
    ROUND(AVG(total_amount), 2) AS 平均金额，
    ROUND(SUM(total_amount), 2) AS 总金额，
    MAX(total_amount) AS 最高金额，
    MIN(total_amount) AS 最低金额
FROM orders;
```

### 前端页面验证

1. 访问管理后台：http://localhost:3003
2. 登录系统（账号：admin / 密码：admin123）
3. 进入"订单管理" → "订单列表"
4. 查看：
   - 表格中应显示 400 条订单
   - 分页组件显示共 20 页（每页 20 条）
   - 各种状态的订单都有分布

---

## ⚠️ 注意事项

### 1. 清空旧数据（可选）
如果需要重新生成，建议先清空现有订单数据：
```sql
TRUNCATE TABLE orders;
```

### 2. 关联数据
确保以下表有足够的测试数据：
- `users` 表：至少 300 条用户数据
- `demands` 表：至少 400 条需求数据

### 3. 自增 ID
订单表的 ID 是自增长的，多次执行会累加数据。

---

## 🎯 自定义配置

### 修改生成数量
编辑 `generate_orders.sql`，找到：
```sql
WHILE i <= 400 DO  -- 改为需要的数量
```

### 修改状态比例
编辑 `generate_orders.sql`，调整 CASE 语句中的概率值。

### 修改价格范围
编辑 `generate_orders.sql`：
```sql
SET actual_price = ROUND(50 + RAND() * 450, 2);  -- 调整参数修改价格范围
```

---

## 📝 执行日志

### 成功标志
看到以下输出表示成功：
```
========================================
订单数据生成成功！
========================================

总订单数：400
待服务：60
服务中：80
已完成：160
已评价：60
已取消：28
售后：12
```

### 失败排查
如果执行失败，检查：
1. MySQL 服务是否运行
2. 数据库 `jiazheng_miniapp` 是否存在
3. `orders` 表结构是否正确
4. 是否有足够的权限

---

## 📞 技术支持

如遇问题，请检查：
1. Laragon 是否正常运行
2. MySQL 服务状态
3. 数据库连接是否正常
4. 查看错误日志

---

**创建时间**: 2026-03-28  
**最后更新**: 2026-03-28  
**维护人员**: 开发团队
