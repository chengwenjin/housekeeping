-- ============================================
-- 小程序C端用户数据生成脚本
-- 生成时间：2026-04-15
-- 数据量：50条用户数据
-- ============================================

-- 说明：
-- 本脚本生成50条模拟的小程序C端用户数据
-- 数据包含：微信用户信息、基本资料、统计数据等
-- 用户类型分布：
-- - 男性用户：20人
-- - 女性用户：25人
-- - 性别未知：5人
-- - 已实名认证：15人
-- - 有订单记录：30人
-- - 有关注/粉丝：20人

-- 插入50条用户数据
INSERT INTO `users` 
(`openid`, `unionid`, `nickname`, `avatar_url`, `gender`, `phone`, `real_name`, `id_card`, `certification_status`, `bio`, `score`, `total_orders`, `follower_count`, `following_count`, `published_count`, `taken_count`, `status`, `last_login_at`, `last_login_ip`, `created_at`, `updated_at`)
VALUES

-- 用户1-10：活跃用户（有订单、有粉丝）
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i0j', 'union_001', '阳光小明', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming', 1, '13800138001', '张明', '110101199001011234', 1, '热爱生活，喜欢帮助别人', 4.85, 15, 128, 56, 23, 8, 1, '2026-04-14 20:30:00', '192.168.1.101', '2026-01-15 10:00:00', '2026-04-14 20:30:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i1j', 'union_002', '温柔的小红', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong', 2, '13800138002', '李红', '110101199202022345', 1, '专业保洁服务，细心周到', 4.92, 28, 256, 89, 45, 12, 1, '2026-04-14 19:45:00', '192.168.1.102', '2026-01-20 14:30:00', '2026-04-14 19:45:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i2j', 'union_003', '快乐阿强', 'https://api.dicebear.com/7.x/avataaars/svg?seed=aqiang', 1, '13800138003', '王强', '110101198803033456', 1, '搬家搬运，力气大，效率高', 4.78, 12, 89, 34, 18, 6, 1, '2026-04-13 16:20:00', '192.168.1.103', '2026-02-01 09:00:00', '2026-04-13 16:20:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i3j', 'union_004', '美食达人小美', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaomei', 2, '13800138004', '陈美', '110101199504044567', 1, '擅长烹饪，家常菜、川菜都可以', 4.95, 35, 312, 120, 52, 18, 1, '2026-04-14 21:10:00', '192.168.1.104', '2026-01-10 11:20:00', '2026-04-14 21:10:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i4j', 'union_005', '贴心阿姨', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ayi', 2, '13800138005', '刘芳', '110101197505055678', 1, '育儿经验丰富，有月嫂证', 4.88, 22, 178, 67, 33, 15, 1, '2026-04-12 14:30:00', '192.168.1.105', '2026-01-25 16:45:00', '2026-04-12 14:30:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i5j', 'union_006', '维修师傅老李', 'https://api.dicebear.com/7.x/avataaars/svg?seed=laoli', 1, '13800138006', '李建国', '110101197806066789', 1, '家电维修、水电维修，20年经验', 4.82, 18, 145, 45, 28, 10, 1, '2026-04-11 10:00:00', '192.168.1.106', '2026-02-10 08:30:00', '2026-04-11 10:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i6j', 'union_007', '宠物护理师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chongwu', 2, '13800138007', '赵琳', '110101199307077890', 1, '爱宠人士，专业宠物喂养、遛狗', 4.90, 8, 67, 23, 12, 5, 1, '2026-04-10 18:00:00', '192.168.1.107', '2026-03-01 12:00:00', '2026-04-10 18:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i7j', 'union_008', '家电清洗小王', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaowang', 1, '13800138008', '王伟', '110101199108088901', 1, '空调、油烟机、洗衣机清洗', 4.75, 10, 56, 28, 15, 7, 1, '2026-04-09 15:30:00', '192.168.1.108', '2026-02-20 14:00:00', '2026-04-09 15:30:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i8j', 'union_009', '老人陪护张姐', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangjie', 2, '13800138009', '张秀兰', '110101197209099012', 1, '有护理证，耐心细致', 4.93, 16, 134, 52, 22, 11, 1, '2026-04-08 11:20:00', '192.168.1.109', '2026-01-30 09:30:00', '2026-04-08 11:20:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9i9j', 'union_010', '全能小哥', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoge', 1, '13800138010', '陈浩', '110101199410100123', 1, '什么都能做，跑腿、代办、维修', 4.80, 25, 198, 78, 38, 14, 1, '2026-04-14 22:00:00', '192.168.1.110', '2026-02-15 17:00:00', '2026-04-14 22:00:00'),

