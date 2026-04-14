SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `orders` (
    `order_no`, `demand_id`, `publisher_id`, `taker_id`, `category_id`,
    `title`, `description`, `service_time`, `province`, `city`, `district`,
    `address`, `actual_price`, `price_unit`, `actual_duration`, `total_amount`,
    `payment_status`, `payment_method`, `paid_at`, `status`,
    `start_time`, `end_time`, `confirmed_at`, `completed_at`,
    `cancelled_at`, `cancel_reason`, `cancel_by`, `remark`,
    `created_at`, `updated_at`
)
SELECT 
    CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(seq, 6, '0')),
    FLOOR(1 + RAND() * 400),
    FLOOR(1 + RAND() * 300),
    MOD(FLOOR(1 + RAND() * 300) + seq, 300) + 1,
    FLOOR(1 + RAND() * 5),
    CONCAT('Order-', seq),
    CONCAT('Description for order ', seq),
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
    NULL, NULL, NULL,
    CASE WHEN RAND() < 0.07 THEN DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY) ELSE NULL END,
    CASE WHEN RAND() < 0.07 THEN 'Cancelled' ELSE NULL END,
    CASE WHEN RAND() < 0.07 THEN FLOOR(1 + RAND() * 300) ELSE NULL END,
    CONCAT('Remark-', seq),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    NOW()
FROM (
    SELECT @row := @row + 1 AS seq 
    FROM information_schema.COLUMNS, (SELECT @row := 3) AS init 
    LIMIT 400
) AS numbers
WHERE seq <= 400;

SET FOREIGN_KEY_CHECKS = 1;
