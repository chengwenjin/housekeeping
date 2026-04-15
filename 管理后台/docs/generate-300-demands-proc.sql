-- ============================================
-- 小程序C端需求数据生成脚本（完整版）
-- 生成时间：2026-04-15
-- 数据量：300条需求数据（20个用户 × 15条需求）
-- 执行方式：在MySQL客户端中执行此脚本
-- ============================================

-- 说明：
-- 本脚本使用存储过程为前20个用户生成需求数据
-- 需求分类：保洁(1)、烹饪(2)、育儿嫂(3)、老人照护(4)、搬运(5)、宠物服务(6)、维修服务(7)、家电清洗(8)
-- 服务类型：1=小时工，2=天工，3=包月
-- 状态分布：招募中(40%)、已接单(20%)、进行中(15%)、已完成(20%)、已取消(5%)

-- 切换数据库
USE jiazheng_miniapp_new;

-- 如果存储过程存在则删除
DROP PROCEDURE IF EXISTS generate_demands_data;

-- 创建存储过程生成需求数据
DELIMITER //

CREATE PROCEDURE generate_demands_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE v_publisher_id BIGINT;
    DECLARE v_category_id BIGINT;
    DECLARE v_service_type INT;
    DECLARE v_status INT;
    DECLARE v_title VARCHAR(128);
    DECLARE v_description TEXT;
    DECLARE v_expected_price DECIMAL(10,2);
    DECLARE v_price_unit VARCHAR(16);
    DECLARE v_min_duration DOUBLE;
    DECLARE v_max_duration DOUBLE;
    DECLARE v_city VARCHAR(32);
    DECLARE v_district VARCHAR(32);
    DECLARE v_address VARCHAR(256);
    DECLARE v_contact_name VARCHAR(64);
    DECLARE v_contact_phone VARCHAR(20);
    DECLARE v_view_count INT;
    DECLARE v_footprint_count INT;
    DECLARE v_created_at DATETIME;
    DECLARE v_updated_at DATETIME;
    DECLARE v_service_time DATETIME;
    DECLARE v_taker_id BIGINT;
    
    -- 分类名称映射
    DECLARE v_category_name VARCHAR(64);
    
    -- 循环20个用户
    WHILE i <= 20 DO
        SET v_publisher_id = i;
        
        -- 根据用户ID设置城市和区域
        CASE 
            WHEN i <= 5 THEN
                SET v_city = '北京市';
                SET v_district = '朝阳区';
                SET v_address = CONCAT('建国路', FLOOR(RAND() * 100 + 1), '号');
            WHEN i <= 10 THEN
                SET v_city = '北京市';
                SET v_district = '海淀区';
                SET v_address = CONCAT('中关村大街', FLOOR(RAND() * 100 + 1), '号');
            WHEN i <= 15 THEN
                SET v_city = '北京市';
                SET v_district = '西城区';
                SET v_address = CONCAT('西单北大街', FLOOR(RAND() * 100 + 1), '号');
            ELSE
                SET v_city = '北京市';
                SET v_district = '东城区';
                SET v_address = CONCAT('王府井大街', FLOOR(RAND() * 100 + 1), '号');
        END CASE;
        
        -- 设置联系人信息
        SET v_contact_name = CONCAT('用户', i);
        SET v_contact_phone = CONCAT('138001380', LPAD(i, 2, '0'));
        
        -- 为每个用户生成15条需求
        SET j = 1;
        WHILE j <= 15 DO
            -- 随机选择分类 (1-8)
            SET v_category_id = FLOOR(RAND() * 8 + 1);
            
            -- 根据分类设置标题和描述
            CASE v_category_id
                WHEN 1 THEN
                    SET v_category_name = '保洁';
                    SET v_title = ELT(FLOOR(RAND() * 5 + 1), 
                        '家庭日常保洁', 
                        '深度保洁服务', 
                        '开荒保洁', 
                        '擦玻璃服务', 
                        '厨房油污清理');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT(FLOOR(RAND() * 100 + 80), '㎡房屋需要日常保洁，包括客厅、卧室、厨房、卫生间。要求细致认真，有保洁经验优先。'),
                        '刚装修完，需要深度保洁，重点是擦玻璃和清理装修灰尘。要求使用专业清洁设备。',
                        '新房装修完成，需要开荒保洁。要求彻底清洁，包括窗户、地面、墙面、厨房、卫生间等。',
                        '家里窗户很多，需要专业擦玻璃服务。要求内外都擦，使用专业工具和清洁剂。');
                WHEN 2 THEN
                    SET v_category_name = '烹饪';
                    SET v_title = ELT(FLOOR(RAND() * 5 + 1),
                        '家庭聚餐厨师',
                        '月子餐定制',
                        '生日宴预订',
                        '烹饪教学',
                        '私厨上门服务');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT('周末家里有', FLOOR(RAND() * 10 + 6), '位客人，需要一位厨师上门做饭。要求会做家常菜和一些特色菜。'),
                        '刚生完宝宝，需要专业的月子餐服务。要求营养均衡，食材新鲜，每天三顿。',
                        CONCAT('宝宝', FLOOR(RAND() * 10 + 1), '岁生日宴，需要厨师上门做', FLOOR(RAND() * 5 + 8), '道菜。要求味道好，摆盘精致。'),
                        '想学习做家常菜，需要一位有经验的厨师上门教学。每次约2小时。');
                WHEN 3 THEN
                    SET v_category_name = '育儿嫂';
                    SET v_title = ELT(FLOOR(RAND() * 4 + 1),
                        '住家育儿嫂',
                        '临时育儿嫂',
                        '月嫂服务',
                        '育儿嫂包月');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT('需要住家育儿嫂照顾', FLOOR(RAND() * 12 + 1), '个月宝宝。要求有月嫂证，有丰富的育儿经验。'),
                        '周末需要临时照顾宝宝，约8小时。要求有育儿经验，有耐心，喜欢孩子。',
                        '预产期临近，需要预订月嫂服务。要求有丰富的月嫂经验，会做月子餐。',
                        '需要包月育儿嫂服务，每周工作5天。要求有育儿经验，会做辅食。');
                WHEN 4 THEN
                    SET v_category_name = '老人照护';
                    SET v_title = ELT(FLOOR(RAND() * 4 + 1),
                        '住家保姆照顾老人',
                        '临时照顾老人',
                        '住院陪护',
                        '老人照护包月');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT('家里有一位', FLOOR(RAND() * 20 + 70), '岁老人，需要住家保姆照顾。要求有护理经验，有耐心。'),
                        '需要临时照顾老人一天，帮忙做饭、陪聊、简单家务。要求有耐心，会做饭。',
                        '老人住院期间需要陪护，约一周时间。要求有医院陪护经验，能熬夜。',
                        '需要包月照顾老人，白天8小时。要求有护理经验，会做家常菜。');
                WHEN 5 THEN
                    SET v_category_name = '搬运';
                    SET v_title = ELT(FLOOR(RAND() * 5 + 1),
                        '居民搬家',
                        '长途搬家',
                        '公司搬迁',
                        '家具搬运',
                        '钢琴搬运');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        '需要从旧家搬到新家，有一些家具需要搬运，包括沙发、床、衣柜等。需要有搬运经验的师傅。',
                        CONCAT('需要从北京搬家到', ELT(FLOOR(RAND() * 3 + 1), '天津', '石家庄', '济南'), '，有家具和家电。要求有长途搬家经验。'),
                        '公司办公室搬迁，有办公家具和设备需要搬运。要求有专业设备和经验。',
                        '新买的家具需要从楼下搬到楼上，没有电梯。需要力气大的师傅。');
                WHEN 6 THEN
                    SET v_category_name = '宠物服务';
                    SET v_title = ELT(FLOOR(RAND() * 4 + 1),
                        '宠物寄养',
                        '宠物上门喂养',
                        '宠物美容',
                        '遛狗服务');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT('国庆期间要出门，需要寄养一只', ELT(FLOOR(RAND() * 3 + 1), '金毛犬', '泰迪犬', '猫咪'), '，约7天时间。要求有养宠经验。'),
                        '出差期间需要有人上门喂养猫咪，每天两次，喂食、换水、清理猫砂。要求有养猫经验。',
                        CONCAT('需要给', ELT(FLOOR(RAND() * 2 + 1), '泰迪犬', '猫咪'), '做美容，包括洗澡、修剪造型、指甲修剪等。要求有宠物美容经验。'),
                        '工作日需要有人帮忙遛狗，每天一次，约30分钟。要求喜欢狗狗，有遛狗经验。');
                WHEN 7 THEN
                    SET v_category_name = '维修服务';
                    SET v_title = ELT(FLOOR(RAND() * 5 + 1),
                        '空调维修',
                        '水电维修',
                        '房屋装修维修',
                        '灯具安装维修',
                        '家电维修');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        '家里的空调不制冷了，需要专业师傅上门检查维修。要求有空调维修经验。',
                        '家里水电有问题，需要师傅上门维修。要求有电工证，经验丰富。',
                        '刚装修完，有一些小问题需要维修。包括墙面修补、灯具安装等。',
                        '家里需要安装几个新灯具，还有几个旧灯具需要维修。要求有电工证。');
                WHEN 8 THEN
                    SET v_category_name = '家电清洗';
                    SET v_title = ELT(FLOOR(RAND() * 4 + 1),
                        '空调清洗',
                        '油烟机清洗',
                        '洗衣机清洗',
                        '全屋家电清洗');
                    SET v_description = ELT(FLOOR(RAND() * 4 + 1),
                        CONCAT('夏天来临前需要清洗', FLOOR(RAND() * 3 + 1), '台空调。要求专业清洗，清洗彻底。'),
                        '家里的油烟机很久没清洗了，油污很多。需要专业清洗服务。',
                        '洗衣机用了很久，需要深度清洗。要求专业清洗，包括内筒清洗。',
                        '需要清洗空调、油烟机、洗衣机、冰箱等全屋家电。要求专业清洗服务。');
            END CASE;
            
            -- 随机选择服务类型
            SET v_service_type = FLOOR(RAND() * 3 + 1);
            
            -- 根据服务类型设置价格和时长
            CASE v_service_type
                WHEN 1 THEN
                    SET v_price_unit = '小时';
                    SET v_expected_price = CASE v_category_id
                        WHEN 1 THEN FLOOR(RAND() * 50 + 50)   -- 保洁 50-100元/小时
                        WHEN 2 THEN FLOOR(RAND() * 100 + 100) -- 烹饪 100-200元/小时
                        WHEN 3 THEN FLOOR(RAND() * 100 + 150) -- 育儿嫂 150-250元/小时
                        WHEN 4 THEN FLOOR(RAND() * 80 + 100)  -- 老人照护 100-180元/小时
                        WHEN 5 THEN FLOOR(RAND() * 150 + 150) -- 搬运 150-300元/小时
                        WHEN 6 THEN FLOOR(RAND() * 50 + 60)   -- 宠物服务 60-110元/小时
                        WHEN 7 THEN FLOOR(RAND() * 100 + 100) -- 维修 100-200元/小时
                        WHEN 8 THEN FLOOR(RAND() * 60 + 80)   -- 家电清洗 80-140元/小时
                        ELSE 100
                    END;
                    SET v_min_duration = FLOOR(RAND() * 3 + 1);
                    SET v_max_duration = v_min_duration + FLOOR(RAND() * 3 + 1);
                WHEN 2 THEN
                    SET v_price_unit = '天';
                    SET v_expected_price = CASE v_category_id
                        WHEN 1 THEN FLOOR(RAND() * 300 + 300)   -- 保洁 300-600元/天
                        WHEN 2 THEN FLOOR(RAND() * 1000 + 500) -- 烹饪 500-1500元/天
                        WHEN 3 THEN FLOOR(RAND() * 500 + 400) -- 育儿嫂 400-900元/天
                        WHEN 4 THEN FLOOR(RAND() * 400 + 300)  -- 老人照护 300-700元/天
                        WHEN 5 THEN FLOOR(RAND() * 2000 + 800) -- 搬运 800-2800元/天
                        WHEN 6 THEN FLOOR(RAND() * 200 + 200) -- 宠物服务 200-400元/天
                        WHEN 7 THEN FLOOR(RAND() * 800 + 500) -- 维修 500-1300元/天
                        WHEN 8 THEN FLOOR(RAND() * 500 + 500) -- 家电清洗 500-1000元/天
                        ELSE 500
                    END;
                    SET v_min_duration = NULL;
                    SET v_max_duration = NULL;
                WHEN 3 THEN
                    SET v_price_unit = '月';
                    SET v_expected_price = CASE v_category_id
                        WHEN 1 THEN FLOOR(RAND() * 2000 + 3000)   -- 保洁 3000-5000元/月
                        WHEN 2 THEN FLOOR(RAND() * 5000 + 8000) -- 烹饪 8000-13000元/月
                        WHEN 3 THEN FLOOR(RAND() * 5000 + 8000) -- 育儿嫂 8000-13000元/月
                        WHEN 4 THEN FLOOR(RAND() * 4000 + 6000)  -- 老人照护 6000-10000元/月
                        WHEN 5 THEN FLOOR(RAND() * 5000 + 10000) -- 搬运 10000-15000元/月
                        WHEN 6 THEN FLOOR(RAND() * 2000 + 3000) -- 宠物服务 3000-5000元/月
                        WHEN 7 THEN FLOOR(RAND() * 4000 + 6000) -- 维修 6000-10000元/月
                        WHEN 8 THEN FLOOR(RAND() * 3000 + 4000) -- 家电清洗 4000-7000元/月
                        ELSE 5000
                    END;
                    SET v_min_duration = NULL;
                    SET v_max_duration = NULL;
            END CASE;
            
            -- 随机选择状态 (1:招募中40%, 2:已接单20%, 3:进行中15%, 4:已完成20%, 5:已取消5%)
            SET v_status = CASE 
                WHEN RAND() < 0.40 THEN 1
                WHEN RAND() < 0.60 THEN 2
                WHEN RAND() < 0.75 THEN 3
                WHEN RAND() < 0.95 THEN 4
                ELSE 5
            END;
            
            -- 如果状态不是招募中，设置接单人ID（从其他用户中随机选择）
            IF v_status != 1 AND v_status != 5 THEN
                SET v_taker_id = FLOOR(RAND() * 20 + 1);
                WHILE v_taker_id = v_publisher_id DO
                    SET v_taker_id = FLOOR(RAND() * 20 + 1);
                END WHILE;
            ELSE
                SET v_taker_id = NULL;
            END IF;
            
            -- 随机设置浏览次数和足迹数
            SET v_view_count = FLOOR(RAND() * 200 + 10);
            SET v_footprint_count = FLOOR(RAND() * 50 + 2);
            
            -- 随机设置创建时间（过去3个月内）
            SET v_created_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY);
            SET v_updated_at = DATE_ADD(v_created_at, INTERVAL FLOOR(RAND() * 7) DAY);
            
            -- 随机设置服务时间（创建时间之后）
            SET v_service_time = DATE_ADD(v_created_at, INTERVAL FLOOR(RAND() * 30 + 1) DAY);
            
            -- 插入需求数据
            INSERT INTO `demands` (
                `title`, `description`, `category_id`, `service_type`, 
                `expected_price`, `price_unit`, `min_duration`, `max_duration`,
                `province`, `city`, `district`, `address`,
                `latitude`, `longitude`, `service_time`,
                `contact_name`, `contact_phone`, `images`,
                `publisher_id`, `taker_id`, `status`,
                `view_count`, `footprint_count`,
                `created_at`, `updated_at`
            ) VALUES (
                v_title, v_description, v_category_id, v_service_type,
                v_expected_price, v_price_unit, v_min_duration, v_max_duration,
                '北京市', v_city, v_district, v_address,
                39.9042 + (RAND() - 0.5) * 0.1, 116.4074 + (RAND() - 0.5) * 0.1, v_service_time,
                v_contact_name, v_contact_phone, NULL,
                v_publisher_id, v_taker_id, v_status,
                v_view_count, v_footprint_count,
                v_created_at, v_updated_at
            );
            
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
    
    -- 输出统计信息
    SELECT '需求数据生成完成！' AS message;
    SELECT COUNT(*) AS total_demands FROM demands;
    SELECT 
        CASE status 
            WHEN 1 THEN '招募中'
            WHEN 2 THEN '已接单'
            WHEN 3 THEN '进行中'
            WHEN 4 THEN '已完成'
            WHEN 5 THEN '已取消'
            ELSE '未知'
        END AS status_name,
        COUNT(*) AS count
    FROM demands 
    GROUP BY status;
    
