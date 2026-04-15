"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080";
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = common_vendor.index.getStorageSync("token");
    common_vendor.index.request({
      url: BASE_URL + options.url,
      method: options.method || "GET",
      data: options.data || {},
      header: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
        ...token ? { "Authorization": `Bearer ${token}` } : {},
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data);
          } else if (res.data.code === 401 || res.data.code === 10002 || res.data.code === 10003) {
            common_vendor.index.removeStorageSync("token");
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.reLaunch({
              url: "/pages/login/login"
            });
            reject(new Error("登录已过期，请重新登录"));
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "请求失败",
              icon: "none"
            });
            reject(new Error(res.data.message));
          }
        } else {
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
          reject(new Error("网络错误"));
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络连接失败",
          icon: "none"
        });
        reject(err);
      }
    });
  });
};
const get = (url, data) => {
  return request({
    url,
    method: "GET",
    data
  });
};
const post = (url, data) => {
  return request({
    url,
    method: "POST",
    data
  });
};
const put = (url, data) => {
  return request({
    url,
    method: "PUT",
    data
  });
};
const del = (url, data) => {
  return request({
    url,
    method: "DELETE",
    data
  });
};
const uploadFile = (url, filePath, name = "file", formData = {}) => {
  return new Promise((resolve, reject) => {
    const token = common_vendor.index.getStorageSync("token");
    common_vendor.index.uploadFile({
      url: BASE_URL + url,
      filePath,
      name,
      formData,
      header: {
        ...token ? { "Authorization": `Bearer ${token}` } : {}
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(data);
          } else {
            reject(new Error(data.message));
          }
        } else {
          reject(new Error("上传失败"));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};
exports.del = del;
exports.get = get;
exports.post = post;
exports.put = put;
exports.uploadFile = uploadFile;
