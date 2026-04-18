SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 第一步：删除所有评价数据
DELETE FROM reviews WHERE id > 0;

-- 第二步：生成评价数据（使用 CASE 代替临时表）
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
)
SELECT 
    o.id,
    o.demand_id,
    o.publisher_id,
    o.taker_id,
    1,
    -- 评分分布：60% 5分，30% 4分，10% 3分
    CASE 
        WHEN RAND() < 0.6 THEN 5
        WHEN RAND() < 0.9 THEN 4
        ELSE 3
    END,
    -- 评价内容
    CASE 
        WHEN RAND() < 0.1 THEN '服务非常专业，准时到达，态度很好，强烈推荐！'
        WHEN RAND() < 0.2 THEN '师傅技术很好，干活认真仔细，下次还会再找他。'
        WHEN RAND() < 0.3 THEN '非常满意的一次服务体验，师傅很有耐心，把家里打扫得干干净净。'
        WHEN RAND() < 0.4 THEN '预约的时间很准时，师傅干活麻利，效果超出预期，好评！'
        WHEN RAND() < 0.5 THEN '服务态度好，工作认真负责，价格合理，值得信赖！'
        WHEN RAND() < 0.6 THEN '师傅很专业，提前沟通确认需求，服务过程很顺利。'
        WHEN RAND() < 0.7 THEN '非常棒的服务，师傅经验丰富，很快就解决了我的问题。'
        WHEN RAND() < 0.8 THEN '整体还不错，师傅服务态度好，就是稍微晚了一点点。'
        WHEN RAND() < 0.9 THEN '服务质量可以，师傅很认真，时间安排合理。'
        ELSE '服务一般般吧，没有想象中的好，也不算太差。'
    END,
    -- 是否匿名（10% 概率匿名）
    IF(RAND() < 0.1, 1, 0),
    -- 有用数（0-9）
    FLOOR(RAND() * 10),
    -- 创建时间
    IF(o.completed_at IS NOT NULL, 
        DATE_ADD(o.completed_at, INTERVAL FLOOR(1 + RAND() * 2) DAY), 
        DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)
    ),
    IF(o.completed_at IS NOT NULL, 
        DATE_ADD(o.completed_at, INTERVAL FLOOR(1 + RAND() * 2) DAY), 
        DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)
    )
FROM orders o
WHERE o.status >= 3 
  AND o.publisher_id IS NOT NULL 
  AND o.taker_id IS NOT NULL;

-- 第三步：验证
SELECT '验证结果：' as info;
SELECT 
    COUNT(*) AS total_reviews,
    ROUND(AVG(rating), 2) AS avg_rating
FROM reviews;

SELECT id, order_id, rating, LEFT(content, 30) as content_preview 
FROM reviews 
ORDER BY id DESC 
LIMIT 5;
