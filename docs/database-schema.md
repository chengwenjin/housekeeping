# 家政小程序 - 数据库表结构设计文档

## 1. 数据库设计原则

### 1.1 设计规范
- 使用 InnoDB 存储引擎，支持事务和外键
- 字符集使用 utf8mb4，支持 emoji 表情
- 所有表必须包含主键 id、创建时间 created_at、更新时间 updated_at
- 使用软删除 (deleted_at) 而非物理删除重要业务数据
- 合理设计索引，避免过度索引

### 1.2 命名规范
- 表名：小写字母 + 下划线，复数形式 (如：users, demands)
- 字段名：小写字母 + 下划线
- 主键：统一命名为 id
- 外键：关联表名单数_关联字段 (如：user_id, demand_id)

## 2. 核心业务表

### 2.1 用户表 (users)

**用途**: 存储用户基本信息

```sql
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `openid` VARCHAR(64) NOT NULL COMMENT '微信 openid',
  `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信 unionid(多应用统一标识)',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
  `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像 URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号 (加密存储)',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `id_card` VARCHAR(32) DEFAULT NULL COMMENT '身份证号 (加密存储)',
  `certification_status` TINYINT DEFAULT 0 COMMENT '认证状态 0-未认证 1-认证中 2-已认证 3-认证失败',
  `bio` VARCHAR(512) DEFAULT NULL COMMENT '个人简介',
  `score` DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
  `total_orders` INT UNSIGNED DEFAULT 0 COMMENT '累计接单数',
  `follower_count` INT UNSIGNED DEFAULT 0 COMMENT '粉丝数',
  `following_count` INT UNSIGNED DEFAULT 0 COMMENT '关注数',
  `published_count` INT UNSIGNED DEFAULT 0 COMMENT '发布需求数',
  `taken_count` INT UNSIGNED DEFAULT 0 COMMENT '接单数',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录 IP',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间 (软删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_nickname` (`nickname`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 2.2 服务分类表 (categories)

**用途**: 家政服务分类

```sql
CREATE TABLE `categories` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
  `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(256) DEFAULT NULL COMMENT '图标 emoji 或 URL',
  `parent_id` INT UNSIGNED DEFAULT 0 COMMENT '父分类 ID 0-一级分类',
  `level` TINYINT DEFAULT 1 COMMENT '层级 1-一级 2-二级',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重 (越大越靠前)',
  `is_hot` TINYINT DEFAULT 0 COMMENT '是否热门 0-否 1-是',
  `demand_count` INT UNSIGNED DEFAULT 0 COMMENT '需求数量',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务分类表';
```

**初始化数据**:
```sql
INSERT INTO `categories` (`name`, `icon`, `level`, `sort_order`, `is_hot`) VALUES
('保洁', '🧹', 1, 100, 1),
('烹饪', '🍳', 1, 90, 1),
('育儿嫂', '👶', 1, 80, 1),
('老人照护', '🧓', 1, 70, 1),
('搬运', '📦', 1, 60, 0),
('宠物服务', '🐾', 1, 50, 0),
('维修服务', '🔧', 1, 40, 0),
('家电清洗', '🧼', 1, 30, 0);
```

### 2.3 需求表 (demands)

**用途**: 存储用户发布的家政需求

```sql
CREATE TABLE `demands` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '需求 ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '发布者 ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类 ID',
  `title` VARCHAR(128) NOT NULL COMMENT '标题',
  `description` TEXT NOT NULL COMMENT '详细描述',
  `service_type` TINYINT DEFAULT 1 COMMENT '服务类型 1-小时工 2-长期工 3-临时工',
  `expected_price` DECIMAL(10,2) NOT NULL COMMENT '预期价格',
  `price_unit` VARCHAR(16) DEFAULT '小时' COMMENT '价格单位 小时/天/月/次',
  `min_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '最小时长 (小时)',
  `max_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '最大时长 (小时)',
  `province` VARCHAR(32) DEFAULT NULL COMMENT '省份',
  `city` VARCHAR(32) DEFAULT NULL COMMENT '城市',
  `district` VARCHAR(32) DEFAULT NULL COMMENT '区县',
  `address` VARCHAR(256) DEFAULT NULL COMMENT '详细地址',
  `latitude` DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
  `service_time` DATETIME DEFAULT NULL COMMENT '服务时间',
  `service_time_desc` VARCHAR(128) DEFAULT NULL COMMENT '服务时间描述 (如：明天上午)',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话 (可选)',
  `images` JSON DEFAULT NULL COMMENT '图片 URL 数组',
  `view_count` INT UNSIGNED DEFAULT 0 COMMENT '浏览次数',
  `footprint_count` INT UNSIGNED DEFAULT 0 COMMENT '足迹数',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-招募中 2-已接单 3-进行中 4-已完成 5-已取消 6-已过期',
  `taken_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '接单者 ID',
  `taken_at` DATETIME DEFAULT NULL COMMENT '接单时间',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancelled_at` DATETIME DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` VARCHAR(512) DEFAULT NULL COMMENT '取消原因',
  `remarks` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_taken_by` (`taken_by`),
  KEY `idx_location` (`city`, `district`),
  KEY `idx_created_at` (`created_at`),
  FULLTEXT KEY `ft_title_description` (`title`, `description`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求表';
```

### 2.4 订单表 (orders)

**用途**: 记录需求被接单后形成的订单

```sql
CREATE TABLE `orders` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号 (唯一)',
  `demand_id` BIGINT UNSIGNED NOT NULL COMMENT '需求 ID',
  `publisher_id` BIGINT UNSIGNED NOT NULL COMMENT '发布者 ID',
  `taker_id` BIGINT UNSIGNED NOT NULL COMMENT '接单者 ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类 ID',
  `title` VARCHAR(128) NOT NULL COMMENT '订单标题',
  `description` TEXT NOT NULL COMMENT '订单描述',
  `service_time` DATETIME DEFAULT NULL COMMENT '服务时间',
  `province` VARCHAR(32) DEFAULT NULL COMMENT '省份',
  `city` VARCHAR(32) DEFAULT NULL COMMENT '城市',
  `district` VARCHAR(32) DEFAULT NULL COMMENT '区县',
  `address` VARCHAR(256) DEFAULT NULL COMMENT '服务地址',
  `latitude` DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
  `actual_price` DECIMAL(10,2) NOT NULL COMMENT '实际成交价格',
  `price_unit` VARCHAR(16) DEFAULT '小时' COMMENT '价格单位',
  `actual_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '实际服务时长',
  `total_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '订单总金额',
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付 2-已退款',
  `payment_method` TINYINT DEFAULT 0 COMMENT '支付方式 0-线下 1-微信支付',
  `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT DEFAULT 1 COMMENT '订单状态 1-待服务 2-服务中 3-待确认 4-已完成 5-已取消 6-已评价',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始服务时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束服务时间',
  `confirmed_at` DATETIME DEFAULT NULL COMMENT '确认完成时间',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancelled_at` DATETIME DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` VARCHAR(512) DEFAULT NULL COMMENT '取消原因',
  `cancel_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '取消操作人 ID',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_demand_id` (`demand_id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_taker_id` (`taker_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 2.5 评价表 (reviews)

**用途**: 用户对订单的评价

```sql
CREATE TABLE `reviews` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价 ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单 ID',
  `demand_id` BIGINT UNSIGNED NOT NULL COMMENT '需求 ID',
  `reviewer_id` BIGINT UNSIGNED NOT NULL COMMENT '评价者 ID',
  `reviewee_id` BIGINT UNSIGNED NOT NULL COMMENT '被评价者 ID',
  `review_type` TINYINT DEFAULT 1 COMMENT '评价类型 1-对服务者 2-对发布者',
  `rating` TINYINT NOT NULL COMMENT '评分 1-5 分',
  `content` VARCHAR(1024) NOT NULL COMMENT '评价内容',
  `images` JSON DEFAULT NULL COMMENT '评价图片',
  `reply_content` VARCHAR(512) DEFAULT NULL COMMENT '回复内容',
  `reply_at` DATETIME DEFAULT NULL COMMENT '回复时间',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名 0-否 1-是',
  `helpful_count` INT UNSIGNED DEFAULT 0 COMMENT '有用次数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_demand_id` (`demand_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_reviewee_id` (`reviewee_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';
```

### 2.6 足迹表 (footprints)

**用途**: 记录用户浏览需求的历史

```sql
CREATE TABLE `footprints` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '足迹 ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
  `demand_id` BIGINT UNSIGNED NOT NULL COMMENT '需求 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_demand` (`user_id`, `demand_id`),
  KEY `idx_user_id_created_at` (`user_id`, `created_at`),
  KEY `idx_demand_id` (`demand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='足迹表';
```

### 2.7 关注表 (follows)

**用途**: 记录用户之间的关注关系

```sql
CREATE TABLE `follows` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关注 ID',
  `follower_id` BIGINT UNSIGNED NOT NULL COMMENT '关注者 ID',
  `followed_id` BIGINT UNSIGNED NOT NULL COMMENT '被关注者 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_id`, `followed_id`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_followed_id` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';
```

### 2.8 消息通知表 (notifications)

**用途**: 站内消息通知

```sql
CREATE TABLE `notifications` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知 ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '接收用户 ID',
  `type` TINYINT NOT NULL COMMENT '通知类型 1-系统通知 2-接单通知 3-订单状态变更 4-评价通知',
  `title` VARCHAR(128) NOT NULL COMMENT '标题',
  `content` VARCHAR(512) NOT NULL COMMENT '内容',
  `related_type` VARCHAR(32) DEFAULT NULL COMMENT '关联类型 demand/order/review',
  `related_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联 ID',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
```

## 3. 管理相关表

### 3.1 管理员表 (admins)

**用途**: 后台管理系统用户

```sql
CREATE TABLE `admins` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员 ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码 (bcrypt 加密)',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `role_id` INT UNSIGNED DEFAULT NULL COMMENT '角色 ID',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录 IP',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';
```

### 3.2 角色表 (roles)

**用途**: 后台管理角色

```sql
CREATE TABLE `roles` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `permissions` JSON DEFAULT NULL COMMENT '权限列表',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
```

### 3.3 操作日志表 (operation_logs)

**用途**: 记录后台管理操作日志

```sql
CREATE TABLE `operation_logs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `admin_id` INT UNSIGNED NOT NULL COMMENT '管理员 ID',
  `action` VARCHAR(64) NOT NULL COMMENT '操作动作',
  `module` VARCHAR(64) DEFAULT NULL COMMENT '模块',
  `method` VARCHAR(16) DEFAULT NULL COMMENT '请求方法',
  `url` VARCHAR(512) DEFAULT NULL COMMENT '请求 URL',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP 地址',
  `user_agent` VARCHAR(512) DEFAULT NULL COMMENT 'User-Agent',
  `request_data` JSON DEFAULT NULL COMMENT '请求参数',
  `response_code` INT DEFAULT NULL COMMENT '响应状态码',
  `response_data` JSON DEFAULT NULL COMMENT '响应数据',
  `duration` INT DEFAULT NULL COMMENT '耗时 (ms)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_action` (`action`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
```

## 4. 系统配置表

### 4.1 系统配置表 (system_configs)

**用途**: 存储系统配置项

```sql
CREATE TABLE `system_configs` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置 ID',
  `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `config_type` TINYINT DEFAULT 1 COMMENT '类型 1-字符串 2-数字 3-布尔 4-JSON',
  `group` VARCHAR(64) DEFAULT NULL COMMENT '分组',
  `description` VARCHAR(256) DEFAULT NULL COMMENT '描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_group` (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
```

**初始化数据**:
```sql
INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `group`, `description`) VALUES
('wechat_appid', '', 1, 'wechat', '微信小程序 AppID'),
('wechat_secret', '', 1, 'wechat', '微信小程序 Secret'),
('oss_access_key', '', 1, 'oss', 'OSS AccessKey'),
('oss_secret_key', '', 1, 'oss', 'OSS SecretKey'),
('oss_bucket', '', 1, 'oss', 'OSS Bucket 名称'),
('oss_endpoint', '', 1, 'oss', 'OSS Endpoint'),
('default_avatar', '/images/default-avatar.png', 1, 'system', '默认头像'),
('max_image_size', '5', 2, 'system', '图片最大大小 (MB)'),
('demand_expire_days', '30', 2, 'system', '需求过期天数');
```

## 5. 表关系说明

### 5.1 ER 图

```
┌─────────────┐       ┌─────────────┐
│   users     │       │ categories  │
└─────────────┘       └─────────────┘
      │                       │
      │ 发布                  │ 属于
      ▼                       ▼
┌─────────────┐       ┌─────────────┐
│   demands   │◄──────│   orders    │
└─────────────┘       └─────────────┘
      │                    │  ▲
      │ 包含               │  │
      ▼                    │  │
┌─────────────┐            │  │
│  footprints │            │  │
└─────────────┘            │  │
                           │  │
      ┌────────────────────┘  │
      │ 评价                  │ 接单
      ▼                       │
┌─────────────┐              │
│   reviews   │──────────────┘
└─────────────┘
```

### 5.2 关系说明

1. **用户 - 需求**: 一对多，一个用户可以发布多个需求
2. **用户 - 订单**: 一对多，一个用户可以有多个订单 (作为发布者或接单者)
3. **分类 - 需求**: 一对多，一个分类下有多个需求
4. **需求 - 足迹**: 一对多，一个需求可以被多个用户浏览
5. **需求 - 评价**: 一对多，一个需求可以有多个评价
6. **订单 - 评价**: 一对一，一个订单对应一个评价
7. **用户 - 用户 (关注)**: 多对多，通过 follows 表关联

## 6. 索引优化建议

### 6.1 查询频繁字段
- `demands.status`: 状态筛选
- `demands.category_id`: 分类筛选
- `demands.city/district`: 地区筛选
- `orders.taker_id/publisher_id`: 订单查询
- `users.openid`: 登录查询

### 6.2 组合索引
- `demands(city, district, status, created_at)`: 地理位置 + 状态 + 时间排序
- `orders(taker_id, status, created_at)`: 我的订单查询
- `footprints(user_id, created_at)`: 足迹列表

### 6.3 全文索引
- `demands(title, description)`: 搜索功能

## 7. 数据字典

### 7.1 通用状态枚举

**需求/订单状态**:
- 1: 招募中/待服务
- 2: 已接单/服务中
- 3: 进行中/待确认
- 4: 已完成
- 5: 已取消
- 6: 已评价/已过期

**用户认证状态**:
- 0: 未认证
- 1: 认证中
- 2: 已认证
- 3: 认证失败

### 7.2 数据类型说明

- **DECIMAL(10,2)**: 金额，精确到分
- **DECIMAL(5,2)**: 时长，精确到 0.01 小时
- **BIGINT UNSIGNED**: ID 类型，支持大数据量
- **VARCHAR(512)**: URL 存储，考虑 CDN 长路径
- **JSON**: 灵活存储 (图片数组、配置等)

## 8. 扩展性设计

### 8.1 预留字段
- `demands.remarks`: 备注字段，用于特殊需求
- `users.ext_info`: 可扩展 JSON 字段存储额外信息

### 8.2 分表策略
当数据量过大时，可考虑以下分表方案:
- `footprints` 按月份分表：`footprints_202601`, `footprints_202602`
- `notifications` 按用户 ID 取模分表
- `operation_logs` 按时间范围分表

---

**版本**: v1.0  
**更新日期**: 2026-03-26  
**作者**: AI 数据库架构师
