package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.SystemConfig;

import java.util.List;

/**
 * 系统配置服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValueByKey(String configKey);

    /**
     * 更新配置
     * 
     * @param configKey 配置键
     * @param configValue 配置值
     */
    void updateConfig(String configKey, String configValue);

    /**
     * 批量获取配置
     * 
     * @param configKeys 配置键列表
     * @return 配置 Map
     */
    java.util.Map<String, String> getConfigs(List<String> configKeys);

    /**
     * 分页查询配置
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param category 分类（可选）
     * @return 配置分页数据
     */
    Page<SystemConfig> getConfigs(int page, int pageSize, String category);
}
