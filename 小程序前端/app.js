App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080'
  },

  onLaunch: function () {
    console.log('App Launch')
    
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token) {
      this.globalData.token = token
    }
    if (userInfo) {
      this.globalData.userInfo = userInfo
    }
  },

  onShow: function () {
    console.log('App Show')
  },

  onHide: function () {
    console.log('App Hide')
  },

  setToken: function (token, refreshToken) {
    this.globalData.token = token
    wx.setStorageSync('token', token)
    if (refreshToken) {
      wx.setStorageSync('refreshToken', refreshToken)
    }
  },

  setUserInfo: function (userInfo) {
    this.globalData.userInfo = userInfo
    wx.setStorageSync('userInfo', userInfo)
  },

  logout: function () {
    this.globalData.token = null
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('refreshToken')
    wx.removeStorageSync('userInfo')
  },

  isLoggedIn: function () {
    return !!this.globalData.token
  }
})
