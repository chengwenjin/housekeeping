package com.jz.miniapp.controller.admin;

import com.jz.miniapp.common.AnonymousAccess;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.AdminLoginDTO;
import com.jz.miniapp.entity.Admin;
import com.jz.miniapp.service.AdminService;
import com.jz.miniapp.vo.AdminLoginVO;
import com.jz.miniapp.vo.AdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理后台认证 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/auth")
@Tag(name = "管理后台 - 认证", description = "管理后台认证接口")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @AnonymousAccess
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员账号密码登录")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        log.info("管理员登录请求 - username: {}", dto.getUsername());

        // 管理员登录
        Admin admin = adminService.login(dto.getUsername(), dto.getPassword());

        // 构建响应
        AdminLoginVO loginVO = new AdminLoginVO();
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        loginVO.setAdmin(adminVO);
        
        // TODO: 生成 token (暂时使用 mock 数据)
        loginVO.setToken("mock_admin_token_" + admin.getId());
        loginVO.setRefreshToken("mock_admin_refresh_token_" + admin.getId());
        loginVO.setExpiresIn(28800L); // 8 小时

        log.info("管理员登录成功 - adminId: {}", admin.getId());
        return Result.success(loginVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "管理员退出登录")
    public Result<Void> logout() {
        log.info("管理员退出登录");
        // TODO: 实现退出登录逻辑 (token 黑名单等)
        return Result.success(null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "管理员修改登录密码")
    public Result<Void> updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        log.info("修改密码请求");
        // TODO: 实现修改密码逻辑
        return Result.success(null);
    }

    /**
     * 刷新 Token
     */
    @AnonymousAccess
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 refresh token 刷新 access token")
    public Result<AdminLoginVO> refreshToken(@RequestParam String refreshToken) {
        log.info("刷新 Token - refreshToken: {}", refreshToken);
        // TODO: 实现刷新 token 逻辑
        
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setToken("mock_new_admin_token");
        loginVO.setRefreshToken("mock_new_admin_refresh_token");
        loginVO.setExpiresIn(28800L);
        
        return Result.success(loginVO);
    }
}
