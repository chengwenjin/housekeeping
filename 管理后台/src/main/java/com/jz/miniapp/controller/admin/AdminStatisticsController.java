package com.jz.miniapp.controller.admin;

import com.jz.miniapp.common.Result;
import com.jz.miniapp.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 管理后台统计控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "管理后台 - 统计", description = "管理后台统计接口")
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/dashboard")
    @Operation(summary = "获取首页统计数据")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/users")
    @Operation(summary = "获取用户统计数据")
    public Result<Map<String, Object>> getUserStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = statisticsService.getUserStatistics(startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/orders")
    @Operation(summary = "获取订单统计数据")
    public Result<Map<String, Object>> getOrderStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = statisticsService.getOrderStatistics(startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取交易统计数据
     */
    @GetMapping("/transactions")
    @Operation(summary = "获取交易统计数据")
    public Result<Map<String, Object>> getTransactionStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = new java.util.HashMap<>(statisticsService.getTransactionStatistics(startDate, endDate));
        return Result.success(stats);
    }

    /**
     * 获取需求统计数据
     */
    @GetMapping("/demands")
    @Operation(summary = "获取需求统计数据")
    public Result<Map<String, Object>> getDemandStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = statisticsService.getDemandStatistics(startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取详细统计数据
     */
    @GetMapping("/detail")
    @Operation(summary = "获取详细统计数据")
    public Result<Map<String, Object>> getDetailStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        log.info("获取详细统计数据 - startDate: {}, endDate: {}", startDate, endDate);
        
        // 如果没有传日期，默认查询最近 30 天
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 综合统计数据
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 用户统计
        result.put("userStats", statisticsService.getUserStatistics(startDate, endDate));
        
        // 订单统计
        result.put("orderStats", statisticsService.getOrderStatistics(startDate, endDate));
        
        // 交易统计
        result.put("transactionStats", statisticsService.getTransactionStatistics(startDate, endDate));
        
        // 需求统计
        result.put("demandStats", statisticsService.getDemandStatistics(startDate, endDate));
        
        return Result.success(result);
    }
}
