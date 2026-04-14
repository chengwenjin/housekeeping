package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.ReviewDTO;
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

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序评价 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/mini/reviews")
@Tag(name = "小程序 - 评价", description = "小程序评价接口")
public class MiniReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取评价列表
     */
    @GetMapping
    @Operation(summary = "获取评价列表", description = "查看某个需求的评价列表")
    public Result<Page<ReviewVO>> getReviews(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "需求 ID") @RequestParam(required = false) Long demandId,
            @Parameter(description = "类型") @RequestParam(required = false) Integer type) {
        
        log.info("获取评价列表 - page: {}, pageSize: {}, demandId: {}", page, pageSize, demandId);
        
        Page<Review> reviewPage = reviewService.getReviews(page, pageSize, demandId, type);
        
        // 转换为 VO
        Page<ReviewVO> voPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取评价详情", description = "查看评价详细信息")
    public Result<ReviewVO> getReviewById(
            @Parameter(description = "评价 ID", required = true) @PathVariable Long id) {
        
        log.info("获取评价详情 - id: {}", id);
        
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.fail("评价不存在");
        }
        
        return Result.success(convertToVO(review));
    }

    /**
     * 创建评价
     */
    @PostMapping
    @Operation(summary = "创建评价", description = "对订单进行评价")
    public Result<Void> createReview(@Valid @RequestBody ReviewDTO dto) {
        log.info("创建评价 - orderId: {}, rating: {}", dto.getOrderId(), dto.getRating());
        
        Review review = new Review();
        BeanUtils.copyProperties(dto, review);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        
        reviewService.createReview(review, userId);
        
        return Result.success(null);
    }

    /**
     * 回复评价
     */
    @PostMapping("/{id}/reply")
    @Operation(summary = "回复评价", description = "回复评价")
    public Result<Void> replyReview(
            @Parameter(description = "评价 ID", required = true) @PathVariable Long id,
            @Parameter(description = "回复内容", required = true) @RequestParam String content) {
        
        log.info("回复评价 - id: {}, content: {}", id, content);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 2L;
        
        reviewService.replyReview(id, content, userId);
        
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
        vo.setCreatedAtText("5 分钟前");
        
        return vo;
    }
}