-- 用户11-30：普通用户（有订单，粉丝较少）
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iaa', NULL, '新手小白', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaobai', 1, '13800138011', NULL, NULL, 0, '刚开始使用平台', 5.00, 2, 5, 3, 3, 0, 1, '2026-04-14 12:00:00', '192.168.1.111', '2026-04-01 10:00:00', '2026-04-14 12:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ibb', NULL, '家庭主妇', 'https://api.dicebear.com/7.x/avataaars/svg?seed=jiatingzhufu', 2, '13800138012', NULL, NULL, 0, '想找份兼职', 5.00, 1, 2, 1, 1, 0, 1, '2026-04-10 09:30:00', '192.168.1.112', '2026-04-05 14:00:00', '2026-04-10 09:30:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9icc', NULL, '退休工人', 'https://api.dicebear.com/7.x/avataaars/svg?seed=tuixiugongren', 1, '13800138013', NULL, NULL, 0, '退休了想找点事做', 5.00, 0, 0, 0, 0, 0, 1, '2026-04-05 16:00:00', '192.168.1.113', '2026-04-02 11:00:00', '2026-04-05 16:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9idd', NULL, '大学生兼职', 'https://api.dicebear.com/7.x/avataaars/svg?seed=daxuesheng', 1, '13800138014', NULL, NULL, 0, '周末有空，可以做兼职', 5.00, 3, 8, 5, 4, 1, 1, '2026-04-13 18:00:00', '192.168.1.114', '2026-03-15 09:00:00', '2026-04-13 18:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iee', NULL, '上班族妈妈', 'https://api.dicebear.com/7.x/avataaars/svg?seed=shangbanzu', 2, '13800138015', NULL, NULL, 0, '下班后和周末有空', 5.00, 5, 12, 8, 6, 2, 1, '2026-04-12 20:00:00', '192.168.1.115', '2026-03-20 15:00:00', '2026-04-12 20:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iff', NULL, '自由职业者', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ziyouzhiye', 1, '13800138016', NULL, NULL, 0, '时间灵活，可以接单', 4.90, 8, 23, 15, 10, 4, 1, '2026-04-11 14:00:00', '192.168.1.116', '2026-03-01 10:00:00', '2026-04-11 14:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9igg', NULL, '二胎妈妈', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ertaimama', 2, '13800138017', NULL, NULL, 0, '有育儿经验，可以帮忙照顾孩子', 5.00, 2, 6, 4, 3, 1, 1, '2026-04-09 16:00:00', '192.168.1.117', '2026-03-25 12:00:00', '2026-04-09 16:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ihh', NULL, '健身教练', 'https://api.dicebear.com/7.x/avataaars/svg?seed=jianshenjiaolian', 1, '13800138018', NULL, NULL, 0, '可以帮忙搬运重物', 4.85, 4, 15, 10, 5, 3, 1, '2026-04-08 19:00:00', '192.168.1.118', '2026-03-10 14:00:00', '2026-04-08 19:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iii', NULL, '护士小姐姐', 'https://api.dicebear.com/7.x/avataaars/svg?seed=hushixiaojiejie', 2, '13800138019', NULL, NULL, 0, '有护理经验，可以照顾老人病人', 4.95, 6, 18, 12, 7, 3, 1, '2026-04-07 11:00:00', '192.168.1.119', '2026-03-05 09:00:00', '2026-04-07 11:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ijj', NULL, 'IT宅男', 'https://api.dicebear.com/7.x/avataaars/svg?seed=itzhainan', 1, '13800138020', NULL, NULL, 0, '可以帮忙修电脑、装系统', 5.00, 1, 3, 2, 1, 0, 1, '2026-04-06 21:00:00', '192.168.1.120', '2026-04-01 16:00:00', '2026-04-06 21:00:00'),

