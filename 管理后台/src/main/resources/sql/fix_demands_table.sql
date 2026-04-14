-- 修复 demands 表结构 - 添加缺失的字段
-- 日期：2026-03-27

USE `jiazheng_miniapp`;

-- 添加 publisher_id 字段
ALTER TABLE `demands` 
ADD COLUMN `publisher_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '发布者 ID' AFTER `images`;

-- 添加 taker_id 字段
ALTER TABLE `demands` 
ADD COLUMN `taker_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '接单者 ID' AFTER `publisher_id`;
