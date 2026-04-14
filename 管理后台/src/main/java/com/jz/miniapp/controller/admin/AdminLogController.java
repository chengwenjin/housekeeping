package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.OperationLog;
import com.jz.miniapp.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台操作日志控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@Tag(name = "管理后台 - 日志", description = "管理后台操作日志接口")
public class AdminLogController {

    private final OperationLogService operationLogService;

    /**
     * 获取操作日志列表
     */
    @GetMapping
    @Operation(summary = "获取操作日志列表")
    public Result<Page<OperationLog>> getLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "操作人员") @RequestParam(required = false) String operator,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        
        log.info("获取操作日志列表 - page: {}, pageSize: {}, operator: {}, operationType: {}, startDate: {}, endDate: {}", 
                page, pageSize, operator, operationType, startDate, endDate);
        
        try {
            Page<OperationLog> logs = operationLogService.getLogs(page, pageSize, operator, operationType, startDate, endDate);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("获取操作日志列表失败", e);
            throw e;
        }
    }
}
