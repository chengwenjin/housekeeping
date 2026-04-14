package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户足迹 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "用户足迹信息")
public class FootprintVO {

    /**
     * 足迹 ID
     */
    @Schema(description = "足迹 ID")
    private Long id;

    /**
     * 目标类型 (1:需求，2:服务者)
     */
    @Schema(description = "目标类型")
    private Integer targetType;

    /**
     * 目标类型文本
     */
    @Schema(description = "目标类型文本")
    private String targetTypeText;

    /**
     * 目标 ID
     */
    @Schema(description = "目标 ID")
    private Long targetId;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 图片 URL
     */
    @Schema(description = "图片 URL")
    private String imageUrl;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
