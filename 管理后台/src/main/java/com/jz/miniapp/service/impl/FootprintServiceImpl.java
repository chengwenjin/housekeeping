package com.jz.miniapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Footprint;
import com.jz.miniapp.mapper.FootprintMapper;
import com.jz.miniapp.service.FootprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户足迹服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Service
@Slf4j
public class FootprintServiceImpl extends ServiceImpl<FootprintMapper, Footprint> implements FootprintService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFootprint(Long userId, Integer targetType, Long targetId, String title, String imageUrl) {
        // 检查是否已经存在相同的足迹（同一天内不重复记录）
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        LambdaQueryWrapper<Footprint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Footprint::getUserId, userId)
                    .eq(Footprint::getTargetType, targetType)
                    .eq(Footprint::getTargetId, targetId)
                    .ge(Footprint::getCreatedAt, todayStart);
        
        long count = count(queryWrapper);
        if (count > 0) {
            log.debug("今天已浏览过，不重复记录 - userId: {}, targetType: {}, targetId: {}", userId, targetType, targetId);
            return;
        }
        
        // 创建足迹
        Footprint footprint = new Footprint();
        footprint.setUserId(userId);
        footprint.setTargetType(targetType);
        footprint.setTargetId(targetId);
        footprint.setTitle(title);
        footprint.setImageUrl(imageUrl);
        footprint.setCreatedAt(LocalDateTime.now());
        
        save(footprint);
        log.info("添加足迹 - userId: {}, targetType: {}, targetId: {}, title: {}", userId, targetType, targetId, title);
    }

    @Override
    public Page<Footprint> getUserFootprints(int page, int pageSize, Long userId) {
        Page<Footprint> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Footprint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Footprint::getUserId, userId)
               .orderByDesc(Footprint::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFootprint(Long footprintId, Long userId) {
        LambdaQueryWrapper<Footprint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Footprint::getId, footprintId)
               .eq(Footprint::getUserId, userId);
        
        remove(wrapper);
        log.info("删除足迹 - footprintId: {}, userId: {}", footprintId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserFootprints(Long userId) {
        LambdaQueryWrapper<Footprint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Footprint::getUserId, userId);
        
        remove(wrapper);
        log.info("清空用户足迹 - userId: {}", userId);
    }
}
