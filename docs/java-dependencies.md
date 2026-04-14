# 家政小程序 - Java 后端技术依赖说明

## 1. 开发环境要求

### 1.1 基础环境
- **JDK 版本**: Java 11 (LTS)
- **IDE 推荐**: IntelliJ IDEA 2023+ / Eclipse 2023+
- **构建工具**: Maven 3.8+ / Gradle 7+
- **版本控制**: Git

### 1.2 中间件
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **对象存储**: 阿里云 OSS / 腾讯云 COS

## 2. Maven 依赖配置 (pom.xml)

### 2.1 父工程配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>
    
    <groupId>com.jiazheng</groupId>
    <artifactId>jiazheng-backend</artifactId>
    <version>1.0.0</version>
    <name>家政小程序后端</name>
    <description>家政服务撮合平台后端服务</description>
    
    <properties>
        <java.version>11</java.version>
        <mybatis-plus.version>3.5.3.1</mybatis-plus.version>
        <jwt.version>0.11.5</jwt.version>
        <knife4j.version>3.0.3</knife4j.version>
        <hutool.version>5.8.16</hutool.version>
        <oss.version>3.17.2</oss.version>
    </properties>
</project>
```

### 2.2 核心依赖

#### Spring Boot 基础
```xml
<dependencies>
    <!-- Web 服务 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 验证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- AOP -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

#### 数据库相关
```xml
<!-- MySQL 驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>${mybatis-plus.version}</version>
</dependency>

<!-- 分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.6</version>
</dependency>

<!-- 连接池 -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>
```

#### Redis 缓存
```xml
<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Redis 连接池 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

#### JWT 认证
```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>${jwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>${jwt.version}</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jwt.version}</version>
    <scope>runtime</scope>
</dependency>
```

#### 微信 SDK
```xml
<!-- 微信小程序 SDK -->
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-miniapp</artifactId>
    <version>4.5.0</version>
</dependency>
```

#### OSS 对象存储
```xml
<!-- 阿里云 OSS -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>${oss.version}</version>
</dependency>

<!-- 或 腾讯云 COS -->
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.89</version>
</dependency>
```

#### API 文档
```xml
<!-- Knife4j (Swagger 增强) -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>${knife4j.version}</version>
</dependency>
```

#### 工具类
```xml
<!-- Hutool 工具包 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>${hutool.version}</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- JSON 处理 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.25</version>
</dependency>

<!-- 图片压缩 -->
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.19</version>
</dependency>
```

#### 测试相关
```xml
<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
</dependencies>
```

### 2.3 构建配置
```xml
<build>
    <plugins>
        <!-- Spring Boot Maven 插件 -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
        
        <!-- Maven Compiler 插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>11</source>
                <target>11</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 3. Spring Boot 配置文件

### 3.1 application.yml 主配置
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: jiazheng-backend
  
  profiles:
    active: dev
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiazheng?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
  
  # Redis 配置
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  
  # Jackson 配置
  jackson:
    time-zone: Asia/Shanghai
    date-format: yyyy-MM-dd HH:mm:ss
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# MyBatis-Plus 配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.jiazheng.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deletedAt
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT 配置
jwt:
  secret: your-secret-key-here-at-least-256-bits-long
  expiration: 604800000  # 7 天
  refresh-expiration: 2592000000  # 30 天
  header: Authorization
  prefix: "Bearer "

# 微信小程序配置
wx:
  miniapp:
    appid: ${WX_MINIAPP_APPID}
    secret: ${WX_MINIAPP_SECRET}
    config-storage:
      type: Memory

# 阿里云 OSS 配置
aliyun:
  oss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: ${ALIYUN_ACCESS_KEY_ID}
    access-key-secret: ${ALIYUN_ACCESS_KEY_SECRET}
    bucket-name: jiazheng-images

# Knife4j 配置
knife4j:
  enable: true
  setting:
    language: zh-CN
```

### 3.2 application-dev.yml 开发环境
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiazheng_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: dev_user
    password: dev_password
  
  redis:
    host: localhost
    port: 6379

logging:
  level:
    root: INFO
    com.jiazheng: DEBUG
    org.springframework.web: DEBUG
    com.baomidou.mybatisplus: DEBUG
```

