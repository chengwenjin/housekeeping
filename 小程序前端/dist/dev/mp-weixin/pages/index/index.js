"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "index",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const currentCity = common_vendor.ref("北京市朝阳区");
    const categories = common_vendor.ref([]);
    const selectedCategory = common_vendor.ref(null);
    const homeData = common_vendor.ref({});
    const demandList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const refreshing = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const page = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const showFilter = common_vendor.ref(false);
    const getAvatarEmoji = (id) => {
      const emojis = ["👩", "👨", "👩‍🦳", "👨‍🦳", "👩‍🍳", "👨‍⚕️"];
      return emojis[(id || 0) % emojis.length];
    };
    const loadCategories = async () => {
      try {
        const res = await api_index.categoryApi.getList();
        if (res.code === 200) {
          categories.value = res.data || [];
        }
      } catch (e) {
        console.error("加载分类失败", e);
        categories.value = [
          { id: 1, name: "保洁", icon: "🧹" },
          { id: 2, name: "烹饪", icon: "🍳" },
          { id: 3, name: "育儿", icon: "👶" },
          { id: 4, name: "老人", icon: "🧓" },
          { id: 5, name: "搬运", icon: "📦" },
          { id: 6, name: "宠物", icon: "🐾" },
          { id: 7, name: "维修", icon: "🔧" }
        ];
      }
    };
    const loadHomeData = async () => {
      try {
        const res = await api_index.homeApi.getData();
        if (res.code === 200) {
          homeData.value = res.data || {};
        }
      } catch (e) {
        console.error("加载首页数据失败", e);
        homeData.value = {
          banner: {
            title: "附近有3条新需求",
            subtitle: "快来抢单，收入翻倍！"
          }
        };
      }
    };
    const loadDemands = async (isRefresh = false) => {
      var _a, _b, _c;
      if (loading.value)
        return;
      if (isRefresh) {
        page.value = 1;
        hasMore.value = true;
        demandList.value = [];
      }
      if (!hasMore.value)
        return;
      loading.value = true;
      try {
        const params = {
          page: page.value,
          pageSize: pageSize.value,
          status: 1
        };
        if ((_a = selectedCategory.value) == null ? void 0 : _a.id) {
          params.categoryId = selectedCategory.value.id;
        }
        const res = await api_index.demandApi.getList(params);
        console.log('【调试】需求列表接口返回:', JSON.stringify(res));
        console.log('【调试】res.code:', res.code);
        console.log('【调试】res.data:', JSON.stringify(res.data));
        if (res.code === 200) {
          const newList = ((_b = res.data) == null ? void 0 : _b.list) || [];
          console.log('【调试】从 res.data.list 取到的数据条数:', newList.length);
          console.log('【调试】res.data 是否直接是数组:', Array.isArray(res.data));
          if (Array.isArray(res.data) && res.data.length > 0) {
            console.log('【调试】res.data 直接是数组，长度:', res.data.length);
          }
          if (isRefresh) {
            demandList.value = newList;
          } else {
            demandList.value = [...demandList.value, ...newList];
          }
          console.log('【调试】最终 demandList 长度:', demandList.value.length);
          const pagination = ((_c = res.data) == null ? void 0 : _c.pagination) || {};
          hasMore.value = page.value < pagination.totalPages;
          page.value++;
        }
      } catch (e) {
        console.error("加载需求列表失败", e);
      } finally {
        loading.value = false;
        refreshing.value = false;
      }
    };
    const onRefresh = () => {
      refreshing.value = true;
      loadDemands(true);
    };
    const loadMore = () => {
      if (!loading.value && hasMore.value) {
        loadDemands();
      }
    };
    const selectCategory = (cat) => {
      var _a;
      selectedCategory.value = ((_a = selectedCategory.value) == null ? void 0 : _a.id) === cat.id ? null : cat;
      loadDemands(true);
    };
    const selectCity = () => {
      common_vendor.index.showToast({
        title: "城市选择功能开发中",
        icon: "none"
      });
    };
    const goToSearch = () => {
      common_vendor.index.showToast({
        title: "搜索功能开发中",
        icon: "none"
      });
    };
    const goToMine = () => {
      if (userStore.isLoggedIn) {
        common_vendor.index.switchTab({
          url: "/pages/mine/mine"
        });
      } else {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
      }
    };
    const goToDemands = () => {
      common_vendor.index.switchTab({
        url: "/pages/orders/orders"
      });
    };
    const goToDetail = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/demand/detail?id=${id}`
      });
    };
    const takeOrder = async (demand) => {
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      common_vendor.index.showModal({
        title: "确认接单",
        content: `确定要接取"${demand.title}"这个需求吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "接单中..." });
              const result = await api_index.orderApi.takeOrder(demand.id, { remark: "" });
              common_vendor.index.hideLoading();
              if (result.code === 200) {
                common_vendor.index.showToast({
                  title: "接单成功",
                  icon: "success"
                });
                loadDemands(true);
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
      loadCategories();
      loadHomeData();
      loadDemands(true);
    });
    const __returned__ = { userStore, currentCity, categories, selectedCategory, homeData, demandList, loading, refreshing, hasMore, page, pageSize, showFilter, getAvatarEmoji, loadCategories, loadHomeData, loadDemands, onRefresh, loadMore, selectCategory, selectCity, goToSearch, goToMine, goToDemands, goToDetail, takeOrder, ref: common_vendor.ref, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get demandApi() {
      return api_index.demandApi;
    }, get categoryApi() {
      return api_index.categoryApi;
    }, get homeApi() {
      return api_index.homeApi;
    }, get orderApi() {
      return api_index.orderApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a, _b, _c;
  return common_vendor.e({
    a: common_vendor.t($setup.currentCity),
    b: common_vendor.o($setup.selectCity),
    c: (_a = $setup.userStore.userInfo) == null ? void 0 : _a.avatarUrl
  }, ((_b = $setup.userStore.userInfo) == null ? void 0 : _b.avatarUrl) ? {
    d: common_vendor.t($setup.userStore.userInfo.avatarUrl)
  } : {}, {
    e: common_vendor.o($setup.goToMine),
    f: common_vendor.o($setup.goToSearch),
    g: common_vendor.f($setup.categories, (cat, k0, i0) => {
      return {
        a: common_vendor.t(cat.icon),
        b: common_vendor.n("c" + (cat.id % 5 + 1)),
        c: common_vendor.t(cat.name),
        d: cat.id,
        e: common_vendor.o(($event) => $setup.selectCategory(cat), cat.id)
      };
    }),
    h: $setup.homeData.banner
  }, $setup.homeData.banner ? {
    i: common_vendor.t($setup.homeData.banner.title),
    j: common_vendor.t($setup.homeData.banner.subtitle),
    k: common_vendor.o($setup.goToDemands)
  } : {}, {
    l: common_vendor.t(((_c = $setup.selectedCategory) == null ? void 0 : _c.name) || "全部"),
    m: common_vendor.o(($event) => $setup.showFilter = true),
    n: common_vendor.f($setup.demandList, (demand, k0, i0) => {
      var _a2, _b2, _c2, _d, _e, _f;
      return common_vendor.e({
        a: common_vendor.t($setup.getAvatarEmoji((_a2 = demand.publisher) == null ? void 0 : _a2.id)),
        b: common_vendor.n("ua" + ((((_b2 = demand.publisher) == null ? void 0 : _b2.id) || 1) % 4 + 1)),
        c: common_vendor.t(((_c2 = demand.publisher) == null ? void 0 : _c2.nickname) || "用户"),
        d: common_vendor.t(demand.createdAtText),
        e: common_vendor.t(((_d = demand.location) == null ? void 0 : _d.district) || ""),
        f: common_vendor.t(demand.statusText),
        g: common_vendor.n(demand.status === 1 ? "ds-open" : "ds-taken"),
        h: common_vendor.t(demand.title),
        i: common_vendor.t(demand.description),
        j: (_e = demand.location) == null ? void 0 : _e.distance
      }, ((_f = demand.location) == null ? void 0 : _f.distance) ? {
        k: common_vendor.t(demand.location.distance)
      } : {}, {
        l: demand.serviceTimeDesc
      }, demand.serviceTimeDesc ? {
        m: common_vendor.t(demand.serviceTimeDesc)
      } : {}, {
        n: demand.minDuration
      }, demand.minDuration ? {
        o: common_vendor.t(demand.minDuration),
        p: common_vendor.t(demand.maxDuration)
      } : {}, {
        q: common_vendor.t(demand.expectedPrice),
        r: common_vendor.t(demand.priceUnit || "小时"),
        s: demand.status === 1
      }, demand.status === 1 ? {
        t: common_vendor.o(($event) => $setup.takeOrder(demand), demand.id)
      } : {
        v: common_vendor.t(demand.statusText)
      }, {
        w: demand.id,
        x: common_vendor.o(($event) => $setup.goToDetail(demand.id), demand.id)
      });
    }),
    o: $setup.loading
  }, $setup.loading ? {} : {}, {
    p: !$setup.loading && $setup.demandList.length === 0
  }, !$setup.loading && $setup.demandList.length === 0 ? {} : {}, {
    q: !$setup.loading && !$setup.hasMore && $setup.demandList.length > 0
  }, !$setup.loading && !$setup.hasMore && $setup.demandList.length > 0 ? {} : {}, {
    r: common_vendor.o($setup.loadMore),
    s: $setup.refreshing,
    t: common_vendor.o($setup.onRefresh)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-83a5a03c"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/index/index.vue"]]);
wx.createPage(MiniProgramPage);
