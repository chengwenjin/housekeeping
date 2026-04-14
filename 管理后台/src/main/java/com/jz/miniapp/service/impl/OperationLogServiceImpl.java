package com.jz.miniapp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.OperationLog;
import com.jz.miniapp.mapper.OperationLogMapper;
import com.jz.miniapp.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Service
@Slf4j
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void logOperation(Long userId, String username, String module, String action, 
                            String description, String ip) {
        OperationLog log = new OperationLog();
        log.setAdminId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setAction(action);
        // log.setDescription(description); // 数据库表中无 description 字段，暂时注释
        log.setIp(ip);
        log.setCreatedAt(LocalDateTime.now());
        
        save(log);
    }

    @Override
    public Page<OperationLog> getLogs(int page, int pageSize, String operator, String operationType, String startDate, String endDate) {
        log.info("查询操作日志 - page: {}, pageSize: {}, operator: {}, operationType: {}, startDate: {}, endDate: {}", 
                page, pageSize, operator, operationType, startDate, endDate);
        
        Page<OperationLog> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 操作人员模糊查询（用户名或手机号）- 处理空字符串
        if (StrUtil.isNotBlank(operator)) {
            log.debug("添加操作人员筛选：{}", operator);
            wrapper.and(w -> w
                    .like(OperationLog::getUsername, operator)
            );
        }
        
        // 操作类型精确匹配 - 处理空字符串
        if (StrUtil.isNotBlank(operationType)) {
            log.debug("添加操作类型筛选：{}", operationType);
            wrapper.eq(OperationLog::getAction, operationType);
        }
        
        // 日期范围查询 - 处理空字符串
        if (StrUtil.isNotBlank(startDate)) {
            LocalDateTime startDateTime = parseDate(startDate, true);
            if (startDateTime != null) {
                log.debug("添加开始日期筛选：{}", startDateTime);
                wrapper.ge(OperationLog::getCreatedAt, startDateTime);
            }
        }
        
        if (StrUtil.isNotBlank(endDate)) {
            LocalDateTime endDateTime = parseDate(endDate, false);
            if (endDateTime != null) {
                log.debug("添加结束日期筛选：{}", endDateTime);
                wrapper.le(OperationLog::getCreatedAt, endDateTime);
            }
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        
        Page<OperationLog> result = page(mpPage, wrapper);
        log.info("查询操作日志完成 - 总数：{}", result.getTotal());
        
        return result;
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @param isStart 是否为开始日期（true 则时间为 00:00:00，false 则为 23:59:59）
     * @return LocalDateTime 对象
     */
    private LocalDateTime parseDate(String dateStr, boolean isStart) {
        try {
            // 尝试多种日期格式
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    if (pattern.contains("HH:mm:ss")) {
                        return LocalDateTime.parse(dateStr, formatter);
                    } else {
                        LocalDate localDate = LocalDate.parse(dateStr, formatter);
                        return isStart ? 
                            localDate.atStartOfDay() : 
                            localDate.atTime(LocalTime.MAX);
                    }
                } catch (Exception e) {
                    // 尝试下一个格式
                }
            }
        } catch (Exception e) {
            log.warn("日期解析失败：{}", dateStr, e);
        }
        return null;
    }
}
