package com.jz.miniapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建评价请求 DTO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "创建评价请求")
public class ReviewDTO {

    /**
     * 订单 ID
     */
    @NotNull(message = "订单 ID 不能为空")
    @Schema(description = "订单 ID", required = true)
    private Long orderId;

    /**
     * 需求 ID
     */
    @NotNull(message = "需求 ID 不能为空")
    @Schema(description = "需求 ID", required = true)
    private Long demandId;

    /**
     * 被评价者 ID
     */
    @NotNull(message = "被评价者 ID 不能为空")
    @Schema(description = "被评价者 ID", required = true)
    private Long revieweeId;

    /**
     * 评分 (1-5 星)
     */
    @NotNull(message = "评分不能为空")
    @Schema(description = "评分 (1-5 星)", required = true, example = "5.0")
    private BigDecimal rating;

    /**
     * 评价内容
     */
    @NotBlank(message = "评价内容不能为空")
    @Schema(description = "评价内容", required = true)
    private String content;

    /**
     * 评价图片 (逗号分隔)
     */
    @Schema(description = "评价图片")
    private String images;

    /**
     * 评价类型 (1:客户评价服务者，2:服务者评价客户)
     */
    @NotNull(message = "评价类型不能为空")
    @Schema(description = "评价类型", required = true)
    private Integer type;

    /**
     * 是否匿名 (0:否，1:是)
     */
    @Schema(description = "是否匿名", defaultValue = "0")
    private Integer isAnonymous;
}
