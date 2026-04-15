const app = getApp()
const { orderApi } = require('../../utils/api')

Page({
  data: {
    orderId: null,
    order: {
      demand: {},
      location: {},
      publisher: {},
      taker: {}
    },
    isPublisher: false,
    isTaker: false,
    loading: false
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ orderId: options.id })
      this.loadOrderDetail()
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
    }
  },

  getStatusIcon: function (status) {
    const icons = {
      1: '⏳',
      2: '🔄',
      3: '✅',
      4: '🎉'
    }
    return icons[status] || '📋'
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

  getStatusDesc: function (status) {
    const descs = {
      1: '等待服务者开始服务',
      2: '服务进行中',
      3: '等待发布者确认完成',
      4: '订单已完成'
    }
    return descs[status] || ''
  },

  getAvatarEmoji: function (id) {
    const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
    return emojis[(id || 0) % emojis.length]
  },

  formatTime: function (time) {
    if (!time) return ''
    const date = new Date(time)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()
    return `${year}年${month}月${day}日 ${hour}:${minute.toString().padStart(2, '0')}`
  },

  loadOrderDetail: async function () {
    if (!this.data.orderId) return
    
    this.setData({ loading: true })
    try {
      const res = await orderApi.getDetail(this.data.orderId)
      if (res.code === 200) {
        const order = res.data || {}
        const userInfo = wx.getStorageSync('userInfo')
        
        this.setData({
          order: order,
          isPublisher: order.publisher?.id === userInfo?.id,
          isTaker: order.taker?.id === userInfo?.id
        })
      }
    } catch (e) {
      console.error('加载订单详情失败', e)
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

  startService: async function () {
    const that = this
    wx.showModal({
      title: '确认开始服务',
      content: '确定要开始服务吗？',
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' })
            const result = await orderApi.updateStatus(that.data.orderId, {
              status: 2,
              remark: '开始服务'
            })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '已开始服务',
                icon: 'success'
              })
              that.loadOrderDetail()
            }
          } catch (e) {
            wx.hideLoading()
            console.error('开始服务失败', e)
          }
        }
      }
    })
  },

  completeService: async function () {
    const that = this
    wx.showModal({
      title: '确认完成服务',
      content: '确定要确认完成服务吗？请等待雇主确认。',
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' })
            const result = await orderApi.updateStatus(that.data.orderId, {
              status: 3,
              remark: '服务已完成'
            })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '已提交确认',
                icon: 'success'
              })
              that.loadOrderDetail()
            }
          } catch (e) {
            wx.hideLoading()
            console.error('确认完成失败', e)
          }
        }
      }
    })
  },

  confirmComplete: async function () {
    const that = this
    wx.showModal({
      title: '确认订单完成',
      content: '确定要确认订单完成吗？确认后可以进行评价。',
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' })
            const result = await orderApi.updateStatus(that.data.orderId, {
              status: 4,
              remark: '订单已完成'
            })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '订单已完成',
                icon: 'success'
              })
              that.loadOrderDetail()
            }
          } catch (e) {
            wx.hideLoading()
            console.error('确认订单失败', e)
          }
        }
      }
    })
  },

  cancelOrder: async function () {
    const that = this
    wx.showModal({
      title: '取消订单',
      content: '确定要取消订单吗？',
      success: async function (res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' })
            const result = await orderApi.cancel(that.data.orderId, {
              reason: '用户主动取消',
              cancelBy: that.data.isPublisher ? 'publisher' : 'taker'
            })
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              })
              setTimeout(() => {
                wx.navigateBack()
              }, 1500)
            }
          } catch (e) {
            wx.hideLoading()
            console.error('取消订单失败', e)
          }
        }
      }
    })
  }
})
