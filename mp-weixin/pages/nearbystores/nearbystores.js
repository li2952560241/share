"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const __default__ = {
  data() {
    return {};
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "nearbystores",
  setup(__props) {
    const toDetail = (near) => {
      common_vendor.index.navigateTo({
        url: "/pages/detail/detail?near=" + JSON.stringify(near)
      });
    };
    var QQMapWX = require("../../static/js/qqmap-wx-jssdk.min.js");
    new QQMapWX({
      key: "BGBBZ-S3ZWI-6GIGD-UUYSX-5WSQO-ZSBQJ"
      // 必填
    });
    const centerPosition = common_vendor.ref(
      { latitude: 39.984104, longitude: 116.307503 }
    );
    const markers = common_vendor.ref([
      {
        id: 0,
        // latitude: res.result.location.lat,
        // longitude: res.result.location.lng,
        latitude: "39.984104",
        longitude: "116.307503",
        // iconPath: "/resources/my_marker.png",
        width: 30,
        height: 30,
        title: "中心点"
      }
    ]);
    const scollViewId = common_vendor.ref("");
    const clickMark = (e) => {
      console.log(e);
      scollViewId.value = e.markerId;
      currentItemId.value = e.markerId;
    };
    const currentItemId = common_vendor.ref("");
    const nearbyList = common_vendor.ref([]);
    const getNearbyStation = async (latitude = "39.984104", longitude = "116.307503") => {
      const result = await utils_request.request(`/device/device/nearbyStation/${latitude}/${longitude}`);
      if (result.code === 200) {
        console.log(result.data);
        nearbyList.value = result.data;
        currentItemId.value = nearbyList.value[0].id;
        var mks = [];
        for (var i = 0; i < nearbyList.value.length; i++) {
          mks.push({
            // 获取返回结果，放到mks数组中
            title: nearbyList.value[i].name,
            id: nearbyList.value[i].id,
            latitude: nearbyList.value[i].latitude,
            longitude: nearbyList.value[i].longitude,
            iconPath: "../../static/images/Flag.png",
            //图标路径
            width: 20,
            height: 20
          });
        }
        markers.value = [...markers.value, ...mks];
      } else {
        common_vendor.index.showToast({
          title: "获取附近门店失败"
        });
      }
    };
    common_vendor.onMounted(() => {
      getNearbyStation();
    });
    return (_ctx, _cache) => {
      return {
        a: markers.value,
        b: centerPosition.value.latitude,
        c: centerPosition.value.longitude,
        d: common_vendor.o(clickMark),
        e: common_vendor.f(nearbyList.value, (near, k0, i0) => {
          return {
            a: near.imageUrl,
            b: common_vendor.t(near.name),
            c: common_vendor.t(near.fullAddress),
            d: common_vendor.t(near.businessHours),
            e: common_vendor.t(near.distance),
            f: common_vendor.t(near.isUsable === "1" ? "可借" : "不可借"),
            g: common_vendor.t(near.isReturn === "1" ? "可还" : "不可还"),
            h: common_vendor.o(($event) => toDetail(near), near.id),
            i: near.id,
            j: "scroll" + near.id,
            k: common_vendor.n(near.id === currentItemId.value && "active")
          };
        }),
        f: "scroll" + scollViewId.value
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/nearbystores/nearbystores.vue"]]);
wx.createPage(MiniProgramPage);
