package com.jz.miniapp.config;

import com.alibaba.fastjson.JSON;
import com.jz.miniapp.common.AnonymousAccess;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.common.ResultCode;
import com.jz.miniapp.common.UserContext;
import com.jz.miniapp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private static final String OPTIONS_METHOD = "OPTIONS";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> ANONYMOUS_PATHS = Arrays.asList(
            "/mini/auth/login",
            "/mini/auth/refresh",
            "/mini/health",
            "/health"
    );

    private static final List<String> SWAGGER_PATHS = Arrays.asList(
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-resources",
            "/v3/api-docs",
            "/webjars"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        log.debug("拦截请求: {} {}", requestMethod, requestURI);

        if (OPTIONS_METHOD.equalsIgnoreCase(requestMethod)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        if (isSwaggerPath(requestURI)) {
            return true;
        }

        if (isAnonymousPath(requestURI)) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Class<?> clazz = method.getDeclaringClass();

            if (method.isAnnotationPresent(AnonymousAccess.class) 
                    || clazz.isAnnotationPresent(AnonymousAccess.class)) {
                return true;
            }
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || authorization.isEmpty()) {
            log.warn("未找到Authorization头: {}", requestURI);
            sendErrorResponse(response, ResultCode.UNAUTHORIZED, "未登录或Token已过期");
            return false;
        }

        try {
            String token = authorization;
            if (token.startsWith(BEARER_PREFIX)) {
                token = token.substring(BEARER_PREFIX.length()).trim();
            }

            Claims claims = jwtUtil.parseToken(token);
            String tokenType = (String) claims.get("type");

            if (isMiniApiPath(requestURI)) {
                if (!"mini".equals(tokenType)) {
                    log.warn("小程序接口使用了非小程序Token: {}", requestURI);
                    sendErrorResponse(response, ResultCode.FORBIDDEN, "无权限访问");
                    return false;
                }

                Object userIdObj = claims.get("userId");
                Long userId = null;
                if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                }

                String openid = (String) claims.get("openid");

                if (userId == null) {
                    log.warn("Token中缺少userId: {}", requestURI);
                    sendErrorResponse(response, ResultCode.UNAUTHORIZED, "Token无效");
                    return false;
                }

                UserContext.setUserId(userId);
                UserContext.setOpenid(openid);
                UserContext.setToken(token);

            } else if (isAdminApiPath(requestURI)) {
                if (!"admin".equals(tokenType)) {
                    log.warn("管理后台接口使用了非管理员Token: {}", requestURI);
                    sendErrorResponse(response, ResultCode.FORBIDDEN, "无权限访问");
                    return false;
                }

                Object adminIdObj = claims.get("adminId");
                Long adminId = null;
                if (adminIdObj instanceof Integer) {
                    adminId = ((Integer) adminIdObj).longValue();
                } else if (adminIdObj instanceof Long) {
                    adminId = (Long) adminIdObj;
                }

                String username = (String) claims.get("username");

                if (adminId == null) {
                    log.warn("Token中缺少adminId: {}", requestURI);
                    sendErrorResponse(response, ResultCode.UNAUTHORIZED, "Token无效");
                    return false;
                }

                UserContext.setAdminId(adminId);
                UserContext.setUsername(username);
                UserContext.setToken(token);
            }

            log.debug("Token校验成功, userId: {}, uri: {}", UserContext.getUserId(), requestURI);
            return true;

        } catch (RuntimeException e) {
            log.warn("Token校验失败: {} - {}", requestURI, e.getMessage());
            if ("Token已过期".equals(e.getMessage())) {
                sendErrorResponse(response, ResultCode.UNAUTHORIZED, "Token已过期");
            } else {
                sendErrorResponse(response, ResultCode.UNAUTHORIZED, "Token无效");
            }
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private boolean isAnonymousPath(String uri) {
        return ANONYMOUS_PATHS.stream().anyMatch(uri::contains);
    }

    private boolean isSwaggerPath(String uri) {
        return SWAGGER_PATHS.stream().anyMatch(uri::contains);
    }

    private boolean isMiniApiPath(String uri) {
        return uri.contains("/mini/") && !uri.contains("/admin/");
    }

    private boolean isAdminApiPath(String uri) {
        return uri.contains("/admin/");
    }

    private void sendErrorResponse(HttpServletResponse response, ResultCode code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        Result<Object> result = Result.fail(code.getCode(), message);
        String jsonResponse = JSON.toJSONString(result);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
