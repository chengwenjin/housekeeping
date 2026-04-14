package com.jz.miniapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.miniapp.entity.User;

/**
 * 用户服务接口
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
public interface UserService extends IService<User> {

    /**
     * 微信小程序登录
     * 
     * @param code 微信登录凭证
     * @param encryptedData 加密数据
     * @param iv 解密向量
     * @param userInfo 用户信息
     * @return 登录用户信息
     */
    User wxLogin(String code, String encryptedData, String iv, UserInfoDTO userInfo);

    /**
     * 根据 openid 获取用户
     * 
     * @param openid 微信 openid
     * @return 用户信息
     */
    User getByOpenid(String openid);

    /**
     * 用户 DTO
     */
    class UserInfoDTO {
        private String nickName;
        private String avatarUrl;
        private Integer gender;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }
    }
}
