package com.jz.miniapp.config;

import cn.hutool.json.JSONUtil;
import com.jz.miniapp.common.UserContext;
import com.jz.miniapp.entity.OperationLog;
import com.jz.miniapp.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    private static final Map<String, String> MODULE_MAP = new HashMap<>();
    private static final Map<String, String> ACTION_DESC_MAP = new HashMap<>();

    static {
        MODULE_MAP.put("/admin/auth", "认证管理");
        MODULE_MAP.put("/admin/categories", "分类管理");
        MODULE_MAP.put("/admin/users", "用户管理");
        MODULE_MAP.put("/admin/demands", "需求管理");
        MODULE_MAP.put("/admin/orders", "订单管理");
        MODULE_MAP.put("/admin/reviews", "评价管理");
        MODULE_MAP.put("/admin/logs", "日志管理");
        MODULE_MAP.put("/admin/system", "系统管理");
        MODULE_MAP.put("/admin/statistics", "统计管理");

        ACTION_DESC_MAP.put("CREATE", "创建");
        ACTION_DESC_MAP.put("UPDATE", "更新");
        ACTION_DESC_MAP.put("DELETE", "删除");
        ACTION_DESC_MAP.put("QUERY", "查询");
        ACTION_DESC_MAP.put("LOGIN", "登录");
        ACTION_DESC_MAP.put("OTHER", "其他");
    }

    @Pointcut("execution(* com.jz.miniapp.controller.admin.*.*(..))")
    public void adminControllerPointcut() {
    }

    @Around("adminControllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("拦截管理后台操作: {}.{}", className, methodName);
        
        OperationLog operationLog = new OperationLog();
        
        try {
            if (request != null) {
                operationLog.setIp(getClientIp(request));
                operationLog.setUrl(request.getRequestURI());
                operationLog.setMethod(request.getMethod());
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                
                String action = determineAction(request.getMethod(), request.getRequestURI());
                operationLog.setAction(action);
                
                String module = determineModule(request.getRequestURI());
                operationLog.setModule(module);
                
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (parameterMap != null && !parameterMap.isEmpty()) {
                    operationLog.setRequestData(JSONUtil.toJsonStr(parameterMap));
                }
            }
            
            Long adminId = UserContext.getAdminId();
            String username = UserContext.getUsername();
            if (adminId != null) {
                operationLog.setAdminId(adminId);
            }
            if (username != null) {
                operationLog.setUsername(username);
            }
            
            operationLog.setCreatedAt(LocalDateTime.now());
            
        } catch (Exception e) {
            log.warn("收集操作日志信息失败", e);
        }
        
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            
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
                
                if (shouldSaveLog(operationLog)) {
                    log.debug("保存操作日志: action={}, module={}, adminId={}, duration={}ms", 
                            operationLog.getAction(), operationLog.getModule(), 
                            operationLog.getAdminId(), duration);
                    operationLogService.save(operationLog);
                }
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    private String determineAction(String httpMethod, String uri) {
        if (uri.contains("/login")) {
            return "LOGIN";
        }
        
        if ("GET".equalsIgnoreCase(httpMethod)) {
            return "QUERY";
        } else if ("POST".equalsIgnoreCase(httpMethod)) {
            return "CREATE";
        } else if ("PUT".equalsIgnoreCase(httpMethod)) {
            return "UPDATE";
        } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
            return "DELETE";
        }
        
        return "OTHER";
    }

    private String determineModule(String uri) {
        for (Map.Entry<String, String> entry : MODULE_MAP.entrySet()) {
            if (uri.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "其他";
    }

    private boolean shouldSaveLog(OperationLog log) {
        if ("/admin/logs".equals(log.getUrl()) || log.getUrl().contains("/admin/logs")) {
            return false;
        }
        
        if (log.getAdminId() == null && !"LOGIN".equals(log.getAction())) {
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
