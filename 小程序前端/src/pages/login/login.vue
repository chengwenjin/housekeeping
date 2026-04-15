<template>
  <view class="login-page">
    <view class="login-logo">
      <view class="logo-icon">🏠</view>
      <text class="logo-title">家政速帮</text>
      <text class="logo-subtitle">身边的家政服务平台</text>
    </view>
    
    <view class="login-card">
      <text class="login-title">欢迎使用</text>
      <button class="wechat-login-btn" @click="handleLogin">
        <text class="wechat-icon">💬</text>
        <text>微信一键登录</text>
      </button>
      <text class="login-tip">
        授权登录即表示您同意《用户协议》及《隐私政策》
      </text>
    </view>
    
    <view class="footer-text">
      <text>家政速帮 · 让生活更轻松</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)

const handleLogin = async () => {
  if (loading.value) return
  loading.value = true
  
  try {
    uni.showLoading({ title: '登录中...' })
    
    uni.login({
      provider: 'weixin',
      success: async (loginRes) => {
        console.log('登录code:', loginRes.code)
        
        try {
          const mockUserInfo = {
            code: loginRes.code,
            userInfo: {
              nickName: '测试用户',
              avatarUrl: '',
              gender: 1
            }
          }
          
          const res = await userStore.login(mockUserInfo)
          
          uni.hideLoading()
          
          if (res) {
            uni.showToast({
              title: '登录成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.switchTab({
                url: '/pages/index/index'
              })
            }, 1500)
          }
        } catch (e) {
          uni.hideLoading()
          loading.value = false
          console.error('登录失败', e)
        }
      },
      fail: (err) => {
        uni.hideLoading()
        loading.value = false
        uni.showToast({
          title: '微信登录失败',
          icon: 'none'
        })
        console.error('微信登录失败', err)
      }
    })
  } catch (e) {
    uni.hideLoading()
    loading.value = false
    console.error(e)
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
}

.login-logo {
  text-align: center;
  margin-bottom: 64rpx;
}

.logo-icon {
  width: 144rpx;
  height: 144rpx;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 76rpx;
  margin: 0 auto 24rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.15);
}

.logo-title {
  display: block;
  color: #FFFFFF;
  font-size: 44rpx;
  font-weight: 800;
}

.logo-subtitle {
  display: block;
  color: rgba(255, 255, 255, 0.8);
  font-size: 26rpx;
  margin-top: 12rpx;
}

.login-card {
  background: #FFFFFF;
  border-radius: 40rpx;
  padding: 44rpx;
  width: 100%;
  max-width: 600rpx;
  box-shadow: 0 8rpx 48rpx rgba(0, 0, 0, 0.12);
}

.login-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #424242;
  text-align: center;
  margin-bottom: 32rpx;
}

.wechat-login-btn {
  width: 100%;
  background: linear-gradient(135deg, #07C160, #06AD56);
  color: #FFFFFF;
  border: none;
  padding: 28rpx;
  border-radius: 9999rpx;
  font-size: 30rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  box-shadow: 0 8rpx 32rpx rgba(7, 193, 96, 0.35);
}

.wechat-icon {
  font-size: 40rpx;
}

.login-tip {
  display: block;
  font-size: 20rpx;
  color: #9E9E9E;
  text-align: center;
  margin-top: 24rpx;
  line-height: 1.5;
}

.footer-text {
  color: rgba(255, 255, 255, 0.6);
  font-size: 20rpx;
  margin-top: 48rpx;
  text-align: center;
}
</style>
