-- Generate 400 orders test data
SET FOREIGN_KEY_CHECKS = 0;

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_400_orders $$

CREATE PROCEDURE generate_400_orders()
BEGIN
    DECLARE i INT DEFAULT 1;
    
    WHILE i <= 400 DO
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
            CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(i, 6, '0')),
            FLOOR(1 + RAND() * 400),
            FLOOR(1 + RAND() * 300),
            CASE 
                WHEN FLOOR(1 + RAND() * 300) = FLOOR(1 + RAND() * 300) 
                THEN FLOOR(1 + RAND() * 300) + 1 
                ELSE FLOOR(1 + RAND() * 300) 
            END,
            FLOOR(1 + RAND() * 5),
            CONCAT(ELT(FLOOR(1 + RAND() * 6), 'Daily Cleaning', 'Deep Cleaning', 'Cooking', 'Nanny', 'Elder Care'), '-Order-', i),
            CONCAT('This is order #', i, '. Professional service needed.'),
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
            'Guangdong',
            'Shenzhen',
            ELT(FLOOR(1 + RAND() * 7), 'Nanshan', 'Futian', 'Luohu', 'Baoan', 'Longgang', 'Longhua', 'Guangming'),
            CONCAT('Street ', FLOOR(1 + RAND() * 999)),
            ROUND(50 + RAND() * 450, 2),
            ELT(FLOOR(1 + RAND() * 3), 'hour', 'day', 'month'),
            ROUND(2 + RAND() * 8, 2),
            ROUND((50 + RAND() * 450) * (2 + RAND() * 8), 2),
            CASE WHEN RAND() < 0.1 THEN 0 ELSE 1 END,
            FLOOR(1 + RAND() * 3),
            CASE WHEN RAND() >= 0.1 THEN DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 28) DAY) ELSE NULL END,
            CASE 
                WHEN RAND() < 0.15 THEN 1
                WHEN RAND() < 0.35 THEN 2
                WHEN RAND() < 0.75 THEN 3
                WHEN RAND() < 0.90 THEN 4
                WHEN RAND() < 0.97 THEN 5
                ELSE 6
            END,
            CASE WHEN RAND() >= 0.2 THEN DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 27) DAY) ELSE NULL END,
            NULL,
            NULL,
            NULL,
            CASE WHEN RAND() < 0.07 THEN DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY) ELSE NULL END,
            CASE WHEN RAND() < 0.07 THEN 'Personal reason' ELSE NULL END,
            CASE WHEN RAND() < 0.07 THEN FLOOR(1 + RAND() * 300) ELSE NULL END,
            CONCAT('Remark-', i),
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
            NOW()
        );
        
        SET i = i + 1;
    END WHILE;
END $$

DELIMITER ;

CALL generate_400_orders();

DROP PROCEDURE IF EXISTS generate_400_orders;

SET FOREIGN_KEY_CHECKS = 1;

SELECT COUNT(*) AS total_orders FROM orders;
