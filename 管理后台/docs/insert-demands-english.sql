USE jiazheng_miniapp_new;

-- Insert test demand data for user 1
INSERT INTO `demands` (`title`, `description`, `category_id`, `service_type`, `expected_price`, `price_unit`, `province`, `city`, `district`, `address`, `contact_name`, `contact_phone`, `publisher_id`, `status`, `view_count`, `footprint_count`, `created_at`, `updated_at`) VALUES
('House Cleaning Service', 'Need regular house cleaning for 3 hours. Living room, bedrooms, kitchen, and bathroom.', 1, 1, 80.00, 'hour', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 1, 45, 12, NOW(), NOW()),
('Deep Cleaning', 'Need deep cleaning after renovation. Focus on window cleaning and dust removal.', 1, 1, 120.00, 'hour', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 1, 89, 23, NOW(), NOW()),
('Private Chef for Family Dinner', 'Need a chef for family dinner this weekend. 8-10 people.', 2, 2, 500.00, 'day', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 1, 34, 8, NOW(), NOW()),
('Moving Service', 'Need to move furniture from Chaoyang to Haidian. Sofa, bed, wardrobe.', 5, 2, 800.00, 'day', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 2, 156, 45, NOW(), NOW()),
('AC Repair', 'Air conditioner not cooling. Need professional repair service.', 7, 1, 150.00, 'hour', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 3, 67, 18, NOW(), NOW()),
('Range Hood Cleaning', 'Range hood is very oily. Need professional cleaning service.', 8, 1, 120.00, 'hour', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 4, 23, 5, NOW(), NOW()),
('Monthly Cleaning Service', 'Monthly cleaning service, twice a week. Already served for 2 months.', 1, 3, 3000.00, 'month', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 4, 234, 67, NOW(), NOW()),
('Pet Sitting', 'Need pet sitting for 7 days during holiday. Have a golden retriever.', 6, 2, 350.00, 'day', 'Beijing', 'Beijing', 'Chaoyang', 'Jianguo Road 88', 'Zhang', '13800138001', 1, 5, 56, 15, NOW(), NOW());

-- Insert demand data for user 2
INSERT INTO `demands` (`title`, `description`, `category_id`, `service_type`, `expected_price`, `price_unit`, `province`, `city`, `district`, `address`, `contact_name`, `contact_phone`, `publisher_id`, `status`, `view_count`, `footprint_count`, `created_at`, `updated_at`) VALUES
('Premium Cleaning Service', '180sqm apartment needs premium cleaning service. Professional equipment required.', 1, 1, 150.00, 'hour', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 1, 67, 18, NOW(), NOW()),
('Postpartum Meal Service', 'Need professional postpartum meal service. Nutritious and fresh ingredients.', 2, 3, 15000.00, 'month', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 1, 89, 25, NOW(), NOW()),
('Live-in Nanny', 'Need live-in nanny for 6-month baby. Must have maternity certificate.', 3, 3, 12000.00, 'month', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 1, 156, 45, NOW(), NOW()),
('Elderly Care', 'Need live-in caregiver for 80-year-old elderly. Must have care experience.', 4, 3, 8000.00, 'month', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 2, 78, 22, NOW(), NOW()),
('Home Appliance Cleaning', 'Need to clean 3 AC units, 1 range hood, 1 washing machine.', 8, 2, 800.00, 'day', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 3, 45, 12, NOW(), NOW()),
('Lighting Installation', 'Need to install new lights and repair old ones. Must have electrician certificate.', 7, 1, 100.00, 'hour', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 4, 34, 8, NOW(), NOW()),
('Pet Grooming', 'Need pet grooming for poodle. Bathing, haircut, nail trimming.', 6, 1, 180.00, 'hour', 'Beijing', 'Beijing', 'Haidian', 'Zhongguancun Street 1', 'Li', '13800138002', 2, 5, 56, 15, NOW(), NOW());

-- Statistics
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
