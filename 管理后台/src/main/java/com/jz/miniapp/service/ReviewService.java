package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Review;

import java.math.BigDecimal;

/**
 * 评价服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface ReviewService extends IService<Review> {

    /**
     * 创建评价
     * 
     * @param review 评价信息
     * @param userId 用户 ID
     * @return 评价 ID
     */
    Long createReview(Review review, Long userId);

    /**
     * 分页查询评价列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param demandId 需求 ID (可选)
     * @param type 类型 (可选)
     * @return 评价分页数据
     */
    Page<Review> getReviews(int page, int pageSize, Long demandId, Integer type);

    /**
     * 回复评价
     * 
     * @param id 评价 ID
     * @param content 回复内容
     * @param userId 用户 ID
     */
    void replyReview(Long id, String content, Long userId);

    /**
     * 审核评价
     * 
     * @param id 评价 ID
     * @param status 状态
     * @param userId 用户 ID
     */
    void auditReview(Long id, Integer status, Long userId);

    /**
     * 管理员分页查询评价列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param keyword 关键词（搜索评价内容）
     * @param rating 评分（可选）
     * @param type 类型（可选）
     * @param status 状态（可选）
     * @return 评价分页数据
     */
    Page<Review> getAdminReviewPage(int page, int pageSize, String keyword, BigDecimal rating, Integer type, Integer status);
}
