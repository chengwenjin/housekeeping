-- ============================================
-- 订单评价数据生成脚本
-- 生成时间：2026-04-18
-- 数据量：为每个已完成的订单生成 1 条评价
-- 规则：
--   1. 只处理状态 >= 3 的订单（服务完成、已评价、退款/售后）
--   2. reviewer_id = publisher_id（发布者评价接单者）
--   3. reviewee_id = taker_id（被评价者是接单者）
--   4. 评分随机 3-5 分
--   5. 评价内容随机生成（合理的中文评价）
-- ============================================

-- 设置字符集（防止中文乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 第一步：统计数据
-- ============================================

SELECT '订单统计:' as info;
SELECT 
    COUNT(*) AS total_orders,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS pending_service,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS in_service,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS reviewed,
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS cancelled,
    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS refund
FROM orders;

SELECT '现有评价统计:' as info;
SELECT COUNT(*) AS total_reviews FROM reviews;

-- ============================================
-- 第二步：创建评价内容表
-- ============================================

DROP TEMPORARY TABLE IF EXISTS temp_review_contents;
CREATE TEMPORARY TABLE temp_review_contents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rating TINYINT NOT NULL,
    content VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5 分好评内容
INSERT INTO temp_review_contents (rating, content) VALUES
(5, '服务非常专业，准时到达，态度很好，强烈推荐！'),
(5, '师傅技术很好，干活认真仔细，下次还会再找他。'),
(5, '非常满意的一次服务体验，师傅很有耐心，把家里打扫得干干净净。'),
(5, '预约的时间很准时，师傅干活麻利，效果超出预期，好评！'),
(5, '服务态度好，工作认真负责，价格合理，值得信赖！'),
(5, '师傅很专业，提前沟通确认需求，服务过程很顺利。'),
(5, '非常棒的服务，师傅经验丰富，很快就解决了我的问题。'),
(5, '服务质量很好，师傅手脚麻利，人也很nice，推荐给大家！'),
(5, '整体体验非常好，从预约到服务完成都很顺畅，五星好评！'),
(5, '师傅很敬业，干活细致，效果满意，以后会继续使用。');

-- 4 分好评内容
INSERT INTO temp_review_contents (rating, content) VALUES
(4, '整体还不错，师傅服务态度好，就是稍微晚了一点点。'),
(4, '服务质量可以，师傅很认真，时间安排合理。'),
(4, '比较满意的一次服务，师傅技术不错，沟通也顺畅。'),
(4, '服务过程很顺利，师傅很专业，只是价格稍贵了一点。'),
(4, '整体体验良好，师傅做事认真，值得推荐。'),
(4, '师傅很准时，干活也不错，就是细节处理可以再改进一下。'),
(4, '服务态度很好，工作也比较细致，总体满意。'),
(4, '师傅经验丰富，很快就完成了工作，只是效果基本符合预期。');

-- 3 分一般评价
INSERT INTO temp_review_contents (rating, content) VALUES
(3, '服务一般般吧，没有想象中的好，也不算太差。'),
(3, '师傅准时到了，就是干活速度有点慢，整体还行。'),
(3, '服务基本达标，态度还可以，就是效果一般。'),
(3, '预约过程比较顺利，服务质量中规中矩。'),
(3, '没有特别满意的地方，也没有太失望的地方。');

-- ============================================
-- 第三步：创建存储过程生成评价
-- ============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_reviews_for_orders $$

