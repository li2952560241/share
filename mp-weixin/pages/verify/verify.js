"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const __default__ = {
  data() {
    return {};
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "verify",
  setup(__props) {
    const verify = async () => {
      console.log(111);
      const result = await utils_request.request("/user/userInfo/isFreeDeposit");
      if (result.code === 200) {
        const userResult = await utils_request.request("/user/userInfo/getLoginUserInfo");
        if (userResult.code === 200) {
          common_vendor.index.setStorageSync("userInfo", userResult.data);
          common_vendor.index.navigateBack();
        } else {
          common_vendor.index.showToast({
            title: "获取用户信息失败" + userResult.msg
          });
        }
      } else {
        common_vendor.index.showToast({
          title: "验证失败" + result.msg
        });
      }
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(verify)
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/verify/verify.vue"]]);
wx.createPage(MiniProgramPage);
