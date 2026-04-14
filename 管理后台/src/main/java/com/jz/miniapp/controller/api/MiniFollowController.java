package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.FollowDTO;
import com.jz.miniapp.entity.Follow;
import com.jz.miniapp.service.FollowService;
import com.jz.miniapp.vo.FollowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序关注控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/mini/follows")
@RequiredArgsConstructor
@Tag(name = "小程序 - 关注", description = "小程序关注接口")
public class MiniFollowController {

    private final FollowService followService;

    /**
     * 模拟当前登录用户 ID（实际应从 JWT token 获取）
     */
    private static final Long CURRENT_USER_ID = 1L;

    /**
     * 关注用户
     */
    @PostMapping
    @Operation(summary = "关注用户")
    public Result<Void> follow(@Validated @RequestBody FollowDTO dto) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        if (userId.equals(dto.getFolloweeId())) {
            return Result.fail("不能关注自己");
        }
        
        followService.followUser(userId, dto.getFolloweeId());
        return Result.success();
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{followeeId}")
    @Operation(summary = "取消关注")
    public Result<Void> unfollow(
            @Parameter(description = "被关注者 ID") @PathVariable Long followeeId) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        followService.unfollowUser(userId, followeeId);
        return Result.success();
    }

    /**
     * 检查是否关注
     */
    @GetMapping("/check")
    @Operation(summary = "检查是否关注")
    public Result<Boolean> checkFollow(
            @Parameter(description = "被关注者 ID") @RequestParam Long followeeId) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        boolean isFollowing = followService.isFollowing(userId, followeeId);
        return Result.success(isFollowing);
    }

    /**
     * 获取我关注的列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取我关注的列表")
    public Result<Page<FollowVO>> getMyFollowings(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        Page<Follow> mpPage = followService.getFollowings(page, pageSize, userId);
        
        // 转换为 VO（TODO: 实际应关联查询用户信息）
        List<FollowVO> voList = mpPage.getRecords().stream()
                .map(follow -> {
                    FollowVO vo = new FollowVO();
                    vo.setId(follow.getId());
                    vo.setFolloweeId(follow.getFolloweeId());
                    vo.setFolloweeNickname("用户_" + follow.getFolloweeId()); // TODO: 关联查询用户表
                    vo.setFolloweeAvatar("/avatar/default.png"); // TODO: 关联查询用户表
                    vo.setStatus(follow.getStatus());
                    vo.setCreatedAt(follow.getCreatedAt());
                    return vo;
                })
                .collect(Collectors.toList());
        
        Page<FollowVO> voPage = new Page<>(page, pageSize, mpPage.getTotal());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取我的粉丝列表
     */
    @GetMapping("/followers")
    @Operation(summary = "获取我的粉丝列表")
    public Result<Page<FollowVO>> getMyFollowers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        Page<Follow> mpPage = followService.getFollowers(page, pageSize, userId);
        
        // 转换为 VO（TODO: 实际应关联查询用户信息）
        List<FollowVO> voList = mpPage.getRecords().stream()
                .map(follow -> {
                    FollowVO vo = new FollowVO();
                    vo.setId(follow.getId());
                    vo.setFolloweeId(follow.getFolloweeId());
                    vo.setFolloweeNickname("用户_" + follow.getFollowerId()); // TODO: 关联查询用户表
                    vo.setFolloweeAvatar("/avatar/default.png"); // TODO: 关联查询用户表
                    vo.setStatus(follow.getStatus());
                    vo.setCreatedAt(follow.getCreatedAt());
                    return vo;
                })
                .collect(Collectors.toList());
        
        Page<FollowVO> voPage = new Page<>(page, pageSize, mpPage.getTotal());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }
}
