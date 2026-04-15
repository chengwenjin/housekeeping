"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "publish",
  setup(__props, { expose: __expose }) {
    __expose();
    const userStore = store_user.useUserStore();
    const categories = common_vendor.ref([]);
    const selectedCategory = common_vendor.ref(null);
    const imageList = common_vendor.ref([]);
    const submitting = common_vendor.ref(false);
    const priceUnitIndex = common_vendor.ref(0);
    const priceUnits = [
      { id: 1, name: "元/小时", value: "小时" },
      { id: 2, name: "元/次", value: "次" },
      { id: 3, name: "元/天", value: "天" },
      { id: 4, name: "元/月", value: "月" }
    ];
    const timeRange = [
      ["今天", "明天", "后天", "本周", "下周"],
      ["上午", "下午", "晚上", "全天"]
    ];
    const timeValue = common_vendor.ref([0, 0]);
    const formData = common_vendor.reactive({
      categoryId: null,
      title: "",
      description: "",
      expectedPrice: "",
      priceUnit: "小时",
      minDuration: "",
      maxDuration: "",
      serviceTimeDesc: "",
      province: "北京市",
      city: "北京市",
      district: "",
      address: "",
      latitude: 39.9042,
      longitude: 116.4074,
      contactName: "",
      contactPhone: "",
      imageUrls: []
    });
    const getRandomEmoji = () => {
      const emojis = ["🏠", "🛁", "🍳", "🧹", "👶", "🧓"];
      return emojis[Math.floor(Math.random() * emojis.length)];
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
          { id: 7, name: "维修", icon: "🔧" },
          { id: 8, name: "更多", icon: "➕" }
        ];
      }
    };
    const selectCategory = (cat) => {
      var _a, _b;
      selectedCategory.value = ((_a = selectedCategory.value) == null ? void 0 : _a.id) === cat.id ? null : cat;
      formData.categoryId = ((_b = selectedCategory.value) == null ? void 0 : _b.id) || null;
    };
    const onPriceUnitChange = (e) => {
      priceUnitIndex.value = e.detail.value;
      formData.priceUnit = priceUnits[e.detail.value].value;
    };
    const onTimeChange = (e) => {
      timeValue.value = e.detail.value;
      const day = timeRange[0][e.detail.value[0]];
      const period = timeRange[1][e.detail.value[1]];
      formData.serviceTimeDesc = `${day} ${period}`;
    };
    const selectLocation = () => {
      common_vendor.index.showActionSheet({
        itemList: ["朝阳区", "海淀区", "西城区", "东城区", "丰台区", "石景山区"],
        success: (res) => {
          const districts = ["朝阳区", "海淀区", "西城区", "东城区", "丰台区", "石景山区"];
          formData.district = districts[res.tapIndex];
        }
      });
    };
    const chooseImage = () => {
      common_vendor.index.chooseImage({
        count: 9 - imageList.value.length,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: async (res) => {
          const tempFilePaths = res.tempFilePaths;
          for (const path of tempFilePaths) {
            try {
              imageList.value.push(path);
            } catch (e) {
              console.error("上传图片失败", e);
            }
          }
        }
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const validateForm = () => {
      if (!formData.categoryId) {
        common_vendor.index.showToast({ title: "请选择服务类型", icon: "none" });
        return false;
      }
      if (!formData.title.trim()) {
        common_vendor.index.showToast({ title: "请输入需求标题", icon: "none" });
        return false;
      }
      if (!formData.expectedPrice) {
        common_vendor.index.showToast({ title: "请输入预期报酬", icon: "none" });
        return false;
      }
      if (!formData.district) {
        common_vendor.index.showToast({ title: "请选择服务地区", icon: "none" });
        return false;
      }
      if (!formData.address.trim()) {
        common_vendor.index.showToast({ title: "请输入详细地址", icon: "none" });
        return false;
      }
      return true;
    };
    const submitForm = async () => {
      if (!userStore.isLoggedIn) {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
        return;
      }
      if (!validateForm())
        return;
      submitting.value = true;
      try {
        common_vendor.index.showLoading({ title: "发布中..." });
        const submitData = {
          categoryId: formData.categoryId,
          title: formData.title.trim(),
          description: formData.description.trim(),
          expectedPrice: parseFloat(formData.expectedPrice) || 0,
          priceUnit: formData.priceUnit,
          minDuration: formData.minDuration ? parseFloat(formData.minDuration) : null,
          maxDuration: formData.maxDuration ? parseFloat(formData.maxDuration) : null,
          serviceTimeDesc: formData.serviceTimeDesc || "待定",
          province: formData.province,
          city: formData.city,
          district: formData.district,
          address: formData.address.trim(),
          latitude: formData.latitude,
          longitude: formData.longitude,
          contactName: formData.contactName.trim() || "用户",
          contactPhone: formData.contactPhone.trim() || "",
          imageUrls: formData.imageUrls
        };
        const res = await api_index.demandApi.create(submitData);
        common_vendor.index.hideLoading();
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "发布成功",
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
        submitting.value = false;
        console.error("发布失败", e);
      }
    };
    common_vendor.onMounted(() => {
      loadCategories();
    });
    const __returned__ = { userStore, categories, selectedCategory, imageList, submitting, priceUnitIndex, priceUnits, timeRange, timeValue, formData, getRandomEmoji, loadCategories, selectCategory, onPriceUnitChange, onTimeChange, selectLocation, chooseImage, goBack, validateForm, submitForm, ref: common_vendor.ref, reactive: common_vendor.reactive, onMounted: common_vendor.onMounted, computed: common_vendor.computed, get useUserStore() {
      return store_user.useUserStore;
    }, get demandApi() {
      return api_index.demandApi;
    }, get categoryApi() {
      return api_index.categoryApi;
    }, get uploadApi() {
      return api_index.uploadApi;
    } };
    Object.defineProperty(__returned__, "__isScriptSetup", { enumerable: false, value: true });
    return __returned__;
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o($setup.goBack),
    b: common_vendor.f($setup.categories, (cat, k0, i0) => {
      var _a;
      return {
        a: common_vendor.t(cat.icon),
        b: common_vendor.t(cat.name),
        c: cat.id,
        d: common_vendor.n(((_a = $setup.selectedCategory) == null ? void 0 : _a.id) === cat.id ? "selected" : ""),
        e: common_vendor.o(($event) => $setup.selectCategory(cat), cat.id)
      };
    }),
    c: $setup.formData.title,
    d: common_vendor.o(($event) => $setup.formData.title = $event.detail.value),
    e: $setup.formData.description,
    f: common_vendor.o(($event) => $setup.formData.description = $event.detail.value),
    g: $setup.formData.expectedPrice,
    h: common_vendor.o(($event) => $setup.formData.expectedPrice = $event.detail.value),
    i: common_vendor.t($setup.priceUnits[$setup.priceUnitIndex].name),
    j: $setup.priceUnitIndex,
    k: $setup.priceUnits,
    l: common_vendor.o($setup.onPriceUnitChange),
    m: $setup.formData.minDuration,
    n: common_vendor.o(($event) => $setup.formData.minDuration = $event.detail.value),
    o: $setup.formData.maxDuration,
    p: common_vendor.o(($event) => $setup.formData.maxDuration = $event.detail.value),
    q: $setup.formData.serviceTimeDesc
  }, $setup.formData.serviceTimeDesc ? {
    r: common_vendor.t($setup.formData.serviceTimeDesc)
  } : {}, {
    s: $setup.timeValue,
    t: $setup.timeRange,
    v: common_vendor.o($setup.onTimeChange),
    w: $setup.formData.district
  }, $setup.formData.district ? {
    x: common_vendor.t($setup.formData.city),
    y: common_vendor.t($setup.formData.district)
  } : {}, {
    z: common_vendor.o($setup.selectLocation),
    A: $setup.formData.address,
    B: common_vendor.o(($event) => $setup.formData.address = $event.detail.value),
    C: common_vendor.f($setup.imageList, (img, index, i0) => {
      return {
        a: index
      };
    }),
    D: common_vendor.t($setup.getRandomEmoji()),
    E: $setup.imageList.length < 9
  }, $setup.imageList.length < 9 ? {
    F: common_vendor.o($setup.chooseImage)
  } : {}, {
    G: $setup.formData.contactName,
    H: common_vendor.o(($event) => $setup.formData.contactName = $event.detail.value),
    I: $setup.formData.contactPhone,
    J: common_vendor.o(($event) => $setup.formData.contactPhone = $event.detail.value),
    K: $setup.submitting
  }, $setup.submitting ? {} : {}, {
    L: common_vendor.o($setup.submitForm),
    M: $setup.submitting
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-7d70bb1b"], ["__file", "F:/AiCoding/traeWork/小程序前端/src/pages/publish/publish.vue"]]);
wx.createPage(MiniProgramPage);
