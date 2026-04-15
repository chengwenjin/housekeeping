const { get, post, put, del, uploadFile } = require('./request')

const authApi = {
  login: (data) => post('/api/mini/auth/login', data),
  refreshToken: (refreshToken) => post('/api/mini/auth/refresh', { refreshToken }),
  bindPhone: (data) => post('/api/mini/user/bind-phone', data)
}

const userApi = {
  getProfile: () => get('/api/mini/user/profile'),
  updateProfile: (data) => put('/api/mini/user/profile', data),
  getUserInfo: (id) => get(`/api/mini/user/${id}`),
  follow: (id) => post(`/api/mini/user/${id}/follow`),
  unfollow: (id) => del(`/api/mini/user/${id}/follow`),
  getFollowing: (params) => get('/api/mini/user/following', params),
  getFollowers: (params) => get('/api/mini/user/follower', params)
}

const demandApi = {
  getList: (params) => get('/api/mini/demands', params),
  getDetail: (id) => get(`/api/mini/demands/${id}`),
  create: (data) => post('/api/mini/demands', data),
  update: (id, data) => put(`/api/mini/demands/${id}`, data),
  delete: (id) => del(`/api/mini/demands/${id}`),
  addFootprint: (id) => post(`/api/mini/demands/${id}/footprint`)
}

const orderApi = {
  takeOrder: (demandId, data) => post(`/api/mini/orders/${demandId}/take`, data),
  getPublished: (params) => get('/api/mini/orders/published', params),
  getTaken: (params) => get('/api/mini/orders/taken', params),
  getDetail: (id) => get(`/api/mini/orders/${id}`),
  updateStatus: (id, data) => put(`/api/mini/orders/${id}/status`, data),
  cancel: (id, data) => post(`/api/mini/orders/${id}/cancel`, data)
}

const reviewApi = {
  create: (data) => post('/api/review', data),
  getUserReviews: (userId, params) => get(`/api/mini/reviews/user/${userId}`, params)
}

const footprintApi = {
  getList: (params) => get('/api/mini/footprints', params),
  clear: (ids) => del('/api/mini/footprints', { ids })
}

const messageApi = {
  getList: (params) => get('/api/mini/messages', params),
  markRead: (id) => put(`/api/mini/message/${id}/read`),
  markAllRead: () => put('/api/mini/messages/read-all')
}

const categoryApi = {
  getList: () => get('/api/mini/categories')
}

const homeApi = {
  getData: () => get('/api/mini/home')
}

const uploadApi = {
  uploadImage: (filePath, type = 'demand') => {
    return uploadFile('/api/mini/upload/image', filePath, 'file', { type })
  }
}

module.exports = {
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
