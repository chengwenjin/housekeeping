# 家政小程序 - 系统架构设计文档

## 1. 项目概述

### 1.1 项目背景
家政小程序是一个家政服务撮合平台，连接有家政服务需求的用户和提供服务的服务者。用户可以通过小程序发布家政需求，服务者可以接单提供服务。

### 1.2 核心功能
- 用户微信登录认证
- 家政需求发布与浏览
- 在线接单功能
- 订单管理 (发布单/接单)
- 足迹与关注功能
- 用户评价系统

## 2. 技术栈选型

### 2.1 前端技术栈

#### 小程序前端
- **框架**: 微信小程序原生开发 / uni-app(推荐)
- **UI 组件库**: uView UI / Vant Weapp
- **状态管理**: Vuex (uni-app) / 小程序全局数据
- **网络请求**: wx.request 封装

#### 管理后台前端
- **框架**: Vue 3 + Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **图表**: ECharts

### 2.2 后端技术栈
- **运行环境**: Java 11 (LTS)
- **Web 框架**: Spring Boot 2.7+ / Spring Boot 3.x
- **ORM**: MyBatis-Plus / JPA (Hibernate)
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **消息队列**: RabbitMQ / RocketMQ (可选，用于异步通知)
- **构建工具**: Maven 3.8+ / Gradle 7+
- **API 文档**: Swagger 3 / Knife4j

### 2.3 基础设施
- **服务器**: Nginx (反向代理)
- **容器化**: Docker + Docker Compose
- **对象存储**: 阿里云 OSS / 腾讯云 COS (存储图片)
- **CDN**: 加速静态资源
- **SSL 证书**: HTTPS 加密

## 3. 系统架构设计

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────┐
│                      用户层                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ 微信小程序  │  │ 管理后台 Web │  │ 移动端 H5   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    接入层                                │
│  ┌─────────────────────────────────────────────────┐    │
│  │              Nginx 负载均衡                      │    │
│  │         SSL 终止 / 静态资源 CDN                  │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   应用服务层                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │  用户服务   │  │  需求服务   │  │  订单服务   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │  评价服务   │  │  消息服务   │  │  文件服务   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   数据层                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   MySQL     │  │   Redis     │  │    OSS      │     │
│  │  主从复制   │  │  缓存集群   │  │  对象存储   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
```

### 3.2 服务模块划分

#### 3.2.1 用户服务 (User Service)
**职责**: 用户认证、信息管理、权限控制

**核心功能**:
- 微信登录授权 (code2session)
- JWT Token 生成与验证 (使用 jjwt 或 nimbus-jose-jwt)
- 用户信息管理 (CRUD)
- 用户认证审核
- 关注/取消关注

**技术实现**:
- Spring Security + JWT 认证
- MyBatis-Plus 数据访问
- Redis 存储 Token 黑名单

**接口示例**:
- `POST /api/user/login` - 微信登录
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/profile` - 更新用户信息
- `POST /api/user/follow/:userId` - 关注用户
- `DELETE /api/user/follow/:userId` - 取消关注

#### 3.2.2 需求服务 (Demand Service)
**职责**: 家政需求管理

**核心功能**:
- 需求发布
- 需求列表查询 (支持筛选、排序、分页)
- 需求详情查询
- 需求更新/删除
- 需求状态管理 (招募中/已接单/已完成)
- 足迹记录

**技术实现**:
- Spring MVC RESTful API
- MyBatis-Plus 分页插件
- Elasticsearch 全文搜索 (可选)

**接口示例**:
- `POST /api/demand` - 发布需求
- `GET /api/demands` - 获取需求列表
- `GET /api/demand/:id` - 获取需求详情
- `PUT /api/demand/:id` - 更新需求
- `DELETE /api/demand/:id` - 删除需求
- `POST /api/demand/:id/footprint` - 添加足迹

#### 3.2.3 订单服务 (Order Service)
**职责**: 订单生命周期管理

**核心功能**:
- 接单操作
- 订单状态流转 (待接单→进行中→已完成→已评价)
- 订单取消
- 订单列表查询 (我发布的/我接的)
- 订单详情查询

**技术实现**:
- 状态模式处理订单状态流转
- 分布式锁防止并发接单 (Redisson)
- 事务管理 (@Transactional)

**接口示例**:
- `POST /api/order/:demandId/take` - 接单
- `GET /api/orders/published` - 我发布的订单
- `GET /api/orders/taken` - 我接的订单
- `PUT /api/order/:id/status` - 更新订单状态
- `POST /api/order/:id/cancel` - 取消订单

#### 3.2.4 评价服务 (Review Service)
**职责**: 用户评价管理

**核心功能**:
- 发布评价
- 评价列表查询
- 评分计算
- 评价回复

