package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.annotation.LogOperation;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/orders")
@Tag(name = "管理后台 - 订单管理", description = "管理后台订单接口")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "获取订单列表", description = "管理员查看所有订单")
    @LogOperation(module = "订单管理", action = "QUERY", description = "查询订单列表")
    public Result<Page<OrderVO>> getOrders(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "支付状态") @RequestParam(required = false) Integer paymentStatus) {
        
        log.info("管理员获取订单列表 - page: {}, pageSize: {}, orderNo: {}, keyword: {}, status: {}, paymentStatus: {}", 
                page, pageSize, orderNo, keyword, status, paymentStatus);
        
        try {
            Page<Order> orderPage = orderService.getAdminOrderPage(page, pageSize, orderNo, keyword, status, paymentStatus);
            Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
            
            List<OrderVO> voList = orderPage.getRecords().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            voPage.setRecords(voList);
            
            log.info("订单列表返回 - 总数：{}, 当前页：{}, 每页大小：{}, 记录数：{}", 
                    orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), voList.size());
            
            return Result.success(voPage);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.fail("获取订单列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "管理员查看订单详细信息")
    @LogOperation(module = "订单管理", action = "QUERY", description = "查询订单详情")
    public Result<OrderVO> getOrderById(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员获取订单详情 - id: {}", id);
        
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        
        return Result.success(convertToVO(order));
    }

    @PostMapping("/{id}/force-cancel")
    @Operation(summary = "强制取消订单", description = "管理员强制取消订单")
    @LogOperation(module = "订单管理", action = "UPDATE", description = "强制取消订单")
    public Result<Void> forceCancelOrder(
            @Parameter(description = "订单 ID", required = true) @PathVariable Long id,
            @Parameter(description = "取消原因", required = true) @RequestParam String reason) {
        
        log.info("管理员强制取消订单 - id: {}, reason: {}", id, reason);
        
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        
        order.setStatus(5);
        order.setCancelReason(reason);
        order.setCancelTime(java.time.LocalDateTime.now());
        orderService.updateById(order);
        
        return Result.success(null);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        vo.setServiceTypeText("小时工");
        vo.setStatusText("待服务");
        
        return vo;
    }
}
