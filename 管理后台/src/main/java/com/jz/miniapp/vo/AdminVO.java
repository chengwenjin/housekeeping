package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "管理员信息")
public class AdminVO {

    /**
     * 管理员 ID
     */
    @Schema(description = "管理员 ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 头像 URL
     */
    @Schema(description = "头像 URL")
    private String avatar;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 角色 ID
     */
    @Schema(description = "角色 ID")
    private Long roleId;
}
