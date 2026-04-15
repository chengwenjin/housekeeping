const app = getApp()
const { userApi, reviewApi, categoryApi } = require('../../utils/api')

Page({
  data: {
    userId: null,
    userInfo: {},
    activeTab: 'reviews',
    reviewList: [],
    categories: [],
    isFollowed: false,
    isLoggedIn: false,
    isSelf: false,
    loading: false,
    hasMore: true,
    page: 1,
    pageSize: 10
  },

  onLoad: function (options) {
    const isLoggedIn = app.isLoggedIn()
    const currentUser = wx.getStorageSync('userInfo')
    
    if (options.id) {
      this.setData({
        userId: options.id,
        isLoggedIn: isLoggedIn,
        isSelf: currentUser && currentUser.id == options.id
      })
      this.loadUserInfo()
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

  getCategoryIcon: function (name) {
    const icons = {
      '保洁': '🧹',
      '烹饪': '🍳',
      '育儿': '👶',
      '老人': '🧓',
      '搬运': '📦',
      '宠物': '🐾',
      '维修': '🔧'
    }
    return icons[name] || '🏠'
  },

  formatTime: function (time) {
    if (!time) return ''
    const date = new Date(time)
    const now = new Date()
    const diff = now - date
    
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
    if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
    
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${year}年${month}月${day}日`
  },

  loadUserInfo: async function () {
    if (!this.data.userId) return
    
    this.setData({ loading: true })
    try {
      const res = await userApi.getUserInfo(this.data.userId)
      if (res.code === 200) {
        const userInfo = res.data || {}
        
        let categories = []
        if (userInfo.categories && userInfo.categories.length > 0) {
          categories = userInfo.categories.map((name, index) => ({
            id: index + 1,
            name: name,
            icon: this.getCategoryIcon(name)
          }))
        }
        
        this.setData({
          userInfo: userInfo,
          isFollowed: userInfo.isFollowed || false,
          categories: categories
        })
        
        this.loadReviews()
      }
    } catch (e) {
      console.error('加载用户信息失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadReviews: async function () {
    if (!this.data.userId) return
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    try {
      const res = await reviewApi.getUserReviews(this.data.userId, {
        page: this.data.page,
        pageSize: this.data.pageSize
      })
      
      if (res.code === 200) {
        const newList = res.data?.list || []
        const pagination = res.data?.pagination || {}
        
        this.setData({
          reviewList: this.data.page === 1 ? newList : [...this.data.reviewList, ...newList],
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载评价失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
    
    if (tab === 'reviews' && this.data.reviewList.length === 0) {
      this.setData({
        page: 1,
        hasMore: true
      })
      this.loadReviews()
    }
  },

  loadMore: function () {
    if (!this.data.loading && this.data.hasMore && this.data.activeTab === 'reviews') {
      this.loadReviews()
    }
  },

  goBack: function () {
    wx.navigateBack()
  },

  toggleFollow: async function () {
    if (!this.data.isLoggedIn) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    try {
      if (this.data.isFollowed) {
        const res = await userApi.unfollow(this.data.userId)
        if (res.code === 200) {
          this.setData({ isFollowed: false })
          wx.showToast({
            title: '已取消关注',
            icon: 'success'
          })
        }
      } else {
        const res = await userApi.follow(this.data.userId)
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
      title: '私信功能开发中',
      icon: 'none'
    })
  }
})
