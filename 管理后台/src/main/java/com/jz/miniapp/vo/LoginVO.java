package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    /**
     * Token
     */
    @Schema(description = "访问令牌 token")
    private String token;

    /**
     * 刷新 Token
     */
    @Schema(description = "刷新令牌 refreshToken")
    private String refreshToken;

    /**
     * 过期时间 (秒)
     */
    @Schema(description = "token 有效期 (秒)")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserVO user;
}
