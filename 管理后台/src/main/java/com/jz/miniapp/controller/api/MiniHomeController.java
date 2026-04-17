package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.Category;
import com.jz.miniapp.entity.Demand;
import com.jz.miniapp.mapper.CategoryMapper;
import com.jz.miniapp.mapper.DemandMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/mini/home")
@RequiredArgsConstructor
@Tag(name = "小程序 - 首页", description = "小程序首页推荐接口")
public class MiniHomeController {

    private final CategoryMapper categoryMapper;
    private final DemandMapper demandMapper;

    @GetMapping
    @Operation(summary = "获取首页数据", description = "获取首页推荐数据")
    public Result<Map<String, Object>> getHomeData() {
        log.info("小程序获取首页数据");
        
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<Demand> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Demand::getStatus, 1);
        Long nearbyDemandCount = demandMapper.selectCount(countWrapper);
        result.put("nearbyDemandCount", nearbyDemandCount != null ? nearbyDemandCount.intValue() : 0);
        
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getStatus, 1);
        categoryWrapper.orderByAsc(Category::getSortOrder);
        categoryWrapper.last("LIMIT 8");
        
        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        List<Map<String, Object>> categoryList = categories.stream()
                .map(category -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", category.getId());
                    item.put("name", category.getName());
                    item.put("icon", category.getIcon());
                    item.put("sortOrder", category.getSortOrder());
                    return item;
                })
                .collect(Collectors.toList());
        result.put("categories", categoryList);
        
        Map<String, Object> banner = new HashMap<>();
        banner.put("title", "附近有 " + nearbyDemandCount + " 条新需求");
        banner.put("subtitle", "快来抢单，收入翻倍！");
        banner.put("imageUrl", "");
        result.put("banner", banner);
        
        Page<Demand> demandPage = new Page<>(1, 10);
        LambdaQueryWrapper<Demand> demandWrapper = new LambdaQueryWrapper<>();
        demandWrapper.eq(Demand::getStatus, 1);
        demandWrapper.orderByDesc(Demand::getCreatedAt);
        
        Page<Demand> resultPage = demandMapper.selectPage(demandPage, demandWrapper);
        
        List<Map<String, Object>> recommendedDemands = resultPage.getRecords().stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
        result.put("recommendedDemands", recommendedDemands);
        
        return Result.success(result);
    }
    
    private Map<String, Object> convertToSimpleVO(Demand demand) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", demand.getId());
        vo.put("title", demand.getTitle());
        vo.put("expectedPrice", demand.getExpectedPrice());
        vo.put("priceUnit", demand.getPriceUnit());
        vo.put("distance", 0.8);
        vo.put("createdAtText", "5 分钟前");
        
        if (demand.getCreatedAt() != null) {
            vo.put("createdAt", demand.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        return vo;
    }
}
