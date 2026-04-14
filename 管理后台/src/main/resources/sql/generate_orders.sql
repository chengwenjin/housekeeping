-- ========================================
-- 生成 400 条订单测试数据
-- 执行时间：2026-03-28
-- ========================================

-- 清空现有订单数据（可选，谨慎执行）
-- TRUNCATE TABLE `orders`;

-- 设置字符集
SET NAMES utf8mb4;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 使用存储过程批量生成订单数据
-- ========================================

DELIMITER $$

-- 删除已存在的存储过程
DROP PROCEDURE IF EXISTS generate_orders $$

-- 创建存储过程
CREATE PROCEDURE generate_orders()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE order_no VARCHAR(64);
    DECLARE demand_id BIGINT;
    DECLARE publisher_id BIGINT;
    DECLARE taker_id BIGINT;
    DECLARE category_id INT;
    DECLARE title VARCHAR(128);
    DECLARE description TEXT;
    DECLARE service_time DATETIME;
    DECLARE province VARCHAR(32);
    DECLARE city VARCHAR(32);
    DECLARE district VARCHAR(32);
    DECLARE address VARCHAR(256);
    DECLARE actual_price DECIMAL(10,2);
    DECLARE price_unit VARCHAR(16);
    DECLARE actual_duration DECIMAL(5,2);
    DECLARE total_amount DECIMAL(10,2);
    DECLARE payment_status TINYINT;
    DECLARE payment_method TINYINT;
    DECLARE paid_at DATETIME;
    DECLARE status TINYINT;
    DECLARE start_time DATETIME;
    DECLARE end_time DATETIME;
    DECLARE confirmed_at DATETIME;
    DECLARE completed_at DATETIME;
    DECLARE cancelled_at DATETIME;
    DECLARE cancel_reason VARCHAR(512);
    DECLARE cancel_by BIGINT;
    DECLARE remark VARCHAR(512);
    DECLARE created_at DATETIME;
    
    -- 开始循环
    WHILE i <= 400 DO
        -- 生成订单编号 (格式：ORD + 时间戳 + 随机数)
        SET order_no = CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(i, 6, '0'));
        
        -- 随机选择需求 ID (1-400 之间)
        SET demand_id = FLOOR(1 + RAND() * 400);
        
        -- 随机选择发布者 ID (1-300 之间)
        SET publisher_id = FLOOR(1 + RAND() * 300);
        
        -- 随机选择接单者 ID (1-300 之间，不能是自己)
        SET taker_id = FLOOR(1 + RAND() * 300);
        IF taker_id = publisher_id THEN
            SET taker_id = taker_id + 1;
            IF taker_id > 300 THEN
                SET taker_id = 1;
            END IF;
        END IF;
        
        -- 随机分类 ID (1-5 之间)
        SET category_id = FLOOR(1 + RAND() * 5);
        
        -- 生成订单标题
        SET title = CONCAT(
            ELT(FLOOR(1 + RAND() * 6), 
                '日常保洁', '深度保洁', '做饭阿姨', '育儿嫂', '老人照护'
            ), 
            '订单-', i
        );
        
        -- 生成订单描述
        SET description = CONCAT(
            '这是第', i, '个订单的详细描述。',
            ELT(FLOOR(1 + RAND() * 4),
                '需要专业的家政服务，要求认真负责。',
                '希望找一位有经验的阿姨，工作内容包括打扫卫生、做饭等。',
                '急寻家政服务人员，价格面议，待遇从优。',
                '长期需要家政服务，要求身体健康，无不良嗜好。'
            )
        );
        
        -- 随机服务时间（过去 30 天内）
        SET service_time = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY);
        
        -- 地址信息
        SET province = '广东省';
        SET city = '深圳市';
        SET district = ELT(FLOOR(1 + RAND() * 7), 
            '南山区', '福田区', '罗湖区', '宝安区', '龙岗区', '龙华区', '光明区'
        );
        SET address = CONCAT(district, ' ', FLOOR(1 + RAND() * 999), '号');
        
        -- 价格信息
        SET actual_price = ROUND(50 + RAND() * 450, 2); -- 50-500 之间
        SET price_unit = ELT(FLOOR(1 + RAND() * 3), '小时', '天', '月');
        SET actual_duration = ROUND(2 + RAND() * 8, 2); -- 2-10 小时
        SET total_amount = ROUND(actual_price * actual_duration, 2);
        
        -- 支付状态 (0:未支付 10%, 1:已支付 90%)
        IF RAND() < 0.1 THEN
            SET payment_status = 0;
            SET paid_at = NULL;
        ELSE
            SET payment_status = 1;
            SET paid_at = DATE_ADD(service_time, INTERVAL FLOOR(RAND() * 2) DAY);
        END IF;
        
        -- 支付方式
        SET payment_method = FLOOR(1 + RAND() * 3); -- 1:微信 2:支付宝 3:银行卡
        
        -- 订单状态分布:
        -- 1:待服务 15%
        -- 2:服务中 20%
        -- 3:服务完成 40%
        -- 4:已评价 15%
        -- 5:已取消 7%
        -- 6:退款/售后 3%
        SET status = CASE 
            WHEN RAND() < 0.15 THEN 1
            WHEN RAND() < 0.35 THEN 2
            WHEN RAND() < 0.75 THEN 3
            WHEN RAND() < 0.90 THEN 4
            WHEN RAND() < 0.97 THEN 5
            ELSE 6
        END;
        
        -- 根据状态设置相关时间
        IF status >= 2 THEN
            SET start_time = DATE_ADD(service_time, INTERVAL FLOOR(RAND() * 3) DAY);
        ELSE
            SET start_time = NULL;
        END IF;
        
        IF status >= 3 THEN
            SET end_time = DATE_ADD(start_time, INTERVAL FLOOR(1 + RAND() * 8) HOUR);
            SET confirmed_at = end_time;
            SET completed_at = DATE_ADD(end_time, INTERVAL FLOOR(RAND() * 2) DAY);
        ELSE
            SET end_time = NULL;
            SET confirmed_at = NULL;
            SET completed_at = NULL;
        END IF;
        
        IF status = 4 THEN
            SET completed_at = DATE_ADD(completed_at, INTERVAL FLOOR(RAND() * 3) DAY);
        END IF;
        
        IF status = 5 THEN
            SET cancelled_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY);
            SET cancel_reason = ELT(FLOOR(1 + RAND() * 5),
                '临时有事不需要了',
                '找到了更合适的服务员',
                '价格谈不拢',
                '时间安排冲突',
                '其他原因'
            );
            SET cancel_by = publisher_id;
        ELSE
            SET cancelled_at = NULL;
            SET cancel_reason = NULL;
            SET cancel_by = NULL;
        END IF;
        
        -- 备注
        SET remark = CONCAT('订单备注-', i);
        
        -- 创建时间（过去 30 天内）
        SET created_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY);
        
        -- 插入订单记录
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
            order_no,
            demand_id,
            publisher_id,
            taker_id,
            category_id,
            title,
            description,
            service_time,
            province,
            city,
            district,
            address,
            actual_price,
            price_unit,
            actual_duration,
            total_amount,
            payment_status,
            payment_method,
            paid_at,
            status,
            start_time,
            end_time,
            confirmed_at,
            completed_at,
            cancelled_at,
            cancel_reason,
            cancel_by,
            remark,
            created_at,
            NOW()
        );
        
        SET i = i + 1;
    END WHILE;
END $$

DELIMITER ;

-- 调用存储过程生成数据
CALL generate_orders();

-- 删除存储过程
DROP PROCEDURE IF EXISTS generate_orders;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 验证生成的数据
SELECT 
    COUNT(*) AS total_orders,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS pending_service,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS in_service,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS reviewed,
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS cancelled,
    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS refund,
    AVG(total_amount) AS avg_amount,
    SUM(total_amount) AS total_amount
FROM orders;

-- 显示前 10 条数据
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
