# 操作日志接口 400 错误修复

## 问题描述

前端请求 `/api/admin/logs?page=1&pageSize=10&operator=&operationType=&startDate=&endDate=` 时返回 400 错误。

**错误响应**:
```json
{
  "code": 400,
  "message": "服务器内部错误",
  "success": false,
  "data": null,
  "timestamp": 1774689848948
}
```

## 问题原因

1. **前后端参数不匹配**：
   - 前端传递参数：`operator`, `operationType`, `startDate`, `endDate`
   - 后端接收参数：`userId`, `module`
   - Spring MVC 无法将前端参数绑定到后端方法参数上，导致 400 错误

2. **实体类与数据库表字段不一致**：
   - 数据库表使用 `admin_id`
   - 实体类使用 `user_id`
   - 数据库表缺少 `username` 字段

## 解决方案

### 1. 更新实体类 (OperationLog.java)

已更新字段映射，使其与数据库表结构一致：
- ✅ `user_id` → `admin_id` (管理员 ID)
- ✅ 添加 `username` 字段 (操作人员用户名)
- ✅ 添加完整字段：`method`, `url`, `user_agent`, `request_data`, `response_code`, `response_data`, `duration`

### 2. 更新 Controller (AdminLogController.java)

修改请求参数以匹配前端：
```java
@GetMapping
public Result<Page<OperationLog>> getLogs(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String operator,      // 操作人员
    @RequestParam(required = false) String operationType, // 操作类型
    @RequestParam(required = false) String startDate,     // 开始日期
    @RequestParam(required = false) String endDate        // 结束日期
)
```

### 3. 更新 Service (OperationLogServiceImpl.java)

实现完整的查询逻辑：
- ✅ 操作人员模糊查询（支持用户名）
- ✅ 操作类型精确匹配（CREATE/UPDATE/DELETE/QUERY/LOGIN/OTHER）
- ✅ 日期范围查询（支持多种日期格式）
- ✅ 自动解析日期：`yyyy-MM-dd HH:mm:ss`, `yyyy-MM-dd` 等

### 4. 数据库表结构升级

#### 方案一：如果是新项目（无数据）
直接执行最新的 `init.sql` 重新创建表

#### 方案二：如果已有数据
执行迁移脚本：
```sql
-- 添加 username 字段
ALTER TABLE `operation_logs` 
ADD COLUMN `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人员用户名' AFTER `admin_id`;

-- 添加 username 索引
ALTER TABLE `operation_logs` 
ADD INDEX `idx_username` (`username`);
```

迁移脚本位置：`管理后台/src/main/resources/sql/migration/20260326_add_username_to_operation_logs.sql`

## 测试验证

### 测试用例 1：基本查询
```bash
GET /api/admin/logs?page=1&pageSize=10
```

### 测试用例 2：按操作人员查询
```bash
GET /api/admin/logs?page=1&pageSize=10&operator=admin
```

### 测试用例 3：按操作类型查询
```bash
GET /api/admin/logs?page=1&pageSize=10&operationType=LOGIN
```

### 测试用例 4：日期范围查询
```bash
GET /api/admin/logs?page=1&pageSize=10&startDate=2026-03-01&endDate=2026-03-26
```

### 测试用例 5：组合查询
```bash
GET /api/admin/logs?page=1&pageSize=10&operator=admin&operationType=LOGIN&startDate=2026-03-01&endDate=2026-03-26
```

## 修改文件清单

### 后端文件
1. ✅ `管理后台/src/main/java/com/jz/miniapp/entity/OperationLog.java`
   - 更新字段映射和注释
   
2. ✅ `管理后台/src/main/java/com/jz/miniapp/controller/admin/AdminLogController.java`
   - 更新请求参数
   
3. ✅ `管理后台/src/main/java/com/jz/miniapp/service/OperationLogService.java`
   - 更新接口方法签名
   
4. ✅ `管理后台/src/main/java/com/jz/miniapp/service/impl/OperationLogServiceImpl.java`
   - 实现完整查询逻辑
   - 添加日期解析方法

### SQL 文件
5. ✅ `管理后台/src/main/resources/sql/init.sql`
   - 添加 username 字段和索引
   
6. ✅ `管理后台/src/main/resources/sql/migration/20260326_add_username_to_operation_logs.sql`
   - 新增迁移脚本

### 前端文件（无需修改）
- ✅ `管理后台前端/src/views/log/OperationLogs.vue` - 已经是正确的
- ✅ `管理后台前端/src/api/log.js` - 已经是正确的

## 注意事项

1. **数据库必须先执行迁移脚本**，否则会出现字段不存在的错误
2. 日期格式支持多种输入方式：
   - `yyyy-MM-dd`（推荐）
   - `yyyy-MM-dd HH:mm:ss`
   - `yyyy/MM/dd`
   - `yyyy/MM/dd HH:mm:ss`
3. 操作类型包括：`CREATE`, `UPDATE`, `DELETE`, `QUERY`, `LOGIN`, `OTHER`
4. 操作人员支持模糊查询，可以输入用户名或手机号的部分内容

## 重启服务

完成上述修改后，重启后端服务即可正常使用。
