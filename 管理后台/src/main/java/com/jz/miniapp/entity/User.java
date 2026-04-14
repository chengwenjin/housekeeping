package com.jz.miniapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("users")
@Schema(description = "用户信息")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户 ID")
    private Long id;

    /**
     * 微信 openid
     */
    @TableField("openid")
    @Schema(description = "微信 openid")
    private String openid;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 昵称
     */
    @TableField("nickname")
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 头像 URL
     */
    @TableField("avatar_url")
    @Schema(description = "头像 URL")
    private String avatarUrl;

    /**
     * 性别 (0:未知，1:男，2:女)
     */
    @TableField("gender")
    @Schema(description = "性别 (0:未知，1:男，2:女)")
    private Integer gender;

    /**
     * 实名认证状态 (0:未认证，1:已认证)
     */
    @TableField("certification_status")
    @Schema(description = "实名认证状态 (0:未认证，1:已认证)")
    private Integer certificationStatus;

    /**
     * 评分
     */
    @TableField("score")
    @Schema(description = "用户评分")
    private BigDecimal score;

    /**
     * 总订单数
     */
    @TableField("total_orders")
    @Schema(description = "总订单数")
    private Integer totalOrders;

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    @Schema(description = "粉丝数")
    private Integer followerCount;

    /**
     * 关注数
     */
    @TableField("following_count")
    @Schema(description = "关注数")
    private Integer followingCount;

    /**
     * 状态 (0:禁用，1:正常)
     */
    @TableField("status")
    @Schema(description = "状态 (0:禁用，1:正常)")
    private Integer status;

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
