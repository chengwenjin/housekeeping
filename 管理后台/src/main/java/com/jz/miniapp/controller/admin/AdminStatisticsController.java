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

@Slf4j
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "管理后台 - 统计", description = "管理后台统计接口")
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "获取首页统计数据")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }

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

    @GetMapping("/detail")
    @Operation(summary = "获取详细统计数据")
    public Result<Map<String, Object>> getDetailStatistics(
            @Parameter(description = "开始日期") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        log.info("获取详细统计数据 - startDate: {}, endDate: {}", startDate, endDate);
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        
        result.put("userStats", statisticsService.getUserStatistics(startDate, endDate));
        result.put("orderStats", statisticsService.getOrderStatistics(startDate, endDate));
        result.put("transactionStats", statisticsService.getTransactionStatistics(startDate, endDate));
        result.put("demandStats", statisticsService.getDemandStatistics(startDate, endDate));
        
        return Result.success(result);
    }
}
