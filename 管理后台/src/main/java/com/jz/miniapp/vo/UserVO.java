package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID")
    private Long id;

    /**
     * 微信 openid
     */
    @Schema(description = "微信 openid")
    private String openid;

    /**
     * 昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

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

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 实名认证状态 (0:未认证，1:已认证)
     */
    @Schema(description = "实名认证状态")
    private Integer certificationStatus;

    /**
     * 评分
     */
    @Schema(description = "用户评分")
    private java.math.BigDecimal score;

    /**
     * 总订单数
     */
    @Schema(description = "总订单数")
    private Integer totalOrders;

    /**
     * 粉丝数
     */
    @Schema(description = "粉丝数")
    private Integer followerCount;

    /**
     * 关注数
     */
    @Schema(description = "关注数")
    private Integer followingCount;
}
