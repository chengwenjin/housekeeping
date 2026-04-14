package com.jz.miniapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 关注/取消关注请求 DTO
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Data
@Schema(description = "关注请求")
public class FollowDTO {

    /**
     * 被关注者 ID
     */
    @NotNull(message = "被关注者 ID 不能为空")
    @Schema(description = "被关注者 ID", required = true, example = "2")
    private Long followeeId;
}
