package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.SystemConfig;
import com.jz.miniapp.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台系统配置控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/system")
@RequiredArgsConstructor
@Tag(name = "管理后台 - 系统配置", description = "管理后台系统配置接口")
public class AdminSystemController {

    private final SystemConfigService systemConfigService;

    /**
     * 获取配置列表
     */
    @GetMapping("/configs")
    @Operation(summary = "获取配置列表")
    public Result<Page<SystemConfig>> getConfigs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类") @RequestParam(required = false) String category) {
        
        Page<SystemConfig> configs = systemConfigService.getConfigs(page, pageSize, category);
        return Result.success(configs);
    }

    /**
     * 根据 Key 获取配置
     */
    @GetMapping("/config/{key}")
    @Operation(summary = "根据 Key 获取配置")
    public Result<String> getConfigByKey(@PathVariable String key) {
        String value = systemConfigService.getConfigValueByKey(key);
        return Result.success(value);
    }

    /**
     * 批量获取配置
     */
    @GetMapping("/configs/batch")
    @Operation(summary = "批量获取配置")
    public Result<Map<String, String>> getConfigsBatch(
            @Parameter(description = "配置键列表，逗号分隔") @RequestParam List<String> keys) {
        
        Map<String, String> configs = systemConfigService.getConfigs(keys);
        return Result.success(configs);
    }

    /**
     * 更新配置
     */
    @PutMapping("/config")
    @Operation(summary = "更新配置")
    public Result<Void> updateConfig(
            @Parameter(description = "配置键") @RequestParam String key,
            @Parameter(description = "配置值") @RequestParam String value) {
        
        systemConfigService.updateConfig(key, value);
        return Result.success();
    }
}