END //

DELIMITER ;

-- 执行存储过程生成数据
CALL generate_demands_data();

-- 清理存储过程
DROP PROCEDURE IF EXISTS generate_demands_data;

-- ============================================
-- 数据验证查询
-- ============================================

-- 查看总需求数
SELECT '总需求数' AS info, COUNT(*) AS count FROM demands;

-- 按状态统计
SELECT 
    CASE status 
        WHEN 1 THEN '招募中'
        WHEN 2 THEN '已接单'
        WHEN 3 THEN '进行中'
        WHEN 4 THEN '已完成'
        WHEN 5 THEN '已取消'
        ELSE '未知'
    END AS 状态,
    COUNT(*) AS 数量,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM demands), 2) AS 占比
FROM demands 
GROUP BY status
ORDER BY status;

-- 按分类统计
SELECT 
    CASE category_id 
        WHEN 1 THEN '保洁'
        WHEN 2 THEN '烹饪'
        WHEN 3 THEN '育儿嫂'
        WHEN 4 THEN '老人照护'
        WHEN 5 THEN '搬运'
        WHEN 6 THEN '宠物服务'
        WHEN 7 THEN '维修服务'
        WHEN 8 THEN '家电清洗'
        ELSE '未知'
    END AS 分类,
    COUNT(*) AS 数量
FROM demands 
GROUP BY category_id
ORDER BY category_id;

-- 按用户统计（前20个用户）
SELECT publisher_id AS 用户ID, COUNT(*) AS 发布需求数
FROM demands 
GROUP BY publisher_id
ORDER BY publisher_id;

-- 查看最新发布的10条需求
SELECT 
    id AS 需求ID,
    title AS 标题,
    CASE category_id 
        WHEN 1 THEN '保洁'
        WHEN 2 THEN '烹饪'
        WHEN 3 THEN '育儿嫂'
        WHEN 4 THEN '老人照护'
        WHEN 5 THEN '搬运'
        WHEN 6 THEN '宠物服务'
        WHEN 7 THEN '维修服务'
        WHEN 8 THEN '家电清洗'
        ELSE '未知'
    END AS 分类,
    CONCAT(expected_price, '元/', price_unit) AS 价格,
    CASE status 
        WHEN 1 THEN '招募中'
        WHEN 2 THEN '已接单'
        WHEN 3 THEN '进行中'
        WHEN 4 THEN '已完成'
        WHEN 5 THEN '已取消'
        ELSE '未知'
    END AS 状态,
    created_at AS 发布时间
FROM demands 
ORDER BY created_at DESC 
LIMIT 10;
