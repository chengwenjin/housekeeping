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
 * 关注关系实体类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@TableName("follows")
@Schema(description = "关注关系信息")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "关注 ID")
    private Long id;

    /**
     * 关注者 ID
     */
    @TableField("follower_id")
    @Schema(description = "关注者 ID")
    private Long followerId;

    /**
     * 被关注者 ID
     */
    @TableField("followee_id")
    @Schema(description = "被关注者 ID")
    private Long followeeId;

    /**
     * 状态 (0:已取消，1:关注中)
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
