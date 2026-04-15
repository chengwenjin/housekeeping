"use strict";
const common_vendor = require("../common/vendor.js");
const api_index = require("../api/index.js");
const useUserStore = common_vendor.defineStore("user", () => {
  const token = common_vendor.ref(common_vendor.index.getStorageSync("token") || "");
  const refreshToken = common_vendor.ref(common_vendor.index.getStorageSync("refreshToken") || "");
  const userInfo = common_vendor.ref(common_vendor.index.getStorageSync("userInfo") || null);
  const isLoggedIn = common_vendor.computed(() => !!token.value);
  const setToken = (newToken, newRefreshToken) => {
    token.value = newToken;
    refreshToken.value = newRefreshToken;
    common_vendor.index.setStorageSync("token", newToken);
    common_vendor.index.setStorageSync("refreshToken", newRefreshToken);
  };
  const setUserInfo = (info) => {
    userInfo.value = info;
    common_vendor.index.setStorageSync("userInfo", info);
  };
  const login = async (loginData) => {
    const res = await api_index.authApi.login(loginData);
    if (res.code === 200) {
      setToken(res.data.token, res.data.refreshToken);
      setUserInfo(res.data.user);
      return res.data;
    }
    throw new Error(res.message);
  };
  const logout = () => {
    token.value = "";
    refreshToken.value = "";
    userInfo.value = null;
    common_vendor.index.removeStorageSync("token");
    common_vendor.index.removeStorageSync("refreshToken");
    common_vendor.index.removeStorageSync("userInfo");
  };
  const refreshUserInfo = async () => {
    if (!token.value)
      return;
    try {
      const res = await api_index.userApi.getProfile();
      if (res.code === 200) {
        setUserInfo(res.data);
      }
    } catch (e) {
      console.error("刷新用户信息失败", e);
    }
  };
  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    logout,
    refreshUserInfo
  };
});
exports.useUserStore = useUserStore;
