"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "detail",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const demandId = common_vendor.ref(null);
    const demand = common_vendor.ref({});
    const isFollowed = common_vendor.ref(false);
    const loading = common_vendor.ref(false);
    const getAvatarEmoji = (id) => {
      const emojis = ["👩", "👨", "👩‍🦳", "👨‍🦳", "👩‍🍳", "👨‍⚕️", "🧹", "👶"];
      return emojis[(id || 0) % emojis.length];
    };
    const formatTime = (time) => {
      if (!time)
        return "";
      const date = new Date(time);
      const now = /* @__PURE__ */ new Date();
      const diff = now - date;
      if (diff < 6e4)
        return "刚刚";
      if (diff < 36e5)
        return `${Math.floor(diff / 6e4)}分钟前`;
      if (diff < 864e5)
        return `${Math.floor(diff / 36e5)}小时前`;
      if (diff < 6048e5)
        return `${Math.floor(diff / 864e5)}天前`;
      return time.substring(0, 10);
    };
    const loadDemandDetail = async () => {
      var _a, _b;
      if (!demandId.value)
        return;
      loading.value = true;
      try {
        const res = await api_index.demandApi.getDetail(demandId.value);
        if (res.code === 200) {
          demand.value = res.data || {};
          isFollowed.value = ((_b = (_a = res.data) == null ? void 0 : _a.publisher) == null ? void 0 : _b.isFollowed) || false;
          try {
            await api_index.demandApi.addFootprint(demandId.value);
          } catch (e) {
            console.log("添加足迹失败", e);
          }
        }
      } catch (e) {
        console.error("加载需求详情失败", e);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const goToUserProfile = () => {
      var _a;
      if ((_a = demand.value.publisher) == null ? void 0 : _a.id) {
        common_vendor.index.navigateTo({
          url: `/pages/user-profile/user-profile?id=${demand.value.publisher.id}`
        });
      }
    };
    const toggleFollow = async () => {
      var _a;
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      if (!((_a = demand.value.publisher) == null ? void 0 : _a.id))
        return;
      try {
        if (isFollowed.value) {
          const res = await api_index.userApi.unfollow(demand.value.publisher.id);
          if (res.code === 200) {
            isFollowed.value = false;
            common_vendor.index.showToast({
              title: "已取消关注",
              icon: "success"
            });
          }
        } else {
          const res = await api_index.userApi.follow(demand.value.publisher.id);
          if (res.code === 200) {
            isFollowed.value = true;
            common_vendor.index.showToast({
              title: "关注成功",
              icon: "success"
            });
          }
        }
      } catch (e) {
        console.error("关注操作失败", e);
      }
    };
    const handleChat = () => {
      common_vendor.index.showToast({
        title: "聊天功能开发中",
        icon: "none"
      });
    };
    const handleTakeOrder = async () => {
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      common_vendor.index.showModal({
        title: "确认接单",
        content: `确定要接取"${demand.value.title}"这个需求吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "接单中..." });
              const result = await api_index.orderApi.takeOrder(demandId.value, { remark: "" });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "接单成功",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.switchTab({
                    url: "/pages/orders/orders"
                  });
                }, 1500);
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("接单失败", e);
            }
          }
        }
      });
    };
    common_vendor.onMounted(() => {
      var _a;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      demandId.value = (_a = currentPage.options) == null ? void 0 : _a.id;
      if (demandId.value) {
        loadDemandDetail();
      } else {
        common_vendor.index.showToast({
          title: "参数错误",
          icon: "none"
        });
      }
    });
    const __returned__ = { userStore, demandId, demand, isFollowed, loading, getAvatarEmoji, formatTime, loadDemandDetail, goBack, goToUserProfile, toggleFollow, handleChat, handleTakeOrder, ref: common_vendor.ref, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get demandApi() {
      return api_index.demandApi;
    }, get orderApi() {
      return api_index.orderApi;
    }, get userApi() {
      return api_index.userApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b, _c, _d, _e, _f, _g, _h, _i, _j;
  return common_vendor.e({
    a: common_vendor.o($setup.goBack),
    b: (_a = $setup.demand.category) == null ? void 0 : _a.name
  }, ((_b = $setup.demand.category) == null ? void 0 : _b.name) ? {
    c: common_vendor.t($setup.demand.category.name)
  } : {}, {
    d: $setup.demand.status === 1
  }, $setup.demand.status === 1 ? {} : {}, {
    e: common_vendor.t($setup.demand.title),
    f: common_vendor.t($setup.demand.description),
    g: common_vendor.t(((_c = $setup.demand.location) == null ? void 0 : _c.district) || ""),
    h: common_vendor.t(((_d = $setup.demand.location) == null ? void 0 : _d.address) || ""),
    i: common_vendor.t($setup.demand.serviceTimeDesc || "待定"),
    j: common_vendor.t($setup.demand.minDuration ? `约 ${$setup.demand.minDuration}-${$setup.demand.maxDuration} 小时` : "待定"),
    k: common_vendor.t($setup.demand.houseArea || "待定"),
    l: common_vendor.t($setup.getAvatarEmoji((_e = $setup.demand.publisher) == null ? void 0 : _e.id)),
    m: common_vendor.t(((_f = $setup.demand.publisher) == null ? void 0 : _f.nickname) || "用户"),
    n: common_vendor.t(((_g = $setup.demand.publisher) == null ? void 0 : _g.score) || 5),
    o: common_vendor.t(((_h = $setup.demand.publisher) == null ? void 0 : _h.totalOrders) || 0),
    p: common_vendor.o($setup.goToUserProfile),
    q: common_vendor.t($setup.isFollowed ? "已关注" : "+ 关注"),
    r: common_vendor.n($setup.isFollowed ? "followed" : ""),
    s: common_vendor.o($setup.toggleFollow),
    t: ((_i = $setup.demand.reviews) == null ? void 0 : _i.length) > 0
  }, ((_j = $setup.demand.reviews) == null ? void 0 : _j.length) > 0 ? {
    v: common_vendor.f($setup.demand.reviews, (review, k0, i0) => {
      var _a2, _b2;
      return {
        a: common_vendor.t($setup.getAvatarEmoji((_a2 = review.reviewer) == null ? void 0 : _a2.id)),
        b: common_vendor.t(((_b2 = review.reviewer) == null ? void 0 : _b2.nickname) || "用户"),
        c: common_vendor.t(review.content),
        d: common_vendor.t($setup.formatTime(review.createdAt)),
        e: review.id
      };
    })
  } : {}, {
    w: common_vendor.t($setup.demand.expectedPrice || 0),
    x: common_vendor.t($setup.demand.priceUnit || "时"),
    y: common_vendor.t($setup.demand.minDuration || 2),
    z: common_vendor.t($setup.demand.maxDuration || 3),
    A: common_vendor.t(($setup.demand.expectedPrice || 0) * ($setup.demand.minDuration || 2)),
    B: common_vendor.t(($setup.demand.expectedPrice || 0) * ($setup.demand.maxDuration || 3)),
    C: common_vendor.o($setup.handleChat),
    D: $setup.demand.status === 1
  }, $setup.demand.status === 1 ? {
    E: common_vendor.o($setup.handleTakeOrder)
  } : {
    F: common_vendor.t($setup.demand.statusText || "已结束")
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5e1a6ddb"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/demand/detail.vue"]]);
wx.createPage(MiniProgramPage);
