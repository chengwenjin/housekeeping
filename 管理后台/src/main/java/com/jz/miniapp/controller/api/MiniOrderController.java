package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.Order;
import com.jz.miniapp.service.OrderService;
import com.jz.miniapp.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序订单 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/mini/orders")
@Tag(name = "小程序 - 订单", description = "小程序订单接口")
public class MiniOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取我发布的订单列表
     */
    @GetMapping("/published")
    @Operation(summary = "获取我发布的订单", description = "查看客户发布的订单列表")
    public Result<Page<OrderVO>> getPublishedOrders(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("获取我发布的订单 - page: {}, pageSize: {}", page, pageSize);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        
        Page<Order> orderPage = orderService.getPublishedOrders(page, pageSize, userId, status);
        
        // 转换为 VO
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取我接的订单列表
     */
    @GetMapping("/taken")
    @Operation(summary = "获取我接的订单", description = "查看服务者接的订单列表")
    public Result<Page<OrderVO>> getTakenOrders(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("获取我接的订单 - page: {}, pageSize: {}", page, pageSize);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 2L;
        
        Page<Order> orderPage = orderService.getTakenOrders(page, pageSize, userId, status);
        
        // 转换为 VO
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "查看订单详细信息")
    public Result<OrderVO> getOrderById(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id) {
        
        log.info("获取订单详情 - id: {}", id);
        
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        
        return Result.success(convertToVO(order));
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "客户取消订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id,
            @Parameter(description = "取消原因", required = true) @RequestParam String reason) {
        
        log.info("取消订单 - id: {}, reason: {}", id, reason);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        
        orderService.cancelOrder(id, userId, reason);
        
        return Result.success(null);
    }

    /**
     * 确认完成订单
     */
    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认完成订单", description = "服务者确认完成订单")
    public Result<Void> confirmOrder(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id) {
        
        log.info("确认完成订单 - id: {}", id);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 2L;
        
        orderService.completeOrder(id, userId);
        
        return Result.success(null);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单服务状态")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id,
            @Parameter(description = "新状态", required = true) @RequestParam Integer status) {
        
        log.info("更新订单状态 - id: {}, status: {}", id, status);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 2L;
        
        orderService.updateOrderStatus(id, status, userId);
        
        return Result.success(null);
    }

    /**
     * 转换为 VO
     */
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // TODO: 设置服务类型文本、状态文本等
        vo.setServiceTypeText("小时工");
        vo.setStatusText("待服务");
        vo.setCreatedAtText("5 分钟前");
        
        return vo;
    }
}
