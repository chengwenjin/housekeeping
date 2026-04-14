package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Demand;

/**
 * 需求服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface DemandService extends IService<Demand> {

    /**
     * 分页查询需求列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param categoryId 分类 ID (可选)
     * @param city 城市 (可选)
     * @param district 区县 (可选)
     * @param status 状态 (可选)
     * @param keyword 关键词 (可选)
     * @return 需求分页数据
     */
    Page<Demand> getDemands(int page, int pageSize, Long categoryId, String city, 
                           String district, Integer status, String keyword);

    /**
     * 发布需求
     * 
     * @param demand 需求信息
     * @param userId 用户 ID
     */
    void publishDemand(Demand demand, Long userId);

    /**
     * 更新需求
     * 
     * @param demand 需求信息
     * @param userId 用户 ID
     */
    void updateDemand(Demand demand, Long userId);

    /**
     * 删除需求
     * 
     * @param id 需求 ID
     * @param userId 用户 ID
     */
    void deleteDemand(Long id, Long userId);

    /**
     * 接单
     * 
     * @param id 需求 ID
     * @param takerId 接单人 ID
     */
    void takeDemand(Long id, Long takerId);

    /**
     * 记录足迹
     * 
     * @param id 需求 ID
     * @param userId 用户 ID
     */
    void addFootprint(Long id, Long userId);
}
