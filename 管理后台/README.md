# 家政小程序后端服务

家政服务撮合平台后端 - Java Spring Boot 实现

## 📋 项目简介

基于 Java 11 + Spring Boot 的家政服务小程序后端服务，提供用户认证、需求发布、在线接单、订单管理等功能。

## 🛠️ 技术栈

- **运行环境**: Java 11 (LTS)
- **Web 框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **认证**: JWT (jjwt 0.11.5)
- **API 文档**: Knife4j 3.0.3
- **构建工具**: Maven 3.8+

## 📁 项目结构

```
管理后台/
├── src/
│   ├── main/
│   │   ├── java/com/jz/miniapp/
│   │   │   ├── controller/          # Controller 层
│   │   │   │   ├── api/            # 小程序 API
│   │   │   │   └── admin/          # 管理后台 API
│   │   │   ├── service/             # Service 层
│   │   │   │   └── impl/           # Service 实现
│   │   │   ├── mapper/              # Mapper 层
│   │   │   ├── entity/              # 实体类
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   ├── vo/                  # 视图对象
│   │   │   ├── common/              # 公共类
│   │   │   ├── config/              # 配置类
│   │   │   ├── enums/               # 枚举
│   │   │   ├── exception/           # 异常处理
│   │   │   ├── interceptor/         # 拦截器
│   │   │   └── util/                # 工具类
│   │   └── resources/
│   │       ├── mapper/              # MyBatis XML
│   │       ├── sql/                 # SQL 脚本
│   │       └── application.yml      # 配置文件
│   └── test/                        # 测试代码
└── pom.xml                          # Maven 配置
```

## 🚀 快速开始

### 1. 环境要求

- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 2. 数据库初始化

```bash
# 执行 SQL 初始化脚本
mysql -u root -p < src/main/resources/sql/init.sql
```

### 3. 配置修改

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiazheng_miniapp?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password  # 修改为你的密码
  
  redis:
    host: localhost
    port: 6379

wechat:
  miniapp:
    appid: your_wechat_appid      # 替换为微信小程序 AppID
    secret: your_wechat_secret    # 替换为微信小程序 Secret

aliyun:
  oss:
    access-key-id: your_access_key
    access-key-secret: your_secret_key
    bucket-name: your_bucket
```

**注意**: 如果暂时没有微信 AppID 和 Secret，可以先使用默认值，系统会模拟登录流程。

### 4. 编译打包

```bash
mvn clean package -DskipTests
```

### 5. 运行项目

```bash
# 方式一：使用 Maven 运行
mvn spring-boot:run

