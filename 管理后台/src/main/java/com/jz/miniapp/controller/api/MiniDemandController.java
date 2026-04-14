package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.DemandDTO;
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

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序需求 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/mini/demands")
@Tag(name = "小程序 - 需求", description = "小程序需求接口")
public class MiniDemandController {

    @Autowired
    private DemandService demandService;

    /**
     * 获取需求列表
     */
    @GetMapping
    @Operation(summary = "获取需求列表", description = "获取需求列表 (支持筛选、分页)")
    public Result<Page<DemandVO>> getDemands(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "城市") @RequestParam(required = false) String city,
            @Parameter(description = "区县") @RequestParam(required = false) String district,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        
        log.info("获取需求列表 - page: {}, pageSize: {}, categoryId: {}, city: {}", 
                page, pageSize, categoryId, city);
        
        Page<Demand> demandPage = demandService.getDemands(page, pageSize, categoryId, city, district, status, keyword);
        
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
    @Operation(summary = "获取需求详情", description = "获取需求详细信息")
    public Result<DemandVO> getDemandById(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("获取需求详情 - id: {}", id);
        
        Demand demand = demandService.getById(id);
        if (demand == null) {
            return Result.fail("需求不存在");
        }
        
        // 增加浏览次数
        demandService.addFootprint(id, 1L); // TODO: 实际应从 token 获取 userId
        
        return Result.success(convertToVO(demand));
    }

    /**
     * 发布需求
     */
    @PostMapping
    @Operation(summary = "发布需求", description = "发布新的服务需求")
    public Result<Void> publishDemand(@Valid @RequestBody DemandDTO dto) {
        log.info("发布需求 - title: {}", dto.getTitle());
        
        Demand demand = new Demand();
        BeanUtils.copyProperties(dto, demand);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        demandService.publishDemand(demand, userId);
        
        return Result.success(null);
    }

    /**
     * 修改需求
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改需求", description = "修改已发布的需求")
    public Result<Void> updateDemand(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody DemandDTO dto) {
        
        log.info("修改需求 - id: {}", id);
        
        Demand demand = new Demand();
        BeanUtils.copyProperties(dto, demand);
        demand.setId(id);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        demandService.updateDemand(demand, userId);
        
        return Result.success(null);
    }

    /**
     * 删除需求
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除需求", description = "删除已发布的需求")
    public Result<Void> deleteDemand(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("删除需求 - id: {}", id);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        demandService.deleteDemand(id, userId);
        
        return Result.success(null);
    }

    /**
     * 接单
     */
    @PostMapping("/{id}/take")
    @Operation(summary = "接单", description = "接取服务需求")
    public Result<Void> takeDemand(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("接单 - id: {}", id);
        
        // TODO: 实际应从 token 获取 userId
        Long takerId = 2L;
        demandService.takeDemand(id, takerId);
        
        return Result.success(null);
    }

    /**
     * 记录足迹
     */
    @PostMapping("/{id}/footprint")
    @Operation(summary = "记录足迹", description = "记录用户浏览需求足迹")
    public Result<Void> addFootprint(
            @Parameter(description = "需求 ID", required = true) @PathVariable Long id) {
        
        log.info("记录足迹 - id: {}", id);
        
        // TODO: 实际应从 token 获取 userId
        Long userId = 1L;
        demandService.addFootprint(id, userId);
        
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
        vo.setCreatedAtText("5 分钟前");
        
        return vo;
    }
}
