"use strict";
const common_vendor = require("../../common/vendor.js");
const __default__ = {
  data() {
    return {};
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "center",
  setup(__props) {
    const toCenter = () => {
      common_vendor.index.navigateTo({
        url: "/pages/order/order"
      });
    };
    const userInfo = common_vendor.ref({});
    common_vendor.onMounted(() => {
      userInfo.value = common_vendor.index.getStorageSync("userInfo");
    });
    return (_ctx, _cache) => {
      return {
        a: common_vendor.t(userInfo.value.nickname),
        b: common_vendor.t(userInfo.value.depositStatus === "0" ? "未冲押金" : "免押金"),
        c: common_vendor.o(toCenter)
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-3f122818"], ["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/center/center.vue"]]);
wx.createPage(MiniProgramPage);
