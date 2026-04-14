# API 接口测试指南

## 问题排查总结

### 原始问题
- **错误**: 404 Not Found
- **请求路径**: `http://localhost:3001/api/admin/auth/login`
- **错误信息**: 
```json
{
  "timestamp": "2026-03-27 13:13:16",
  "status": 404,
  "error": "Not Found",
  "path": "/api/admin/auth/login"
}
```

### 配置核查 ✅

#### 后端配置
- **端口**: 8080 ✅
- **Context Path**: `/api` ✅
- **Controller 路径**: `/api/admin/auth` ✅
- **方法路径**: `POST /login` ✅
- **完整后端地址**: `http://localhost:8080/api/admin/auth/login` ✅

#### 前端配置
- **开发服务器端口**: 3001 ✅
- **Vite 代理**: `/api` → `http://localhost:8080` ✅
- **Axios baseURL**: `/api` ✅
- **登录请求路径**: `/admin/auth/login` ✅
- **完整前端请求**: `/api/admin/auth/login` ✅

### 请求流程

```
浏览器
  ↓ http://localhost:3001/api/admin/auth/login
Vite Dev Server (端口 3001)
  ↓ 代理配置：/api → http://localhost:8080
Spring Boot (端口 8080)
  ↓ Context Path: /api
Controller: /api/admin/auth + /login
  ↓
AdminAuthController.login() 方法
```

### 测试步骤

#### 方法 1: 直接访问后端接口（推荐先测试这个）

在浏览器中访问或使用 Postman：
```
POST http://localhost:8080/api/admin/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
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
      "avatar": "https://xxx.com/admin-avatar.png",
      "role": {
        "id": 1,
        "name": "超级管理员",
        "code": "super_admin"
      },
      "permissions": ["*"]
    }
  },
  "timestamp": 1711568000000
}
```

#### 方法 2: 通过前端访问

1. 打开浏览器访问：`http://localhost:3001`
2. 在登录页面填写：
   - 用户名：`admin`
   - 密码：`admin123`（或任意 6 位以上密码）
3. 点击登录按钮
4. 打开浏览器开发者工具（F12）
5. 查看 Network 标签中的请求

**预期结果**:
- 请求 URL: `http://localhost:3001/api/admin/auth/login`
- 请求方法：`POST`
- 状态码：`200`
- 响应数据：包含 token 的 JSON

### 可能的问题及解决方案

#### 问题 1: 后端服务未启动
**症状**: 访问 `http://localhost:8080/api/doc.html` 无法打开  
**解决**: 
```bash
cd "F:\AiCoding\家政小程序\管理后台"
mvn spring-boot:run
```

#### 问题 2: 前端服务未启动
**症状**: 访问 `http://localhost:3001` 无法打开  
**解决**:
```bash
cd "F:\AiCoding\家政小程序\管理后台前端"
npm run dev
```

#### 问题 3: 浏览器缓存问题
**症状**: 后端和前端都启动了，但仍然报 404  
**解决**: 
1. 清除浏览器缓存
2. 硬刷新（Ctrl + Shift + R）
3. 或使用无痕模式

#### 问题 4: 数据库连接失败
**症状**: 后端启动时报数据库连接错误  
**解决**: 
1. 确保 MySQL 服务已启动
2. 检查数据库 `jiazheng_miniapp` 是否存在
3. 检查 `application.yml` 中的数据库配置

#### 问题 5: AdminService 未实现
**症状**: 500 Internal Server Error  
**解决**: 检查 `AdminServiceImpl.java` 是否实现了 login 方法

### 快速验证命令

#### 使用 curl 测试（Windows PowerShell）
```powershell
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/auth/login" -Method POST -Body $body -ContentType "application/json"
```

#### 使用 Postman
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

### 调试技巧

#### 1. 查看后端日志
启动后端后，控制台会显示所有请求日志：
```
2026-03-27 13:xx:xx INFO  c.j.m.c.a.AdminAuthController - 管理员登录请求 - username: admin
2026-03-27 13:xx:xx INFO  c.j.m.c.a.AdminAuthController - 管理员登录成功 - adminId: 1
```

#### 2. 查看前端网络请求
1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 点击登录按钮
4. 查看 `api/admin/auth/login` 请求详情

#### 3. 使用 Swagger/Knife4j 测试
访问：`http://localhost:8080/api/doc.html`
- 找到 "管理后台 - 认证" 接口
- 展开 "管理员登录"
- 点击 "Try it out"
- 填写参数并执行

### 当前配置总结

✅ **后端服务**: 运行中 (PID: 23276)  
✅ **前端服务**: 运行中 (端口：3001)  
✅ **API 代理**: 已配置  
✅ **Controller 路径**: 正确  
✅ **数据库**: 需确认是否启动  

---

**下一步**: 按照上述测试步骤验证接口是否正常
