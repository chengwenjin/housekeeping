-- 更新 admin 用户的密码为 admin123 的 BCrypt 哈希
-- BCrypt 哈希值 (admin123): $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- 这个哈希值是通过标准 BCrypt 算法生成的

UPDATE admins 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE username = 'admin';
