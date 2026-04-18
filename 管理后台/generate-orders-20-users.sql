-- ============================================
-- 订单数据生成脚本
-- 生成时间：2026-04-18
-- 数据量：20个用户 × 5个订单 = 100条订单
-- 规则：
--   1. 从 users 表随机选择 20 个用户作为接单者
--   2. 为每个用户生成 5 个订单
--   3. 订单的需求数据从 demands 表随机选择
--   4. publisher_id = 需求发布者 (demand.user_id)
--   5. taker_id = 随机选择的用户（不能等于 publisher_id）
-- ============================================

-- 设置字符集（防止中文乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 第一步：检查数据是否足够
-- ============================================

-- 检查用户数量
SET @user_count = (SELECT COUNT(*) FROM users);
SET @demand_count = (SELECT COUNT(*) FROM demands);

SELECT 
    @user_count as total_users,
    @demand_count as total_demands;

-- ============================================
-- 第二步：创建临时表存储随机选择的 20 个用户
-- ============================================

DROP TEMPORARY TABLE IF EXISTS temp_taker_users;
CREATE TEMPORARY TABLE temp_taker_users (
    user_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    nickname VARCHAR(64),
    row_num INT AUTO_INCREMENT UNIQUE
);

-- 随机选择 20 个用户
INSERT INTO temp_taker_users (user_id, nickname)
SELECT id, nickname 
FROM users 
WHERE status = 1
ORDER BY RAND() 
LIMIT 20;

SELECT '随机选择的 20 个用户（接单者）:' as info;
SELECT * FROM temp_taker_users;

-- ============================================
-- 第三步：创建存储过程生成订单数据
-- ============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_orders_for_20_users $$

