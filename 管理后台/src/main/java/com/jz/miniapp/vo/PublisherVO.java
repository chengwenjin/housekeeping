package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布人信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "发布人信息")
public class PublisherVO {

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID")
    private Long id;

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
     * 评分
     */
    @Schema(description = "用户评分")
    private BigDecimal score;
}
