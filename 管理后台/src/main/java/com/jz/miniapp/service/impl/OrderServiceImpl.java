package com.jz.miniapp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.miniapp.entity.Order;
import com.jz.miniapp.exception.BusinessException;
import com.jz.miniapp.mapper.OrderMapper;
import com.jz.miniapp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单服务实现类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Order order, Long userId) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 设置状态为待服务
        order.setStatus(1);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        save(order);
        log.info("创建订单成功 - orderId: {}, orderNo: {}", order.getId(), orderNo);
        
        return order.getId();
    }

    @Override
    public Page<Order> getPublishedOrders(int page, int pageSize, Long userId, Integer status) {
        Page<Order> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCustomerId, userId);
        
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    public Page<Order> getTakenOrders(int page, int pageSize, Long userId, Integer status) {
        Page<Order> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getServiceProviderId, userId);
        
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id, Long userId, String reason) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查是否是订单所有者
        if (!order.getCustomerId().equals(userId)) {
            throw new BusinessException("无权限取消此订单");
        }
        
        // 检查订单状态
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不能取消");
        }
        
        // 更新订单状态
        order.setStatus(5); // 已取消
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        updateById(order);
        log.info("取消订单成功 - orderId: {}, reason: {}", id, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long id, Long userId) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查是否是服务者
        if (!order.getServiceProviderId().equals(userId)) {
            throw new BusinessException("无权限完成此订单");
        }
        
        // 检查订单状态
        if (order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不能完成");
        }
        
        // 更新订单状态
        order.setStatus(3); // 服务完成
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        updateById(order);
        log.info("完成订单成功 - orderId: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long id, Integer status, Long userId) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 更新订单状态
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        updateById(order);
        log.info("更新订单状态成功 - orderId: {}, status: {}", id, status);
    }

    @Override
    public Page<Order> getAdminOrderPage(int page, int pageSize, String orderNo, String keyword, Integer status, Integer paymentStatus) {
        Page<Order> mpPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 订单号模糊查询
        if (StrUtil.isNotBlank(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        
        // 关键词查询（订单标题或地址）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Order::getTitle, keyword)
                    .or()
                    .like(Order::getProvince, keyword)
                    .or()
                    .like(Order::getCity, keyword)
                    .or()
                    .like(Order::getDistrict, keyword)
                    .or()
                    .like(Order::getAddress, keyword)
            );
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        // 支付状态筛选
        if (paymentStatus != null) {
            wrapper.eq(Order::getPaymentStatus, paymentStatus);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Order::getCreatedAt);
        
        return page(mpPage, wrapper);
    }

    /**
     * 生成订单号
     * 规则：ORD + 年月日时分秒 + 随机数 (4 位)
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = (int) ((Math.random() * 9 + 1) * 1000);
        return "ORD" + timestamp + random;
    }
}
