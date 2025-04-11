"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const __default__ = {
  data() {
    return {};
  }
};
const _sfc_main = /* @__PURE__ */ Object.assign(__default__, {
  __name: "order",
  setup(__props) {
    const toDetail = (orderId) => {
      common_vendor.index.navigateTo({
        url: "/pages/orderDetail/orderDetail?orderId=" + orderId
      });
    };
    const orderList = common_vendor.ref([]);
    const pageNum = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const total = common_vendor.ref(1);
    const getOrderList = async () => {
      if (orderList.value.length >= total.value)
        return;
      const result = await utils_request.request(`/order/orderInfo/userOrderInfoList/${pageNum.value}/${pageSize.value}`);
      console.log(result);
      if (result.code === 200) {
        pageNum.value++;
        orderList.value = [...orderList.value, ...result.rows];
        total.value = result.total;
      }
    };
    common_vendor.onMounted(() => {
      getOrderList();
    });
    const scrolltolower = () => {
      getOrderList();
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(orderList.value, (order, k0, i0) => {
          return common_vendor.e({
            a: order.status === "0"
          }, order.status === "0" ? {} : {}, {
            b: order.status === "1"
          }, order.status === "1" ? {} : {}, {
            c: order.status === "2"
          }, order.status === "2" ? {} : {}, {
            d: common_vendor.t(order.startTime),
            e: common_vendor.t(order.startStationName),
            f: common_vendor.t(order.duration),
            g: common_vendor.t(order.totalAmount),
            h: common_vendor.o(($event) => toDetail(order.id), order.id),
            i: order.id
          });
        }),
        b: common_vendor.o(scrolltolower)
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__file", "C:/Users/liuyuan/Desktop/test/guli-chongdian/pages/order/order.vue"]]);
wx.createPage(MiniProgramPage);
