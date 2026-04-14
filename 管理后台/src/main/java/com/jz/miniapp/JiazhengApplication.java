package com.jz.miniapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.jz.miniapp.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class JiazhengApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiazhengApplication.class, args);
        System.out.println("========================================");
        System.out.println("家政小程序后端服务启动成功!");
        System.out.println("API 文档地址：http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}
