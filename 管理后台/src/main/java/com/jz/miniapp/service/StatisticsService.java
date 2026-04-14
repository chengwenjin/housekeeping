package com.jz.miniapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 统计服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface StatisticsService {

    /**
     * 获取首页统计数据
     * 
     * @return 统计数据
     */
    Map<String, Object> getDashboardStats();

    /**
     * 获取用户统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户统计数据
     */
    Map<String, Object> getUserStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取订单统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单统计数据
     */
    Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取交易统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易统计数据
     */
    Map<String, BigDecimal> getTransactionStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取需求统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 需求统计数据
     */
    Map<String, Object> getDemandStatistics(LocalDate startDate, LocalDate endDate);
}
