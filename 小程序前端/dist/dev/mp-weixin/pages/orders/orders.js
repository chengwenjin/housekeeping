"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "orders",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const orderType = common_vendor.ref("published");
    const currentStatus = common_vendor.ref(null);
    const orderList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const refreshing = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const page = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const statusOptions = [
      { label: "全部", value: null },
      { label: "待服务", value: 1 },
      { label: "进行中", value: 2 },
      { label: "待确认", value: 3 },
      { label: "已完成", value: 4 }
    ];
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
    const getStatusText = (status) => {
      const texts = {
        1: "待服务",
        2: "进行中",
        3: "待确认",
        4: "已完成"
      };
      return texts[status] || "未知";
    };
    const getStatusBadgeClass = (status) => {
      const classes = {
        1: "status-pending",
        2: "status-active",
        3: "status-confirm",
        4: "status-done"
      };
      return classes[status] || "status-pending";
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
    const switchOrderType = (type) => {
      orderType.value = type;
      page.value = 1;
      hasMore.value = true;
      orderList.value = [];
      loadOrders();
    };
    const selectStatus = (status) => {
      currentStatus.value = status;
      page.value = 1;
      hasMore.value = true;
      orderList.value = [];
      loadOrders();
    };
    const loadOrders = async () => {
      var _a, _b;
      if (!userStore.isLoggedIn)
        return;
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      try {
        const params = {
          page: page.value,
          pageSize: pageSize.value
        };
        if (currentStatus.value !== null) {
          params.status = currentStatus.value;
        }
        let res;
        if (orderType.value === "published") {
          res = await api_index.orderApi.getPublished(params);
        } else {
          res = await api_index.orderApi.getTaken(params);
        }
        if (res.code === 200) {
          const newList = ((_a = res.data) == null ? void 0 : _a.list) || [];
          orderList.value = [...orderList.value, ...newList];
          const pagination = ((_b = res.data) == null ? void 0 : _b.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载订单失败", e);
      } finally {
        loading.value = false;
        refreshing.value = false;
      }
    };
    const onRefresh = () => {
      refreshing.value = true;
      page.value = 1;
      hasMore.value = true;
      orderList.value = [];
      loadOrders();
    };
    const loadMore = () => {
      if (!loading.value && hasMore.value) {
        loadOrders();
      }
    };
    const goToOrderDetail = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/order-detail/order-detail?id=${id}`
      });
    };
    const startService = async (order) => {
      common_vendor.index.showModal({
        title: "确认开始服务",
        content: "确定要开始服务吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(order.id, {
                status: 2,
                remark: "开始服务"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "已开始服务",
                  icon: "success"
                });
                onRefresh();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("开始服务失败", e);
            }
          }
        }
      });
    };
    const completeService = async (order) => {
      common_vendor.index.showModal({
        title: "确认完成服务",
        content: "确定要确认完成服务吗？请等待雇主确认。",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(order.id, {
                status: 3,
                remark: "服务已完成"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "已提交确认",
                  icon: "success"
                });
                onRefresh();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("确认完成失败", e);
            }
          }
        }
      });
    };
    const confirmComplete = async (order) => {
      common_vendor.index.showModal({
        title: "确认订单完成",
        content: "确定要确认订单完成吗？确认后可以进行评价。",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.updateStatus(order.id, {
                status: 4,
                remark: "订单已完成"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "订单已完成",
                  icon: "success"
                });
                onRefresh();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("确认订单失败", e);
            }
          }
        }
      });
    };
    const cancelOrder = async (order) => {
      common_vendor.index.showModal({
        title: "取消订单",
        content: "确定要取消订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "处理中..." });
              const result = await api_index.orderApi.cancel(order.id, {
                reason: "用户主动取消",
                cancelBy: orderType.value === "published" ? "publisher" : "taker"
              });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "订单已取消",
                  icon: "success"
                });
                onRefresh();
              }
            } catch (e) {
              common_vendor.index.hideLoading();
              console.error("取消订单失败", e);
            }
          }
        }
      });
    };
    const goToReview = (order) => {
      common_vendor.index.showToast({
        title: "评价功能开发中",
        icon: "none"
      });
    };
    common_vendor.onMounted(() => {
      var _a;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const type = (_a = currentPage.options) == null ? void 0 : _a.type;
      if (type === "taken") {
        orderType.value = "taken";
      }
      if (userStore.isLoggedIn) {
        loadOrders();
      }
    });
    const __returned__ = { userStore, orderType, currentStatus, orderList, loading, refreshing, hasMore, page, pageSize, statusOptions, getCategoryIcon, getStatusText, getStatusBadgeClass, formatTime, switchOrderType, selectStatus, loadOrders, onRefresh, loadMore, goToOrderDetail, startService, completeService, confirmComplete, cancelOrder, goToReview, ref: common_vendor.ref, onMounted: common_vendor.onMounted, get useUserStore() {
      return store_user.useUserStore;
    }, get orderApi() {
      return api_index.orderApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.n($setup.orderType === "published" ? "active" : ""),
    b: common_vendor.o(($event) => $setup.switchOrderType("published")),
    c: common_vendor.n($setup.orderType === "taken" ? "active" : ""),
    d: common_vendor.o(($event) => $setup.switchOrderType("taken")),
    e: common_vendor.f($setup.statusOptions, (status, k0, i0) => {
      return {
        a: common_vendor.t(status.label),
        b: status.value,
        c: common_vendor.n($setup.currentStatus === status.value ? "active" : ""),
        d: common_vendor.o(($event) => $setup.selectStatus(status.value), status.value)
      };
    }),
    f: common_vendor.f($setup.orderList, (order, k0, i0) => {
      var _a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k;
      return common_vendor.e({
        a: common_vendor.t($setup.getCategoryIcon((_b = (_a = order.demand) == null ? void 0 : _a.category) == null ? void 0 : _b.id)),
        b: common_vendor.n("oi" + ((((_d = (_c = order.demand) == null ? void 0 : _c.category) == null ? void 0 : _d.id) || 1) % 3 + 1)),
        c: common_vendor.t(((_e = order.demand) == null ? void 0 : _e.title) || "需求"),
        d: common_vendor.t(order.orderNo),
        e: common_vendor.t($setup.getStatusText(order.status)),
        f: common_vendor.n($setup.getStatusBadgeClass(order.status)),
        g: $setup.orderType === "published" && order.taker
      }, $setup.orderType === "published" && order.taker ? {
        h: common_vendor.t(((_f = order.taker) == null ? void 0 : _f.nickname) || "用户")
      } : $setup.orderType === "taken" && order.publisher ? {
        j: common_vendor.t(((_g = order.publisher) == null ? void 0 : _g.nickname) || "用户")
      } : {}, {
        i: $setup.orderType === "taken" && order.publisher,
        k: order.serviceTime
      }, order.serviceTime ? {
        l: common_vendor.t($setup.formatTime(order.serviceTime))
      } : {}, {
        m: (_h = order.location) == null ? void 0 : _h.address
      }, ((_i = order.location) == null ? void 0 : _i.address) ? {
        n: common_vendor.t(order.location.district),
        o: common_vendor.t(order.location.address)
      } : {}, {
        p: common_vendor.t(order.actualPrice || ((_j = order.demand) == null ? void 0 : _j.expectedPrice) || 0),
        q: common_vendor.t(order.priceUnit || ((_k = order.demand) == null ? void 0 : _k.priceUnit) || "小时"),
        r: order.status === 1 && $setup.orderType === "taken"
      }, order.status === 1 && $setup.orderType === "taken" ? {
        s: common_vendor.o(($event) => $setup.startService(order), order.id)
      } : {}, {
        t: order.status === 2 && $setup.orderType === "taken"
      }, order.status === 2 && $setup.orderType === "taken" ? {
        v: common_vendor.o(($event) => $setup.completeService(order), order.id)
      } : {}, {
        w: order.status === 3 && $setup.orderType === "published"
      }, order.status === 3 && $setup.orderType === "published" ? {
        x: common_vendor.o(($event) => $setup.confirmComplete(order), order.id)
      } : {}, {
        y: order.status === 4
      }, order.status === 4 ? {
        z: common_vendor.o(($event) => $setup.goToReview(order), order.id)
      } : {}, {
        A: order.status === 1 || order.status === 2
      }, order.status === 1 || order.status === 2 ? {
        B: common_vendor.o(($event) => $setup.cancelOrder(order), order.id)
      } : {}, {
        C: order.id,
        D: common_vendor.o(($event) => $setup.goToOrderDetail(order.id), order.id)
      });
    }),
    g: $setup.orderList.length === 0 && !$setup.loading
  }, $setup.orderList.length === 0 && !$setup.loading ? {} : {}, {
    h: $setup.loading
  }, $setup.loading ? {} : {}, {
    i: !$setup.loading && !$setup.hasMore && $setup.orderList.length > 0
  }, !$setup.loading && !$setup.hasMore && $setup.orderList.length > 0 ? {} : {}, {
    j: common_vendor.o($setup.loadMore),
    k: $setup.refreshing,
    l: common_vendor.o($setup.onRefresh)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-926fbf1b"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/orders/orders.vue"]]);
wx.createPage(MiniProgramPage);