**技术实现**:
- 事务保证评分一致性
- 异步更新用户评分 (事件驱动)

**接口示例**:
- `POST /api/review` - 发布评价
- `GET /api/reviews/user/:userId` - 用户评价列表
- `GET /api/reviews/order/:orderId` - 订单评价

#### 3.2.5 消息服务 (Message Service)
**职责**: 站内消息推送

**核心功能**:
- 系统通知发送
- 接单通知
- 评论通知
- 消息列表查询

**技术实现**:
- WebSocket 实时推送 (可选)
- 微信小程序模板消息
- 消息队列异步处理

**接口示例**:
- `GET /api/messages` - 消息列表
- `PUT /api/message/:id/read` - 标记已读

#### 3.2.6 文件服务 (File Service)
**职责**: 文件上传管理

**核心功能**:
- 图片上传 (OSS)
- 图片压缩处理
- 文件访问鉴权

**技术实现**:
- 阿里云 OSS SDK / 腾讯云 COS SDK
- Thumbnailator 图片压缩

**接口示例**:
- `POST /api/upload/image` - 上传图片

## 4. 数据流转流程

### 4.1 用户登录流程
```
1. 小程序调用 wx.login() 获取 code
2. 小程序将 code 发送到后端
3. 后端调用微信 API(code2session) 获取 openid/session_key
4. 后端创建/更新用户信息
5. 后端生成 JWT token 返回给小程序
6. 小程序存储 token，后续请求携带 token
```

### 4.2 发布需求流程
```
1. 用户填写需求表单
2. 上传图片到 OSS，获取图片 URL
3. 提交需求数据到后端
4. 后端验证数据并保存到数据库
5. 返回需求详情
6. 其他用户可浏览该需求
```

### 4.3 接单流程
```
1. 服务者浏览需求列表
2. 查看需求详情
3. 点击"立即接单"
4. 后端检查需求状态 (必须为"招募中")
5. 创建订单，更新需求状态为"已接单"
6. 发送通知给需求发布者
7. 双方可进行后续沟通
```

### 4.4 订单完成流程
```
1. 服务者确认服务完成
2. 需求者确认并支付 (如有线上支付)
3. 需求者发布评价
4. 更新订单状态为"已完成"
5. 更新服务者评分
```

## 5. 安全设计

### 5.1 认证机制
- **JWT Token**: 有效期 7 天，支持刷新
- **Token 存储**: 小程序端使用 wx.setStorage
- **Token 刷新**: 过期前自动刷新或使用 refresh token

### 5.2 权限控制
- **用户角色**: 普通用户、管理员
- **接口权限**: 
  - 公开接口：登录、需求列表
  - 登录接口：发布需求、接单、个人信息
  - 管理员接口：后台管理功能

### 5.3 数据安全
- **SQL 注入防护**: 使用 ORM 参数化查询
- **XSS 防护**: 输入过滤、输出转义
- **CSRF 防护**: Token 验证
- **敏感信息加密**: 密码、手机号等加密存储

## 6. 性能优化

### 6.1 缓存策略
- **热点数据**: 需求列表、用户信息使用 Redis 缓存
- **缓存过期**: 设置合理的 TTL(如 30 分钟)
- **缓存更新**: 数据变更时主动失效缓存

### 6.2 数据库优化
- **索引设计**: 常用查询字段建立索引
- **读写分离**: 主从复制，查询走从库
- **分表策略**: 数据量大时按时间/地区分表

### 6.3 接口优化
- **分页加载**: 列表接口强制分页
- **按需查询**: 避免 SELECT *
- **批量操作**: 减少数据库往返次数

## 7. 部署架构

### 7.1 开发环境
```
本地开发 → Git 仓库 → 测试环境
```

### 7.2 生产环境
```
Git 仓库 → CI/CD(Jenkins/GitLab CI) → 生产环境
                                    ↓
                              Docker 容器化部署
                                    ↓
                          Kubernetes 集群 (可选)
```

**CI/CD流程**:
1. 代码提交触发构建
2. Maven 编译打包
3. 单元测试 + 集成测试
4. 构建 Docker 镜像
5. 推送到镜像仓库
6. 部署到 Kubernetes

### 7.3 监控告警
- **应用监控**: Spring Boot Admin + Prometheus + Grafana
- **链路追踪**: SkyWalking / Zipkin
- **日志收集**: ELK Stack(Elasticsearch + Logstash + Kibana) / Loki
- **性能监控**: 接口响应时间、错误率、JVM 指标
- **告警通知**: 企业微信/钉钉/短信/邮件

## 8. 目录结构

