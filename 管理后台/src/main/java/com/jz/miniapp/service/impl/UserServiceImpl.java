package com.jz.miniapp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.User;
import com.jz.miniapp.exception.BusinessException;
import com.jz.miniapp.mapper.UserMapper;
import com.jz.miniapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User wxLogin(String code, String encryptedData, String iv, UserInfoDTO userInfo) {
        // TODO: 这里需要调用微信 API 获取 openid
        // 暂时使用 code 作为 openid (实际开发中需要调用微信接口)
        String openid = "wx_" + code;
        
        // 查询用户是否存在
        User user = getByOpenid(openid);
        
        if (user == null) {
            // 创建新用户
            user = new User();
            user.setOpenid(openid);
            user.setNickname(userInfo.getNickName());
            user.setAvatarUrl(userInfo.getAvatarUrl());
            user.setGender(userInfo.getGender());
            user.setScore(java.math.BigDecimal.valueOf(5.00));
            user.setTotalOrders(0);
            user.setFollowerCount(0);
            user.setFollowingCount(0);
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            save(user);
            log.info("新用户注册成功，userId: {}", user.getId());
        } else {
            // 更新用户信息
            if (StrUtil.isNotBlank(userInfo.getNickName())) {
                user.setNickname(userInfo.getNickName());
            }
            if (StrUtil.isNotBlank(userInfo.getAvatarUrl())) {
                user.setAvatarUrl(userInfo.getAvatarUrl());
            }
            if (userInfo.getGender() != null) {
                user.setGender(userInfo.getGender());
            }
            user.setUpdatedAt(LocalDateTime.now());
            updateById(user);
        }
        
        return user;
    }

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        wrapper.eq(User::getStatus, 1);
        return getOne(wrapper);
    }
}
