package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Footprint;

/**
 * 用户足迹服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface FootprintService extends IService<Footprint> {

    /**
     * 添加足迹
     * 
     * @param userId 用户 ID
     * @param targetType 目标类型 (1:需求，2:服务者)
     * @param targetId 目标 ID
     * @param title 标题
     * @param imageUrl 图片 URL
     */
    void addFootprint(Long userId, Integer targetType, Long targetId, String title, String imageUrl);

    /**
     * 分页查询用户足迹
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户 ID
     * @return 足迹分页数据
     */
    Page<Footprint> getUserFootprints(int page, int pageSize, Long userId);

    /**
     * 删除足迹
     * 
     * @param footprintId 足迹 ID
     * @param userId 用户 ID
     */
    void deleteFootprint(Long footprintId, Long userId);

    /**
     * 清空用户所有足迹
     * 
     * @param userId 用户 ID
     */
    void clearUserFootprints(Long userId);
}
