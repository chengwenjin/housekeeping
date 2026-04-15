const app = getApp()
const { orderApi } = require('../../utils/api')

Page({
  data: {
    orderType: 'published',
    currentStatus: null,
    orderList: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    statusOptions: [
      { label: '全部', value: null },
      { label: '待服务', value: 1 },
      { label: '进行中', value: 2 },
      { label: '待确认', value: 3 },
      { label: '已完成', value: 4 }
    ]
  },

  onLoad: function (options) {
    if (options.type) {
      this.setData({ orderType: options.type })
    }
    if (app.isLoggedIn()) {
      this.loadOrders()
    }
  },

  onShow: function () {
    if (app.isLoggedIn()) {
      this.setData({
        page: 1,
        hasMore: true,
        orderList: []
      })
      this.loadOrders()
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

  getStatusText: function (status) {
    const texts = {
      1: '待服务',
      2: '进行中',
      3: '待确认',
      4: '已完成'
    }
    return texts[status] || '未知状态'
  },

  getStatusBadgeClass: function (status) {
    const classes = {
      1: 'status-pending',
      2: 'status-active',
      3: 'status-confirm',
      4: 'status-done'
    }
    return classes[status] || 'status-pending'
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

  switchOrderType: function (e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      orderType: type,
      page: 1,
      hasMore: true,
      orderList: []
    })
    this.loadOrders()
  },

  selectStatus: function (e) {
    const status = e.currentTarget.dataset.status
    this.setData({
      currentStatus: status,
      page: 1,
      hasMore: true,
      orderList: []
    })
    this.loadOrders()
  },

  loadOrders: async function () {
    if (!app.isLoggedIn()) return
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    try {
      const params = {
        page: this.data.page,
        pageSize: this.data.pageSize
      }
      
      if (this.data.currentStatus !== null) {
        params.status = this.data.currentStatus
      }
      
      let res
      if (this.data.orderType === 'published') {
        res = await orderApi.getPublished(params)
      } else {
        res = await orderApi.getTaken(params)
      }
      
      if (res.code === 200) {
        const newList = res.data?.list || []
        const pagination = res.data?.pagination || {}
        
        this.setData({
          orderList: this.data.page === 1 ? newList : [...this.data.orderList, ...newList],
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载订单失败', e)
    } finally {
      this.setData({ 
        loading: false,
        refreshing: false
      })
    }
  },

  onRefresh: function () {
    this.setData({ refreshing: true })
    this.setData({
      page: 1,
      hasMore: true,
      orderList: []
    })
    this.loadOrders()
  },

  loadMore: function () {
    if (!this.data.loading && this.data.hasMore) {
      this.loadOrders()
    }
  },

  goToOrderDetail: function (e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    })
  }
})
