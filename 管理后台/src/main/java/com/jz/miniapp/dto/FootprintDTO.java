package com.jz.miniapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加足迹请求 DTO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "添加足迹请求")
public class FootprintDTO {

    /**
     * 目标类型 (1:需求，2:服务者)
     */
    @NotNull(message = "目标类型不能为空")
    @Schema(description = "目标类型", required = true, example = "1")
    private Integer targetType;

    /**
     * 目标 ID (需求 ID 或服务者 ID)
     */
    @NotNull(message = "目标 ID 不能为空")
    @Schema(description = "目标 ID", required = true, example = "100")
    private Long targetId;

    /**
     * 标题
     */
    @Schema(description = "标题", example = "专业保洁服务")
    private String title;

    /**
     * 图片 URL
     */
    @Schema(description = "图片 URL", example = "/images/service1.jpg")
    private String imageUrl;
}
