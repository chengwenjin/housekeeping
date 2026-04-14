package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.Category;
import com.jz.miniapp.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台分类 Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "管理后台 - 分类管理", description = "管理后台服务分类接口")
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;

    /**
     * 获取分类列表
     */
    @GetMapping
    @Operation(summary = "获取分类列表", description = "管理员查看分类列表")
    public Result<Page<Category>> getCategories(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("管理员获取分类列表 - page: {}, pageSize: {}", page, pageSize);
        
        Page<Category> categoryPage = new Page<>(page, pageSize);
        
        // 构建查询条件
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Category::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Category::getStatus, status);
        }
        wrapper.orderByAsc(Category::getSortOrder);
        
        categoryPage = categoryMapper.selectPage(categoryPage, wrapper);
        
        return Result.success(categoryPage);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "管理员查看分类详细信息")
    public Result<Category> getCategoryById(
            @Parameter(description = "分类 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员获取分类详情 - id: {}", id);
        
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.fail("分类不存在");
        }
        
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "管理员创建新的服务分类")
    public Result<Category> createCategory(@RequestBody Category category) {
        
        log.info("管理员创建分类 - name: {}", category.getName());
        
        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(100);
        }
        
        categoryMapper.insert(category);
        
        return Result.success(category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "管理员更新分类信息")
    public Result<Category> updateCategory(
            @Parameter(description = "分类 ID", required = true) @PathVariable Long id,
            @RequestBody Category category) {
        
        log.info("管理员更新分类 - id: {}", id);
        
        Category existCategory = categoryMapper.selectById(id);
        if (existCategory == null) {
            return Result.fail("分类不存在");
        }
        
        category.setId(id);
        categoryMapper.updateById(category);
        
        return Result.success(category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "管理员删除服务分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类 ID", required = true) @PathVariable Long id) {
        
        log.info("管理员删除分类 - id: {}", id);
        
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.fail("分类不存在");
        }
        
        categoryMapper.deleteById(id);
        
        return Result.success();
    }

    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新分类状态", description = "管理员更新分类启用/禁用状态")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类 ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用", required = true) @RequestParam Integer status) {
        
        log.info("管理员更新分类状态 - id: {}, status: {}", id, status);
        
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.fail("分类不存在");
        }
        
        category.setStatus(status);
        categoryMapper.updateById(category);
        
        return Result.success();
    }
}
