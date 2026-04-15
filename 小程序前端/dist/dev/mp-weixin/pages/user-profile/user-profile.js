"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "user-profile",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const userId = common_vendor.ref(null);
    const userInfo = common_vendor.ref({});
    const activeTab = common_vendor.ref("reviews");
    const reviewList = common_vendor.ref([]);
    const categories = common_vendor.ref([]);
    const isFollowed = common_vendor.ref(false);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const page = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const getAvatarEmoji = (id) => {
      const emojis = ["👩", "👨", "👩‍🍳", "👨‍⚕️", "🧹", "👶", "👩‍🦳", "👨‍🦳"];
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
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${year}年${month}月${day}日`;
    };
    const loadUserInfo = async () => {
      var _a, _b, _c;
      if (!userId.value)
        return;
      try {
        const res = await api_index.userApi.getUserInfo(userId.value);
        if (res.code === 200) {
          userInfo.value = res.data || {};
          isFollowed.value = ((_a = res.data) == null ? void 0 : _a.isFollowed) || false;
          if (((_c = (_b = res.data) == null ? void 0 : _b.categories) == null ? void 0 : _c.length) > 0) {
            categories.value = res.data.categories.map((name, index) => ({
              id: index + 1,
              name,
              icon: getCategoryIcon(name)
            }));
          }
        }
      } catch (e) {
        console.error("加载用户信息失败", e);
      }
    };
    const getCategoryIcon = (name) => {
      const icons = {
        "保洁": "🧹",
        "烹饪": "🍳",
        "育儿": "👶",
        "老人": "🧓",
        "搬运": "📦",
        "宠物": "🐾",
        "维修": "🔧"
      };
      return icons[name] || "🏠";
    };
    const loadReviews = async () => {
      var _a, _b;
      if (!userId.value)
        return;
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      try {
        const res = await api_index.reviewApi.getUserReviews(userId.value, {
          page: page.value,
          pageSize: pageSize.value
        });
        if (res.code === 200) {
          const newList = ((_a = res.data) == null ? void 0 : _a.list) || [];
          reviewList.value = [...reviewList.value, ...newList];
          const pagination = ((_b = res.data) == null ? void 0 : _b.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载评价失败", e);
      } finally {
        loading.value = false;
      }
    };
    const loadMore = () => {
      if (!loading.value && hasMore.value && activeTab.value === "reviews") {
        loadReviews();
      }
    };
    const switchTab = (tab) => {
      activeTab.value = tab;
      if (tab === "reviews" && reviewList.value.length === 0) {
        page.value = 1;
        hasMore.value = true;
        loadReviews();
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const toggleFollow = async () => {
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      try {
        if (isFollowed.value) {
          const res = await api_index.userApi.unfollow(userId.value);
          if (res.code === 200) {
            isFollowed.value = false;
            common_vendor.index.showToast({
              title: "已取消关注",
              icon: "success"
            });
          }
        } else {
          const res = await api_index.userApi.follow(userId.value);
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
        title: "私信功能开发中",
        icon: "none"
      });
    };
    common_vendor.onMounted(() => {
      var _a;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      userId.value = (_a = currentPage.options) == null ? void 0 : _a.id;
      if (userId.value) {
        loadUserInfo();
        loadReviews();
      } else {
        common_vendor.index.showToast({
          title: "参数错误",
          icon: "none"
        });
      }
    });
    const __returned__ = { userStore, userId, userInfo, activeTab, reviewList, categories, isFollowed, loading, hasMore, page, pageSize, getAvatarEmoji, formatTime, loadUserInfo, getCategoryIcon, loadReviews, loadMore, switchTab, goBack, toggleFollow, handleChat, ref: common_vendor.ref, onMounted: common_vendor.onMounted, get useUserStore() {
      return store_user.useUserStore;
    }, get userApi() {
      return api_index.userApi;
    }, get reviewApi() {
      return api_index.reviewApi;
    }, get categoryApi() {
      return api_index.categoryApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b;
  return common_vendor.e({
    a: common_vendor.o($setup.goBack),
    b: common_vendor.t($setup.getAvatarEmoji($setup.userInfo.id)),
    c: common_vendor.t($setup.userInfo.nickname || "用户"),
    d: common_vendor.t($setup.userInfo.bio || "暂无简介"),
    e: common_vendor.t($setup.userInfo.totalOrders || 0),
    f: common_vendor.t($setup.userInfo.score || 5),
    g: common_vendor.t($setup.userInfo.followerCount || 0),
    h: $setup.userStore.isLoggedIn && $setup.userInfo.id !== ((_a = $setup.userStore.userInfo) == null ? void 0 : _a.id)
  }, $setup.userStore.isLoggedIn && $setup.userInfo.id !== ((_b = $setup.userStore.userInfo) == null ? void 0 : _b.id) ? {
    i: common_vendor.t($setup.isFollowed ? "已关注" : "+ 关注"),
    j: common_vendor.n($setup.isFollowed ? "followed" : ""),
    k: common_vendor.o($setup.toggleFollow),
    l: common_vendor.o($setup.handleChat)
  } : {}, {
    m: common_vendor.n($setup.activeTab === "reviews" ? "active" : ""),
    n: common_vendor.o(($event) => $setup.switchTab("reviews")),
    o: common_vendor.n($setup.activeTab === "categories" ? "active" : ""),
    p: common_vendor.o(($event) => $setup.switchTab("categories")),
    q: $setup.activeTab === "reviews"
  }, $setup.activeTab === "reviews" ? common_vendor.e({
    r: common_vendor.f($setup.reviewList, (review, k0, i0) => {
      var _a2, _b2, _c, _d;
      return common_vendor.e({
        a: common_vendor.t($setup.getAvatarEmoji((_a2 = review.reviewer) == null ? void 0 : _a2.id)),
        b: common_vendor.t(((_b2 = review.reviewer) == null ? void 0 : _b2.nickname) || "用户"),
        c: common_vendor.f(5, (i, k1, i1) => {
          return {
            a: i,
            b: common_vendor.n(i <= review.rating ? "active" : "")
          };
        }),
        d: common_vendor.t($setup.formatTime(review.createdAt)),
        e: common_vendor.t(review.content),
        f: ((_c = review.images) == null ? void 0 : _c.length) > 0
      }, ((_d = review.images) == null ? void 0 : _d.length) > 0 ? {
        g: common_vendor.f(review.images, (img, index, i1) => {
          return {
            a: index
          };
        })
      } : {}, {
        h: review.replyContent
      }, review.replyContent ? {
        i: common_vendor.t(review.replyContent)
      } : {}, {
        j: review.id
      });
    }),
    s: $setup.reviewList.length === 0 && !$setup.loading
  }, $setup.reviewList.length === 0 && !$setup.loading ? {} : {}) : {}, {
    t: $setup.activeTab === "categories"
  }, $setup.activeTab === "categories" ? common_vendor.e({
    v: common_vendor.f($setup.categories, (cat, k0, i0) => {
      return {
        a: common_vendor.t(cat.icon),
        b: common_vendor.t(cat.name),
        c: cat.id
      };
    }),
    w: $setup.categories.length === 0
  }, $setup.categories.length === 0 ? {} : {}) : {}, {
    x: $setup.loading
  }, $setup.loading ? {} : {}, {
    y: !$setup.loading && !$setup.hasMore && $setup.reviewList.length > 0
  }, !$setup.loading && !$setup.hasMore && $setup.reviewList.length > 0 ? {} : {}, {
    z: common_vendor.o($setup.loadMore)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-0750bc74"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/user-profile/user-profile.vue"]]);
wx.createPage(MiniProgramPage);
