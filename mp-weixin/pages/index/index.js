"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
if (!Array) {
  const _component_mask = common_vendor.resolveComponent("mask");
  _component_mask();
}
const __default__ = {
  data() {
    return {
      title: "Hello"
    };
  },
  onLoad() {
  },
  methods: {}
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "index",
  setup(__props) {
    var QQMapWX = require("../../static/js/qqmap-wx-jssdk.min.js");
    new QQMapWX({
      key: "BGBBZ-S3ZWI-6GIGD-UUYSX-5WSQO-ZSBQJ"
      // 必填
    });
    common_vendor.ref({});
    const markers = common_vendor.ref([
      {
        id: 0,
        // latitude: res.result.location.lat,
        // longitude: res.result.location.lng,
        latitude: 39.984104,
        longitude: 116.307503,
        // iconPath: "/resources/my_marker.png",
        width: 30,
        height: 30,
        title: "中心点"
      }
    ]);
    common_vendor.onMounted(() => {
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
          common_vendor.index.navigateTo({
            url: "/pages/index/index"
          });
        }
      });
    };
    const toNear = () => {
      common_vendor.index.navigateTo({
        url: "/pages/nearbystores/nearbystores"
      });
    };
    const toCenter = () => {
      common_vendor.index.navigateTo({
        url: "/pages/center/center"
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: markers.value,
        b: common_vendor.o(toNear),
        c: common_vendor.o(scan),
        d: common_vendor.o(toCenter),
        e: isShowMask.value
      }, isShowMask.value ? {
        f: common_vendor.o(_ctx.hideMask)
      } : {});
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/index/index.vue"]]);
wx.createPage(MiniProgramPage);
