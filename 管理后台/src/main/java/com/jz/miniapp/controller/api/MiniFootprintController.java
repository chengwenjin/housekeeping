package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.FootprintDTO;
import com.jz.miniapp.entity.Footprint;
import com.jz.miniapp.service.FootprintService;
import com.jz.miniapp.vo.FootprintVO;
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
 * 小程序足迹控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/mini/footprints")
@RequiredArgsConstructor
@Tag(name = "小程序 - 足迹", description = "小程序足迹接口")
public class MiniFootprintController {

    private final FootprintService footprintService;

    /**
     * 模拟当前登录用户 ID（实际应从 JWT token 获取）
     */
    private static final Long CURRENT_USER_ID = 1L;

    /**
     * 添加足迹
     */
    @PostMapping
    @Operation(summary = "添加足迹")
    public Result<Void> addFootprint(@Validated @RequestBody FootprintDTO dto) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        // 如果标题或图片为空，使用默认值
        String title = dto.getTitle();
        String imageUrl = dto.getImageUrl();
        
        if (title == null || title.isEmpty()) {
            title = getTitleByTargetType(dto.getTargetType());
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = getDefaultImageUrl();
        }
        
        footprintService.addFootprint(userId, dto.getTargetType(), dto.getTargetId(), title, imageUrl);
        return Result.success();
    }

    /**
     * 获取我的足迹列表
     */
    @GetMapping
    @Operation(summary = "获取我的足迹列表")
    public Result<Page<FootprintVO>> getMyFootprints(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        Page<Footprint> mpPage = footprintService.getUserFootprints(page, pageSize, userId);
        
        // 转换为 VO
        List<FootprintVO> voList = mpPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<FootprintVO> voPage = new Page<>(page, pageSize, mpPage.getTotal());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 删除足迹
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除足迹")
    public Result<Void> deleteFootprint(@PathVariable Long id) {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        footprintService.deleteFootprint(id, userId);
        return Result.success();
    }

    /**
     * 清空足迹
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空我的足迹")
    public Result<Void> clearFootprints() {
        Long userId = CURRENT_USER_ID; // TODO: 从 JWT token 获取
        
        footprintService.clearUserFootprints(userId);
        return Result.success();
    }

    /**
     * 根据目标类型获取默认标题
     */
    private String getTitleByTargetType(Integer targetType) {
        switch (targetType) {
            case 1:
                return "浏览了需求";
            case 2:
                return "浏览了服务者";
            default:
                return "浏览记录";
        }
    }

    /**
     * 获取默认图片 URL
     */
    private String getDefaultImageUrl() {
        return "/images/default-footprint.png";
    }

    /**
     * 将 Footprint 转换为 FootprintVO
     */
    private FootprintVO convertToVO(Footprint footprint) {
        FootprintVO vo = new FootprintVO();
        vo.setId(footprint.getId());
        vo.setTargetType(footprint.getTargetType());
        vo.setTargetTypeText(getTargetTypeText(footprint.getTargetType()));
        vo.setTargetId(footprint.getTargetId());
        vo.setTitle(footprint.getTitle());
        vo.setImageUrl(footprint.getImageUrl());
        vo.setCreatedAt(footprint.getCreatedAt());
        return vo;
    }

    /**
     * 获取目标类型文本
     */
    private String getTargetTypeText(Integer targetType) {
        switch (targetType) {
            case 1:
                return "需求";
            case 2:
                return "服务者";
            default:
                return "未知";
        }
    }
}
