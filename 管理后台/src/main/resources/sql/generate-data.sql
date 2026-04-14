-- ====================================
-- Generate Test Data
-- Database: jiazheng_miniapp
-- ====================================

USE jiazheng_miniapp;

-- ====================================
-- Part 1: Generate 300 Users
-- ====================================

DELIMITER $$

DROP PROCEDURE IF EXISTS create_users$$
CREATE PROCEDURE create_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    
    START TRANSACTION;
    
    WHILE i <= 300 DO
        INSERT INTO users (
            openid,
            nickname,
            phone,
            avatar_url,
            gender,
            bio,
            score,
            status,
            created_at,
            updated_at
        ) VALUES (
            CONCAT('openid_', LPAD(i, 10, '0')),
            CONCAT('测试用户', i),
            CONCAT('138', LPAD(FLOOR(RAND() * 100000000), 8, '0')),
            CONCAT('https://example.com/avatar/', i, '.jpg'),
            FLOOR(RAND() * 3),
            CONCAT('这是测试用户', i, '的简介'),
            ROUND(4.0 + RAND() * 2, 2),
            1,
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY),
            NOW()
        );
        
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
END$$

DELIMITER ;

CALL create_users();
DROP PROCEDURE IF EXISTS create_users;

SELECT CONCAT('Generated ', COUNT(*), ' users') AS result FROM users;

-- ====================================
-- Part 2: Generate 400 Demands
-- ====================================

DELIMITER $$

DROP PROCEDURE IF EXISTS create_demands$$
CREATE PROCEDURE create_demands()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_user_id BIGINT;
    DECLARE v_category_id INT;
    DECLARE v_service_type TINYINT;
    DECLARE v_status TINYINT;
    
    START TRANSACTION;
    
    WHILE i <= 400 DO
        SET v_user_id = FLOOR(RAND() * 300) + 1;
        SET v_category_id = FLOOR(RAND() * 10) + 1;
        SET v_service_type = FLOOR(RAND() * 3) + 1;
        SET v_status = FLOOR(RAND() * 6) + 1;
        
        INSERT INTO demands (
            user_id,
            category_id,
            title,
            description,
            service_type,
            expected_price,
            price_unit,
            province,
            city,
            district,
            address,
            contact_phone,
            status,
            view_count,
            footprint_count,
            created_at,
            updated_at
        ) VALUES (
            v_user_id,
            v_category_id,
            CONCAT(
                CASE v_service_type
                    WHEN 1 THEN '小时工'
                    WHEN 2 THEN '长期工'
                    WHEN 3 THEN '临时工'
                END,
                '需求-',
                i
            ),
            CONCAT('需要家政服务，具体要求电话联系。这是第', i, '个需求。'),
            v_service_type,
            ROUND(100 + RAND() * 400, 2),
            '小时',
            '广东省',
            '深圳市',
            ELT(FLOOR(RAND() * 6) + 1, '南山区', '福田区', '罗湖区', '盐田区', '宝安区', '龙岗区'),
            CONCAT('详细地址', i, '号'),
            CONCAT('139', LPAD(FLOOR(RAND() * 100000000), 8, '0')),
            v_status,
            FLOOR(RAND() * 100),
            FLOOR(RAND() * 20),
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY),
            NOW()
        );
        
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
END$$

DELIMITER ;

CALL create_demands();
DROP PROCEDURE IF EXISTS create_demands;

SELECT CONCAT('Generated ', COUNT(*), ' demands') AS result FROM demands;

-- ====================================
-- Summary
-- ====================================

SELECT 'Data generation completed!' AS message;
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM demands) AS total_demands;