# 方式二：运行 jar 包
java -jar target/jiazheng-miniapp-1.0.0-SNAPSHOT.jar
```

### 6. 访问应用

启动成功后，访问以下地址:

- **API 文档**: http://localhost:8080/api/doc.html
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **应用端口**: 8080

## 🔑 默认管理员账号

- **用户名**: admin
- **密码**: admin123

## 📖 API 接口说明

### 小程序端 API (/api/mini/**)

#### 认证接口
- `POST /mini/auth/login` - 微信登录
- `POST /mini/auth/refresh` - 刷新 Token

#### 需求接口
- `GET /mini/demands` - 获取需求列表
- `GET /mini/demands/{id}` - 获取需求详情
- `POST /mini/demands` - 发布需求
- `PUT /mini/demands/{id}` - 更新需求
- `DELETE /mini/demands/{id}` - 删除需求
- `POST /mini/demands/{id}/take` - 接单
- `POST /mini/demands/{id}/footprint` - 添加足迹

#### 订单接口
- `GET /mini/orders/published` - 我发布的订单
- `GET /mini/orders/taken` - 我接的订单
- `GET /mini/orders/{id}` - 订单详情
- `PUT /mini/orders/{id}/status` - 更新订单状态
- `POST /mini/orders/{id}/cancel` - 取消订单
- `POST /mini/orders/{id}/complete` - 完成订单

#### 用户接口
- `GET /mini/user/profile` - 获取当前用户信息
- `PUT /mini/user/profile` - 更新用户信息
- `GET /mini/user/{id}` - 获取其他用户信息
- `POST /mini/user/{id}/follow` - 关注用户
- `DELETE /mini/user/{id}/follow` - 取消关注
- `GET /mini/user/following` - 我的关注列表
- `GET /mini/user/follower` - 我的粉丝列表

### 管理后台 API (/api/admin/**)

#### 认证接口
- `POST /admin/auth/login` - 管理员登录
- `GET /admin/auth/info` - 获取管理员信息
- `POST /admin/auth/logout` - 退出登录

#### 需求管理
- `GET /admin/demands` - 获取需求列表
- `GET /admin/demands/{id}` - 获取需求详情
- `PUT /admin/demands/{id}/offline` - 下架需求
- `DELETE /admin/demands/{id}` - 删除需求

---

## 💡 重要提示

### 1. 关于微信登录

当前版本使用**模拟数据**实现微信登录流程。如需真实微信登录，需要:

1. 注册微信小程序账号
2. 获取 AppID 和 Secret
3. 在 `application.yml` 中配置真实的 AppID 和 Secret
4. 实现 `UserServiceImpl.wxLogin()` 方法中的微信 API 调用逻辑

### 2. 关于文件上传

文件上传功能框架已搭建，需要配置阿里云 OSS 后才能使用。
详见：`DEVELOPMENT-GUIDE.md` 中的文件上传示例。

### 3. 关于待完善功能

本项目已完成基础架构和核心功能框架，部分业务逻辑需要进一步完善。
详见：`PROJECT-SUMMARY.md` 中的"待完善功能"章节。

---

## 📚 更多文档

- **[QUICKSTART.md](./QUICKSTART.md)** - 5 分钟快速启动指南
- **[DEVELOPMENT-GUIDE.md](./DEVELOPMENT-GUIDE.md)** - 开发指南和最佳实践
- **[PROJECT-SUMMARY.md](./PROJECT-SUMMARY.md)** - 项目总结和待办事项
- **[COMPLETION-SUMMARY.md](./COMPLETION-SUMMARY.md)** - 完成总结

## 🔧 开发说明

### 统一响应格式

所有接口返回统一使用 `Result<T>` 封装:

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1711468800000
}
```

### 错误码规范

- **200**: 成功
- **400**: 请求参数错误
- **401**: 未登录/Token 失效
- **403**: 无权限
- **404**: 资源不存在
- **500**: 服务器内部错误
- **10000-19999**: 小程序业务错误
- **40000-49999**: 管理后台业务错误

### JWT 认证

需要在请求头中携带 Token:

```
Authorization: Bearer <token>
```

## 📝 待完善功能

以下是当前简化版本需要进一步完善的功能:

1. **微信登录完整实现**
   - 调用微信 API 获取 openid
   - 解密用户信息

2. **订单服务完整实现**
   - 订单创建逻辑
   - 订单状态流转
   - 订单取消/完成

3. **评价系统**
   - 评价发布
   - 评价回复
   - 评分计算

4. **消息通知**
   - 站内信
   - 模板消息推送

5. **文件上传**
   - OSS 图片上传
   - 图片压缩

6. **数据统计**
   - 首页数据统计
   - 图表数据

7. **管理后台完整功能**
   - 用户管理
   - 角色权限管理
   - 操作日志

## ⚠️ 注意事项

1. **生产环境配置**
   - 修改默认密码
   - 配置 HTTPS
   - 使用环境变量管理敏感信息

2. **安全性**
   - 密码加密存储 (bcrypt)
   - SQL 注入防护 (MyBatis 预编译)
   - XSS 攻击防护

3. **性能优化**
   - Redis 缓存常用数据
   - 数据库索引优化
   - 分页查询

## 📄 License

MIT License

## 👥 开发团队

家政小程序团队

## 📚 更多文档

- **[DEVELOPMENT-PLAN.md](./DEVELOPMENT-PLAN.md)** - 开发计划 (唯一，推荐查看)
- **[QUICKSTART.md](./QUICKSTART.md)** - 5 分钟快速开始
- **[DEVELOPMENT-GUIDE.md](./DEVELOPMENT-GUIDE.md)** - 开发指南与代码规范
- **[SWAGGER-GUIDE.md](./SWAGGER-GUIDE.md)** - Swagger/Knife4j 使用说明
- **[STARTUP-SUCCESS.md](./STARTUP-SUCCESS.md)** - 启动成功总结

---

**版本**: v1.0  
**更新日期**: 2026-03-26
