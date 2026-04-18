package com.jz.miniapp.config;

import cn.hutool.json.JSONUtil;
import com.jz.miniapp.annotation.LogOperation;
import com.jz.miniapp.common.UserContext;
import com.jz.miniapp.entity.OperationLog;
import com.jz.miniapp.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogOperationAspect {

    private final OperationLogService operationLogService;

    @Pointcut("@annotation(com.jz.miniapp.annotation.LogOperation)")
    public void logOperationPointcut() {
    }

    @Around("logOperationPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        
        String module = logOperation.module();
        String action = logOperation.action();
        String description = logOperation.description();
        
        log.info("========== 注解拦截开始 ==========");
        log.info("方法: {}.{}", signature.getDeclaringTypeName(), method.getName());
        log.info("模块: {}, 操作: {}, 描述: {}", module, action, description);
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        OperationLog operationLog = new OperationLog();
        operationLog.setModule(module);
        operationLog.setAction(action);
        operationLog.setCreatedAt(LocalDateTime.now());
        
        try {
            if (request != null) {
                operationLog.setIp(getClientIp(request));
                operationLog.setUrl(request.getRequestURI());
                operationLog.setMethod(request.getMethod());
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (parameterMap != null && !parameterMap.isEmpty()) {
                    operationLog.setRequestData(JSONUtil.toJsonStr(parameterMap));
                }
                
                log.info("请求URI: {}", request.getRequestURI());
                log.info("请求方法: {}", request.getMethod());
            }
            
            Long adminId = UserContext.getAdminId();
            String username = UserContext.getUsername();
            log.info("当前UserContext - adminId: {}, username: {}", adminId, username);
            
            if (adminId != null) {
                operationLog.setAdminId(adminId);
            }
            if (username != null) {
                operationLog.setUsername(username);
            }
            
        } catch (Exception e) {
            log.warn("收集操作日志信息失败", e);
        }
        
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            
            if (operationLog.getAdminId() == null) {
                Long currentAdminId = UserContext.getAdminId();
                String currentUsername = UserContext.getUsername();
                log.info("方法执行后UserContext - adminId: {}, username: {}", currentAdminId, currentUsername);
                
                if (currentAdminId != null) {
                    operationLog.setAdminId(currentAdminId);
                }
                if (currentUsername != null) {
                    operationLog.setUsername(currentUsername);
                }
            }
            
            try {
                if (result != null) {
                    String responseJson = JSONUtil.toJsonStr(result);
                    if (responseJson.length() > 2000) {
                        responseJson = responseJson.substring(0, 2000) + "...";
                    }
                    operationLog.setResponseData(responseJson);
                }
                operationLog.setResponseCode(200);
            } catch (Exception e) {
                log.warn("记录响应数据失败", e);
            }
            
            return result;
            
        } catch (Throwable e) {
            exception = e;
            operationLog.setResponseCode(500);
            operationLog.setResponseData("异常: " + e.getMessage());
            throw e;
            
        } finally {
            try {
                long duration = System.currentTimeMillis() - startTime;
                operationLog.setDuration(duration);
                
                log.info("========== 准备保存操作日志 ==========");
                log.info("URL: {}", operationLog.getUrl());
                log.info("模块: {}", operationLog.getModule());
                log.info("操作: {}", operationLog.getAction());
                log.info("AdminId: {}", operationLog.getAdminId());
                log.info("Username: {}", operationLog.getUsername());
                log.info("耗时: {}ms", duration);
                
                if (shouldSaveLog(operationLog)) {
                    log.info("执行保存操作日志...");
                    operationLogService.save(operationLog);
                    log.info("操作日志保存成功！");
                } else {
                    log.info("操作日志不满足保存条件，跳过保存");
                }
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    private boolean shouldSaveLog(OperationLog log) {
        if (log.getUrl() == null) {
            log.warn("URL 为 null，不保存");
            return false;
        }
        
        if (log.getUrl().contains("/admin/logs")) {
            log.info("是操作日志查询接口，不保存");
            return false;
        }
        
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
