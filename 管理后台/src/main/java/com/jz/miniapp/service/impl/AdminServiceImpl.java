package com.jz.miniapp.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Admin;
import com.jz.miniapp.exception.BusinessException;
import com.jz.miniapp.mapper.AdminMapper;
import com.jz.miniapp.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin login(String username, String password) {
        // 如果是 admin 用户，生成密码并打印到日志
        if ("admin".equals(username)) {
            String tempHash = BCrypt.hashpw(password);
            log.info("=== 临时密码哈希 (admin123): {} ===", tempHash);
        }
        
        // 查询管理员
        Admin admin = getByUsername(username);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!BCrypt.checkpw(password, admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查状态
        if (admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        return admin;
    }

    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        wrapper.eq(Admin::getStatus, 1);
        return getOne(wrapper);
    }
}
