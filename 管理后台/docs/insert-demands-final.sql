-- ============================================
-- 小程序C端需求数据生成脚本（最终版）
-- 生成时间：2026-04-15
-- 数据量：30条需求数据（2个用户 × 15条需求）
-- ============================================

USE jiazheng_miniapp_new;

-- 插入需求数据 - 用户1
INSERT INTO `demands` (`user_id`, `category_id`, `title`, `description`, `service_type`, `expected_price`, `price_unit`, `max_duration`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `service_time`, `service_time_desc`, `contact_name`, `contact_phone`, `images`, `publisher_id`, `taken_by`, `view_count`, `footprint_count`, `status`, `taken_at`, `completed_at`, `cancelled_at`, `cancel_reason`, `remarks`) VALUES
(1, 1, 'House Cleaning Service', 'Need regular house cleaning for 3 hours. Living room, bedrooms, kitchen, and bathroom.', 1, 80.00, 'hour', 4.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-20 09:00:00', 'Tomorrow morning', 'Zhang', '13800138001', NULL, 1, NULL, 45, 12, 1, NULL, NULL, NULL, NULL, NULL),
(1, 1, 'Deep Cleaning', 'Need deep cleaning after renovation. Focus on window cleaning and dust removal.', 1, 120.00, 'hour', 6.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-18 14:00:00', 'This afternoon', 'Zhang', '13800138001', NULL, 1, 5, 89, 23, 2, '2026-04-15 10:00:00', NULL, NULL, NULL, NULL),
(1, 2, 'Private Chef for Family Dinner', 'Need a chef for family dinner this weekend. 8-10 people.', 2, 500.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-19 11:00:00', 'Sunday 11am', 'Zhang', '13800138001', NULL, 1, NULL, 34, 8, 1, NULL, NULL, NULL, NULL, NULL),
(1, 5, 'Moving Service', 'Need to move furniture from Chaoyang to Haidian. Sofa, bed, wardrobe.', 2, 800.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-22 08:00:00', 'Next Tuesday 8am', 'Zhang', '13800138001', NULL, 1, 10, 156, 45, 3, '2026-04-14 08:00:00', NULL, NULL, NULL, NULL),
(1, 7, 'AC Repair', 'Air conditioner not cooling. Need professional repair service.', 1, 150.00, 'hour', 3.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-16 10:00:00', 'Tomorrow 10am', 'Zhang', '13800138001', NULL, 1, 6, 67, 18, 4, '2026-04-13 09:00:00', '2026-04-13 12:00:00', NULL, NULL, NULL),
(1, 8, 'Range Hood Cleaning', 'Range hood is very oily. Need professional cleaning service.', 1, 120.00, 'hour', 2.50, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-17 15:00:00', 'Tomorrow 3pm', 'Zhang', '13800138001', NULL, 1, NULL, 23, 5, 1, NULL, NULL, NULL, NULL, NULL),
(1, 1, 'Monthly Cleaning Service', 'Monthly cleaning service, twice a week. Already served for 2 months.', 3, 3000.00, 'month', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-03-15 09:00:00', 'Started March 15', 'Zhang', '13800138001', NULL, 1, 4, 234, 67, 4, '2026-03-10 10:00:00', '2026-04-10 18:00:00', NULL, NULL, NULL),
(1, 2, 'New Year Eve Dinner', 'Booked chef for New Year Eve dinner. 12 dishes, very delicious.', 2, 2000.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-02-17 12:00:00', 'Chinese New Year Eve', 'Zhang', '13800138001', NULL, 1, 4, 456, 123, 4, '2026-02-01 15:00:00', '2026-02-17 20:00:00', NULL, NULL, NULL),
(1, 5, 'Office Moving', 'Company office moving last month. Very professional service.', 2, 3000.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-03-20 08:00:00', 'March 20', 'Zhang', '13800138001', NULL, 1, 15, 345, 89, 4, '2026-03-10 09:00:00', '2026-03-20 18:00:00', NULL, NULL, NULL),
(1, 7, 'Water Pipe Leak Repair', 'Bathroom water pipe leaking. Fixed quickly, reasonable price.', 1, 200.00, 'hour', 2.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-03-25 14:00:00', 'March 25 afternoon', 'Zhang', '13800138001', NULL, 1, 6, 123, 34, 4, '2026-03-24 16:00:00', '2026-03-25 17:00:00', NULL, NULL, NULL),
(1, 8, 'AC Cleaning Service', 'AC cleaning before summer. Very professional, works much better now.', 1, 150.00, 'hour', 2.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-01 10:00:00', 'April 1', 'Zhang', '13800138001', NULL, 1, 8, 89, 23, 4, '2026-03-28 11:00:00', '2026-04-01 12:00:00', NULL, NULL, NULL),
(1, 1, 'Temporary Cleaning', 'Booked cleaning service but cancelled due to schedule change.', 1, 100.00, 'hour', 3.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-15 09:00:00', 'Today 9am', 'Zhang', '13800138001', NULL, 1, NULL, 12, 3, 5, NULL, NULL, '2026-04-14 11:00:00', 'Schedule change', NULL),
(1, 6, 'Pet Sitting', 'Need pet sitting for 7 days during holiday. Have a golden retriever.', 2, 350.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-10-01 09:00:00', 'National Day holiday', 'Zhang', '13800138001', NULL, 1, NULL, 56, 15, 1, NULL, NULL, NULL, NULL, NULL),
(1, 3, 'Temporary Nanny', 'Need temporary nanny for 2-year-old baby this weekend. 8 hours.', 1, 200.00, 'hour', 10.00, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-20 09:00:00', 'This Sunday', 'Zhang', '13800138001', NULL, 1, NULL, 34, 9, 1, NULL, NULL, NULL, NULL, NULL),
(1, 4, 'Elderly Care', 'Need temporary elderly care for one day. Cooking, chatting, light housework.', 2, 300.00, 'day', NULL, 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 39.9042, 116.4074, '2026-04-21 08:00:00', 'Next Monday', 'Zhang', '13800138001', NULL, 1, NULL, 28, 7, 1, NULL, NULL, NULL, NULL, NULL);

-- 插入需求数据 - 用户2
INSERT INTO `demands` (`user_id`, `category_id`, `title`, `description`, `service_type`, `expected_price`, `price_unit`, `max_duration`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `service_time`, `service_time_desc`, `contact_name`, `contact_phone`, `images`, `publisher_id`, `taken_by`, `view_count`, `footprint_count`, `status`, `taken_at`, `completed_at`, `cancelled_at`, `cancel_reason`, `remarks`) VALUES
(2, 1, 'Premium Cleaning Service', '180sqm apartment needs premium cleaning service. Professional equipment required.', 1, 150.00, 'hour', 6.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-19 09:00:00', 'This Saturday', 'Li', '13800138002', NULL, 2, NULL, 67, 18, 1, NULL, NULL, NULL, NULL, NULL),
(2, 2, 'Postpartum Meal Service', 'Need professional postpartum meal service. Nutritious and fresh ingredients.', 3, 15000.00, 'month', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-20 08:00:00', 'Starting April 20', 'Li', '13800138002', NULL, 2, NULL, 89, 25, 1, NULL, NULL, NULL, NULL, NULL),
(2, 3, 'Live-in Nanny', 'Need live-in nanny for 6-month baby. Must have maternity certificate.', 3, 12000.00, 'month', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-05-01 00:00:00', 'Starting May 1', 'Li', '13800138002', NULL, 2, NULL, 156, 45, 1, NULL, NULL, NULL, NULL, NULL),
(2, 4, 'Elderly Care', 'Need live-in caregiver for 80-year-old elderly. Must have care experience.', 3, 8000.00, 'month', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-25 00:00:00', 'Starting April 25', 'Li', '13800138002', NULL, 2, 4, 78, 22, 2, '2026-04-14 10:00:00', NULL, NULL, NULL, NULL),
(2, 1, 'Monthly Cleaning', 'Monthly cleaning service, twice a week. Served for 2 months already.', 3, 4000.00, 'month', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-02-15 09:00:00', 'Started Feb 15', 'Li', '13800138002', NULL, 2, 4, 345, 89, 4, '2026-02-01 10:00:00', '2026-04-10 18:00:00', NULL, NULL, NULL),
(2, 2, 'Birthday Party Chef', 'Booked chef for baby birthday party. 10 dishes, very delicious.', 2, 1500.00, 'day', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-03-20 12:00:00', 'March 20 noon', 'Li', '13800138002', NULL, 2, 4, 234, 67, 4, '2026-03-10 14:00:00', '2026-03-20 20:00:00', NULL, NULL, NULL),
(2, 8, 'Home Appliance Cleaning', 'Need to clean 3 AC units, 1 range hood, 1 washing machine.', 2, 800.00, 'day', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-18 09:00:00', 'This Friday', 'Li', '13800138002', NULL, 2, NULL, 45, 12, 1, NULL, NULL, NULL, NULL, NULL),
(2, 7, 'Lighting Installation', 'Need to install new lights and repair old ones. Must have electrician certificate.', 1, 100.00, 'hour', 4.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-17 14:00:00', 'This Thursday', 'Li', '13800138002', NULL, 2, NULL, 34, 8, 1, NULL, NULL, NULL, NULL, NULL),
(2, 6, 'Pet Grooming', 'Need pet grooming for poodle. Bathing, haircut, nail trimming.', 1, 180.00, 'hour', 2.50, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-19 10:00:00', 'This Saturday', 'Li', '13800138002', NULL, 2, NULL, 56, 15, 1, NULL, NULL, NULL, NULL, NULL),
(2, 5, 'Furniture Moving', 'New sofa needs to be moved to 5th floor. No elevator. Need strong workers.', 1, 300.00, 'hour', 2.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-20 14:00:00', 'This Sunday', 'Li', '13800138002', NULL, 2, NULL, 23, 6, 1, NULL, NULL, NULL, NULL, NULL),
(2, 3, 'Temporary Nanny Service', 'Hired temporary nanny before. Very professional, baby liked her.', 1, 180.00, 'hour', 8.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-03-15 09:00:00', 'March 15', 'Li', '13800138002', NULL, 2, 3, 123, 34, 4, '2026-03-10 10:00:00', '2026-03-15 18:00:00', NULL, NULL, NULL),
(2, 4, 'Temporary Elderly Care', 'Hired temporary elderly care before. Very patient and caring.', 2, 280.00, 'day', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-03-25 08:00:00', 'March 25', 'Li', '13800138002', NULL, 2, 4, 89, 23, 4, '2026-03-20 14:00:00', '2026-03-25 20:00:00', NULL, NULL, NULL),
(2, 1, 'Deep Cleaning Service', 'Just moved, need deep cleaning. Windows, kitchen, bathroom.', 1, 100.00, 'hour', 6.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-16 09:00:00', 'This Wednesday', 'Li', '13800138002', NULL, 2, 8, 67, 18, 2, '2026-04-14 10:00:00', NULL, NULL, NULL, NULL),
(2, 2, 'Family Dinner Chef', 'Weekend family dinner. Need chef for 6-8 people. Chinese and Western food.', 2, 600.00, 'day', NULL, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-19 11:00:00', 'This Sunday', 'Li', '13800138002', NULL, 2, 4, 45, 12, 2, '2026-04-13 16:00:00', NULL, NULL, NULL, NULL),
(2, 8, 'AC Cleaning', 'Booked AC cleaning but cancelled because AC fixed itself.', 1, 120.00, 'hour', 2.00, 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 39.9847, 116.3046, '2026-04-15 10:00:00', 'Today 10am', 'Li', '13800138002', NULL, 2, NULL, 8, 2, 5, NULL, NULL, '2026-04-14 09:00:00', 'AC fixed itself', NULL);

-- 统计信息
SELECT 'Demand data inserted successfully!' AS message;
SELECT COUNT(*) AS total_demands FROM demands;
SELECT 
    CASE status 
        WHEN 1 THEN 'Recruiting'
        WHEN 2 THEN 'Accepted'
        WHEN 3 THEN 'In Progress'
        WHEN 4 THEN 'Completed'
        WHEN 5 THEN 'Cancelled'
        ELSE 'Unknown'
    END AS status_name,
    COUNT(*) AS count
FROM demands 
GROUP BY status;

SELECT user_id, COUNT(*) AS demand_count FROM demands GROUP BY user_id;
