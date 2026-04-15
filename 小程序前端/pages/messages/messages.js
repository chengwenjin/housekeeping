const app = getApp()
const { messageApi } = require('../../utils/api')

Page({
  data: {
    activeTab: 'all',
    messageList: [],
    unreadCount: 0,
    loading: false,
    hasMore: true,
    page: 1,
    pageSize: 10
  },

  onLoad: function (options) {
    if (app.isLoggedIn()) {
      this.loadMessages()
    }
  },

  onShow: function () {
    if (app.isLoggedIn()) {
      this.setData({
        page: 1,
        hasMore: true,
        messageList: []
      })
      this.loadMessages()
    }
  },

  getMessageIcon: function (type) {
    const icons = {
      1: '🔔',
      2: '📋',
      3: '⭐',
      4: '💬'
    }
    return icons[type] || '🔔'
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
    
    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${month}月${day}日`
  },

  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab,
      page: 1,
      hasMore: true,
      messageList: []
    })
    this.loadMessages()
  },

  loadMessages: async function () {
    if (!app.isLoggedIn()) return
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    try {
      const params = {
        page: this.data.page,
        pageSize: this.data.pageSize
      }
      
      if (this.data.activeTab !== 'all') {
        params.type = this.data.activeTab
      }
      
      const res = await messageApi.getList(params)
      
      if (res.code === 200) {
        const newList = res.data?.list || []
        const pagination = res.data?.pagination || {}
        
        this.setData({
          messageList: this.data.page === 1 ? newList : [...this.data.messageList, ...newList],
          unreadCount: res.data?.unreadCount || 0,
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载消息失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  loadMore: function () {
    if (!this.data.loading && this.data.hasMore) {
      this.loadMessages()
    }
  },

  markAllRead: async function () {
    try {
      const res = await messageApi.markAllRead()
      if (res.code === 200) {
        this.setData({ unreadCount: 0 })
        const updatedList = this.data.messageList.map(msg => ({
          ...msg,
          isRead: true
        }))
        this.setData({ messageList: updatedList })
        wx.showToast({
          title: '已全部标记为已读',
          icon: 'success'
        })
      }
    } catch (e) {
      console.error('标记已读失败', e)
    }
  },

  handleMessageClick: async function (e) {
    const msg = e.currentTarget.dataset.msg
    
    if (!msg.isRead) {
      try {
        await messageApi.markRead(msg.id)
        const updatedList = this.data.messageList.map(item => {
          if (item.id === msg.id) {
            return { ...item, isRead: true }
          }
          return item
        })
        this.setData({ 
          messageList: updatedList,
          unreadCount: Math.max(0, this.data.unreadCount - 1)
        })
      } catch (e) {
        console.error('标记已读失败', e)
      }
    }
    
    if (msg.relatedType === 'order' && msg.relatedId) {
      wx.navigateTo({
        url: `/pages/order-detail/order-detail?id=${msg.relatedId}`
      })
    }
  }
})
