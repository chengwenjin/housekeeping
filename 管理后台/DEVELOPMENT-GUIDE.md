# 家政小程序后端开发指南

## 📖 目录

1. [项目架构说明](#项目架构说明)
2. [代码规范](#代码规范)
3. [开发流程](#开发流程)
4. [常见问题](#常见问题)

## 项目架构说明

### 分层架构

```
Controller 层  →  接收请求，参数校验
     ↓
Service 层    →  业务逻辑处理
     ↓
Mapper 层     →  数据访问层
```

### 包结构说明

```
com.jz.miniapp/
├── controller/          # 控制器层
│   ├── api/            # 小程序 API 控制器
│   └── admin/          # 管理后台控制器
├── service/             # 服务层接口
│   └── impl/           # 服务层实现
├── mapper/              # 数据访问层
├── entity/              # 实体类
├── dto/                 # 数据传输对象
├── vo/                  # 视图对象
├── common/              # 公共类 (Result, ResultCode)
├── config/              # 配置类
├── enums/               # 枚举
├── exception/           # 异常处理
├── interceptor/         # 拦截器
└── util/                # 工具类
```

## 代码规范

### 1. 命名规范

#### 类命名
- **Controller**: `XxxController` (例：`DemandController`)
- **Service**: `XxxService` (例：`DemandService`)
- **ServiceImpl**: `XxxServiceImpl` (例：`DemandServiceImpl`)
- **Mapper**: `XxxMapper` (例：`DemandMapper`)
- **Entity**: 业务实体名 (例：`User`, `Demand`)
- **DTO**: `XxxDTO` (例：`DemandDTO`)
- **VO**: `XxxVO` (例：`DemandVO`)

#### 方法命名
- **查询**: `getById`, `listAll`, `pageQuery`
- **新增**: `create`, `save`, `insert`
- **修改**: `update`, `modify`
- **删除**: `delete`, `remove`
- **布尔值**: `isXxx`, `hasXxx`, `canXxx`

### 2. 注释规范

#### 类注释
```java
/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
```

#### 方法注释
```java
/**
 * 根据 openid 查询用户
 * @param openid 微信 openid
 * @return 用户信息
 */
User selectByOpenid(@Param("openid") String openid);
```

### 3. 统一响应格式

所有接口必须返回 `Result<T>` 对象:

```java
// 成功响应
return Result.success(data);

// 带消息的成功响应
return Result.success("操作成功", data);

// 失败响应
return Result.error(code, message);

// 使用枚举
return Result.from(ResultCode.DEMAND_NOT_FOUND);
```

### 4. 异常处理

#### 业务异常
```java
if (demand == null) {
    throw new BusinessException(ResultCode.DEMAND_NOT_FOUND);
}
```

#### 自定义错误码
在 `ResultCode` 枚举中添加:

```java
DEMAND_NOT_FOUND(20001, "需求不存在"),
```

### 5. 事务处理

Service 层的写操作方法需要添加事务注解:

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void updateDemand(Demand demand) {
    // 业务逻辑
}
```

## 开发流程

### 1. 创建新功能的步骤

#### Step 1: 数据库表设计
- 在 `init.sql` 中定义表结构
- 确保包含 `created_at`, `updated_at`, `deleted_at` 字段

#### Step 2: 创建实体类
```java
@Data
@TableName("demands")
@ApiModel(description = "需求信息")
public class Demand implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    // ... 其他字段
}
```

#### Step 3: 创建 Mapper 接口
```java
@Mapper
public interface DemandMapper extends BaseMapper<Demand> {
    // 自定义查询方法
}
```

#### Step 4: 创建 Mapper XML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.jz.miniapp.mapper.DemandMapper">
    <!-- 自定义 SQL -->
</mapper>
```

#### Step 5: 创建 Service 接口和实现
```java
public interface DemandService extends IService<Demand> {
    Page<Demand> getDemandPage(Integer page, Integer pageSize, ...);
}
```

```java
@Slf4j
@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> 
    implements DemandService {
    
    @Override
    public Page<Demand> getDemandPage(...) {
        // 实现逻辑
    }
}
```

#### Step 6: 创建 Controller
```java
@Slf4j
@RestController
@RequestMapping("/mini/demands")
@Api(tags = "需求管理")
public class MiniDemandController {
    
    @Autowired
    private DemandService demandService;
    
    @GetMapping
    public Result<Page<Demand>> getDemands(...) {
        return Result.success(demandService.getDemandPage(...));
    }
}
```

#### Step 7: 测试验证
- 启动项目
- 访问 Swagger 文档：http://localhost:8080/api/doc.html
- 测试接口

## 常用代码示例

### 1. 分页查询

```java
// Service 层
Page<Demand> mpPage = new Page<>(page, pageSize);
LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Demand::getStatus, status);
wrapper.orderByDesc(Demand::getCreatedAt);
return baseMapper.selectPage(mpPage, wrapper);
```

### 2. 条件查询

```java
LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(status != null, Demand::getStatus, status)
       .eq(categoryId != null, Demand::getCategoryId, categoryId)
       .like(StrUtil.isNotBlank(keyword), Demand::getTitle, keyword)
       .orderByDesc(Demand::getCreatedAt);
List<Demand> list = demandMapper.selectList(wrapper);
```

### 3. 事务处理

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void takeDemand(Long demandId, Long takerId) {
    // 更新需求状态
    Demand demand = getById(demandId);
    demand.setStatus(DemandStatus.TAKEN);
    demand.setTakenBy(takerId);
    updateById(demand);
    
    // 创建订单
    Order order = new Order();
    order.setDemandId(demandId);
    orderService.createOrder(order);
}
```

### 4. 获取当前登录用户 ID

```java
@GetMapping("/{id}")
public Result<Demand> getDetail(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    // 使用 userId
}
```

## 常见问题

### Q1: 如何添加新的 API 接口？

1. 在对应的 Controller 中添加方法
2. 使用 `@ApiOperation` 添加接口说明
3. 使用 `@ApiParam` 添加参数说明
4. 确保方法有适当的权限控制 (JWT 拦截)

### Q2: 如何处理文件上传？

```java
@PostMapping("/upload")
public Result<Map<String, String>> uploadImage(
        @RequestParam("file") MultipartFile file) {
    
    if (file.isEmpty()) {
        throw new BusinessException("文件不能为空");
    }
    
    // TODO: 调用 OSS 工具类上传图片
    
    Map<String, String> result = new HashMap<>();
    result.put("url", "https://xxx.com/image.jpg");
    return Result.success(result);
}
```

### Q3: 如何实现定时任务？

```java
@Component
public class ScheduledTasks {
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨 2 点执行
    public void cleanExpiredDemands() {
        // 清理过期的需求
    }
}
```

需要在启动类添加 `@EnableScheduling` 注解。

### Q4: 如何记录操作日志？

使用 AOP 切面编程:

```java
@Aspect
@Component
public class LogAspect {
    
    @Around("@annotation(controllerLog)")
    public Object around(ProceedingJoinPoint point, ControllerLog controllerLog) {
        // 记录日志逻辑
    }
}
```

### Q5: 如何处理跨域问题？

已在 `WebConfig` 中配置 CORS:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

## Git 分支管理

### 分支规范

- **master**: 主分支，生产环境代码
- **develop**: 开发分支，日常开发
- **feature/Xxx**: 功能分支，从 develop 分出，合并回 develop
- **hotfix/Xxx**: 修复分支，从 master 分出，合并回 master 和 develop

### 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具链相关
```

示例:
```bash
git commit -m "feat: 添加用户评价功能"
git commit -m "fix: 修复订单状态更新问题"
```

## 性能优化建议

1. **数据库索引**: 为频繁查询的字段添加索引
2. **Redis 缓存**: 缓存热点数据 (如用户信息、分类列表)
3. **分页查询**: 避免全表扫描
4. **批量操作**: 使用批量插入/更新
5. **慢查询优化**: 定期查看并优化慢 SQL

## 安全建议

1. **密码加密**: 使用 bcrypt 加密存储
2. **SQL 注入防护**: 使用 MyBatis 预编译
3. **XSS 防护**: 对用户输入进行过滤
4. **CSRF 防护**: 使用 Token 验证
5. **敏感信息**: 不打印到日志中

---

**版本**: v1.0  
**更新日期**: 2026-03-26
