package com.jz.miniapp.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/mini/categories")
@RequiredArgsConstructor
@Tag(name = "小程序 - 分类", description = "小程序分类接口")
public class MiniCategoryController {

    private final CategoryMapper categoryMapper;
    private final DemandMapper demandMapper;

    @GetMapping
    @Operation(summary = "获取分类列表", description = "获取所有启用的服务分类")
    public Result<List<Map<String, Object>>> getCategories() {
        log.info("小程序获取分类列表");
        
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getStatus, 1);
        categoryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        
        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        
        Map<Long, Integer> demandCountMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            LambdaQueryWrapper<Demand> demandWrapper = new LambdaQueryWrapper<>();
            demandWrapper.in(Demand::getCategoryId, categoryIds);
            demandWrapper.eq(Demand::getStatus, 1);
            
            List<Demand> demands = demandMapper.selectList(demandWrapper);
            
            for (Demand demand : demands) {
                Long catId = demand.getCategoryId();
                demandCountMap.put(catId, demandCountMap.getOrDefault(catId, 0) + 1);
            }
        }
        
        List<Map<String, Object>> result = categories.stream()
                .map(category -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", category.getId());
                    item.put("name", category.getName());
                    item.put("icon", category.getIcon());
                    item.put("sortOrder", category.getSortOrder());
                    item.put("isHot", category.getSortOrder() != null && category.getSortOrder() <= 50);
                    item.put("demandCount", demandCountMap.getOrDefault(category.getId(), 0));
                    return item;
                })
                .collect(Collectors.toList());
        
        return Result.success(result);
    }
}
