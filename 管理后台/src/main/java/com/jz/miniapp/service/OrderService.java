package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.Order;

/**
 * 订单服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     * 
     * @param order 订单信息
     * @param userId 用户 ID
     * @return 订单 ID
     */
    Long createOrder(Order order, Long userId);

    /**
     * 分页查询我发布的订单
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户 ID
     * @param status 状态 (可选)
     * @return 订单分页数据
     */
    Page<Order> getPublishedOrders(int page, int pageSize, Long userId, Integer status);

    /**
     * 分页查询我接的订单
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 用户 ID
     * @param status 状态 (可选)
     * @return 订单分页数据
     */
    Page<Order> getTakenOrders(int page, int pageSize, Long userId, Integer status);

    /**
     * 取消订单
     * 
     * @param id 订单 ID
     * @param userId 用户 ID
     * @param reason 取消原因
     */
    void cancelOrder(Long id, Long userId, String reason);

    /**
     * 确认完成订单
     * 
     * @param id 订单 ID
     * @param userId 用户 ID
     */
    void completeOrder(Long id, Long userId);

    /**
     * 更新订单状态
     * 
     * @param id 订单 ID
     * @param status 新状态
     * @param userId 用户 ID
     */
    void updateOrderStatus(Long id, Integer status, Long userId);

    /**
     * 管理后台分页查询订单
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @param orderNo 订单号 (可选)
     * @param keyword 关键词 (可选，订单标题/服务地址)
     * @param status 状态 (可选)
     * @param paymentStatus 支付状态 (可选)
     * @return 订单分页数据
     */
    Page<Order> getAdminOrderPage(int page, int pageSize, String orderNo, String keyword, Integer status, Integer paymentStatus);
}
