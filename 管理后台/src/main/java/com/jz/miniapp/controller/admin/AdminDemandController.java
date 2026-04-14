package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.Demand;
import com.jz.miniapp.service.DemandService;
import com.jz.miniapp.vo.DemandVO;
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
 * 管理后台需求 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/demands")
@Tag(name = "管理后台 - 需求管理", description = "管理后台需求接口")
public class AdminDemandController {

    @Autowired
    private DemandService demandService;

    /**
     * 获取需求列表
     */
    @GetMapping
    @Operation(summary = "获取需求列表", description = "管理员查看需求列表")
    public Result<Page<DemandVO>> getDemands(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "城市") @RequestParam(required = false) String city,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("管理员获取需求列表 - page: {}, pageSize: {}", page, pageSize);
        
        Page<Demand> demandPage = demandService.getDemands(page, pageSize, categoryId, city, null, status, null);
        
        // 转换为 VO
        Page<DemandVO> voPage = new Page<>(demandPage.getCurrent(), demandPage.getSize(), demandPage.getTotal());
        List<DemandVO> voList = demandPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取需求详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取需求详情", description = "管理员查看需求详细信息")
    public Result<DemandVO> getDemandById(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员获取需求详情 - id: {}", id);
        
        Demand demand = demandService.getById(id);
        if (demand == null) {
            return Result.fail("需求不存在");
        }
        
        return Result.success(convertToVO(demand));
    }

    /**
     * 下架需求
     */
    @PutMapping("/{id}/offline")
    @Operation(summary = "下架需求", description = "管理员下架违规需求")
    public Result<Void> offlineDemand(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("下架需求 - id: {}", id);
        
        Demand demand = demandService.getById(id);
        if (demand == null) {
            return Result.fail("需求不存在");
        }
        
        // 下架操作：删除或标记为已取消
        demand.setStatus(5); // 已取消
        demandService.updateById(demand);
        
        return Result.success(null);
    }

    /**
     * 删除需求
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除需求", description = "管理员删除需求")
    public Result<Void> deleteDemand(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员删除需求 - id: {}", id);
        
        demandService.removeById(id);
        
        return Result.success(null);
    }

    /**
     * 转换为 VO
     */
    private DemandVO convertToVO(Demand demand) {
        DemandVO vo = new DemandVO();
        BeanUtils.copyProperties(demand, vo);
        
        // 处理图片 URLs
        if (demand.getImages() != null && !demand.getImages().isEmpty()) {
            vo.setImages(Arrays.asList(demand.getImages().split(",")));
        }
        
        // TODO: 设置分类名称、服务类型文本、状态文本等
        vo.setCategoryName("保洁");
        vo.setServiceTypeText("小时工");
        vo.setStatusText("招募中");
        
        return vo;
    }
}
