package com.jz.miniapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Follow;
import com.jz.miniapp.mapper.FollowMapper;
import com.jz.miniapp.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 关注关系服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long followerId, Long followeeId) {
        // 检查是否已经关注
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                    .eq(Follow::getFolloweeId, followeeId);
        
        Follow exist = getOne(queryWrapper);
        if (exist != null) {
            // 如果已取消关注，恢复关注状态
            if (exist.getStatus() == 0) {
                exist.setStatus(1);
                exist.setUpdatedAt(LocalDateTime.now());
                updateById(exist);
                log.info("恢复关注 - followerId: {}, followeeId: {}", followerId, followeeId);
            } else {
                log.warn("已经关注 - followerId: {}, followeeId: {}", followerId, followeeId);
            }
            return;
        }
        
        // 创建新的关注关系
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        follow.setStatus(1); // 1: 关注中
        follow.setCreatedAt(LocalDateTime.now());
        follow.setUpdatedAt(LocalDateTime.now());
        
        save(follow);
        log.info("关注成功 - followerId: {}, followeeId: {}", followerId, followeeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long followerId, Long followeeId) {
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                    .eq(Follow::getFolloweeId, followeeId);
        
        Follow exist = getOne(queryWrapper);
        if (exist != null && exist.getStatus() == 1) {
            // 软删除：将状态改为 0
            exist.setStatus(0);
            exist.setUpdatedAt(LocalDateTime.now());
            updateById(exist);
            log.info("取消关注成功 - followerId: {}, followeeId: {}", followerId, followeeId);
        } else {
            log.warn("未关注该用户 - followerId: {}, followeeId: {}", followerId, followeeId);
        }
    }

    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                    .eq(Follow::getFolloweeId, followeeId)
                    .eq(Follow::getStatus, 1); // 只查询关注中的记录
        
        return count(queryWrapper) > 0;
    }

    @Override
    public Page<Follow> getFollowings(int page, int pageSize, Long userId) {
        Page<Follow> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, userId)
               .eq(Follow::getStatus, 1) // 只查询关注中的记录
               .orderByDesc(Follow::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    public Page<Follow> getFollowers(int page, int pageSize, Long userId) {
        Page<Follow> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFolloweeId, userId)
               .eq(Follow::getStatus, 1) // 只查询关注中的记录
               .orderByDesc(Follow::getCreatedAt);
        
        return page(mpPage, wrapper);
    }
}
