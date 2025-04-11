"use strict";
const common_vendor = require("../common/vendor.js");
function request(url, data = {}, method = "GET") {
  return new Promise((resolve, reject) => {
    common_vendor.index.request({
      url: "http://localhost:18080" + url,
      method,
      data,
      header: {
        "Authorization": "Bearer " + common_vendor.index.getStorageSync("token")
      },
      success: (res) => {
        resolve(res.data);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}
exports.request = request;
