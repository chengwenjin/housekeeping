# 家政小程序后端 - 快速启动指南

## 🚀 5 分钟快速开始

### 前置条件

确保已安装以下软件:

- ✅ JDK 11+
- ✅ MySQL 8.0+
- ✅ Redis 6.0+
- ✅ Maven 3.8+

### Step 1: 启动数据库和缓存

```bash
# 启动 MySQL
net start MySQL80

# 启动 Redis
redis-server
```

### Step 2: 初始化数据库

```bash
# 进入项目目录
cd F:\AiCoding\家政小程序\管理后台

# 执行 SQL 脚本
mysql -u root -p < src/main/resources/sql/init.sql
```

输入 MySQL 密码后，数据库将自动创建并初始化。

### Step 3: 修改配置

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: root          # 修改为你的 MySQL 用户名
    password: your_password # 修改为你的 MySQL 密码
  
  redis:
    host: localhost         # Redis 地址
    port: 6379             # Redis 端口

wechat:
  miniapp:
    appid: your_appid      # 替换为微信小程序 AppID
    secret: your_secret    # 替换为微信小程序 Secret
```

**注意**: 如果暂时没有微信 AppID 和 Secret，可以先使用默认值，系统会模拟登录流程。

### Step 4: 编译项目

```bash
cd F:\AiCoding\家政小程序\管理后台
mvn clean package -DskipTests
```

首次编译可能需要几分钟 (下载依赖),请耐心等待。

### Step 5: 运行项目

```bash
# 方式一：使用 Maven 运行
mvn spring-boot:run

# 方式二：直接运行 jar 包
java -jar target/jiazheng-miniapp-1.0.0-SNAPSHOT.jar
```

### Step 6: 验证启动

看到以下日志表示启动成功:

```
========================================
家政小程序后端服务启动成功!
API 文档地址：http://localhost:8080/api/doc.html
Swagger 地址：http://localhost:8080/api/swagger-ui.html
========================================
```

### Step 7: 访问 API 文档

打开浏览器访问:
- **Knife4j 文档**: http://localhost:8080/api/doc.html
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

### Step 8: 测试接口

#### 8.1 管理员登录

在 Swagger 中找到 `管理后台 - 认证管理` -> `/admin/auth/login`

请求参数:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

响应示例:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "expiresIn": 28800,
    "admin": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员"
    }
  }
}
```

复制返回的 `token`,用于后续接口调用。

#### 8.2 获取需求列表

在 Swagger 中找到 `小程序 - 需求管理` -> `/mini/demands`

点击 "Try it out",然后点击 "Execute" 执行请求。

#### 8.3 发布需求

需要先在请求头中添加 Token:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

请求参数:
```json
{
  "categoryId": 1,
  "title": "家庭日常保洁，约 2-3 小时",
  "description": "120㎡三居室，需要打扫卫生",
  "serviceType": 1,
  "expectedPrice": 80.00,
  "priceUnit": "小时",
  "city": "北京市",
  "district": "朝阳区",
  "address": "建国路 88 号",
  "serviceTimeDesc": "明天上午"
}
```

---

## 🔍 常见问题

### Q1: 启动时报数据库连接失败？

**解决方案**:
1. 检查 MySQL 是否已启动：`net start MySQL80`
2. 检查配置文件中的用户名、密码是否正确
3. 检查数据库是否已创建：`SHOW DATABASES;` 应该能看到 `jiazheng_miniapp`

### Q2: Redis 连接失败？

**解决方案**:
1. 检查 Redis 是否已启动
2. Windows: `redis-cli ping` 应返回 `PONG`
3. 检查配置文件中的 Redis 地址和端口

### Q3: 端口 8080 已被占用？

**解决方案**:
1. 修改 `application.yml` 中的端口:
   ```yaml
   server:
     port: 8081  # 改为其他端口
   ```
2. 或者关闭占用 8080 端口的程序

### Q4: Maven 依赖下载失败？

**解决方案**:
1. 检查网络连接
2. 使用国内镜像 (在 pom.xml 中添加):
   ```xml
   <mirror>
     <id>aliyun</id>
     <name>Aliyun Maven</name>
     <url>https://maven.aliyun.com/repository/public</url>
     <mirrorOf>central</mirrorOf>
   </mirror>
   ```

### Q5: 微信登录失败？

**说明**: 当前版本使用模拟数据，如需真实微信登录，需要:
1. 注册微信小程序账号
2. 获取 AppID 和 Secret
3. 在 `application.yml` 中配置真实的 AppID 和 Secret
4. 实现 `UserServiceImpl.wxLogin()` 方法中的微信 API 调用逻辑

---

## 📖 下一步

启动成功后，可以:

1. **浏览 API 文档**: 熟悉所有接口
2. **测试核心功能**: 
   - 用户登录
   - 发布需求
   - 浏览需求
   - 接单
3. **阅读开发指南**: 了解代码规范和最佳实践
4. **完善待实现功能**: 参考 `PROJECT-SUMMARY.md`

---

## 🎯 默认账号

### 管理后台
- **用户名**: admin
- **密码**: admin123

### 注意事项

1. 生产环境务必修改默认密码
2. 配置 HTTPS 保证传输安全
3. 使用环境变量管理敏感配置
4. 开启日志记录便于问题排查

---

**版本**: v1.0  
**更新日期**: 2026-03-26
