const app = getApp()
const { demandApi, categoryApi, homeApi, orderApi } = require('../../utils/api')

Page({
  data: {
    currentCity: '北京市朝阳区',
    categories: [],
    selectedCategory: null,
    homeData: {},
    demandList: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    userInfo: null
  },

  onLoad: function (options) {
    this.loadCategories()
    this.loadHomeData()
    this.loadDemands(true)
    
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ userInfo })
    }
  },

  onShow: function () {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ userInfo })
    }
  },

  getAvatarEmoji: function (id) {
    const emojis = ['👩', '👨', '👩‍🦳', '👨‍🦳', '👩‍🍳', '👨‍⚕️']
    return emojis[(id || 0) % emojis.length]
  },

  loadCategories: async function () {
    try {
      const res = await categoryApi.getList()
      if (res.code === 200) {
        this.setData({ categories: res.data || [] })
      }
    } catch (e) {
      console.error('加载分类失败', e)
      this.setData({
        categories: [
          { id: 1, name: '保洁', icon: '🧹' },
          { id: 2, name: '烹饪', icon: '🍳' },
          { id: 3, name: '育儿', icon: '👶' },
          { id: 4, name: '老人', icon: '🧓' },
          { id: 5, name: '搬运', icon: '📦' },
          { id: 6, name: '宠物', icon: '🐾' },
          { id: 7, name: '维修', icon: '🔧' }
        ]
      })
    }
  },

  loadHomeData: async function () {
    try {
      const res = await homeApi.getData()
      if (res.code === 200) {
        this.setData({ homeData: res.data || {} })
      }
    } catch (e) {
      console.error('加载首页数据失败', e)
      this.setData({
        homeData: {
          banner: {
            title: '附近有3条新需求',
            subtitle: '快来抢单，收入翻倍！'
          }
        }
      })
    }
  },

  loadDemands: async function (isRefresh) {
    if (this.data.loading) return
    if (isRefresh) {
      this.setData({
        page: 1,
        hasMore: true,
        demandList: []
      })
    }
    
    if (!this.data.hasMore) return
    
    this.setData({ loading: true })
    
    try {
      const params = {
        page: this.data.page,
        pageSize: this.data.pageSize,
        status: 1
      }
      
      if (this.data.selectedCategory && this.data.selectedCategory.id) {
        params.categoryId = this.data.selectedCategory.id
      }
      
      const res = await demandApi.getList(params)
      
      if (res.code === 200) {
        const newList = res.data?.list || []
        const pagination = res.data?.pagination || {}
        
        this.setData({
          demandList: isRefresh ? newList : [...this.data.demandList, ...newList],
          hasMore: this.data.page < pagination.totalPages,
          page: this.data.page + 1
        })
      }
    } catch (e) {
      console.error('加载需求列表失败', e)
    } finally {
      this.setData({ 
        loading: false,
        refreshing: false
      })
    }
  },

  onRefresh: function () {
    this.setData({ refreshing: true })
    this.loadDemands(true)
  },

  loadMore: function () {
    if (!this.data.loading && this.data.hasMore) {
      this.loadDemands(false)
    }
  },

  selectCategory: function (e) {
    const category = e.currentTarget.dataset.category
    const isSame = this.data.selectedCategory && this.data.selectedCategory.id === category.id
    
    this.setData({
      selectedCategory: isSame ? null : category
    })
    
    this.loadDemands(true)
  },

  selectCity: function () {
    wx.showToast({
      title: '城市选择功能开发中',
      icon: 'none'
    })
  },

  goToSearch: function () {
    wx.showToast({
      title: '搜索功能开发中',
      icon: 'none'
    })
  },

  goToMine: function () {
    if (app.isLoggedIn()) {
      wx.switchTab({
        url: '/pages/mine/mine'
      })
    } else {
      wx.navigateTo({
        url: '/pages/login/login'
      })
    }
  },

  goToDemands: function () {
    wx.switchTab({
      url: '/pages/orders/orders'
    })
  },

  goToDetail: function (e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/demand/detail?id=${id}`
    })
  },

  takeOrder: function (e) {
    const demand = e.currentTarget.dataset.demand
    
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    const that = this
    wx.showModal({
      title: '确认接单',
      content: `确定要接取"${demand.title}"这个需求吗？`,
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '接单中...' })
            const result = await orderApi.takeOrder(demand.id, { remark: '' })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '接单成功',
                icon: 'success'
              })
              that.loadDemands(true)
            }
          } catch (e) {
            wx.hideLoading()
            console.error('接单失败', e)
          }
        }
      }
    })
  },

  showFilterModal: function () {
    wx.showToast({
      title: '筛选功能开发中',
      icon: 'none'
    })
  }
})
