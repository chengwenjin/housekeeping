# Swagger/Knife4j API 文档使用说明

## ✅ 配置完成

**Knife4j OpenAPI 3.0 已成功集成!**

### 📖 访问地址

1. **Knife4j 增强界面** (推荐)
   - 地址：http://localhost:8080/api/doc.html
   - 特点：更美观的界面，支持离线文档导出

2. **原生 Swagger UI**
   - 地址：http://localhost:8080/api/swagger-ui.html
   - 特点：标准 Swagger 界面

3. **OpenAPI JSON**
   - 地址：http://localhost:8080/api/v3/api-docs
   - 用途：获取 OpenAPI 规范文档

### 🔧 技术栈

- **框架**: Spring Boot 2.7.18
- **Swagger**: OpenAPI 3.0
- **Knife4j**: 4.1.0 (适配 Spring Boot 2.7.x)

### 📝 使用示例

#### 1. Controller 添加注解

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据 ID 查询用户信息")
    public Result<User> getUser(
            @Parameter(description = "用户 ID", example = "1") 
            @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
    
    @PostMapping
    @Operation(summary = "创建用户")
    public Result<User> createUser(@RequestBody User user) {
        return Result.success(userService.save(user));
    }
}
```

#### 2. 实体类添加注解

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "用户信息")
public class User implements Serializable {
    
    @Schema(description = "用户 ID", example = "1")
    private Long id;
    
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
```

#### 3. 统一响应结果

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {
    
    @Schema(description = "状态码", example = "200")
    private Integer code;
    
    @Schema(description = "响应消息", example = "success")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "时间戳", example = "1711468800000")
    private Long timestamp;
}
```

### 🎯 常用注解说明

| 注解 | 用途 | 示例 |
|------|------|------|
| `@Tag` | 标注 Controller 分类 | `@Tag(name = "用户管理")` |
| `@Operation` | 描述接口功能 | `@Operation(summary = "创建用户")` |
| `@Parameter` | 描述参数信息 | `@Parameter(description = "用户 ID")` |
| `@Schema` | 描述数据结构 | `@Schema(description = "用户昵称")` |
| `@io.swagger.v3.oas.annotations.responses.ApiResponse` | 描述响应 | `@ApiResponse(responseCode = "200")` |

### 📦 已测试接口

当前项目中已添加 Swagger 注解的接口:

1. ✅ **健康检查接口** (`/api/mini/health`)
   - GET 请求
   - 返回：`{"code":200,"message":"success","data":"服务运行正常","timestamp":...}`

### 🚀 下一步工作

建议按以下顺序完善其他接口的 Swagger 注解:

1. **小程序端 API**
   - 用户认证接口
   - 需求管理接口
   - 订单管理接口
   - 评价接口

2. **管理后台 API**
   - 管理员登录接口
   - 需求审核接口
   - 订单管理接口
   - 数据统计接口

### 💡 最佳实践

1. **每个 Controller 都要加 `@Tag` 注解**
   - 清晰标识模块功能
   
2. **每个接口都要加 `@Operation` 注解**
   - 包含 summary 和 description
   
3. **所有入参都要加 `@Parameter` 注解**
   - 说明参数含义和示例值
   
4. **所有 DTO/VO 都要加 `@Schema` 注解**
   - 方便生成 API 文档

### ⚠️ 注意事项

1. **依赖版本兼容性**
   - 已验证：Spring Boot 2.7.18 + Knife4j 4.1.0 ✅
   - 不要升级到 Knife4j 3.x (不兼容)

2. **包扫描路径**
   - 已在 application.yml 中配置
   - `knife4j.setting.package-to-scan: com.jz.miniapp.controller`

3. **生产环境禁用**
   - 生产环境应关闭 Swagger
   - 可通过 profile 控制：`knife4j.enable: false`

---

**Swagger/Knife4j 已就绪，可以开始编写 API 文档了!** 🎉
