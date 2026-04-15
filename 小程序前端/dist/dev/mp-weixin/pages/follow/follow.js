"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "follow",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const activeTab = common_vendor.ref("footprint");
    const footprintList = common_vendor.ref([]);
    const followingList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const page = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const footprintGroups = common_vendor.computed(() => {
      const groups = {};
      const today = (/* @__PURE__ */ new Date()).toDateString();
      const yesterday = new Date(Date.now() - 864e5).toDateString();
      footprintList.value.forEach((item) => {
        const itemDate = new Date(item.browsedAt || item.createdAt).toDateString();
        let groupKey = "更早";
        if (itemDate === today) {
          groupKey = "今天";
        } else if (itemDate === yesterday) {
          groupKey = "昨天";
        }
        if (!groups[groupKey]) {
          groups[groupKey] = { date: groupKey, items: [] };
        }
        groups[groupKey].items.push(item);
      });
      return Object.values(groups);
    });
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
    const getAvatarEmoji = (id) => {
      const emojis = ["👩", "👨", "👩‍🍳", "👨‍⚕️", "🧹", "👶", "👩‍🦳", "👨‍🦳"];
      return emojis[(id || 0) % emojis.length];
    };
    const switchTab = (tab) => {
      activeTab.value = tab;
      page.value = 1;
      hasMore.value = true;
      if (tab === "footprint") {
        footprintList.value = [];
        loadFootprints();
      } else {
        followingList.value = [];
        loadFollowing();
      }
    };
    const loadFootprints = async () => {
      var _a, _b;
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      try {
        const res = await api_index.footprintApi.getList({
          page: page.value,
          pageSize: pageSize.value
        });
        if (res.code === 200) {
          const newList = ((_a = res.data) == null ? void 0 : _a.list) || [];
          footprintList.value = [...footprintList.value, ...newList];
          const pagination = ((_b = res.data) == null ? void 0 : _b.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载足迹失败", e);
      } finally {
        loading.value = false;
      }
    };
    const loadFollowing = async () => {
      var _a, _b;
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      if (loading.value || !hasMore.value)
        return;
      loading.value = true;
      try {
        const res = await api_index.userApi.getFollowing({
          page: page.value,
          pageSize: pageSize.value
        });
        if (res.code === 200) {
          const newList = ((_a = res.data) == null ? void 0 : _a.list) || [];
          followingList.value = [...followingList.value, ...newList.map((item) => ({ ...item, isFollowed: true }))];
          const pagination = ((_b = res.data) == null ? void 0 : _b.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载关注列表失败", e);
      } finally {
        loading.value = false;
      }
    };
    const loadMore = () => {
      if (!loading.value && hasMore.value) {
        if (activeTab.value === "footprint") {
          loadFootprints();
        } else {
          loadFollowing();
        }
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const goToDemand = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/demand/detail?id=${id}`
      });
    };
    const goToUserProfile = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/user-profile/user-profile?id=${id}`
      });
    };
    const toggleFollow = async (user) => {
      try {
        if (user.isFollowed) {
          const res = await api_index.userApi.unfollow(user.id);
          if (res.code === 200) {
            user.isFollowed = false;
            common_vendor.index.showToast({
              title: "已取消关注",
              icon: "success"
            });
          }
        } else {
          const res = await api_index.userApi.follow(user.id);
          if (res.code === 200) {
            user.isFollowed = true;
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
    common_vendor.onMounted(() => {
      var _a;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const tab = (_a = currentPage.options) == null ? void 0 : _a.tab;
      if (tab === "following") {
        activeTab.value = "following";
        loadFollowing();
      } else {
        activeTab.value = "footprint";
        loadFootprints();
      }
    });
    const __returned__ = { userStore, activeTab, footprintList, followingList, loading, hasMore, page, pageSize, footprintGroups, getCategoryIcon, getAvatarEmoji, switchTab, loadFootprints, loadFollowing, loadMore, goBack, goToDemand, goToUserProfile, toggleFollow, ref: common_vendor.ref, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get footprintApi() {
      return api_index.footprintApi;
    }, get userApi() {
      return api_index.userApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o($setup.goBack),
    b: common_vendor.n($setup.activeTab === "footprint" ? "active" : ""),
    c: common_vendor.o(($event) => $setup.switchTab("footprint")),
    d: common_vendor.n($setup.activeTab === "following" ? "active" : ""),
    e: common_vendor.o(($event) => $setup.switchTab("following")),
    f: $setup.activeTab === "footprint"
  }, $setup.activeTab === "footprint" ? common_vendor.e({
    g: common_vendor.f($setup.footprintGroups, (group, k0, i0) => {
      return {
        a: common_vendor.t(group.date),
        b: common_vendor.f(group.items, (item, k1, i1) => {
          var _a, _b;
          return {
            a: common_vendor.t($setup.getCategoryIcon((_a = item.category) == null ? void 0 : _a.id)),
            b: common_vendor.t(item.title),
            c: common_vendor.t(((_b = item.location) == null ? void 0 : _b.district) || ""),
            d: common_vendor.t(item.browsedAtText || ""),
            e: common_vendor.t(item.expectedPrice || 0),
            f: common_vendor.t(item.priceUnit || "时"),
            g: item.id,
            h: common_vendor.o(($event) => $setup.goToDemand(item.id), item.id)
          };
        }),
        c: group.date
      };
    }),
    h: $setup.footprintList.length === 0 && !$setup.loading
  }, $setup.footprintList.length === 0 && !$setup.loading ? {} : {}) : {}, {
    i: $setup.activeTab === "following"
  }, $setup.activeTab === "following" ? common_vendor.e({
    j: common_vendor.f($setup.followingList, (user, k0, i0) => {
      return {
        a: common_vendor.t($setup.getAvatarEmoji(user.id)),
        b: common_vendor.o(($event) => $setup.goToUserProfile(user.id), user.id),
        c: common_vendor.t(user.nickname || "用户"),
        d: common_vendor.t(user.bio || "暂无简介"),
        e: common_vendor.t(user.totalOrders || 0),
        f: common_vendor.t(user.score || 5),
        g: common_vendor.o(($event) => $setup.goToUserProfile(user.id), user.id),
        h: common_vendor.t(user.isFollowed ? "已关注" : "+ 关注"),
        i: common_vendor.n(user.isFollowed ? "followed" : ""),
        j: common_vendor.o(($event) => $setup.toggleFollow(user), user.id),
        k: user.id
      };
    }),
    k: $setup.followingList.length === 0 && !$setup.loading
  }, $setup.followingList.length === 0 && !$setup.loading ? {} : {}) : {}, {
    l: $setup.loading
  }, $setup.loading ? {} : {}, {
    m: !$setup.loading && !$setup.hasMore && ($setup.footprintList.length > 0 || $setup.followingList.length > 0)
  }, !$setup.loading && !$setup.hasMore && ($setup.footprintList.length > 0 || $setup.followingList.length > 0) ? {} : {}, {
    n: common_vendor.o($setup.loadMore)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-b81f2386"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/follow/follow.vue"]]);
wx.createPage(MiniProgramPage);
