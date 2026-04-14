package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Admin;

/**
 * 管理员服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息
     */
    Admin login(String username, String password);

    /**
     * 根据用户名获取管理员
     * 
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getByUsername(String username);
}
