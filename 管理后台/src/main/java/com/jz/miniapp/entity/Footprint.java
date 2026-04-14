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
 * 用户足迹实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("footprints")
@Schema(description = "用户足迹信息")
public class Footprint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "足迹 ID")
    private Long id;

    /**
     * 用户 ID
     */
    @TableField("user_id")
    @Schema(description = "用户 ID")
    private Long userId;

    /**
     * 目标类型 (1:需求，2:服务者)
     */
    @TableField("target_type")
    @Schema(description = "目标类型")
    private Integer targetType;

    /**
     * 目标 ID (需求 ID 或服务者 ID)
     */
    @TableField("target_id")
    @Schema(description = "目标 ID")
    private Long targetId;

    /**
     * 标题
     */
    @TableField("title")
    @Schema(description = "标题")
    private String title;

    /**
     * 图片 URL
     */
    @TableField("image_url")
    @Schema(description = "图片 URL")
    private String imageUrl;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

}
