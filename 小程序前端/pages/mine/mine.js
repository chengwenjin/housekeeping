const app = getApp()
const { orderApi, userApi } = require('../../utils/api')

Page({
  data: {
    isLoggedIn: false,
    userInfo: null,
    publishedOrders: [],
    takenOrders: []
  },

  onLoad: function (options) {
    this.checkLoginStatus()
  },

  onShow: function () {
    this.checkLoginStatus()
    if (this.data.isLoggedIn) {
      this.loadOrders()
      this.refreshUserInfo()
    }
  },

  checkLoginStatus: function () {
    const isLoggedIn = app.isLoggedIn()
    const userInfo = wx.getStorageSync('userInfo')
    
    this.setData({
      isLoggedIn,
      userInfo: userInfo || null
    })
  },

  getCategoryIcon: function (id) {
    const icons = {
      1: '🧹',
      2: '🍳',
      3: '👶',
      4: '🧓',
      5: '📦',
      6: '🐾',
      7: '🔧'
    }
    return icons[id] || '🏠'
  },

  getStatusClass: function (status) {
    const classes = {
      1: 'os-pending',
      2: 'os-active',
      3: 'os-pending',
      4: 'os-done'
    }
    return classes[status] || 'os-pending'
  },

  getStatusText: function (status) {
    const texts = {
      1: '待服务',
      2: '进行中',
      3: '待确认',
      4: '已完成'
    }
    return texts[status] || '待接单'
  },

  formatTime: function (time) {
    if (!time) return ''
    const date = new Date(time)
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()
    return `${month}月${day}日 ${hour}:${minute.toString().padStart(2, '0')}`
  },

  loadOrders: async function () {
    if (!this.data.isLoggedIn) return
    
    try {
      const [publishedRes, takenRes] = await Promise.all([
        orderApi.getPublished({ page: 1, pageSize: 3 }),
        orderApi.getTaken({ page: 1, pageSize: 3 })
      ])
      
      if (publishedRes.code === 200) {
        this.setData({ publishedOrders: publishedRes.data?.list || [] })
      }
      if (takenRes.code === 200) {
        this.setData({ takenOrders: takenRes.data?.list || [] })
      }
    } catch (e) {
      console.error('加载订单失败', e)
    }
  },

  refreshUserInfo: async function () {
    if (!this.data.isLoggedIn) return
    
    try {
      const res = await userApi.getProfile()
      if (res.code === 200) {
        app.setUserInfo(res.data)
        this.setData({ userInfo: res.data })
      }
    } catch (e) {
      console.error('刷新用户信息失败', e)
    }
  },

  goToLogin: function () {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  goToEdit: function () {
    wx.showToast({
      title: '编辑功能开发中',
      icon: 'none'
    })
  },

  goToPublishedOrders: function () {
    wx.navigateTo({
      url: '/pages/orders/orders?type=published'
    })
  },

  goToTakenOrders: function () {
    wx.navigateTo({
      url: '/pages/orders/orders?type=taken'
    })
  },

  goToFollowing: function () {
    wx.navigateTo({
      url: '/pages/follow/follow?tab=following'
    })
  },

  goToFootprints: function () {
    wx.navigateTo({
      url: '/pages/follow/follow?tab=footprint'
    })
  },

  goToReviews: function () {
    wx.showToast({
      title: '评价功能开发中',
      icon: 'none'
    })
  },

  goToMessages: function () {
    wx.switchTab({
      url: '/pages/messages/messages'
    })
  },

  goToSettings: function () {
    wx.showToast({
      title: '设置功能开发中',
      icon: 'none'
    })
  },

  goToOrderDetail: function (e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    })
  },

  handleLogout: function () {
    const that = this
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: function (res) {
        if (res.confirm) {
          app.logout()
          that.setData({
            isLoggedIn: false,
            userInfo: null,
            publishedOrders: [],
            takenOrders: []
          })
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  }
})
