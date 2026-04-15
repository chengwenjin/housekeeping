"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "order-detail",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const orderId = common_vendor.ref(null);
    const order = common_vendor.ref({});
    const loading = common_vendor.ref(false);
    const hasReviewed = common_vendor.ref(false);
    const isPublisher = common_vendor.computed(() => {
      var _a, _b;
      return ((_a = order.value.publisher) == null ? void 0 : _a.id) === ((_b = userStore.userInfo) == null ? void 0 : _b.id);
    });
    const isTaker = common_vendor.computed(() => {
      var _a, _b;
      return ((_a = order.value.taker) == null ? void 0 : _a.id) === ((_b = userStore.userInfo) == null ? void 0 : _b.id);
    });
    const getStatusIcon = (status) => {
      const icons = {
        1: "⏳",
        2: "🔄",
        3: "✅",
        4: "🎉"
      };
      return icons[status] || "📋";
    };
    const getStatusText = (status) => {
      const texts = {
        1: "待服务",
        2: "进行中",
        3: "待确认",
        4: "已完成"
      };
      return texts[status] || "未知状态";
    };
    const getStatusDesc = (status) => {
      const descs = {
        1: "等待服务者开始服务",
        2: "服务进行中",
        3: "等待发布者确认完成",
        4: "订单已完成"
      };
      return descs[status] || "";
    };
    const getAvatarEmoji = (id) => {
      const emojis = ["👩", "👨", "👩‍🍳", "👨‍⚕️", "🧹", "👶", "👩‍🦳", "👨‍🦳"];
      return emojis[(id || 0) % emojis.length];
    };
    const formatTime = (time) => {
      if (!time)
        return "";
      const date = new Date(time);
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hour = date.getHours();
      const minute = date.getMinutes();
      return `${year}年${month}月${day}日 ${hour}:${minute.toString().padStart(2, "0")}`;
    };
    const loadOrderDetail = async () => {
      if (!orderId.value)
        return;
      loading.value = true;
      try {
        const res = await api_index.orderApi.getDetail(orderId.value);
        if (res.code === 200) {
          order.value = res.data || {};
        }
      } catch (e) {
        console.error("加载订单详情失败", e);
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
    const callPhone = (phone) => {
      common_vendor.index.makePhoneCall({
        phoneNumber: phone.replace(/\*/g, "0")
      });
    };
    const startService = async () => {
      common_vendor.index.showModal({
        title: "确认开始服务",
        content: "确定要开始服务吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(orderId.value, {
                status: 2,
                remark: "开始服务"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "已开始服务",
                  icon: "success"
                });
                loadOrderDetail();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("开始服务失败", e);
            }
          }
        }
      });
    };
    const completeService = async () => {
      common_vendor.index.showModal({
        title: "确认完成服务",
        content: "确定要确认完成服务吗？请等待雇主确认。",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(orderId.value, {
                status: 3,
                remark: "服务已完成"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "已提交确认",
                  icon: "success"
                });
                loadOrderDetail();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("确认完成失败", e);
            }
          }
        }
      });
    };
    const confirmComplete = async () => {
      common_vendor.index.showModal({
        title: "确认订单完成",
        content: "确定要确认订单完成吗？确认后可以进行评价。",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(orderId.value, {
                status: 4,
                remark: "订单已完成"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "订单已完成",
                  icon: "success"
                });
                loadOrderDetail();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("确认订单失败", e);
            }
          }
        }
      });
    };
    const cancelOrder = async () => {
      common_vendor.index.showModal({
        title: "取消订单",
        content: "确定要取消订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.cancel(orderId.value, {
                reason: "用户主动取消",
                cancelBy: isPublisher.value ? "publisher" : "taker"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "订单已取消",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.navigateBack();
                }, 1500);
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("取消订单失败", e);
            }
          }
        }
      });
    };
    const goToReview = () => {
      common_vendor.index.showToast({
        title: "评价功能开发中",
        icon: "none"
      });
    };
    common_vendor.onMounted(() => {
      var _a;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      orderId.value = (_a = currentPage.options) == null ? void 0 : _a.id;
      if (orderId.value) {
        loadOrderDetail();
      } else {
        common_vendor.index.showToast({
          title: "参数错误",
          icon: "none"
        });
      }
    });
    const __returned__ = { userStore, orderId, order, loading, hasReviewed, isPublisher, isTaker, getStatusIcon, getStatusText, getStatusDesc, getAvatarEmoji, formatTime, loadOrderDetail, goBack, callPhone, startService, completeService, confirmComplete, cancelOrder, goToReview, ref: common_vendor.ref, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get orderApi() {
      return api_index.orderApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k, _l, _m, _n, _o, _p;
  return common_vendor.e({
    a: common_vendor.o($setup.goBack),
    b: common_vendor.t($setup.getStatusIcon($setup.order.status)),
    c: common_vendor.t($setup.getStatusText($setup.order.status)),
    d: common_vendor.t($setup.getStatusDesc($setup.order.status)),
    e: common_vendor.t(((_a = $setup.order.demand) == null ? void 0 : _a.title) || "暂无"),
    f: common_vendor.t(((_b = $setup.order.demand) == null ? void 0 : _b.description) || "暂无"),
    g: common_vendor.t(((_d = (_c = $setup.order.demand) == null ? void 0 : _c.category) == null ? void 0 : _d.name) || "暂无"),
    h: common_vendor.t($setup.order.actualPrice || ((_e = $setup.order.demand) == null ? void 0 : _e.expectedPrice) || 0),
    i: common_vendor.t($setup.order.priceUnit || ((_f = $setup.order.demand) == null ? void 0 : _f.priceUnit) || "小时"),
    j: $setup.order.totalAmount
  }, $setup.order.totalAmount ? {
    k: common_vendor.t($setup.order.totalAmount)
  } : {}, {
    l: common_vendor.t($setup.order.serviceTime ? $setup.formatTime($setup.order.serviceTime) : "待定"),
    m: common_vendor.t(((_g = $setup.order.location) == null ? void 0 : _g.district) || ""),
    n: common_vendor.t(((_h = $setup.order.location) == null ? void 0 : _h.address) || "暂无"),
    o: $setup.order.publisher
  }, $setup.order.publisher ? common_vendor.e({
    p: common_vendor.t($setup.getAvatarEmoji((_i = $setup.order.publisher) == null ? void 0 : _i.id)),
    q: common_vendor.t(((_j = $setup.order.publisher) == null ? void 0 : _j.nickname) || "用户"),
    r: (_k = $setup.order.publisher) == null ? void 0 : _k.phone
  }, ((_l = $setup.order.publisher) == null ? void 0 : _l.phone) ? {
    s: common_vendor.o(($event) => $setup.callPhone($setup.order.publisher.phone))
  } : {}) : {}, {
    t: $setup.order.taker
  }, $setup.order.taker ? common_vendor.e({
    v: common_vendor.t($setup.getAvatarEmoji((_m = $setup.order.taker) == null ? void 0 : _m.id)),
    w: common_vendor.t(((_n = $setup.order.taker) == null ? void 0 : _n.nickname) || "用户"),
    x: (_o = $setup.order.taker) == null ? void 0 : _o.phone
  }, ((_p = $setup.order.taker) == null ? void 0 : _p.phone) ? {
    y: common_vendor.o(($event) => $setup.callPhone($setup.order.taker.phone))
  } : {}) : {}, {
    z: $setup.order.remark
  }, $setup.order.remark ? {
    A: common_vendor.t($setup.order.remark)
  } : {}, {
    B: $setup.order.status === 1 && $setup.isTaker
  }, $setup.order.status === 1 && $setup.isTaker ? {
    C: common_vendor.o($setup.startService)
  } : {}, {
    D: $setup.order.status === 2 && $setup.isTaker
  }, $setup.order.status === 2 && $setup.isTaker ? {
    E: common_vendor.o($setup.completeService)
  } : {}, {
    F: $setup.order.status === 3 && $setup.isPublisher
  }, $setup.order.status === 3 && $setup.isPublisher ? {
    G: common_vendor.o($setup.confirmComplete)
  } : {}, {
    H: $setup.order.status === 4 && !$setup.hasReviewed
  }, $setup.order.status === 4 && !$setup.hasReviewed ? {
    I: common_vendor.o($setup.goToReview)
  } : {}, {
    J: $setup.order.status === 1 || $setup.order.status === 2
  }, $setup.order.status === 1 || $setup.order.status === 2 ? {
    K: common_vendor.o($setup.cancelOrder)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-48de6e3f"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/order-detail/order-detail.vue"]]);
wx.createPage(MiniProgramPage);
