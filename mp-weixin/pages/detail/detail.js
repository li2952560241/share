"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
if (!Array) {
  const _component_mask = common_vendor.resolveComponent("mask");
  _component_mask();
}
const __default__ = {
  data() {
    return {};
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "detail",
  setup(__props) {
    const currentNear = common_vendor.ref({});
    const nearDetail = common_vendor.ref({});
    common_vendor.onLoad((option) => {
      currentNear.value = JSON.parse(option.near);
      console.log(currentNear.value);
    });
    const getNearDetail = async () => {
      const result = await utils_request.request(`/device/device/getStation/${currentNear.value.id}/${currentNear.value.latitude}/${currentNear.value.longitude}`);
      if (result.code === 200) {
        nearDetail.value = result.data;
      } else {
        console.log("获取门店详情失败");
      }
    };
    common_vendor.onMounted(() => {
      getNearDetail();
    });
    const isShowMask = common_vendor.ref(false);
    let timer = null;
    const scan = () => {
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo.depositStatus === "0") {
        common_vendor.index.navigateTo({
          url: "/pages/verify/verify"
        });
        return;
      }
      common_vendor.index.showLoading({
        title: "正在扫描"
      });
      isShowMask.value = true;
      common_vendor.index.scanCode({
        success: async (res) => {
          let cabinetNo = res.result;
          const result = await utils_request.request(`/device/device/scanCharge/${cabinetNo}`);
          console.log(result);
          if (result.code === 200) {
            if (result.data.status === "1") {
              if (!timer) {
                timer = setInterval(async () => {
                  const status1 = await utils_request.request("/order/orderInfo/getNoFinishOrder");
                  if (status1.code === 200 && status1.data.id) {
                    console.log(status1.data.id);
                    clearInterval(timer);
                    timer = null;
                    common_vendor.index.navigateTo({
                      url: "/pages/orderDetail/orderDetail?orderId=" + status1.data.id
                    });
                    common_vendor.index.hideLoading();
                    isShowMask.value = false;
                  }
                }, 2e3);
              }
            } else if (result.data.status === "2") {
              common_vendor.index.showToast({
                title: result.data.message,
                duration: 3e3
              });
              common_vendor.index.hideLoading();
              isShowMask.value = false;
            } else if (result.data.status === "3") {
              const status3 = await utils_request.request("/order/orderInfo/getNoFinishOrder");
              if (status3.code === 200 && status3.data.id) {
                console.log(status3.data.id);
                clearInterval(timer);
                timer = null;
                common_vendor.index.navigateTo({
                  url: "/pages/orderDetail/orderDetail?orderId=" + status3.data.id
                });
                common_vendor.index.hideLoading();
                isShowMask.value = false;
              }
            }
          } else {
            common_vendor.index.showToast({
              title: result.msg,
              duration: 3e3
            });
            common_vendor.index.hideLoading();
            isShowMask.value = false;
          }
        },
        fail: (err) => {
          console.error("扫描失败：" + err);
          common_vendor.index.showToast({
            title: "扫描失败",
            icon: "none"
          });
        }
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: nearDetail.value.imageUrl,
        b: common_vendor.t(nearDetail.value.name),
        c: common_vendor.t(nearDetail.value.fullAddress),
        d: common_vendor.t(nearDetail.value.businessHours),
        e: common_vendor.t(nearDetail.value.feeRule),
        f: common_vendor.t(nearDetail.value.isUsable === "1" ? "可借" : "不可借"),
        g: common_vendor.t(nearDetail.value.isReturn === "1" ? "可还" : "不可还"),
        h: common_vendor.o(scan),
        i: common_vendor.t(nearDetail.value.distance),
        j: isShowMask.value
      }, isShowMask.value ? {
        k: common_vendor.o(_ctx.hideMask)
      } : {});
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/detail/detail.vue"]]);
wx.createPage(MiniProgramPage);
