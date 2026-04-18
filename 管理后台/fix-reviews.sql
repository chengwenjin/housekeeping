SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 第一步：删除所有评价数据
DELETE FROM reviews WHERE id > 0;

-- 第二步：创建临时表存储评价内容
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

-- 第三步：生成评价数据
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
    (SELECT rating FROM temp_review_contents ORDER BY RAND() LIMIT 1),
    (SELECT content FROM temp_review_contents WHERE rating = (SELECT rating FROM temp_review_contents ORDER BY RAND() LIMIT 1) ORDER BY RAND() LIMIT 1),
    IF(RAND() < 0.1, 1, 0),
    FLOOR(RAND() * 10),
    IF(o.completed_at IS NOT NULL, DATE_ADD(o.completed_at, INTERVAL FLOOR(1 + RAND() * 2) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
    IF(o.completed_at IS NOT NULL, DATE_ADD(o.completed_at, INTERVAL FLOOR(1 + RAND() * 2) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY))
FROM orders o
LEFT JOIN reviews r ON o.id = r.order_id
WHERE o.status >= 3 
  AND r.id IS NULL
  AND o.publisher_id IS NOT NULL 
  AND o.taker_id IS NOT NULL;

-- 第四步：清理
DROP TEMPORARY TABLE IF EXISTS temp_review_contents;

-- 第五步：验证
SELECT 
    COUNT(*) AS total_reviews,
    AVG(rating) AS avg_rating
FROM reviews;

SELECT id, order_id, rating, LEFT(content, 40) as content_preview 
FROM reviews 
ORDER BY id DESC 
LIMIT 10;
