-- 家政小程序数据库初始化脚本
-- 数据库：jiazheng_miniapp
-- 版本：v1.0
-- 日期：2026-03-26

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `jiazheng_miniapp` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `jiazheng_miniapp`;

-- 用户表
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `openid` VARCHAR(64) NOT NULL COMMENT '微信 openid',
  `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信 unionid',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
  `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像 URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `id_card` VARCHAR(32) DEFAULT NULL COMMENT '身份证号',
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
  `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_nickname` (`nickname`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 服务分类表
CREATE TABLE `categories` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
  `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(256) DEFAULT NULL COMMENT '图标 emoji 或 URL',
  `parent_id` INT UNSIGNED DEFAULT 0 COMMENT '父分类 ID 0-一级分类',
  `level` TINYINT DEFAULT 1 COMMENT '层级 1-一级 2-二级',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
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

-- 初始化分类数据
INSERT INTO `categories` (`name`, `icon`, `level`, `sort_order`, `is_hot`) VALUES
('保洁', '🧹', 1, 100, 1),
('烹饪', '🍳', 1, 90, 1),
('育儿嫂', '👶', 1, 80, 1),
('老人照护', '🧓', 1, 70, 1),
('搬运', '📦', 1, 60, 0),
('宠物服务', '🐾', 1, 50, 0),
('维修服务', '🔧', 1, 40, 0),
('家电清洗', '🧼', 1, 30, 0);

-- 需求表
CREATE TABLE `demands` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '需求 ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '发布者 ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类 ID',
  `title` VARCHAR(128) NOT NULL COMMENT '标题',
  `description` TEXT NOT NULL COMMENT '详细描述',
  `service_type` TINYINT DEFAULT 1 COMMENT '服务类型 1-小时工 2-长期工 3-临时工',
  `expected_price` DECIMAL(10,2) NOT NULL COMMENT '预期价格',
  `price_unit` VARCHAR(16) DEFAULT '小时' COMMENT '价格单位',
  `min_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '最小时长',
  `max_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '最大时长',
  `province` VARCHAR(32) DEFAULT NULL COMMENT '省份',
  `city` VARCHAR(32) DEFAULT NULL COMMENT '城市',
  `district` VARCHAR(32) DEFAULT NULL COMMENT '区县',
  `address` VARCHAR(256) DEFAULT NULL COMMENT '详细地址',
  `latitude` DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
  `service_time` DATETIME DEFAULT NULL COMMENT '服务时间',
  `service_time_desc` VARCHAR(128) DEFAULT NULL COMMENT '服务时间描述',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
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
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求表';

-- 订单表
CREATE TABLE `orders` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
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
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态',
  `payment_method` TINYINT DEFAULT 0 COMMENT '支付方式',
  `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT DEFAULT 1 COMMENT '订单状态',
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

-- 评价表
CREATE TABLE `reviews` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价 ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单 ID',
  `demand_id` BIGINT UNSIGNED NOT NULL COMMENT '需求 ID',
  `reviewer_id` BIGINT UNSIGNED NOT NULL COMMENT '评价者 ID',
  `reviewee_id` BIGINT UNSIGNED NOT NULL COMMENT '被评价者 ID',
  `review_type` TINYINT DEFAULT 1 COMMENT '评价类型',
  `rating` TINYINT NOT NULL COMMENT '评分 1-5 分',
  `content` VARCHAR(1024) NOT NULL COMMENT '评价内容',
  `images` JSON DEFAULT NULL COMMENT '评价图片',
  `reply_content` VARCHAR(512) DEFAULT NULL COMMENT '回复内容',
  `reply_at` DATETIME DEFAULT NULL COMMENT '回复时间',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名',
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

-- 足迹表
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

-- 关注表
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

-- 通知表
CREATE TABLE `notifications` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知 ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '接收用户 ID',
  `type` TINYINT NOT NULL COMMENT '通知类型',
  `title` VARCHAR(128) NOT NULL COMMENT '标题',
  `content` VARCHAR(512) NOT NULL COMMENT '内容',
  `related_type` VARCHAR(32) DEFAULT NULL COMMENT '关联类型',
  `related_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联 ID',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
  `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 管理员表
CREATE TABLE `admins` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员 ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `role_id` INT UNSIGNED DEFAULT NULL COMMENT '角色 ID',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录 IP',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 插入默认管理员账号 (密码：admin123 经过 bcrypt 加密)
INSERT INTO `admins` (`username`, `password`, `real_name`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJfLqosKzW8UZB8c0eF7CqRZGm.', '系统管理员', 1);

-- 角色表
CREATE TABLE `roles` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `permissions` JSON DEFAULT NULL COMMENT '权限列表',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 插入默认角色
INSERT INTO `roles` (`name`, `code`, `description`, `permissions`, `status`) VALUES
('超级管理员', 'super_admin', '拥有所有权限', '["*"]', 1),
('运营管理员', 'operation_admin', '负责内容和用户管理', '["user:*", "demand:*", "review:*"]', 1);

-- 操作日志表
CREATE TABLE `operation_logs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `admin_id` INT UNSIGNED NOT NULL COMMENT '管理员 ID',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名',
  `action` VARCHAR(64) NOT NULL COMMENT '操作动作',
  `module` VARCHAR(64) DEFAULT NULL COMMENT '模块',
  `method` VARCHAR(16) DEFAULT NULL COMMENT '请求方法',
  `url` VARCHAR(512) DEFAULT NULL COMMENT '请求 URL',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP 地址',
  `user_agent` VARCHAR(512) DEFAULT NULL COMMENT 'User-Agent',
  `request_data` JSON DEFAULT NULL COMMENT '请求参数',
  `response_code` INT DEFAULT NULL COMMENT '响应状态码',
  `response_data` JSON DEFAULT NULL COMMENT '响应数据',
  `duration` INT DEFAULT NULL COMMENT '耗时 ms',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_username` (`username`),
  KEY `idx_action` (`action`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 系统配置表
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

-- 插入系统配置
INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `group`, `description`) VALUES
('wechat_appid', '', 1, 'wechat', '微信小程序 AppID'),
('wechat_secret', '', 1, 'wechat', '微信小程序 Secret'),
('oss_access_key', '', 1, 'oss', 'OSS AccessKey'),
('oss_secret_key', '', 1, 'oss', 'OSS SecretKey'),
('oss_bucket', '', 1, 'oss', 'OSS Bucket 名称'),
('oss_endpoint', '', 1, 'oss', 'OSS Endpoint'),
('default_avatar', '/images/default-avatar.png', 1, 'system', '默认头像'),
('max_image_size', '5', 2, 'system', '图片最大大小 MB'),
('demand_expire_days', '30', 2, 'system', '需求过期天数');
