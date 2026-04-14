package com.jz.miniapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.SystemConfig;
import com.jz.miniapp.mapper.SystemConfigMapper;
import com.jz.miniapp.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Service
@Slf4j
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public String getConfigValueByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        
        SystemConfig config = getOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public void updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        
        SystemConfig existConfig = getOne(wrapper);
        if (existConfig != null) {
            existConfig.setConfigValue(configValue);
            existConfig.setUpdatedAt(LocalDateTime.now());
            updateById(existConfig);
            log.info("更新配置成功 - key: {}, value: {}", configKey, configValue);
        } else {
            log.warn("配置不存在 - key: {}", configKey);
        }
    }

    @Override
    public Map<String, String> getConfigs(List<String> configKeys) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SystemConfig::getConfigKey, configKeys);
        
        List<SystemConfig> configs = list(wrapper);
        return configs.stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        SystemConfig::getConfigValue
                ));
    }

    @Override
    public Page<SystemConfig> getConfigs(int page, int pageSize, String category) {
        Page<SystemConfig> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(SystemConfig::getCategory, category);
        }
        wrapper.orderByAsc(SystemConfig::getSortOrder);
        
        return page(mpPage, wrapper);
    }
}