CREATE PROCEDURE generate_orders_for_20_users()
BEGIN
    DECLARE v_user_idx INT DEFAULT 1;
    DECLARE v_order_per_user INT DEFAULT 1;
    DECLARE v_total_users INT DEFAULT 20;
    DECLARE v_orders_per_user INT DEFAULT 5;
    
    DECLARE v_taker_id BIGINT;
    DECLARE v_demand_id BIGINT;
    DECLARE v_publisher_id BIGINT;
    DECLARE v_category_id INT;
    DECLARE v_title VARCHAR(128);
    DECLARE v_description TEXT;
    DECLARE v_expected_price DECIMAL(10,2);
    DECLARE v_price_unit VARCHAR(16);
    DECLARE v_province VARCHAR(32);
    DECLARE v_city VARCHAR(32);
    DECLARE v_district VARCHAR(32);
    DECLARE v_address VARCHAR(256);
    DECLARE v_latitude DECIMAL(10,8);
    DECLARE v_longitude DECIMAL(11,8);
    DECLARE v_service_time DATETIME;
    DECLARE v_actual_price DECIMAL(10,2);
    DECLARE v_actual_duration DECIMAL(5,2);
    DECLARE v_total_amount DECIMAL(10,2);
    DECLARE v_payment_status TINYINT;
    DECLARE v_payment_method TINYINT;
    DECLARE v_paid_at DATETIME;
    DECLARE v_status TINYINT;
    DECLARE v_start_time DATETIME;
    DECLARE v_end_time DATETIME;
    DECLARE v_confirmed_at DATETIME;
    DECLARE v_completed_at DATETIME;
    DECLARE v_cancelled_at DATETIME;
    DECLARE v_cancel_reason VARCHAR(512);
    DECLARE v_cancel_by BIGINT;
    DECLARE v_remark VARCHAR(512);
    DECLARE v_created_at DATETIME;
    DECLARE v_order_no VARCHAR(64);
    
    -- 订单计数器
    DECLARE v_order_counter INT DEFAULT 0;
    
    -- ============================================
    -- 开始循环：20个用户 × 5个订单
    -- ============================================
    SET v_user_idx = 1;
    
    WHILE v_user_idx <= v_total_users DO
        -- 获取当前接单用户 ID
        SELECT user_id INTO v_taker_id 
        FROM temp_taker_users 
        WHERE row_num = v_user_idx;
        
        SET v_order_per_user = 1;
        
        WHILE v_order_per_user <= v_orders_per_user DO
            SET v_order_counter = v_order_counter + 1;
            
            -- ============================================
            -- 随机选择一个需求
            -- ============================================
            -- 需求的发布者不能等于接单者（避免自己接单自己的需求）
            SELECT 
                d.id, d.user_id, d.category_id, d.title, d.description, 
                d.expected_price, d.price_unit, d.province, d.city, 
                d.district, d.address, d.latitude, d.longitude, d.service_time
            INTO 
                v_demand_id, v_publisher_id, v_category_id, v_title, v_description,
                v_expected_price, v_price_unit, v_province, v_city,
                v_district, v_address, v_latitude, v_longitude, v_service_time
            FROM demands d
            WHERE d.status IN (1, 2, 3, 4)  -- 招募中、已接单、进行中、已完成
              AND d.user_id != v_taker_id     -- 发布者不能是接单者
            ORDER BY RAND()
            LIMIT 1;
            
            -- 如果没有找到符合条件的需求，使用第一个需求
            IF v_demand_id IS NULL THEN
                SELECT 
                    d.id, d.user_id, d.category_id, d.title, d.description, 
                    d.expected_price, d.price_unit, d.province, d.city, 
                    d.district, d.address, d.latitude, d.longitude, d.service_time
                INTO 
                    v_demand_id, v_publisher_id, v_category_id, v_title, v_description,
                    v_expected_price, v_price_unit, v_province, v_city,
                    v_district, v_address, v_latitude, v_longitude, v_service_time
                FROM demands d
                ORDER BY RAND()
                LIMIT 1;
            END IF;
            
            -- ============================================
            -- 生成订单编号
            -- ============================================
            SET v_order_no = CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(v_order_counter, 6, '0'));
            
            -- ============================================
            -- 价格和时间设置
            -- ============================================
            -- 实际价格（在预期价格基础上浮动 ±20%）
            SET v_actual_price = ROUND(v_expected_price * (0.8 + RAND() * 0.4), 2);
            SET v_actual_duration = ROUND(2 + RAND() * 8, 2);  -- 2-10 小时
            SET v_total_amount = ROUND(v_actual_price * v_actual_duration, 2);
            
            -- 创建时间（过去 30 天内）
            SET v_created_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY);
            
            -- ============================================
            -- 订单状态分布
            -- 1:待服务 15%, 2:服务中 20%, 3:服务完成 40%, 
            -- 4:已评价 15%, 5:已取消 7%, 6:退款/售后 3%
            -- ============================================
            SET v_status = CASE 
                WHEN RAND() < 0.15 THEN 1
                WHEN RAND() < 0.35 THEN 2
                WHEN RAND() < 0.75 THEN 3
                WHEN RAND() < 0.90 THEN 4
                WHEN RAND() < 0.97 THEN 5
                ELSE 6
            END;
            
            -- ============================================
            -- 根据状态设置时间字段
            -- ============================================
            SET v_service_time = DATE_ADD(v_created_at, INTERVAL FLOOR(1 + RAND() * 7) DAY);
            
            -- 支付状态（已完成或服务中的订单大多已支付）
            IF v_status >= 2 THEN
                SET v_payment_status = 1;
                SET v_paid_at = DATE_ADD(v_created_at, INTERVAL FLOOR(RAND() * 24) HOUR);
            ELSE
                SET v_payment_status = 0;
                SET v_paid_at = NULL;
            END IF;
            
            -- 支付方式
            SET v_payment_method = FLOOR(1 + RAND() * 3);  -- 1:微信 2:支付宝 3:银行卡
            
            -- 根据状态设置时间
            IF v_status >= 2 THEN
                SET v_start_time = DATE_ADD(v_service_time, INTERVAL FLOOR(RAND() * 12) HOUR);
            ELSE
                SET v_start_time = NULL;
            END IF;
            
            IF v_status >= 3 THEN
                SET v_end_time = DATE_ADD(v_start_time, INTERVAL FLOOR(1 + RAND() * 8) HOUR);
                SET v_confirmed_at = v_end_time;
                SET v_completed_at = DATE_ADD(v_end_time, INTERVAL FLOOR(RAND() * 2) DAY);
            ELSE
                SET v_end_time = NULL;
                SET v_confirmed_at = NULL;
                SET v_completed_at = NULL;
            END IF;
            
            -- 取消订单的情况
            IF v_status = 5 THEN
                SET v_cancelled_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY);
                SET v_cancel_reason = ELT(FLOOR(1 + RAND() * 5),
                    '临时有事不需要了',
                    '找到了更合适的服务员',
                    '价格谈不拢',
                    '时间安排冲突',
                    '其他原因'
                );
                SET v_cancel_by = v_publisher_id;
            ELSE
                SET v_cancelled_at = NULL;
                SET v_cancel_reason = NULL;
                SET v_cancel_by = NULL;
            END IF;
            
            -- 备注
            SET v_remark = CONCAT('订单备注-', v_order_counter);
            
            -- ============================================
            -- 插入订单
            -- ============================================
            INSERT INTO `orders` (
                `order_no`,
                `demand_id`,
                `publisher_id`,
                `taker_id`,
                `category_id`,
                `title`,
                `description`,
                `service_time`,
                `province`,
                `city`,
                `district`,
                `address`,
                `latitude`,
                `longitude`,
                `actual_price`,
                `price_unit`,
                `actual_duration`,
                `total_amount`,
                `payment_status`,
                `payment_method`,
                `paid_at`,
                `status`,
                `start_time`,
                `end_time`,
                `confirmed_at`,
                `completed_at`,
                `cancelled_at`,
                `cancel_reason`,
                `cancel_by`,
                `remark`,
                `created_at`,
                `updated_at`
            ) VALUES (
                v_order_no,
                v_demand_id,
                v_publisher_id,
                v_taker_id,
                v_category_id,
                v_title,
                v_description,
                v_service_time,
                v_province,
                v_city,
                v_district,
                v_address,
                v_latitude,
                v_longitude,
                v_actual_price,
                v_price_unit,
                v_actual_duration,
                v_total_amount,
                v_payment_status,
                v_payment_method,
                v_paid_at,
                v_status,
                v_start_time,
                v_end_time,
                v_confirmed_at,
                v_completed_at,
                v_cancelled_at,
                v_cancel_reason,
                v_cancel_by,
                v_remark,
                v_created_at,
                NOW()
            );
            
            SET v_order_per_user = v_order_per_user + 1;
        END WHILE;
        
        SET v_user_idx = v_user_idx + 1;
    END WHILE;
    
    SELECT CONCAT('成功生成 ', v_order_counter, ' 条订单数据') as result;