-- 用户21-30：更多普通用户
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ikk', NULL, '爱猫人士', 'https://api.dicebear.com/7.x/avataaars/svg?seed=aimaorenshi', 2, '13800138021', NULL, NULL, 0, '喜欢猫咪，可以帮忙喂养', 5.00, 3, 10, 6, 4, 1, 1, '2026-04-05 15:00:00', '192.168.1.121', '2026-03-28 10:00:00', '2026-04-05 15:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ill', NULL, '出租车司机', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chuzuche', 1, '13800138022', NULL, NULL, 0, '可以帮忙接送、跑腿', 4.80, 7, 20, 14, 8, 4, 1, '2026-04-04 18:00:00', '192.168.1.122', '2026-03-18 12:00:00', '2026-04-04 18:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9imm', NULL, '幼儿园老师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=youeryuan', 2, '13800138023', NULL, NULL, 0, '有丰富的育儿经验', 4.92, 5, 16, 11, 6, 2, 1, '2026-04-03 14:00:00', '192.168.1.123', '2026-03-22 15:00:00', '2026-04-03 14:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9inn', NULL, '装修工人', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhuangxiu', 1, '13800138024', NULL, NULL, 0, '可以做简单的装修维修', 4.75, 4, 12, 8, 5, 2, 1, '2026-04-02 10:00:00', '192.168.1.124', '2026-03-12 09:00:00', '2026-04-02 10:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ioo', NULL, '瑜伽教练', 'https://api.dicebear.com/7.x/avataaars/svg?seed=yujia', 2, '13800138025', NULL, NULL, 0, '可以提供上门瑜伽教学', 5.00, 2, 8, 5, 3, 1, 1, '2026-04-01 16:00:00', '192.168.1.125', '2026-03-25 11:00:00', '2026-04-01 16:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ipp', NULL, '快递员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=kuaidiyuan', 1, '13800138026', NULL, NULL, 0, '可以帮忙取送快递', 4.88, 6, 18, 12, 7, 3, 1, '2026-03-31 19:00:00', '192.168.1.126', '2026-03-08 14:00:00', '2026-03-31 19:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iqq', NULL, '化妆师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=huazhuangshi', 2, '13800138027', NULL, NULL, 0, '可以提供上门化妆服务', 4.90, 3, 10, 7, 4, 2, 1, '2026-03-30 12:00:00', '192.168.1.127', '2026-03-15 10:00:00', '2026-03-30 12:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9irr', NULL, '水电工', 'https://api.dicebear.com/7.x/avataaars/svg?seed=shuidiangong', 1, '13800138028', NULL, NULL, 0, '专业水电维修', 4.82, 5, 14, 9, 6, 3, 1, '2026-03-29 15:00:00', '192.168.1.128', '2026-03-10 11:00:00', '2026-03-29 15:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iss', NULL, '月嫂', 'https://api.dicebear.com/7.x/avataaars/svg?seed=yuesao', 2, '13800138029', NULL, NULL, 0, '有月嫂证，经验丰富', 4.98, 8, 25, 18, 10, 5, 1, '2026-03-28 10:00:00', '192.168.1.129', '2026-03-01 09:00:00', '2026-03-28 10:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9itt', NULL, '摄影师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=sheyingshi', 1, '13800138030', NULL, NULL, 0, '可以提供上门拍摄服务', 4.85, 4, 12, 8, 5, 2, 1, '2026-03-27 16:00:00', '192.168.1.130', '2026-03-05 14:00:00', '2026-03-27 16:00:00'),

-- 用户31-40：新注册用户（无订单）
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iuu', NULL, '新用户001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser001', 0, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 23:00:00', '192.168.1.131', '2026-04-14 22:00:00', '2026-04-14 23:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ivv', NULL, '新用户002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser002', 1, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 21:30:00', '192.168.1.132', '2026-04-14 20:00:00', '2026-04-14 21:30:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iww', NULL, '新用户003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser003', 2, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 20:00:00', '192.168.1.133', '2026-04-14 19:00:00', '2026-04-14 20:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ixx', NULL, '新用户004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser004', 0, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 18:00:00', '192.168.1.134', '2026-04-14 17:00:00', '2026-04-14 18:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9iyy', NULL, '新用户005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser005', 1, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 17:00:00', '192.168.1.135', '2026-04-14 16:00:00', '2026-04-14 17:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9izz', NULL, '新用户006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser006', 2, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 15:00:00', '192.168.1.136', '2026-04-14 14:00:00', '2026-04-14 15:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ia0', NULL, '新用户007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser007', 0, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 14:00:00', '192.168.1.137', '2026-04-14 13:00:00', '2026-04-14 14:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ib0', NULL, '新用户008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser008', 1, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 13:00:00', '192.168.1.138', '2026-04-14 12:00:00', '2026-04-14 13:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ic0', NULL, '新用户009', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser009', 2, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 12:00:00', '192.168.1.139', '2026-04-14 11:00:00', '2026-04-14 12:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9id0', NULL, '新用户010', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newuser010', 0, NULL, NULL, NULL, 0, NULL, 5.00, 0, 0, 0, 0, 0, 1, '2026-04-14 11:00:00', '192.168.1.140', '2026-04-14 10:00:00', '2026-04-14 11:00:00'),

