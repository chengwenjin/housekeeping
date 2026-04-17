package com.jz.miniapp.common;

public class UserContext {
    
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> openidHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<Long> adminIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isAdminHolder = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
        isAdminHolder.set(false);
    }

    public static Long getUserId() {
        return userIdHolder.get();
    }

    public static void setOpenid(String openid) {
        openidHolder.set(openid);
    }

    public static String getOpenid() {
        return openidHolder.get();
    }

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void setAdminId(Long adminId) {
        adminIdHolder.set(adminId);
        isAdminHolder.set(true);
    }

    public static Long getAdminId() {
        return adminIdHolder.get();
    }

    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    public static boolean isAdmin() {
        Boolean isAdmin = isAdminHolder.get();
        return isAdmin != null && isAdmin;
    }

    public static void clear() {
        userIdHolder.remove();
        openidHolder.remove();
        tokenHolder.remove();
        adminIdHolder.remove();
        usernameHolder.remove();
        isAdminHolder.remove();
    }
}
