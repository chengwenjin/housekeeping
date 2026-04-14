package com.jz.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("admins")
@Schema(description = "管理员信息")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "管理员 ID")
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码 (加密存储)
     */
    @TableField("password")
    @Schema(description = "密码 (加密存储)")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 头像 URL
     */
    @TableField("avatar")
    @Schema(description = "头像 URL")
    private String avatar;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    /**
     * 角色 ID
     */
    @TableField("role_id")
    @Schema(description = "角色 ID")
    private Long roleId;

    /**
     * 状态 (0:禁用，1:正常)
     */
    @TableField("status")
    @Schema(description = "状态 (0:禁用，1:正常)")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录 IP
     */
    @TableField("last_login_ip")
    @Schema(description = "最后登录 IP")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

}
