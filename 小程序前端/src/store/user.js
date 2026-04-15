import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const refreshToken = ref(uni.getStorageSync('refreshToken') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken, newRefreshToken) => {
    token.value = newToken
    refreshToken.value = newRefreshToken
    uni.setStorageSync('token', newToken)
    uni.setStorageSync('refreshToken', newRefreshToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    uni.setStorageSync('userInfo', info)
  }

  const login = async (loginData) => {
    const res = await authApi.login(loginData)
    if (res.code === 200) {
      setToken(res.data.token, res.data.refreshToken)
      setUserInfo(res.data.user)
      return res.data
    }
    throw new Error(res.message)
  }

  const logout = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('refreshToken')
    uni.removeStorageSync('userInfo')
  }

  const refreshUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await userApi.getProfile()
      if (res.code === 200) {
        setUserInfo(res.data)
      }
    } catch (e) {
      console.error('刷新用户信息失败', e)
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    logout,
    refreshUserInfo
  }
})
