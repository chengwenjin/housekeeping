package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.OperationLog;

/**
 * 操作日志服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     * 
     * @param userId 用户 ID
     * @param username 用户名
     * @param module 模块
     * @param action 操作
     * @param description 描述
     * @param ip IP 地址
     */
    void logOperation(Long userId, String username, String module, String action, 
                     String description, String ip);

    /**
     * 分页查询操作日志
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param operator 操作人员（可选，支持用户名/手机号模糊查询）
     * @param operationType 操作类型（可选，CREATE/UPDATE/DELETE/QUERY/LOGIN/OTHER）
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss）
     * @return 日志分页数据
     */
    Page<OperationLog> getLogs(int page, int pageSize, String operator, String operationType, String startDate, String endDate);
}
