"use strict";
const utils_request = require("../utils/request.js");
const authApi = {
  login: (data) => utils_request.post("/api/mini/auth/login", data),
  refreshToken: (refreshToken) => utils_request.post("/api/mini/auth/refresh", { refreshToken }),
  bindPhone: (data) => utils_request.post("/api/mini/user/bind-phone", data)
};
const userApi = {
  getProfile: () => utils_request.get("/api/mini/user/profile"),
  updateProfile: (data) => utils_request.put("/api/mini/user/profile", data),
  getUserInfo: (id) => utils_request.get(`/api/mini/user/${id}`),
  follow: (id) => utils_request.post(`/api/mini/user/${id}/follow`),
  unfollow: (id) => utils_request.del(`/api/mini/user/${id}/follow`),
  getFollowing: (params) => utils_request.get("/api/mini/user/following", params),
  getFollowers: (params) => utils_request.get("/api/mini/user/follower", params)
};
const demandApi = {
  getList: (params) => utils_request.get("/api/mini/demands", params),
  getDetail: (id) => utils_request.get(`/api/mini/demands/${id}`),
  create: (data) => utils_request.post("/api/mini/demands", data),
  update: (id, data) => utils_request.put(`/api/mini/demands/${id}`, data),
  delete: (id) => utils_request.del(`/api/mini/demands/${id}`),
  addFootprint: (id) => utils_request.post(`/api/mini/demands/${id}/footprint`)
};
const orderApi = {
  takeOrder: (demandId, data) => utils_request.post(`/api/mini/orders/${demandId}/take`, data),
  getPublished: (params) => utils_request.get("/api/mini/orders/published", params),
  getTaken: (params) => utils_request.get("/api/mini/orders/taken", params),
  getDetail: (id) => utils_request.get(`/api/mini/orders/${id}`),
  updateStatus: (id, data) => utils_request.put(`/api/mini/orders/${id}/status`, data),
  cancel: (id, data) => utils_request.post(`/api/mini/orders/${id}/cancel`, data)
};
const reviewApi = {
  create: (data) => utils_request.post("/api/review", data),
  getUserReviews: (userId, params) => utils_request.get(`/api/mini/reviews/user/${userId}`, params)
};
const footprintApi = {
  getList: (params) => utils_request.get("/api/mini/footprints", params),
  clear: (ids) => utils_request.del("/api/mini/footprints", { ids })
};
const messageApi = {
  getList: (params) => utils_request.get("/api/mini/messages", params),
  markRead: (id) => utils_request.put(`/api/mini/message/${id}/read`),
  markAllRead: () => utils_request.put("/api/mini/messages/read-all")
};
const categoryApi = {
  getList: () => utils_request.get("/api/mini/categories")
};
const homeApi = {
  getData: () => utils_request.get("/api/mini/home")
};
const uploadApi = {
  uploadImage: (filePath, type = "demand") => {
    return utils_request.uploadFile("/api/mini/upload/image", filePath, "file", { type });
  }
};
exports.authApi = authApi;
exports.categoryApi = categoryApi;
exports.demandApi = demandApi;
exports.footprintApi = footprintApi;
exports.homeApi = homeApi;
exports.messageApi = messageApi;
exports.orderApi = orderApi;
exports.reviewApi = reviewApi;
exports.uploadApi = uploadApi;
exports.userApi = userApi;
