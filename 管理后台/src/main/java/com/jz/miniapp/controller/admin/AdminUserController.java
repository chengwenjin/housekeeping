package com.jz.miniapp.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.miniapp.annotation.LogOperation;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.entity.User;
import com.jz.miniapp.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 
 * @author jiazheng
 * @since 2026-03-27
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理接口")
public class AdminUserController {

    private final UserMapper userMapper;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取用户列表")
    @LogOperation(module = "用户管理", action = "QUERY", description = "查询用户列表")
    public Result<Page<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Long pageSize,
            @Parameter(description = "关键词（昵称/手机号）") @RequestParam(required = false) String keyword,
            @Parameter(description = "认证状态") @RequestParam(required = false) Integer certificationStatus,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (certificationStatus != null) {
            wrapper.eq(User::getCertificationStatus, certificationStatus);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        Page<User> userPage = new Page<>(page, pageSize);
        userPage = userMapper.selectPage(userPage, wrapper);
        
        return Result.success(userPage);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @LogOperation(module = "用户管理", action = "QUERY", description = "查询用户详情")
    public Result<User> getUserDetail(@Parameter(description = "用户 ID") @PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态")
    @LogOperation(module = "用户管理", action = "UPDATE", description = "更新用户状态")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestBody User statusRequest) {
        
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        user.setStatus(statusRequest.getStatus());
        userMapper.updateById(user);
        
        log.info("更新用户状态成功 - userId: {}, 新状态：{}", id, statusRequest.getStatus());
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @LogOperation(module = "用户管理", action = "DELETE", description = "删除用户")
    public Result<Void> deleteUser(@Parameter(description = "用户 ID") @PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        userMapper.deleteById(id);
        log.info("删除用户成功 - userId: {}", id);
        return Result.success();
    }
}
