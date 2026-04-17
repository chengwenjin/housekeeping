"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const _sfc_main = {
  __name: "login",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const loading = common_vendor.ref(false);
    common_vendor.onMounted(() => {
      if (userStore.isLoggedIn) {
        common_vendor.index.switchTab({
          url: "/pages/index/index"
        });
      }
    });
    const handleLogin = async () => {
      if (loading.value)
        return;
      loading.value = true;
      try {
        common_vendor.index.showLoading({ title: "登录中..." });
        common_vendor.index.login({
          provider: "weixin",
          success: async (loginRes) => {
            console.log("登录code:", loginRes.code);
            try {
              const mockUserInfo = {
                code: loginRes.code,
                userInfo: {
                  nickName: "测试用户",
                  avatarUrl: "",
                  gender: 1
                }
              };
              const res = await userStore.login(mockUserInfo);
              common_vendor.index.hideLoading();
              if (res) {
                common_vendor.index.showToast({
                  title: "登录成功",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.switchTab({
                    url: "/pages/index/index"
                  });
                }, 1500);
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              loading.value = false;
              console.error("登录失败", e);
            }
          },
          fail: (err) => {
            common_vendor.index.hideLoading();
            loading.value = false;
            common_vendor.index.showToast({
              title: "微信登录失败",
              icon: "none"
            });
            console.error("微信登录失败", err);
          }
        });
      } catch (e) {
        common_vendor.index.hideLoading();
        loading.value = false;
        console.error(e);
      }
    };
    const __returned__ = { userStore, loading, handleLogin, ref: common_vendor.ref, get useUserStore() {
      return store_user.useUserStore;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.o($setup.handleLogin)
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-cdfe2409"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/login/login.vue"]]);
wx.createPage(MiniProgramPage);
