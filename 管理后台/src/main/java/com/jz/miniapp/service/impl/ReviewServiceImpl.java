package com.jz.miniapp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Review;
import com.jz.miniapp.exception.BusinessException;
import com.jz.miniapp.mapper.ReviewMapper;
import com.jz.miniapp.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Review review, Long userId) {
        // 设置评价者
        review.setReviewerId(userId);
        
        // 默认状态：待审核
        review.setStatus(0);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        save(review);
        log.info("创建评价成功 - reviewId: {}, orderId: {}", review.getId(), review.getOrderId());
        
        return review.getId();
    }

    @Override
    public Page<Review> getReviews(int page, int pageSize, Long demandId, Integer type) {
        Page<Review> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        
        // 需求 ID 筛选
        if (demandId != null) {
            wrapper.eq(Review::getDemandId, demandId);
        }
        
        // 类型筛选
        if (type != null) {
            wrapper.eq(Review::getType, type);
        }
        
        // 只查询已通过的评价
        wrapper.eq(Review::getStatus, 1);
        
        wrapper.orderByDesc(Review::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long id, String content, Long userId) {
        Review review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 设置回复
        review.setReplyContent(content);
        review.setReplyTime(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        updateById(review);
        log.info("回复评价成功 - reviewId: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReview(Long id, Integer status, Long userId) {
        Review review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 更新状态
        review.setStatus(status);
        review.setUpdatedAt(LocalDateTime.now());
        
        updateById(review);
        log.info("审核评价成功 - reviewId: {}, status: {}", id, status);
    }

    @Override
    public Page<Review> getAdminReviewPage(int page, int pageSize, String keyword, BigDecimal rating, Integer type, Integer status) {
        Page<Review> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词查询（评价内容）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Review::getContent, keyword);
        }
        
        // 评分筛选
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        
        // 类型筛选
        if (type != null) {
            wrapper.eq(Review::getType, type);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Review::getCreatedAt);
        
        return page(mpPage, wrapper);
    }
}
