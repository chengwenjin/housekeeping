package com.jz.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注信息 VO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "关注信息")
public class FollowVO {

    /**
     * 关注 ID
     */
    @Schema(description = "关注 ID")
    private Long id;

    /**
     * 被关注者 ID
     */
    @Schema(description = "被关注者 ID")
    private Long followeeId;

    /**
     * 被关注者昵称
     */
    @Schema(description = "被关注者昵称")
    private String followeeNickname;

    /**
     * 被关注者头像
     */
    @Schema(description = "被关注者头像")
    private String followeeAvatar;

    /**
     * 状态 (0:已取消，1:关注中)
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
