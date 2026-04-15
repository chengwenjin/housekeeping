const app = getApp()
const { authApi } = require('../../utils/api')

Page({
  data: {
    loading: false
  },

  onLoad: function (options) {
    
  },

  handleLogin: async function () {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      wx.showLoading({ title: '登录中...' })
      
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        })
      })
      
      console.log('登录code:', loginRes.code)
      
      const mockUserInfo = {
        code: loginRes.code,
        userInfo: {
          nickName: '测试用户',
          avatarUrl: '',
          gender: 1
        }
      }
      
      const res = await authApi.login(mockUserInfo)
      
      wx.hideLoading()
      
      if (res.code === 200) {
        app.setToken(res.data.token, res.data.refreshToken)
        app.setUserInfo(res.data.user)
        
        wx.showToast({
          title: '登录成功',
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
      this.setData({ loading: false })
      console.error('登录失败', e)
      
      wx.showToast({
        title: '登录失败',
        icon: 'none'
      })
    }
  }
})
