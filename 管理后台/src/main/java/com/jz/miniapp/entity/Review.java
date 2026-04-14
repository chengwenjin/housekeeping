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
 * 评价实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("reviews")
@Schema(description = "评价信息")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "评价 ID")
    private Long id;

    /**
     * 订单 ID
     */
    @TableField("order_id")
    @Schema(description = "订单 ID")
    private Long orderId;

    /**
     * 需求 ID
     */
    @TableField("demand_id")
    @Schema(description = "需求 ID")
    private Long demandId;

    /**
     * 评价者 ID
     */
    @TableField("reviewer_id")
    @Schema(description = "评价者 ID")
    private Long reviewerId;

    /**
     * 被评价者 ID
     */
    @TableField("reviewee_id")
    @Schema(description = "被评价者 ID")
    private Long revieweeId;

    /**
     * 评分 (1-5 星)
     */
    @TableField("rating")
    @Schema(description = "评分 (1-5 星)")
    private BigDecimal rating;

    /**
     * 评价内容
     */
    @TableField("content")
    @Schema(description = "评价内容")
    private String content;

    /**
     * 评价图片 (逗号分隔)
     */
    @TableField("images")
    @Schema(description = "评价图片")
    private String images;

    /**
     * 评价类型 (1:客户评价服务者，2:服务者评价客户)
     */
    @TableField("type")
    @Schema(description = "评价类型")
    private Integer type;

    /**
     * 是否匿名 (0:否，1:是)
     */
    @TableField("is_anonymous")
    @Schema(description = "是否匿名")
    private Integer isAnonymous;

    /**
     * 回复内容
     */
    @TableField("reply_content")
    @Schema(description = "回复内容")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("reply_time")
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    /**
     * 状态 (0:待审核，1:已通过，2:已拒绝)
     */
    @TableField("status")
    @Schema(description = "状态")
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
