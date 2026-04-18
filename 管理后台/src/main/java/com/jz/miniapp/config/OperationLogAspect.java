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
        
        log.info("========== 开始拦截管理后台操作 ==========");
        log.info("拦截到方法: {}.{}", className, methodName);
        log.info("Request是否为null: {}", request == null);
        
        if (request != null) {
            log.info("请求URI: {}", request.getRequestURI());
            log.info("请求URL: {}", request.getRequestURL());
            log.info("请求方法: {}", request.getMethod());
            log.info("请求路径是否包含/admin/: {}", request.getRequestURI().contains("/admin/"));
        }
        
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
                
                if (operationLog.getAdminId() == null) {
                    Long currentAdminId = UserContext.getAdminId();
                    String currentUsername = UserContext.getUsername();
                    if (currentAdminId != null) {
                        operationLog.setAdminId(currentAdminId);
                    }
                    if (currentUsername != null) {
                        operationLog.setUsername(currentUsername);
                    }
                }
                
                log.info("准备保存操作日志 - URL: {}, Action: {}, Module: {}, AdminId: {}, Username: {}",
                        operationLog.getUrl(), operationLog.getAction(), operationLog.getModule(),
                        operationLog.getAdminId(), operationLog.getUsername());
                
                if (shouldSaveLog(operationLog)) {
                    log.info("执行保存操作日志 - action={}, module={}, adminId={}, duration={}ms", 
                            operationLog.getAction(), operationLog.getModule(), 
                            operationLog.getAdminId(), duration);
                    operationLogService.save(operationLog);
                    log.info("操作日志保存成功");
                } else {
                    log.info("操作日志不满足保存条件，跳过保存 - URL: {}", operationLog.getUrl());
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
        log.info("========== shouldSaveLog 检查 ==========");
        log.info("URL: {}", log.getUrl());
        log.info("Action: {}", log.getAction());
        log.info("Module: {}", log.getModule());
        log.info("AdminId: {}", log.getAdminId());
        
        if (log.getUrl() == null) {
            log.info("shouldSaveLog 返回 false - URL 为 null");
            return false;
        }
        
        if (log.getUrl().contains("/admin/logs")) {
            log.info("shouldSaveLog 返回 false - 是操作日志查询接口");
            return false;
        }
        
        if (!log.getUrl().contains("/admin/")) {
            log.info("shouldSaveLog 返回 false - URL 不包含 /admin/");
            return false;
        }
        
        log.info("shouldSaveLog 返回 true - 满足所有保存条件");
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
