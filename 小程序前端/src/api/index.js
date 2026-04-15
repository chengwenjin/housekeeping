import { get, post, put, del, uploadFile } from '@/utils/request'

export const authApi = {
  login: (data) => post('/api/mini/auth/login', data),
  refreshToken: (refreshToken) => post('/api/mini/auth/refresh', { refreshToken }),
  bindPhone: (data) => post('/api/mini/user/bind-phone', data)
}

export const userApi = {
  getProfile: () => get('/api/mini/user/profile'),
  updateProfile: (data) => put('/api/mini/user/profile', data),
  getUserInfo: (id) => get(`/api/mini/user/${id}`),
  follow: (id) => post(`/api/mini/user/${id}/follow`),
  unfollow: (id) => del(`/api/mini/user/${id}/follow`),
  getFollowing: (params) => get('/api/mini/user/following', params),
  getFollowers: (params) => get('/api/mini/user/follower', params)
}

export const demandApi = {
  getList: (params) => get('/api/mini/demands', params),
  getDetail: (id) => get(`/api/mini/demands/${id}`),
  create: (data) => post('/api/mini/demands', data),
  update: (id, data) => put(`/api/mini/demands/${id}`, data),
  delete: (id) => del(`/api/mini/demands/${id}`),
  addFootprint: (id) => post(`/api/mini/demands/${id}/footprint`)
}

export const orderApi = {
  takeOrder: (demandId, data) => post(`/api/mini/orders/${demandId}/take`, data),
  getPublished: (params) => get('/api/mini/orders/published', params),
  getTaken: (params) => get('/api/mini/orders/taken', params),
  getDetail: (id) => get(`/api/mini/orders/${id}`),
  updateStatus: (id, data) => put(`/api/mini/orders/${id}/status`, data),
  cancel: (id, data) => post(`/api/mini/orders/${id}/cancel`, data)
}

export const reviewApi = {
  create: (data) => post('/api/review', data),
  getUserReviews: (userId, params) => get(`/api/mini/reviews/user/${userId}`, params)
}

export const footprintApi = {
  getList: (params) => get('/api/mini/footprints', params),
  clear: (ids) => del('/api/mini/footprints', { ids })
}

export const messageApi = {
  getList: (params) => get('/api/mini/messages', params),
  markRead: (id) => put(`/api/mini/message/${id}/read`),
  markAllRead: () => put('/api/mini/messages/read-all')
}

export const categoryApi = {
  getList: () => get('/api/mini/categories')
}

export const homeApi = {
  getData: () => get('/api/mini/home')
}

export const uploadApi = {
  uploadImage: (filePath, type = 'demand') => {
    return uploadFile('/api/mini/upload/image', filePath, 'file', { type })
  }
}

export default {
  authApi,
  userApi,
  demandApi,
  orderApi,
  reviewApi,
  footprintApi,
  messageApi,
  categoryApi,
  homeApi,
  uploadApi
}