### 3.3 application-prod.yml 生产环境
```yaml
server:
  port: 8080
  
spring:
  datasource:
    url: jdbc:mysql://prod-db-host:3306/jiazheng_prod?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
    username: prod_user
    password: ${DB_PASSWORD}
  
  redis:
    host: prod-redis-host
    port: 6379
    password: ${REDIS_PASSWORD}

logging:
  level:
    root: WARN
    com.jiazheng: INFO

# 启用 Actuator 监控
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
```

## 4. 项目启动类

```java
package com.jiazheng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 家政小程序后端启动类
 */
@SpringBootApplication
@MapperScan("com.jiazheng.mapper")
@EnableAsync  // 启用异步方法
@EnableScheduling  // 启用定时任务
public class JiazhengApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiazhengApplication.class, args);
        System.out.println("====================================");
        System.out.println("    家政小程序后端启动成功!");
        System.out.println("====================================");
    }
}
```

## 5. 统一响应格式

```java
package com.jiazheng.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装
 */
@Data
@ApiModel(description = "统一响应结果")
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "状态码", example = "200")
    private Integer code;
    
    @ApiModelProperty(value = "响应消息", example = "success")
    private String message;
    
    @ApiModelProperty(value = "响应数据")
    private T data;
    
    @ApiModelProperty(value = "时间戳", example = "1711468800000")
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success() {
        return success(null);
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> fail(BusinessException ex) {
        return error(ex.getCode(), ex.getMessage());
    }
}
```

## 6. 全局异常处理

```java
package com.jiazheng.common.exception;

import com.jiazheng.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }
    
    /**
     * 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception ex) {
        String message = "参数校验失败";
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        } else if (ex instanceof BindException) {
            BindException e = (BindException) ex;
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.warn("参数校验异常：{}", message);
        return Result.error(400, message);
    }
    
    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("系统异常：", ex);
        return Result.error("系统繁忙，请稍后再试");
    }
}
```

## 7. 业务异常类

```java
package com.jiazheng.common.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
```

## 8. 常用枚举类

### 8.1 需求状态枚举
```java
package com.jiazheng.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 需求状态枚举
 */
@Getter
@AllArgsConstructor
public enum DemandStatusEnum {
    
    RECRUITING(1, "招募中"),
    TAKEN(2, "已接单"),
    IN_PROGRESS(3, "进行中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    EXPIRED(6, "已过期");
    
    private final Integer code;
    private final String desc;
    
    public static DemandStatusEnum getByCode(Integer code) {
        for (DemandStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的需求状态：" + code);
    }
}
```

### 8.2 订单状态枚举
```java
package com.jiazheng.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    
    PENDING_SERVICE(1, "待服务"),
    IN_SERVICE(2, "服务中"),
    PENDING_CONFIRM(3, "待确认"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REVIEWED(6, "已评价");
    
    private final Integer code;
    private final String desc;
}
```

## 9. Docker 部署配置

### 9.1 Dockerfile
```dockerfile
# 基础镜像
FROM openjdk:11-jre-slim

# 作者信息
MAINTAINER jiazheng <admin@jiazheng.com>

# 设置工作目录
WORKDIR /app

# 复制 jar 包
COPY target/jiazheng-backend-1.0.0.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 9.2 docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: jiazheng-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: jiazheng
      MYSQL_USER: jiazheng_user
      MYSQL_PASSWORD: jiazheng123
    ports:
      - "3306:3306"
    volumes:
      - ./data/mysql:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - jiazheng-network

  redis:
    image: redis:6-alpine
    container_name: jiazheng-redis
    ports:
      - "6379:6379"
    networks:
      - jiazheng-network

  backend:
    build: .
    container_name: jiazheng-backend
    depends_on:
      - mysql
      - redis
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: mysql
      REDIS_HOST: redis
    networks:
      - jiazheng-network

networks:
  jiazheng-network:
    driver: bridge
```

## 10. 开发建议

### 10.1 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Checkstyle + SpotBugs 代码检查
- 统一使用 Lombok 简化代码

### 10.2 Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

### 10.3 分支管理
- `master`: 生产分支
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支
- `release/*`: 发布分支

---

**版本**: v1.0  
**更新日期**: 2026-03-26  
**适用版本**: Java 11
