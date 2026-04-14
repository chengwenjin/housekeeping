package com.jz.miniapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jz.miniapp.entity.*;
import com.jz.miniapp.mapper.*;
import com.jz.miniapp.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final DemandMapper demandMapper;
    private final OrderMapper orderMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);
        
        // 需求统计
        long totalDemands = demandMapper.selectCount(null);
        stats.put("totalDemands", totalDemands);
        
        // 订单统计
        long totalOrders = orderMapper.selectCount(null);
        stats.put("totalOrders", totalOrders);
        
        // 交易金额统计 - 使用 JdbcTemplate 执行原生 SQL
        String amountSql = "SELECT IFNULL(SUM(total_amount), 0) FROM orders WHERE deleted_at IS NULL";
        BigDecimal totalAmount = jdbcTemplate.queryForObject(amountSql, BigDecimal.class);
        stats.put("totalAmount", totalAmount);
        
        // 评价统计
        long totalReviews = reviewMapper.selectCount(null);
        stats.put("totalReviews", totalReviews);
        
        log.info("获取首页统计数据成功");
        return stats;
    }

    @Override
    public Map<String, Object> getUserStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(User::getCreatedAt, startDateTime, endDateTime);
        
        long newUserCount = userMapper.selectCount(wrapper);
        stats.put("newUsers", newUserCount);
        
        // 活跃用户（发布过需求或订单的用户）
        // TODO: 需要关联查询
        
        stats.put("period", startDate + " 至 " + endDate);
        
        return stats;
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Order::getCreatedAt, startDateTime, endDateTime);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 订单总数
        stats.put("totalOrders", orders.size());
        
        // 按状态统计
        Map<Integer, Long> statusCount = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        stats.put("statusStats", statusCount);
        
        // 交易金额
        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);
        
        stats.put("period", startDate + " 至 " + endDate);
        
        return stats;
    }

    @Override
    public Map<String, BigDecimal> getTransactionStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> stats = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Order::getCreatedAt, startDateTime, endDateTime);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 总交易额
        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", totalAmount);
        
        // 平均订单金额
        BigDecimal avgAmount = orders.isEmpty() ? BigDecimal.ZERO : 
                totalAmount.divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
        stats.put("avgAmount", avgAmount);
        
        return stats;
    }

    @Override
    public Map<String, Object> getDemandStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Demand::getCreatedAt, startDateTime, endDateTime);
        
        List<Demand> demands = demandMapper.selectList(wrapper);
        
        // 需求总数
        stats.put("totalDemands", demands.size());
        
        // 按状态统计
        Map<Integer, Long> statusCount = demands.stream()
                .collect(Collectors.groupingBy(Demand::getStatus, Collectors.counting()));
        stats.put("statusStats", statusCount);
        
        // 按分类统计
        Map<Long, Long> categoryCount = demands.stream()
                .collect(Collectors.groupingBy(Demand::getCategoryId, Collectors.counting()));
        stats.put("categoryStats", categoryCount);
        
        stats.put("period", startDate + " 至 " + endDate);
        
        return stats;
    }
}