END $$

DELIMITER ;

-- ============================================
-- 第四步：执行存储过程
-- ============================================

CALL generate_orders_for_20_users();

-- ============================================
-- 第五步：清理和验证
-- ============================================

-- 删除存储过程
DROP PROCEDURE IF EXISTS generate_orders_for_20_users;

-- 删除临时表
DROP TEMPORARY TABLE IF EXISTS temp_taker_users;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 验证生成的数据
-- ============================================

SELECT '订单数据统计:' as info;
SELECT 
    COUNT(*) AS total_orders,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS pending_service,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS in_service,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS reviewed,
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS cancelled,
    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS refund,
    ROUND(AVG(total_amount), 2) AS avg_amount,
    ROUND(SUM(total_amount), 2) AS total_amount
FROM orders;

-- 显示最近生成的订单
SELECT '最近生成的 10 条订单:' as info;
SELECT 
    id,
    order_no,
    title,
    status,
    total_amount,
    publisher_id,
    taker_id,
    created_at
FROM orders
ORDER BY id DESC
LIMIT 10;

-- ============================================
-- 订单状态说明
-- ============================================
-- 状态码说明：
-- 1: 待服务（pending_service）
-- 2: 服务中（in_service）
-- 3: 服务完成（completed）
-- 4: 已评价（reviewed）
-- 5: 已取消（cancelled）
-- 6: 退款/售后（refund）
-- ============================================
