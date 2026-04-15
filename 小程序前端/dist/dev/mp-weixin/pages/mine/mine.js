"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "mine",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const publishedOrders = common_vendor.ref([]);
    const takenOrders = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const getCategoryIcon = (id) => {
      const icons = {
        1: "🧹",
        2: "🍳",
        3: "👶",
        4: "🧓",
        5: "📦",
        6: "🐾",
        7: "🔧"
      };
      return icons[id] || "🏠";
    };
    const getStatusClass = (status) => {
      switch (status) {
        case 1:
          return "os-pending";
        case 2:
          return "os-active";
        case 3:
          return "os-pending";
        case 4:
          return "os-done";
        default:
          return "os-pending";
      }
    };
    const getStatusText = (status) => {
      switch (status) {
        case 1:
          return "待服务";
        case 2:
          return "进行中";
        case 3:
          return "待确认";
        case 4:
          return "已完成";
        default:
          return "待接单";
      }
    };
    const formatTime = (time) => {
      if (!time)
        return "";
      const date = new Date(time);
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hour = date.getHours();
      const minute = date.getMinutes();
      return `${month}月${day}日 ${hour}:${minute.toString().padStart(2, "0")}`;
    };
    const loadOrders = async () => {
      var _a, _b;
      if (!userStore.isLoggedIn)
        return;
      loading.value = true;
      try {
        const [publishedRes, takenRes] = await Promise.all([
          api_index.orderApi.getPublished({ page: 1, pageSize: 3 }),
          api_index.orderApi.getTaken({ page: 1, pageSize: 3 })
        ]);
        if (publishedRes.code === 200) {
          publishedOrders.value = ((_a = publishedRes.data) == null ? void 0 : _a.list) || [];
        }
        if (takenRes.code === 200) {
          takenOrders.value = ((_b = takenRes.data) == null ? void 0 : _b.list) || [];
        }
      } catch (e) {
        console.error("加载订单失败", e);
      } finally {
        loading.value = false;
      }
    };
    const goToLogin = () => {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
    };
    const goToEdit = () => {
      common_vendor.index.showToast({
        title: "编辑功能开发中",
        icon: "none"
      });
    };
    const goToPublishedOrders = () => {
      common_vendor.index.navigateTo({
        url: "/pages/orders/orders?type=published"
      });
    };
    const goToTakenOrders = () => {
      common_vendor.index.navigateTo({
        url: "/pages/orders/orders?type=taken"
      });
    };
    const goToFollowing = () => {
      common_vendor.index.navigateTo({
        url: "/pages/follow/follow?tab=following"
      });
    };
    const goToFootprints = () => {
      common_vendor.index.navigateTo({
        url: "/pages/follow/follow?tab=footprint"
      });
    };
    const goToReviews = () => {
      common_vendor.index.showToast({
        title: "评价功能开发中",
        icon: "none"
      });
    };
    const goToMessages = () => {
      common_vendor.index.switchTab({
        url: "/pages/messages/messages"
      });
    };
    const goToSettings = () => {
      common_vendor.index.showToast({
        title: "设置功能开发中",
        icon: "none"
      });
    };
    const goToOrderDetail = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/order-detail/order-detail?id=${id}`
      });
    };
    const handleLogout = () => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            userStore.logout();
            common_vendor.index.showToast({
              title: "已退出登录",
              icon: "success"
            });
          }
        }
      });
    };
    common_vendor.onMounted(() => {
      if (userStore.isLoggedIn) {
        loadOrders();
        userStore.refreshUserInfo();
      }
    });
    const __returned__ = { userStore, publishedOrders, takenOrders, loading, getCategoryIcon, getStatusClass, getStatusText, formatTime, loadOrders, goToLogin, goToEdit, goToPublishedOrders, goToTakenOrders, goToFollowing, goToFootprints, goToReviews, goToMessages, goToSettings, goToOrderDetail, handleLogout, ref: common_vendor.ref, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get orderApi() {
      return api_index.orderApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b, _c, _d, _e, _f, _g, _h, _i;
  return common_vendor.e({
    a: $setup.userStore.isLoggedIn
  }, $setup.userStore.isLoggedIn ? common_vendor.e({
    b: (_a = $setup.userStore.userInfo) == null ? void 0 : _a.avatarUrl
  }, ((_b = $setup.userStore.userInfo) == null ? void 0 : _b.avatarUrl) ? {
    c: common_vendor.t($setup.userStore.userInfo.avatarUrl)
  } : {}, {
    d: common_vendor.t(((_c = $setup.userStore.userInfo) == null ? void 0 : _c.nickname) || "用户"),
    e: common_vendor.t(((_d = $setup.userStore.userInfo) == null ? void 0 : _d.certificationStatus) === 2 ? "已认证" : "未认证"),
    f: common_vendor.t(((_e = $setup.userStore.userInfo) == null ? void 0 : _e.bio) || ""),
    g: common_vendor.o($setup.goToEdit)
  }) : {
    h: common_vendor.o($setup.goToLogin)
  }, {
    i: $setup.userStore.isLoggedIn
  }, $setup.userStore.isLoggedIn ? {
    j: common_vendor.t(((_f = $setup.userStore.userInfo) == null ? void 0 : _f.publishedCount) || 0),
    k: common_vendor.o($setup.goToPublishedOrders),
    l: common_vendor.t(((_g = $setup.userStore.userInfo) == null ? void 0 : _g.takenCount) || 0),
    m: common_vendor.o($setup.goToTakenOrders),
    n: common_vendor.t(((_h = $setup.userStore.userInfo) == null ? void 0 : _h.followingCount) || 0),
    o: common_vendor.o($setup.goToFollowing),
    p: common_vendor.t(((_i = $setup.userStore.userInfo) == null ? void 0 : _i.followerCount) || 0),
    q: common_vendor.o($setup.goToFootprints)
  } : {}, {
    r: $setup.userStore.isLoggedIn
  }, $setup.userStore.isLoggedIn ? common_vendor.e({
    s: common_vendor.o($setup.goToPublishedOrders),
    t: common_vendor.f($setup.publishedOrders, (order, k0, i0) => {
      var _a2, _b2, _c2, _d2, _e2, _f2, _g2;
      return {
        a: common_vendor.t($setup.getCategoryIcon((_b2 = (_a2 = order.demand) == null ? void 0 : _a2.category) == null ? void 0 : _b2.id)),
        b: common_vendor.n("oi" + ((((_d2 = (_c2 = order.demand) == null ? void 0 : _c2.category) == null ? void 0 : _d2.id) || 1) % 3 + 1)),
        c: common_vendor.t(((_e2 = order.demand) == null ? void 0 : _e2.title) || "需求"),
        d: common_vendor.t(((_f2 = order.taker) == null ? void 0 : _f2.nickname) ? order.taker.nickname + " 接单" : "尚未有人接单"),
        e: common_vendor.t(order.serviceTime ? " · " + $setup.formatTime(order.serviceTime) : ""),
        f: common_vendor.t(order.actualPrice || ((_g2 = order.demand) == null ? void 0 : _g2.expectedPrice) || 0),
        g: common_vendor.t(order.priceUnit ? "/" + order.priceUnit : ""),
        h: common_vendor.t($setup.getStatusText(order.status)),
        i: common_vendor.n($setup.getStatusClass(order.status)),
        j: order.id,
        k: common_vendor.o(($event) => $setup.goToOrderDetail(order.id), order.id)
      };
    }),
    v: $setup.publishedOrders.length === 0
  }, $setup.publishedOrders.length === 0 ? {} : {}) : {}, {
    w: $setup.userStore.isLoggedIn
  }, $setup.userStore.isLoggedIn ? common_vendor.e({
    x: common_vendor.o($setup.goToTakenOrders),
    y: common_vendor.f($setup.takenOrders, (order, k0, i0) => {
      var _a2, _b2, _c2, _d2, _e2, _f2, _g2;
      return {
        a: common_vendor.t($setup.getCategoryIcon((_b2 = (_a2 = order.demand) == null ? void 0 : _a2.category) == null ? void 0 : _b2.id)),
        b: common_vendor.n("oi" + ((((_d2 = (_c2 = order.demand) == null ? void 0 : _c2.category) == null ? void 0 : _d2.id) || 1) % 3 + 1)),
        c: common_vendor.t(((_e2 = order.demand) == null ? void 0 : _e2.title) || "需求"),
        d: common_vendor.t(((_f2 = order.publisher) == null ? void 0 : _f2.nickname) ? order.publisher.nickname + " 发布" : ""),
        e: common_vendor.t(order.status === 4 ? " · 已完成" : ""),
        f: common_vendor.t(order.actualPrice || ((_g2 = order.demand) == null ? void 0 : _g2.expectedPrice) || 0),
        g: common_vendor.t(order.priceUnit ? "/" + order.priceUnit : ""),
        h: common_vendor.t($setup.getStatusText(order.status)),
        i: common_vendor.n($setup.getStatusClass(order.status)),
        j: order.id,
        k: common_vendor.o(($event) => $setup.goToOrderDetail(order.id), order.id)
      };
    }),
    z: $setup.takenOrders.length === 0
  }, $setup.takenOrders.length === 0 ? {} : {}) : {}, {
    A: common_vendor.o($setup.goToFootprints),
    B: common_vendor.o($setup.goToFollowing),
    C: common_vendor.o($setup.goToReviews),
    D: common_vendor.o($setup.goToMessages),
    E: common_vendor.o($setup.goToSettings),
    F: $setup.userStore.isLoggedIn
  }, $setup.userStore.isLoggedIn ? {
    G: common_vendor.o($setup.handleLogout)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-d41d38da"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/mine/mine.vue"]]);
wx.createPage(MiniProgramPage);
