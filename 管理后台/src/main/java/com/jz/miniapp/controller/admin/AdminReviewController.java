package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.Review;
import com.jz.miniapp.service.ReviewService;
import com.jz.miniapp.vo.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台评价 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/reviews")
@Tag(name = "管理后台 - 评价管理", description = "管理后台评价接口")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取评价列表
     */
    @GetMapping
    @Operation(summary = "获取评价列表", description = "管理员查看所有评价")
    public Result<Page<ReviewVO>> getReviews(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("管理员获取评价列表 - page: {}, pageSize: {}", page, pageSize);
        
        // TODO: 实现管理员查询所有评价的逻辑
        
        return Result.success(null);
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取评价详情", description = "管理员查看评价详细信息")
    public Result<ReviewVO> getReviewById(
            @Parameter(description = "评价 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员获取评价详情 - id: {}", id);
        
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.fail("评价不存在");
        }
        
        return Result.success(convertToVO(review));
    }

    /**
     * 审核评价
     */
    @PostMapping("/{id}/audit")
    @Operation(summary = "审核评价", description = "管理员审核评价")
    public Result<Void> auditReview(
            @Parameter(description = "评价 ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态", required = true) @RequestParam Integer status) {
        
        log.info("管理员审核评价 - id: {}, status: {}", id, status);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        
        reviewService.auditReview(id, status, userId);
        
        return Result.success(null);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评价", description = "管理员删除评价")
    public Result<Void> deleteReview(
            @Parameter(description = "评价 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员删除评价 - id: {}", id);
        
        reviewService.removeById(id);
        
        return Result.success(null);
    }

    /**
     * 转换为 VO
     */
    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 处理图片 URLs
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            vo.setImages(Arrays.asList(review.getImages().split(",")));
        }
        
        // TODO: 设置类型文本、状态文本等
        vo.setTypeText("客户评价服务者");
        vo.setStatusText("已通过");
        
        return vo;
    }
}
