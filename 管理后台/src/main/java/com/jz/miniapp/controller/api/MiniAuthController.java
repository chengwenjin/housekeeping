package com.jz.miniapp.controller.api;

import cn.hutool.core.util.StrUtil;
import com.jz.miniapp.common.AnonymousAccess;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.WxLoginDTO;
import com.jz.miniapp.entity.User;
import com.jz.miniapp.service.UserService;
import com.jz.miniapp.util.JwtUtil;
import com.jz.miniapp.vo.LoginVO;
import com.jz.miniapp.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 小程序认证 Controller
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/mini/auth")
@RequiredArgsConstructor
@Tag(name = "小程序 - 认证", description = "小程序认证接口")
public class MiniAuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:604800000}")
    private Long expiration;

    /**
     * 微信小程序登录
     */
    @PostMapping("/login")
    @AnonymousAccess
    @Operation(summary = "微信登录", description = "微信小程序授权登录")
    public Result<LoginVO> login(@Valid @RequestBody WxLoginDTO dto) {
        log.info("微信登录请求 - code: {}", dto.getCode());

        User user = userService.wxLogin(
            dto.getCode(),
            dto.getEncryptedData(),
            dto.getIv(),
            buildUserInfoDTO(dto)
        );

        LoginVO loginVO = new LoginVO();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUser(userVO);
        
        String token = jwtUtil.generateMiniToken(user.getId(), user.getOpenid());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), "mini");
        
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(expiration / 1000);

        log.info("微信登录成功 - userId: {}", user.getId());
        return Result.success(loginVO);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @AnonymousAccess
    @Operation(summary = "刷新 Token", description = "使用 refresh token 刷新 access token")
    public Result<LoginVO> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        
        log.info("刷新 Token - refreshToken: {}", refreshToken);
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            
            User user = userService.getById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            String newToken = jwtUtil.generateMiniToken(user.getId(), user.getOpenid());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), "mini");
            
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(newToken);
            loginVO.setRefreshToken(newRefreshToken);
            loginVO.setExpiresIn(expiration / 1000);
            
            log.info("刷新Token成功 - userId: {}", userId);
            return Result.success(loginVO);
            
        } catch (RuntimeException e) {
            log.warn("刷新Token失败: {}", e.getMessage());
            return Result.fail("Token无效或已过期");
        }
    }

    /**
     * 绑定手机号
     */
    @PostMapping("/bind-phone")
    @Operation(summary = "绑定手机号", description = "绑定用户手机号")
    public Result<String> bindPhone(
            @Parameter(description = "微信 code", required = true)
            @RequestParam String code,
            @Parameter(description = "加密数据", required = true)
            @RequestParam String encryptedData,
            @Parameter(description = "解密向量", required = true)
            @RequestParam String iv) {
        
        log.info("绑定手机号请求");
        
        return Result.success("138****1234");
    }

    /**
     * 构建用户信息 DTO
     */
    private UserService.UserInfoDTO buildUserInfoDTO(WxLoginDTO dto) {
        UserService.UserInfoDTO userInfo = new UserService.UserInfoDTO();
        if (StrUtil.isNotBlank(dto.getNickName())) {
            userInfo.setNickName(dto.getNickName());
        }
        if (StrUtil.isNotBlank(dto.getAvatarUrl())) {
            userInfo.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getGender() != null) {
            userInfo.setGender(dto.getGender());
        }
        return userInfo;
    }
}
