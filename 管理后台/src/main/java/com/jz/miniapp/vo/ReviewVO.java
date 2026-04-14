package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "评价信息")
public class ReviewVO {

    /**
     * 评价 ID
     */
    @Schema(description = "评价 ID")
    private Long id;

    /**
     * 订单 ID
     */
    @Schema(description = "订单 ID")
    private Long orderId;

    /**
     * 需求 ID
     */
    @Schema(description = "需求 ID")
    private Long demandId;

    /**
     * 评价者 ID
     */
    @Schema(description = "评价者 ID")
    private Long reviewerId;

    /**
     * 被评价者 ID
     */
    @Schema(description = "被评价者 ID")
    private Long revieweeId;

    /**
     * 评分 (1-5 星)
     */
    @Schema(description = "评分")
    private BigDecimal rating;

    /**
     * 评价内容
     */
    @Schema(description = "评价内容")
    private String content;

    /**
     * 评价图片
     */
    @Schema(description = "评价图片")
    private java.util.List<String> images;

    /**
     * 评价类型
     */
    @Schema(description = "评价类型")
    private Integer type;

    /**
     * 评价类型文本
     */
    @Schema(description = "评价类型文本")
    private String typeText;

    /**
     * 是否匿名
     */
    @Schema(description = "是否匿名")
    private Integer isAnonymous;

    /**
     * 回复内容
     */
    @Schema(description = "回复内容")
    private String replyContent;

    /**
     * 回复时间
     */
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本")
    private String statusText;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 创建时间文本
     */
    @Schema(description = "创建时间文本")
    private String createdAtText;
}
