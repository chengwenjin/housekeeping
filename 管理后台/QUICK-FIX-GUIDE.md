# 操作日志接口 400 错误 - 快速修复指南

## 🔴 问题症状

```
GET /api/admin/logs?page=1&pageSize=10&operator=&operationType=&startDate=&endDate=
Response: 400 "服务器内部错误"
```

## 🔍 可能原因

### 原因 1：数据库表缺少 username 字段（最可能）⭐⭐⭐⭐⭐
数据库表 `operation_logs` 中没有 `username` 字段，但实体类中定义了该字段。

### 原因 2：后端服务未重启
代码已更新但服务未重启，旧代码仍在运行。

### 原因 3：编译错误
代码有编译错误，导致服务启动失败或使用了旧版本。

---

## ✅ 解决方案（按顺序执行）

### 步骤 1：检查并修复数据库表结构 ⭐⭐⭐⭐⭐

**方法 A：使用 MySQL 客户端工具（推荐）**

1. 打开 MySQL 客户端（Navicat、MySQL Workbench 等）
2. 连接到数据库
3. 执行以下 SQL：

```sql
-- 查看 operation_logs 表结构
DESC `operation_logs`;

-- 检查是否有 username 字段
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'operation_logs' 
  AND COLUMN_NAME = 'username';
```

如果查询结果为空，说明缺少 username 字段，执行：

```sql
-- 添加 username 字段
ALTER TABLE `operation_logs` 
ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;

-- 添加索引
ALTER TABLE `operation_logs` 
ADD INDEX `idx_username` (`username`);

-- 验证
DESC `operation_logs`;
```

**方法 B：使用命令行**

```bash
mysql -u root -p
USE your_database_name;  # 替换为实际数据库名

-- 执行上面的 ALTER TABLE 语句
```

**方法 C：执行迁移脚本**

```bash
# 在项目目录下执行
mysql -u root -p your_database_name < 管理后台/src/main/resources/sql/migration/20260326_add_username_to_operation_logs.sql
```

---

### 步骤 2：清理并重新编译项目

**使用 IDEA：**
1. 点击菜单 `Build` → `Rebuild Project`
2. 等待编译完成

**使用 Maven 命令行：**
```bash
cd 管理后台
mvn clean compile
```

---

### 步骤 3：重启后端服务

1. 停止当前运行的服务（Ctrl+C）
2. 重新启动：

**IDEA 方式：**
- 找到启动类（通常是 `xxxApplication.java`）
- 右键 → Run

**Maven 方式：**
```bash
cd 管理后台
mvn spring-boot:run
```

---

### 步骤 4：查看启动日志

启动后，**立即查看日志**，确认没有报错：

```
# 应该看到类似内容
2026-03-26 INFO  OperationLogServiceImpl - 查询操作日志...
2026-03-26 INFO  AdminLogController - 获取操作日志列表...

# 如果出现 ERROR，说明有问题
2026-03-26 ERROR OperationLogServiceImpl - 获取操作日志列表失败
```

---

### 步骤 5：测试接口

**方法 A：使用浏览器（简单测试）**

访问：`http://localhost:8080/api/admin/logs?page=1&pageSize=10`

应该返回 JSON 数据，而不是 400 错误。

**方法 B：使用 Postman/Apifox**

```
GET http://localhost:8080/api/admin/logs
Params:
  - page: 1
  - pageSize: 10
  - operator: (留空)
  - operationType: (留空)
  - startDate: (留空)
  - endDate: (留空)
```

**方法 C：前端页面测试**

访问管理后台前端的操作日志页面，查看是否正常加载。

---

## 🐛 常见错误排查

### 错误 1：Unknown column 'username' in 'field list'

**原因**：数据库表没有 username 字段  
**解决**：执行步骤 1 的 ALTER TABLE 语句

### 错误 2：Table 'xxx.operation_logs' doesn't exist

**原因**：表不存在  
**解决**：执行 init.sql 创建所有表

```bash
mysql -u root -p your_database_name < 管理后台/src/main/resources/sql/init.sql
```

### 错误 3：404 Not Found

**原因**：接口路径不对  
**检查**：
- 确认后端服务已启动
- 确认端口号正确（默认 8080）
- 确认路径是 `/api/admin/logs` 还是 `/admin/logs`

查看 application.yml 中的配置：

```yaml
server:
  servlet:
    context-path: /api  # 如果有这个配置，路径就是 /api/admin/logs
```

### 错误 4：编译错误

**可能原因**：
- 缺少依赖（Hutool、MyBatis-Plus 等）
- 代码语法错误

**解决**：
```bash
cd 管理后台
mvn clean install
```

查看错误信息，根据提示修复。

---

## 📋 验证清单

完成后，逐项检查：

- [ ] 数据库 `operation_logs` 表有 `username` 字段
- [ ] 后端服务已成功重启
- [ ] 启动日志无 ERROR
- [ ] 访问 `/api/admin/logs?page=1&pageSize=10` 返回 200
- [ ] 前端页面能正常显示日志列表

---

## 🎯 快速修复命令（一键执行）

```bash
# 假设：
# - 数据库名：jiazheng
# - MySQL 用户名：root
# - MySQL 密码：（空）

# 1. 修复数据库
mysql -u root jiazheng << EOF
ALTER TABLE operation_logs ADD COLUMN username VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER admin_id;
ALTER TABLE operation_logs ADD INDEX idx_username (username);
EOF

# 2. 重新编译
cd 管理后台
mvn clean compile

# 3. 重启服务（手动）
# mvn spring-boot:run
```

---

## 📞 如果仍然不行

请提供以下信息以便进一步诊断：

1. **后端启动日志**（特别是 ERROR 部分）
2. **数据库表结构**（DESC operation_logs 的输出）
3. **完整的接口响应**（包括 headers 和 body）
4. **application.yml 配置**（端口、context-path 等）

---

## 🔗 相关文件位置

- 实体类：`管理后台/src/main/java/com/jz/miniapp/entity/OperationLog.java`
- Controller：`管理后台/src/main/java/com/jz/miniapp/controller/admin/AdminLogController.java`
- Service：`管理后台/src/main/java/com/jz/miniapp/service/impl/OperationLogServiceImpl.java`
- 迁移脚本：`管理后台/src/main/resources/sql/migration/20260326_add_username_to_operation_logs.sql`
- 快速修复：`管理后台/src/main/resources/sql/quick-fix-logs-table.sql`
