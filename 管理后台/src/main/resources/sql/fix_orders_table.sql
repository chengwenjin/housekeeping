-- 修复 orders 表结构 - 添加缺失的字段
-- 日期：2026-03-27

USE `jiazheng_miniapp`;

-- 添加 service_provider_id 字段
ALTER TABLE `orders` 
ADD COLUMN `service_provider_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '服务者 ID' AFTER `demand_id`;

-- 添加 customer_id 字段
ALTER TABLE `orders` 
ADD COLUMN `customer_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '客户 ID' AFTER `service_provider_id`;

-- 添加 service_type 字段
ALTER TABLE `orders` 
ADD COLUMN `service_type` TINYINT DEFAULT 1 COMMENT '服务类型 1-小时工 2-长期工 3-临时工' AFTER `category_id`;

-- 添加 service_duration 字段
ALTER TABLE `orders` 
ADD COLUMN `service_duration` DECIMAL(5,2) DEFAULT NULL COMMENT '服务时长 (小时)' AFTER `service_type`;

-- 添加 service_price 字段
ALTER TABLE `orders` 
ADD COLUMN `service_price` DECIMAL(10,2) DEFAULT NULL COMMENT '服务单价' AFTER `service_duration`;

-- 添加 provider_remark 字段
ALTER TABLE `orders` 
ADD COLUMN `provider_remark` VARCHAR(512) DEFAULT NULL COMMENT '服务者备注' AFTER `status`;

-- 添加 review_id 字段
ALTER TABLE `orders` 
ADD COLUMN `review_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '评价 ID' AFTER `completed_at`;
