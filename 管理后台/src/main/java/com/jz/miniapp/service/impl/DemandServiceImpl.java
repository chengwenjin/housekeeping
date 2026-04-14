package com.jz.miniapp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Demand;
import com.jz.miniapp.exception.BusinessException;
import com.jz.miniapp.mapper.DemandMapper;
import com.jz.miniapp.service.DemandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 需求服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {

    @Override
    public Page<Demand> getDemands(int page, int pageSize, Long categoryId, String city,
                                   String district, Integer status, String keyword) {
        Page<Demand> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Demand::getCategoryId, categoryId);
        }
        
        // 城市筛选
        if (StrUtil.isNotBlank(city)) {
            wrapper.eq(Demand::getCity, city);
        }
        
        // 区县筛选
        if (StrUtil.isNotBlank(district)) {
            wrapper.eq(Demand::getDistrict, district);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Demand::getStatus, status);
        } else {
            // 默认查询招募中的需求
            wrapper.eq(Demand::getStatus, 1);
        }
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Demand::getTitle, keyword)
                    .or().like(Demand::getDescription, keyword));
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Demand::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishDemand(Demand demand, Long userId) {
        // 设置发布人
        demand.setPublisherId(userId);
        demand.setStatus(1); // 招募中
        demand.setViewCount(0);
        demand.setFootprintCount(0);
        demand.setCreatedAt(LocalDateTime.now());
        demand.setUpdatedAt(LocalDateTime.now());
        
        save(demand);
        log.info("发布需求成功 - demandId: {}, userId: {}", demand.getId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDemand(Demand demand, Long userId) {
        Demand existDemand = getById(demand.getId());
        if (existDemand == null) {
            throw new BusinessException("需求不存在");
        }
        
        // 验证是否是发布者
        if (!existDemand.getPublisherId().equals(userId)) {
            throw new BusinessException("无权限修改此需求");
        }
        
        // 更新允许的字段
        existDemand.setTitle(demand.getTitle());
        existDemand.setDescription(demand.getDescription());
        existDemand.setCategoryId(demand.getCategoryId());
        existDemand.setServiceType(demand.getServiceType());
        existDemand.setExpectedPrice(demand.getExpectedPrice());
        existDemand.setPriceUnit(demand.getPriceUnit());
        existDemand.setMinDuration(demand.getMinDuration());
        existDemand.setMaxDuration(demand.getMaxDuration());
        existDemand.setProvince(demand.getProvince());
        existDemand.setCity(demand.getCity());
        existDemand.setDistrict(demand.getDistrict());
        existDemand.setAddress(demand.getAddress());
        existDemand.setLatitude(demand.getLatitude());
        existDemand.setLongitude(demand.getLongitude());
        existDemand.setServiceTime(demand.getServiceTime());
        existDemand.setContactName(demand.getContactName());
        existDemand.setContactPhone(demand.getContactPhone());
        existDemand.setImages(demand.getImages());
        existDemand.setUpdatedAt(LocalDateTime.now());
        
        updateById(existDemand);
        log.info("更新需求成功 - demandId: {}", demand.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemand(Long id, Long userId) {
        Demand demand = getById(id);
        if (demand == null) {
            throw new BusinessException("需求不存在");
        }
        
        // 验证是否是发布者
        if (!demand.getPublisherId().equals(userId)) {
            throw new BusinessException("无权限删除此需求");
        }
        
        removeById(id);
        log.info("删除需求成功 - demandId: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeDemand(Long id, Long takerId) {
        Demand demand = getById(id);
        if (demand == null) {
            throw new BusinessException("需求不存在");
        }
        
        // 检查状态
        if (demand.getStatus() != 1) {
            throw new BusinessException("需求不是招募中状态");
        }
        
        // 不能接自己的单
        if (demand.getPublisherId().equals(takerId)) {
            throw new BusinessException("不能接自己的需求");
        }
        
        // 更新状态为已接单
        demand.setTakerId(takerId);
        demand.setStatus(2); // 已接单
        demand.setUpdatedAt(LocalDateTime.now());
        
        updateById(demand);
        log.info("接单成功 - demandId: {}, takerId: {}", id, takerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFootprint(Long id, Long userId) {
        // TODO: 先检查是否已有足迹，避免重复
        Demand demand = getById(id);
        if (demand == null) {
            throw new BusinessException("需求不存在");
        }
        
        // 增加足迹数
        Integer footprintCount = demand.getFootprintCount();
        demand.setFootprintCount(footprintCount != null ? footprintCount + 1 : 1);
        demand.setUpdatedAt(LocalDateTime.now());
        
        updateById(demand);
        log.info("记录足迹成功 - demandId: {}, userId: {}", id, userId);
    }
}