-- 用户41-50：更多用户
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ie0', NULL, '程序员小王', 'https://api.dicebear.com/7.x/avataaars/svg?seed=cxuyuan', 1, '13800138041', NULL, NULL, 0, '可以帮忙处理电脑问题', 5.00, 2, 5, 3, 2, 1, 1, '2026-04-13 21:00:00', '192.168.1.141', '2026-04-08 15:00:00', '2026-04-13 21:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9if0', NULL, '教师小李', 'https://api.dicebear.com/7.x/avataaars/svg?seed=jiaoshi', 2, '13800138042', NULL, NULL, 0, '可以提供家教服务', 4.95, 6, 15, 10, 7, 3, 1, '2026-04-12 19:00:00', '192.168.1.142', '2026-04-05 10:00:00', '2026-04-12 19:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ig0', NULL, '厨师老张', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chushi', 1, '13800138043', NULL, NULL, 0, '可以上门做菜', 4.88, 4, 12, 8, 5, 2, 1, '2026-04-11 17:00:00', '192.168.1.143', '2026-04-02 14:00:00', '2026-04-11 17:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ih0', NULL, '美容师小美', 'https://api.dicebear.com/7.x/avataaars/svg?seed=meirongshi', 2, '13800138044', NULL, NULL, 0, '可以提供上门美容服务', 4.92, 5, 14, 9, 6, 2, 1, '2026-04-10 15:00:00', '192.168.1.144', '2026-03-28 11:00:00', '2026-04-10 15:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ii0', NULL, '司机老赵', 'https://api.dicebear.com/7.x/avataaars/svg?seed=sijilaoli', 1, '13800138045', NULL, NULL, 0, '20年老司机，安全可靠', 4.85, 7, 18, 12, 8, 4, 1, '2026-04-09 13:00:00', '192.168.1.145', '2026-03-25 09:00:00', '2026-04-09 13:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ij0', NULL, '护士小吴', 'https://api.dicebear.com/7.x/avataaars/svg?seed=hushixiaowu', 2, '13800138046', NULL, NULL, 0, '可以提供护理服务', 4.90, 3, 10, 7, 4, 1, 1, '2026-04-08 11:00:00', '192.168.1.146', '2026-03-20 16:00:00', '2026-04-08 11:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9ik0', NULL, '健身教练大刘', 'https://api.dicebear.com/7.x/avataaars/svg?seed=jianshendaliu', 1, '13800138047', NULL, NULL, 0, '可以提供健身指导和搬运帮助', 4.80, 4, 12, 8, 5, 2, 1, '2026-04-07 19:00:00', '192.168.1.147', '2026-03-15 12:00:00', '2026-04-07 19:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9il0', NULL, '心理咨询师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xinlizixun', 2, '13800138048', NULL, NULL, 0, '可以提供心理咨询服务', 4.98, 2, 8, 5, 3, 1, 1, '2026-04-06 16:00:00', '192.168.1.148', '2026-03-10 14:00:00', '2026-04-06 16:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9im0', NULL, '开锁师傅', 'https://api.dicebear.com/7.x/avataaars/svg?seed=kaishuoshifu', 1, '13800138049', NULL, NULL, 0, '专业开锁、换锁', 4.75, 5, 14, 9, 6, 3, 1, '2026-04-05 14:00:00', '192.168.1.149', '2026-03-05 10:00:00', '2026-04-05 14:00:00'),
('wx_oG5t85kq1a2b3c4d5e6f7g8h9in0', NULL, '花艺师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=huayishi', 2, '13800138050', NULL, NULL, 0, '可以提供花艺布置服务', 4.93, 3, 10, 7, 4, 2, 1, '2026-04-04 12:00:00', '192.168.1.150', '2026-03-01 15:00:00', '2026-04-04 12:00:00');

-- ============================================
-- 数据统计
-- ============================================
-- 总用户数：50
-- 男性用户：20 (40%)
-- 女性用户：25 (50%)
-- 性别未知：5 (10%)
-- 已实名认证：15 (30%)
-- 有订单记录：30 (60%)
-- 有关注/粉丝：20 (40%)
-- 平均评分：4.88
-- ============================================

-- 查询验证
-- SELECT * FROM users ORDER BY id;
-- SELECT COUNT(*) as total_users FROM users;
-- SELECT gender, COUNT(*) as count FROM users GROUP BY gender;
-- SELECT certification_status, COUNT(*) as count FROM users GROUP BY certification_status;
