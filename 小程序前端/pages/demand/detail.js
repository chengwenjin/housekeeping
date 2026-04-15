const app = getApp()
const { demandApi, orderApi, userApi } = require('../../utils/api')

Page({
  data: {
    demandId: null,
    demand: {
      category: {},
      location: {},
      publisher: {}
    },
    isFollowed: false,
    loading: false
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ demandId: options.id })
      this.loadDemandDetail()
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
    }
  },

  getAvatarEmoji: function (id) {
    const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
    return emojis[(id || 0) % emojis.length]
  },

  loadDemandDetail: async function () {
    if (!this.data.demandId) return
    
    this.setData({ loading: true })
    try {
      const res = await demandApi.getDetail(this.data.demandId)
      if (res.code === 200) {
        this.setData({ 
          demand: res.data || {},
          isFollowed: res.data?.publisher?.isFollowed || false
        })
        
        try {
          await demandApi.addFootprint(this.data.demandId)
        } catch (e) {
          console.log('添加足迹失败', e)
        }
      }
    } catch (e) {
      console.error('加载需求详情失败', e)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  goBack: function () {
    wx.navigateBack()
  },

  goToUserProfile: function () {
    if (this.data.demand.publisher?.id) {
      wx.navigateTo({
        url: `/pages/user-profile/user-profile?id=${this.data.demand.publisher.id}`
      })
    }
  },

  toggleFollow: async function () {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    if (!this.data.demand.publisher?.id) return
    
    try {
      if (this.data.isFollowed) {
        const res = await userApi.unfollow(this.data.demand.publisher.id)
        if (res.code === 200) {
          this.setData({ isFollowed: false })
          wx.showToast({
            title: '已取消关注',
            icon: 'success'
          })
        }
      } else {
        const res = await userApi.follow(this.data.demand.publisher.id)
        if (res.code === 200) {
          this.setData({ isFollowed: true })
          wx.showToast({
            title: '关注成功',
            icon: 'success'
          })
        }
      }
    } catch (e) {
      console.error('关注操作失败', e)
    }
  },

  handleChat: function () {
    wx.showToast({
      title: '聊天功能开发中',
      icon: 'none'
    })
  },

  handleTakeOrder: async function () {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    const that = this
    wx.showModal({
      title: '确认接单',
      content: `确定要接取"${that.data.demand.title}"这个需求吗？`,
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '接单中...' })
            const result = await orderApi.takeOrder(that.data.demandId, { remark: '' })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '接单成功',
                icon: 'success'
              })
              
              setTimeout(() => {
                wx.switchTab({
                  url: '/pages/orders/orders'
                })
              }, 1500)
            }
          } catch (e) {
            wx.hideLoading()
            console.error('接单失败', e)
          }
        }
      }
    })
  }
})
