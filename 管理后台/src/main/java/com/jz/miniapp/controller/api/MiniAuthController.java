package com.jz.miniapp.controller.api;

import cn.hutool.core.util.StrUtil;
import com.jz.miniapp.common.Result;
import com.jz.miniapp.dto.WxLoginDTO;
import com.jz.miniapp.entity.User;
import com.jz.miniapp.service.UserService;
import com.jz.miniapp.vo.LoginVO;
import com.jz.miniapp.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "小程序 - 认证", description = "小程序认证接口")
public class MiniAuthController {

    @Autowired
    private UserService userService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/login")
    @Operation(summary = "微信登录", description = "微信小程序授权登录")
    public Result<LoginVO> login(@Valid @RequestBody WxLoginDTO dto) {
        log.info("微信登录请求 - code: {}", dto.getCode());

        // 调用微信登录服务
        User user = userService.wxLogin(
            dto.getCode(),
            dto.getEncryptedData(),
            dto.getIv(),
            buildUserInfoDTO(dto)
        );

        // 构建响应
        LoginVO loginVO = new LoginVO();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUser(userVO);
        
        // TODO: 生成 token (暂时使用 mock 数据)
        loginVO.setToken("mock_token_" + user.getId());
        loginVO.setRefreshToken("mock_refresh_token_" + user.getId());
        loginVO.setExpiresIn(604800L); // 7 天

        log.info("微信登录成功 - userId: {}", user.getId());
        return Result.success(loginVO);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 refresh token 刷新 access token")
    public Result<LoginVO> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        
        log.info("刷新 Token - refreshToken: {}", refreshToken);
        
        // TODO: 实现刷新 token 逻辑
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken("mock_new_token");
        loginVO.setRefreshToken("mock_new_refresh_token");
        loginVO.setExpiresIn(604800L);
        
        return Result.success(loginVO);
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
        
        // TODO: 实现绑定手机号逻辑
        
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
