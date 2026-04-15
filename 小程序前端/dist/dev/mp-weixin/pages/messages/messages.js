"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "messages",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const activeTab = common_vendor.ref("all");
    const messageList = common_vendor.ref([]);
    const unreadCount = common_vendor.ref(0);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const page = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const getMessageIcon = (type) => {
      const icons = {
        1: "🔔",
        2: "📋",
        3: "⭐",
        4: "💬"
      };
      return icons[type] || "🔔";
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
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    };
    const switchTab = (tab) => {
      activeTab.value = tab;
      page.value = 1;
      hasMore.value = true;
      messageList.value = [];
      loadMessages();
    };
    const loadMessages = async () => {
      var _a, _b, _c;
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
        if (activeTab.value !== "all") {
          params.type = activeTab.value;
        }
        const res = await api_index.messageApi.getList(params);
        if (res.code === 200) {
          const newList = ((_a = res.data) == null ? void 0 : _a.list) || [];
          messageList.value = [...messageList.value, ...newList];
          unreadCount.value = ((_b = res.data) == null ? void 0 : _b.unreadCount) || 0;
          const pagination = ((_c = res.data) == null ? void 0 : _c.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载消息失败", e);
      } finally {
        loading.value = false;
      }
    };
    const loadMore = () => {
      if (!loading.value && hasMore.value) {
        loadMessages();
      }
    };
    const markAllRead = async () => {
      try {
        const res = await api_index.messageApi.markAllRead();
        if (res.code === 200) {
          unreadCount.value = 0;
          messageList.value.forEach((msg) => {
            msg.isRead = true;
          });
          common_vendor.index.showToast({
            title: "已全部标记为已读",
            icon: "success"
          });
        }
      } catch (e) {
        console.error("标记已读失败", e);
      }
    };
    const handleMessageClick = async (msg) => {
      if (!msg.isRead) {
        try {
          await api_index.messageApi.markRead(msg.id);
          msg.isRead = true;
          if (unreadCount.value > 0) {
            unreadCount.value--;
          }
        } catch (e) {
          console.error("标记已读失败", e);
        }
      }
      if (msg.relatedType === "order" && msg.relatedId) {
        common_vendor.index.navigateTo({
          url: `/pages/order-detail/order-detail?id=${msg.relatedId}`
        });
      }
    };
    common_vendor.onMounted(() => {
      if (userStore.isLoggedIn) {
        loadMessages();
      }
    });
    const __returned__ = { userStore, activeTab, messageList, unreadCount, loading, hasMore, page, pageSize, getMessageIcon, formatTime, switchTab, loadMessages, loadMore, markAllRead, handleMessageClick, ref: common_vendor.ref, onMounted: common_vendor.onMounted, get useUserStore() {
      return store_user.useUserStore;
    }, get messageApi() {
      return api_index.messageApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $setup.unreadCount > 0
  }, $setup.unreadCount > 0 ? {
    b: common_vendor.o($setup.markAllRead)
  } : {}, {
    c: $setup.unreadCount > 0
  }, $setup.unreadCount > 0 ? {
    d: common_vendor.t($setup.unreadCount)
  } : {}, {
    e: common_vendor.n($setup.activeTab === "all" ? "active" : ""),
    f: common_vendor.o(($event) => $setup.switchTab("all")),
    g: common_vendor.n($setup.activeTab === "system" ? "active" : ""),
    h: common_vendor.o(($event) => $setup.switchTab("system")),
    i: common_vendor.n($setup.activeTab === "order" ? "active" : ""),
    j: common_vendor.o(($event) => $setup.switchTab("order")),
    k: common_vendor.n($setup.activeTab === "review" ? "active" : ""),
    l: common_vendor.o(($event) => $setup.switchTab("review")),
    m: common_vendor.f($setup.messageList, (msg, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t($setup.getMessageIcon(msg.type)),
        b: common_vendor.t(msg.title),
        c: common_vendor.t($setup.formatTime(msg.createdAt)),
        d: common_vendor.t(msg.content),
        e: !msg.isRead
      }, !msg.isRead ? {} : {}, {
        f: msg.id,
        g: common_vendor.n(!msg.isRead ? "unread" : ""),
        h: common_vendor.o(($event) => $setup.handleMessageClick(msg), msg.id)
      });
    }),
    n: $setup.messageList.length === 0 && !$setup.loading
  }, $setup.messageList.length === 0 && !$setup.loading ? {} : {}, {
    o: $setup.loading
  }, $setup.loading ? {} : {}, {
    p: !$setup.loading && !$setup.hasMore && $setup.messageList.length > 0
  }, !$setup.loading && !$setup.hasMore && $setup.messageList.length > 0 ? {} : {}, {
    q: common_vendor.o($setup.loadMore)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-f5640984"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/messages/messages.vue"]]);
wx.createPage(MiniProgramPage);