CREATE PROCEDURE generate_reviews_for_orders()
BEGIN
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_order_id BIGINT;
    DECLARE v_demand_id BIGINT;
    DECLARE v_publisher_id BIGINT;
    DECLARE v_taker_id BIGINT;
    DECLARE v_completed_at DATETIME;
    DECLARE v_rating TINYINT;
    DECLARE v_content VARCHAR(512);
    DECLARE v_is_anonymous TINYINT;
    DECLARE v_created_at DATETIME;
    DECLARE v_counter INT DEFAULT 0;
    DECLARE v_skip_count INT DEFAULT 0;
    
    -- 游标遍历需要生成评价的订单
    -- 条件：status >= 3（服务完成及以上）且该订单还没有评价
    DECLARE cur_orders CURSOR FOR
        SELECT o.id, o.demand_id, o.publisher_id, o.taker_id, o.completed_at
        FROM orders o
        LEFT JOIN reviews r ON o.id = r.order_id
        WHERE o.status >= 3 
          AND r.id IS NULL
        ORDER BY o.id;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;
    
    OPEN cur_orders;
    
    read_loop: LOOP
        FETCH cur_orders INTO v_order_id, v_demand_id, v_publisher_id, v_taker_id, v_completed_at;
        IF v_done THEN
            LEAVE read_loop;
        END IF;
        
        -- 检查 publisher_id 和 taker_id 是否有效
        IF v_publisher_id IS NULL OR v_taker_id IS NULL THEN
            SET v_skip_count = v_skip_count + 1;
            ITERATE read_loop;
        END IF;
        
        -- 随机选择评分分布（60% 5分，30% 4分，10% 3分）
        SET @rand_val = RAND();
        IF @rand_val < 0.6 THEN
            SELECT rating, content INTO v_rating, v_content 
            FROM temp_review_contents 
            WHERE rating = 5 
            ORDER BY RAND() LIMIT 1;
        ELSEIF @rand_val < 0.9 THEN
            SELECT rating, content INTO v_rating, v_content 
            FROM temp_review_contents 
            WHERE rating = 4 
            ORDER BY RAND() LIMIT 1;
        ELSE
            SELECT rating, content INTO v_rating, v_content 
            FROM temp_review_contents 
            WHERE rating = 3 
            ORDER BY RAND() LIMIT 1;
        END IF;
        
        -- 是否匿名（10% 概率匿名）
        SET v_is_anonymous = IF(RAND() < 0.1, 1, 0);
        
        -- 评价时间（订单完成后 1-3 天内）
        IF v_completed_at IS NOT NULL THEN
            SET v_created_at = DATE_ADD(v_completed_at, INTERVAL FLOOR(1 + RAND() * 2) DAY);
        ELSE
            SET v_created_at = DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY);
        END IF;
        
        -- 插入评价
        INSERT INTO `reviews` (
            `order_id`,
            `demand_id`,
            `reviewer_id`,
            `reviewee_id`,
            `review_type`,
            `rating`,
            `content`,
            `is_anonymous`,
            `helpful_count`,
            `created_at`,
            `updated_at`
        ) VALUES (
            v_order_id,
            v_demand_id,
            v_publisher_id,      -- 发布者是评价者
            v_taker_id,           -- 接单者是被评价者
            1,                    -- 评价类型：1=客户评价服务者
            v_rating,
            v_content,
            v_is_anonymous,
            FLOOR(RAND() * 10),   -- 随机有用数 0-9
            v_created_at,
            v_created_at
        );
        
        SET v_counter = v_counter + 1;
        
    END LOOP;
    
    CLOSE cur_orders;
    
    SELECT CONCAT('成功生成 ', v_counter, ' 条评价数据，跳过 ', v_skip_count, ' 条无效订单') as result;
END $$

DELIMITER ;

-- ============================================
-- 第四步：执行存储过程
-- ============================================

CALL generate_reviews_for_orders();

-- ============================================
-- 第五步：清理
-- ============================================

-- 删除存储过程
DROP PROCEDURE IF EXISTS generate_reviews_for_orders;

-- 删除临时表
DROP TEMPORARY TABLE IF EXISTS temp_review_contents;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 第六步：验证结果
-- ============================================

SELECT '生成后评价统计:' as info;
SELECT 
    COUNT(*) AS total_reviews,
    AVG(rating) AS avg_rating,
    SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) AS rating_5,
    SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) AS rating_4,
    SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) AS rating_3,
    SUM(CASE WHEN rating <= 2 THEN 1 ELSE 0 END) AS rating_2_below
FROM reviews;

-- 显示最近生成的评价
SELECT '最近生成的 10 条评价:' as info;
SELECT 
    id,
    order_id,
    reviewer_id,
    reviewee_id,
    rating,
    LEFT(content, 50) as content_preview,
    is_anonymous,
    created_at
FROM reviews
ORDER BY id DESC
LIMIT 10;

-- ============================================
-- 评价类型说明
-- ============================================
-- review_type:
--   1: 客户评价服务者（发布者评价接单者）
--   2: 服务者评价客户（接单者评价发布者）
--
-- rating: 1-5 分
--
-- is_anonymous:
--   0: 不匿名
--   1: 匿名
-- ============================================