### 8.1 后端目录结构
```
jiazheng-backend/
├── src/main/java/com/jiazheng/
│   ├── JiazhengApplication.java    # 启动类
│   ├── config/                     # 配置类
│   │   ├── WebConfig.java          # Web 配置
│   │   ├── SwaggerConfig.java      # Swagger 配置
│   │   ├── RedisConfig.java        # Redis 配置
│   │   └── WxConfig.java           # 微信配置
│   ├── controller/                 # 控制器层
│   │   ├── AuthController.java     # 认证接口
│   │   ├── UserController.java     # 用户接口
│   │   ├── DemandController.java   # 需求接口
│   │   ├── OrderController.java    # 订单接口
│   │   ├── ReviewController.java   # 评价接口
│   │   └── FileController.java     # 文件接口
│   ├── service/                    # 业务逻辑层
│   │   ├── UserService.java
│   │   ├── DemandService.java
│   │   ├── OrderService.java
│   │   ├── ReviewService.java
│   │   └── MessageService.java
│   ├── service/impl/               # 业务实现层
│   │   ├── UserServiceImpl.java
│   │   └── ...
│   ├── mapper/                     # MyBatis Mapper 层
│   │   ├── UserMapper.java
│   │   ├── DemandMapper.java
│   │   └── ...
│   ├── entity/                     # 实体类
│   │   ├── User.java
│   │   ├── Demand.java
│   │   ├── Order.java
│   │   └── ...
│   ├── dto/                        # 数据传输对象
│   │   ├── request/                # 请求 DTO
│   │   │   ├── LoginRequest.java
│   │   │   ├── CreateDemandRequest.java
│   │   │   └── ...
│   │   └── response/               # 响应 DTO
│   │       ├── UserResponse.java
│   │       ├── DemandListResponse.java
│   │       └── ...
│   ├── common/                     # 公共组件
│   │   ├── Result.java             # 统一响应
│   │   ├── Constants.java          # 常量定义
│   │   ├── enums/                  # 枚举类
│   │   │   ├── UserStatusEnum.java
│   │   │   ├── DemandStatusEnum.java
│   │   │   └── ...
│   │   └── exception/              # 异常处理
│   │       ├── BusinessException.java
│   │       └── GlobalExceptionHandler.java
│   ├── util/                       # 工具类
│   │   ├── JwtUtil.java            # JWT 工具
│   │   ├── WxUtil.java             # 微信工具
│   │   └── OssUtil.java            # OSS 工具
│   └── aspect/                     # 切面
│       ├── LogAspect.java          # 日志切面
│       └── AuthAspect.java         # 认证切面
├── src/main/resources/
│   ├── application.yml             # 主配置文件
│   ├── application-dev.yml         # 开发环境配置
│   ├── application-prod.yml        # 生产环境配置
│   ├── mapper/                     # MyBatis XML
│   │   ├── UserMapper.xml
│   │   └── ...
│   └── static/                     # 静态资源
├── src/test/java/                  # 测试代码
├── pom.xml                         # Maven 配置
├── Dockerfile                      # Docker 镜像
└── README.md
```

### 8.2 小程序目录结构
```
jiazheng-miniprogram/
├── pages/               # 页面
│   ├── index/          # 首页
│   ├── demand/         # 需求相关
│   ├── publish/        # 发布
│   ├── mine/           # 我的
│   └── login/          # 登录
├── components/          # 组件
├── utils/              # 工具函数
├── api/                # API 封装
├── store/              # 状态管理
├── styles/             # 全局样式
├── app.js              # 小程序入口
├── app.json            # 小程序配置
└── project.config.json # 项目配置
```

### 8.3 管理后台目录结构
```
jiazheng-admin/
├── src/
│   ├── views/          # 页面
│   ├── components/     # 组件
│   ├── api/            # API 封装
│   ├── store/          # 状态管理
│   ├── router/         # 路由配置
│   ├── utils/          # 工具函数
│   ├── styles/         # 样式
│   ├── App.vue         # 根组件
│   └── main.js         # 应用入口
├── public/             # 静态资源
├── package.json
└── vite.config.js      # Vite 配置
```

## 9. 开发规范

### 9.1 代码规范
- **命名规范**: 驼峰命名、语义化命名
- **注释规范**: 关键逻辑必须注释
- **提交规范**: Conventional Commits

### 9.2 接口规范
- **RESTful 风格**: 资源导向的 URL 设计
- **统一响应格式**: 
```java
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public static <T> Result<T> success() {
        return success(null);
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}
```

### 9.3 错误处理
- **统一错误码**: 业务错误码定义
- **错误日志**: 完整记录错误堆栈
- **用户提示**: 友好的错误提示

## 10. 总结

本架构设计采用前后端分离模式，基于微服务理念进行模块化拆分。技术选型成熟稳定，具备良好的可扩展性和可维护性。通过合理的分层设计和规范化开发，确保项目高质量交付。

---

**版本**: v1.0  
**更新日期**: 2026-03-26  
**作者**: AI 架构师
