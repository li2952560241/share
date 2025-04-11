"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
const utils_request = require("./utils/request.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/nearbystores/nearbystores.js";
  "./pages/detail/detail.js";
  "./pages/center/center.js";
  "./pages/order/order.js";
  "./pages/orderDetail/orderDetail.js";
  "./pages/verify/verify.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.login({
      provider: "weixin",
      //使用微信登录
      success: async function(loginRes) {
        let code = loginRes.code;
        const result = await utils_request.request(`/auth/h5/login/${code}`);
        if (result.code === 200) {
          console.log(result.data);
          common_vendor.index.setStorageSync("token", result.data.access_token);
          const userResult = await utils_request.request("/user/userInfo/getLoginUserInfo");
          if (userResult.code === 200) {
            common_vendor.index.setStorageSync("userInfo", userResult.data);
          } else {
            common_vendor.index.showToast({
              title: "获取用户信息失败" + userResult.msg
            });
          }
        } else {
          common_vendor.index.showToast({
            title: "授权登录失败" + result.msg
          });
        }
      }
    });
  },
  onShow: function() {
    console.log("App Show");
  },
  onHide: function() {
    console.log("App Hide");
  }
};
const App = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/App.vue"]]);
function createApp() {
  const app = common_vendor.createSSRApp(App);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
