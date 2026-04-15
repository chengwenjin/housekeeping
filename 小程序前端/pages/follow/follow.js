const app = getApp()
const { footprintApi, userApi } = require('../../utils/api')

Page({
  data: {
    activeTab: 'footprint',
    footprintList: [],
    followingList: [],
    loading: false,
    hasMore: true,
    page: 1,
    pageSize: 10
  },

  onLoad: function (options) {
    if (options.tab) {
      this.setData({ activeTab: options.tab })
    }
    if (app.isLoggedIn()) {
      this.loadData()
    }
  },

  onShow: function () {
    if (app.isLoggedIn()) {
      this.setData({
        page: 1,
        hasMore: true,
        footprintList: [],
        followingList: []
      })
      this.loadData()
    }
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

  getAvatarEmoji: function (id) {
    const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
    return emojis[(id || 0) % emojis.length]
  },

  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab,
      page: 1,
      hasMore: true
    })
    this.loadData()
  },

  loadData: async function () {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    if (this.data.activeTab === 'footprint') {
      this.loadFootprints()
    } else {
      this.loadFollowing()
    }
  },

  loadFootprints: async function () {
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    try {
      const res = await footprintApi.getList({
        page: this.data.page,
        pageSize: this.data.pageSize
      })
      
      if (res.code === 200) {
        const newList = res.data?.list || []
        const pagination = res.data?.pagination || {}
        
        this.setData({
          footprintList: this.data.page === 1 ? newList : [...this.data.footprintList, ...newList],
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载足迹失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadFollowing: async function () {
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    try {
      const res = await userApi.getFollowing({
        page: this.data.page,
        pageSize: this.data.pageSize
      })
      
      if (res.code === 200) {
        const newList = (res.data?.list || []).map(item => ({ ...item, isFollowed: true }))
        const pagination = res.data?.pagination || {}
        
        this.setData({
          followingList: this.data.page === 1 ? newList : [...this.data.followingList, ...newList],
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载关注列表失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadMore: function () {
    if (!this.data.loading && this.data.hasMore) {
      this.loadData()
    }
  },

  goBack: function () {
    wx.navigateBack()
  },

  goToDemand: function (e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/demand/detail?id=${id}`
    })
  },

  goToUserProfile: function (e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/user-profile/user-profile?id=${id}`
    })
  },

  toggleFollow: async function (e) {
    const user = e.currentTarget.dataset.user
    
    try {
      if (user.isFollowed) {
        const res = await userApi.unfollow(user.id)
        if (res.code === 200) {
          const updatedList = this.data.followingList.filter(item => item.id !== user.id)
          this.setData({ followingList: updatedList })
          wx.showToast({
            title: '已取消关注',
            icon: 'success'
          })
        }
      } else {
        const res = await userApi.follow(user.id)
        if (res.code === 200) {
          const updatedList = this.data.followingList.map(item => {
            if (item.id === user.id) {
              return { ...item, isFollowed: true }
            }
            return item
          })
          this.setData({ followingList: updatedList })
          wx.showToast({
            title: '关注成功',
            icon: 'success'
          })
        }
      }
    } catch (e) {
      console.error('关注操作失败', e)
    }
  }
})
