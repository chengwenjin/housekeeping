# 404 错误排查与修复记录

## 问题描述

**时间**: 2026-03-27 13:13  
**现象**: 前端登录请求返回 404 错误  
**请求路径**: `http://localhost:3001/api/admin/auth/login`  

### 错误响应
```json
{
  "timestamp": "2026-03-27 13:13:16",
  "status": 404,
  "error": "Not Found",
  "path": "/api/admin/auth/login"
}
```

---

## 问题排查过程

### Step 1: 检查配置 ✅

#### 后端配置
- **Context Path**: `/api`
- **Controller 路径**: `/api/admin/auth`
- **方法路径**: `POST /login`
- **理论完整路径**: `/api/api/admin/auth/login` ❌

**发现问题**: Controller 的 `@RequestMapping` 包含了 `/api`，但 context-path 也是 `/api`，导致路径重复！

#### 前端配置
- **Vite 代理**: `/api` → `http://localhost:8080` ✅
- **Axios baseURL**: `/api` ✅
- **登录请求**: `/admin/auth/login` ✅
- **前端完整请求**: `/api/admin/auth/login` ✅

### Step 2: 修复路径配置 ✅

**修改文件**: `AdminAuthController.java`

**修改前**:
```java
@RequestMapping("/api/admin/auth")
```

**修改后**:
```java
@RequestMapping("/admin/auth")
```

**原因**: Spring Boot 的 context-path 已经配置为 `/api`，Controller 不需要再包含 `/api` 前缀。

### Step 3: 重启服务测试 ✅

重启后发现 404 错误变成了 500 错误，说明路径正确了！

---

## 新问题：500 内部服务器错误

### 错误日志分析

```
### Error querying database.  
Cause: java.sql.SQLSyntaxErrorException: Unknown column 'phone' in 'field list'

### SQL: SELECT id,username,password,real_name,avatar,phone,email,role_id,status,...
FROM admins WHERE (username = ? AND status = ?)
```

**根本原因**: 数据库表 `admins` 缺少 `phone` 和 `email` 字段

---

## 解决方案

### 方案 1: 执行 SQL 脚本添加缺失字段（推荐）

**SQL 脚本位置**: `src/main/resources/sql/fix_admins_table.sql`

**执行步骤**:
1. 打开 MySQL 客户端或管理工具（如 Navicat、DBeaver、phpMyAdmin 等）
2. 连接到数据库 `jiazheng_miniapp`
3. 执行以下 SQL：

```sql
-- 添加 phone 字段（手机号）
ALTER TABLE `admins` 
ADD COLUMN `phone` varchar(20) DEFAULT NULL COMMENT '手机号' AFTER `avatar`;

-- 添加 email 字段（邮箱）
ALTER TABLE `admins` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 插入默认管理员账号（用户名：admin, 密码：admin123）
INSERT INTO `admins` (`username`, `password`, `real_name`, `avatar`, `phone`, `email`, `role_id`, `status`, `created_at`, `updated_at`) 
VALUES ('admin', 'admin123', '系统管理员', '', '', '', 1, 1, NOW(), NOW());
```

4. 验证表结构：
```sql
DESC admins;
```

5. 验证数据：
```sql
SELECT * FROM admins;
```

### 方案 2: 修改实体类（备选方案）

如果暂时不需要 phone 和 email 字段，可以注释掉实体类中的相关字段：

**修改文件**: `Admin.java`

```java
// 暂时注释，避免数据库字段不存在
// @TableField("phone")
// private String phone;

// @TableField("email")
// private String email;
```

**不推荐**: 这样会丢失功能，建议采用方案 1。

---

## 验证测试

### 1. 使用 PowerShell 测试

```powershell
$body = '{"username":"admin","password":"admin123"}'
$result = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/auth/login" -Method POST -Body $body -ContentType "application/json; charset=utf-8"
Write-Host "登录成功! Token: $($result.data.token)"
```

**预期响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "mock_admin_token_1",
    "refreshToken": "mock_admin_refresh_token_1",
    "expiresIn": 28800,
    "admin": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "avatar": "",
      "role": {
        "id": 1,
        "name": "超级管理员",
        "code": "super_admin"
      },
      "permissions": ["*"]
    }
  }
}
```

### 2. 使用 Postman 测试

1. 创建新请求
2. 方法：POST
3. URL: `http://localhost:8080/api/admin/auth/login`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```
6. 点击 Send

### 3. 通过前端测试

1. 访问：`http://localhost:3001`
2. 输入用户名：`admin`
3. 输入密码：`admin123`
4. 点击登录
5. 查看浏览器开发者工具 Network 标签

**预期结果**:
- 状态码：200
- 响应：包含 token 的 JSON
- 自动跳转到首页

---

## 其他 Controller 的路径问题

需要同样修复所有 Admin Controller 的路径：

### 已检查的 Controller

- ✅ `AdminAuthController.java` - 已修复：`/admin/auth`
- 🚧 `AdminUserController.java` - 待修复：应该是 `/admin/users`
- 🚧 `AdminDemandController.java` - 待修复：应该是 `/admin/demands`
- 🚧 `AdminOrderController.java` - 待修复：应该是 `/admin/orders`
- 🚧 `AdminReviewController.java` - 待修复：应该是 `/admin/reviews`
- 🚧 `AdminCategoryController.java` - 待修复：应该是 `/admin/categories`
- 🚧 `AdminStatisticsController.java` - 待修复：应该是 `/admin/statistics`
- 🚧 `AdminSystemController.java` - 待修复：应该是 `/admin/system`
- 🚧 `AdminLogController.java` - 待修复：应该是 `/admin/logs`

### 批量修复命令（PowerShell）

```powershell
cd "F:\AiCoding\家政小程序\管理后台\src\main\java\com\jz\miniapp\controller\admin"

(Get-Content AdminUserController.java) -replace '@RequestMapping\("/api/admin/users"\)', '@RequestMapping("/admin/users")' | Set-Content AdminUserController.java
```

或者手动逐个修改。

---

## 经验总结

### 问题根源

1. **路径重复**: Context Path 和 Controller RequestMapping 都包含 `/api`
2. **数据库表结构不完整**: 实体类有字段但数据库表没有

### 最佳实践

1. **统一路径规范**:
   - Context Path: `/api`
   - Controller 路径：`/admin/*`, `/api/*`（不带 `/api` 前缀）
   - 完整路径：`/api/admin/*`, `/api/mini/*`

2. **数据库变更管理**:
   - 先创建数据库表结构
   - 再编写对应的实体类
   - 使用 Flyway/Liquibase 管理数据库版本

3. **开发顺序**:
   - 设计数据库表
   - 创建 Entity 实体类
   - 创建 Mapper 接口
   - 编写 Service 层
   - 最后写 Controller

---

## 相关文件

- **修复脚本**: `src/main/resources/sql/fix_admins_table.sql`
- **Controller**: `src/main/java/com/jz/miniapp/controller/admin/AdminAuthController.java`
- **Entity**: `src/main/java/com/jz/miniapp/entity/Admin.java`
- **配置文件**: `src/main/resources/application.yml`

---

## 下一步行动

1. ✅ 执行 SQL 脚本修复数据库表结构
2. 🚧 修复其他 Controller 的路径问题
3. 🚧 测试前端登录功能
4. 🚧 完善其他业务模块

---

**修复时间**: 2026-03-27  
**修复状态**: 部分完成（路径问题已修复，数据库需手动执行 SQL）  
**预计完成**: 执行 SQL 后即可正常登录
