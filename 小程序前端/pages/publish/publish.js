const app = getApp()
const { demandApi, categoryApi } = require('../../utils/api')

Page({
  data: {
    categories: [],
    selectedCategory: null,
    priceUnitIndex: 0,
    priceUnits: [
      { id: 1, name: '元/小时', value: '小时' },
      { id: 2, name: '元/次', value: '次' },
      { id: 3, name: '元/天', value: '天' },
      { id: 4, name: '元/月', value: '月' }
    ],
    formData: {
      categoryId: null,
      title: '',
      description: '',
      expectedPrice: '',
      priceUnit: '小时',
      minDuration: '',
      maxDuration: '',
      serviceTimeDesc: '',
      province: '北京市',
      city: '北京市',
      district: '',
      address: '',
      latitude: 39.9042,
      longitude: 116.4074,
      contactName: '',
      contactPhone: '',
      imageUrls: []
    },
    submitting: false
  },

  onLoad: function (options) {
    this.loadCategories()
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
          { id: 7, name: '维修', icon: '🔧' },
          { id: 8, name: '更多', icon: '➕' }
        ]
      })
    }
  },

  selectCategory: function (e) {
    const category = e.currentTarget.dataset.category
    const isSame = this.data.selectedCategory && this.data.selectedCategory.id === category.id
    
    this.setData({
      selectedCategory: isSame ? null : category,
      'formData.categoryId': isSame ? null : category.id
    })
  },

  onInputChange: function (e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`formData.${field}`]: value
    })
  },

  onPriceUnitChange: function (e) {
    const index = e.detail.value
    this.setData({
      priceUnitIndex: index,
      'formData.priceUnit': this.data.priceUnits[index].value
    })
  },

  selectLocation: function () {
    const that = this
    wx.showActionSheet({
      itemList: ['朝阳区', '海淀区', '西城区', '东城区', '丰台区', '石景山区'],
      success: function (res) {
        const districts = ['朝阳区', '海淀区', '西城区', '东城区', '丰台区', '石景山区']
        that.setData({
          'formData.district': districts[res.tapIndex]
        })
      }
    })
  },

  goBack: function () {
    wx.navigateBack()
  },

  validateForm: function () {
    if (!this.data.formData.categoryId) {
      wx.showToast({ title: '请选择服务类型', icon: 'none' })
      return false
    }
    if (!this.data.formData.title.trim()) {
      wx.showToast({ title: '请输入需求标题', icon: 'none' })
      return false
    }
    if (!this.data.formData.expectedPrice) {
      wx.showToast({ title: '请输入预期报酬', icon: 'none' })
      return false
    }
    if (!this.data.formData.district) {
      wx.showToast({ title: '请选择服务地区', icon: 'none' })
      return false
    }
    if (!this.data.formData.address.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' })
      return false
    }
    return true
  },

  submitForm: async function () {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
    
    if (!this.validateForm()) return
    
    this.setData({ submitting: true })
    
    try {
      wx.showLoading({ title: '发布中...' })
      
      const submitData = {
        categoryId: this.data.formData.categoryId,
        title: this.data.formData.title.trim(),
        description: this.data.formData.description.trim(),
        expectedPrice: parseFloat(this.data.formData.expectedPrice) || 0,
        priceUnit: this.data.formData.priceUnit,
        minDuration: this.data.formData.minDuration ? parseFloat(this.data.formData.minDuration) : null,
        maxDuration: this.data.formData.maxDuration ? parseFloat(this.data.formData.maxDuration) : null,
        serviceTimeDesc: this.data.formData.serviceTimeDesc || '待定',
        province: this.data.formData.province,
        city: this.data.formData.city,
        district: this.data.formData.district,
        address: this.data.formData.address.trim(),
        latitude: this.data.formData.latitude,
        longitude: this.data.formData.longitude,
        contactName: this.data.formData.contactName.trim() || '用户',
        contactPhone: this.data.formData.contactPhone.trim() || '',
        imageUrls: this.data.formData.imageUrls
      }
      
      const res = await demandApi.create(submitData)
      wx.hideLoading()
      
      if (res.code === 200) {
        wx.showToast({
          title: '发布成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      }
    } catch (e) {
      wx.hideLoading()
      this.setData({ submitting: false })
      console.error('发布失败', e)
    }
  }
})
