package com.jz.miniapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录请求 DTO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginDTO {

    /**
     * 微信登录凭证 code
     */
    @NotBlank(message = "code 不能为空")
    @Schema(description = "微信登录凭证 code", required = true)
    private String code;

    /**
     * 加密数据
     */
    @NotBlank(message = "encryptedData 不能为空")
    @Schema(description = "加密数据", required = true)
    private String encryptedData;

    /**
     * 解密向量
     */
    @NotBlank(message = "iv 不能为空")
    @Schema(description = "解密向量 iv", required = true)
    private String iv;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 头像 URL
     */
    @Schema(description = "头像 URL")
    private String avatarUrl;

    /**
     * 性别 (0:未知，1:男，2:女)
     */
    @Schema(description = "性别 (0:未知，1:男，2:女)")
    private Integer gender;
}
