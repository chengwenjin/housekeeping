package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Follow;

/**
 * 关注关系服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface FollowService extends IService<Follow> {

    /**
     * 关注用户
     * 
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     */
    void followUser(Long followerId, Long followeeId);

    /**
     * 取消关注
     * 
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     */
    void unfollowUser(Long followerId, Long followeeId);

    /**
     * 检查是否关注
     * 
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     * @return true-已关注，false-未关注
     */
    boolean isFollowing(Long followerId, Long followeeId);

    /**
     * 分页查询关注列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户 ID (查询该用户关注的人)
     * @return 关注分页数据
     */
    Page<Follow> getFollowings(int page, int pageSize, Long userId);

    /**
     * 分页查询粉丝列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户 ID (查询关注该用户的粉丝)
     * @return 粉丝分页数据
     */
    Page<Follow> getFollowers(int page, int pageSize, Long userId);
}
