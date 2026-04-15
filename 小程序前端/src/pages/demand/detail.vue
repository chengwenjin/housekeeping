<template>
  <view class="detail-page">
    <view class="detail-header">
      <view class="detail-nav" @click="goBack">
        <text class="back-icon">‹</text>
        <text class="nav-title">需求详情</text>
      </view>
    </view>
    
    <view class="detail-card">
      <view class="detail-badge-row">
        <view class="chip chip-orange" v-if="demand.category?.name">
          <text>{{ demand.category.name }}</text>
        </view>
        <view class="chip" style="background: #FFF8E1; color: #FF9800;" v-if="demand.status === 1">
          <text>⭐ 急单</text>
        </view>
      </view>
      
      <text class="detail-title">{{ demand.title }}</text>
      <text class="detail-desc">{{ demand.description }}</text>
      
      <view class="detail-meta-grid">
        <view class="detail-meta-item">
          <text class="dmi-label">📍 服务地址</text>
          <text class="dmi-val">{{ demand.location?.district || '' }} {{ demand.location?.address || '' }}</text>
        </view>
        <view class="detail-meta-item">
          <text class="dmi-label">📅 服务时间</text>
          <text class="dmi-val">{{ demand.serviceTimeDesc || '待定' }}</text>
        </view>
        <view class="detail-meta-item">
          <text class="dmi-label">⏱ 预计时长</text>
          <text class="dmi-val">{{ demand.minDuration ? `约 ${demand.minDuration}-${demand.maxDuration} 小时` : '待定' }}</text>
        </view>
        <view class="detail-meta-item">
          <text class="dmi-label">🏠 房屋面积</text>
          <text class="dmi-val">{{ demand.houseArea || '待定' }}</text>
        </view>
      </view>
      
      <view class="divider"></view>
      
      <view class="poster-row">
        <view class="poster-info" @click="goToUserProfile">
          <view class="poster-avatar">
            <text>{{ getAvatarEmoji(demand.publisher?.id) }}</text>
          </view>
          <view>
            <text class="poster-name">{{ demand.publisher?.nickname || '用户' }}</text>
            <view class="poster-rating">
              <text>⭐⭐⭐⭐⭐</text>
              <text>{{ demand.publisher?.score || 5.0 }} · 发布{{ demand.publisher?.totalOrders || 0 }}单</text>
            </view>
          </view>
        </view>
        <button 
          :class="['follow-btn', isFollowed ? 'followed' : '']"
          @click="toggleFollow"
        >
          <text>{{ isFollowed ? '已关注' : '+ 关注' }}</text>
        </button>
      </view>
      
      <view class="divider"></view>
      
      <view class="reviews-section" v-if="demand.reviews?.length > 0">
        <text class="section-title">接单者评价</text>
        <view class="comment-card" v-for="review in demand.reviews" :key="review.id">
          <view class="comment-ava">
            <text>{{ getAvatarEmoji(review.reviewer?.id) }}</text>
          </view>
          <view class="comment-body">
            <text class="comment-name">{{ review.reviewer?.nickname || '用户' }}</text>
            <text class="comment-text">{{ review.content }}</text>
            <text class="comment-time">{{ formatTime(review.createdAt) }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="detail-bottom-bar safe-area-bottom">
      <view class="price-display">
        <view class="price">
          <text class="price-num">¥{{ demand.expectedPrice || 0 }}</text>
          <text class="price-unit">/{{ demand.priceUnit || '时' }}</text>
        </view>
        <text class="price-label">预计{{ demand.minDuration || 2 }}-{{ demand.maxDuration || 3 }}h · 约¥{{ (demand.expectedPrice || 0) * (demand.minDuration || 2) }}-{{ (demand.expectedPrice || 0) * (demand.maxDuration || 3) }}</text>
      </view>
      <button class="btn-chat" @click="handleChat">
        <text>💬</text>
      </button>
      <button 
        v-if="demand.status === 1" 
        class="btn-large" 
        @click="handleTakeOrder"
      >
        <text>立即接单</text>
      </button>
      <button 
        v-else 
        class="btn-large" 
        style="background: #9E9E9E; box-shadow: none;"
        disabled
      >
        <text>{{ demand.statusText || '已结束' }}</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { demandApi, orderApi, userApi } from '@/api'

const userStore = useUserStore()

const demandId = ref(null)
const demand = ref({})
const isFollowed = ref(false)
const loading = ref(false)

const getAvatarEmoji = (id) => {
  const emojis = ['👩', '👨', '👩‍🦳', '👨‍🦳', '👩‍🍳', '👨‍⚕️', '🧹', '👶']
  return emojis[(id || 0) % emojis.length]
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return time.substring(0, 10)
}

const loadDemandDetail = async () => {
  if (!demandId.value) return
  
  loading.value = true
  try {
    const res = await demandApi.getDetail(demandId.value)
    if (res.code === 200) {
      demand.value = res.data || {}
      isFollowed.value = res.data?.publisher?.isFollowed || false
      
      try {
        await demandApi.addFootprint(demandId.value)
      } catch (e) {
        console.log('添加足迹失败', e)
      }
    }
  } catch (e) {
    console.error('加载需求详情失败', e)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  uni.navigateBack()
}

const goToUserProfile = () => {
  if (demand.value.publisher?.id) {
    uni.navigateTo({
      url: `/pages/user-profile/user-profile?id=${demand.value.publisher.id}`
    })
  }
}

const toggleFollow = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  if (!demand.value.publisher?.id) return
  
  try {
    if (isFollowed.value) {
      const res = await userApi.unfollow(demand.value.publisher.id)
      if (res.code === 200) {
        isFollowed.value = false
        uni.showToast({
          title: '已取消关注',
          icon: 'success'
        })
      }
    } else {
      const res = await userApi.follow(demand.value.publisher.id)
      if (res.code === 200) {
        isFollowed.value = true
        uni.showToast({
          title: '关注成功',
          icon: 'success'
        })
      }
    }
  } catch (e) {
    console.error('关注操作失败', e)
  }
}

const handleChat = () => {
  uni.showToast({
    title: '聊天功能开发中',
    icon: 'none'
  })
}

const handleTakeOrder = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  uni.showModal({
    title: '确认接单',
    content: `确定要接取"${demand.value.title}"这个需求吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '接单中...' })
          const result = await orderApi.takeOrder(demandId.value, { remark: '' })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '接单成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.switchTab({
                url: '/pages/orders/orders'
              })
            }, 1500)
          }
        } catch (e) {
          uni.hideLoading()
          console.error('接单失败', e)
        }
      }
    }
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  demandId.value = currentPage.options?.id
  
  if (demandId.value) {
    loadDemandDetail()
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 160rpx;
}

.detail-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  padding: 80rpx 28rpx 40rpx;
}

.detail-nav {
  display: flex;
  align-items: center;
  gap: 16rpx;
  color: #FFFFFF;
  font-size: 30rpx;
  font-weight: 600;
}

.back-icon {
  font-size: 40rpx;
  opacity: 0.8;
}

.nav-title {
  font-size: 30rpx;
}

.detail-card {
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  margin-top: -24rpx;
  padding: 40rpx 28rpx 0;
  position: relative;
  z-index: 10;
}

.detail-badge-row {
  display: flex;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.detail-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #212121;
  margin-bottom: 16rpx;
  line-height: 1.4;
  display: block;
}

.detail-desc {
  font-size: 26rpx;
  color: #757575;
  line-height: 1.7;
  margin-bottom: 28rpx;
  display: block;
}

.detail-meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-bottom: 28rpx;
}

.detail-meta-item {
  background: #FAFAFA;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
}

.dmi-label {
  font-size: 20rpx;
  color: #9E9E9E;
  margin-bottom: 6rpx;
  display: block;
}

.dmi-val {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
  display: block;
}

.divider {
  height: 1rpx;
  background: #F5F5F5;
  margin: 28rpx 0;
}

.poster-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.poster-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.poster-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
}

.poster-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #424242;
  display: block;
}

.poster-rating {
  font-size: 22rpx;
  color: #9E9E9E;
  display: flex;
  align-items: center;
  gap: 4rpx;
  margin-top: 4rpx;
}

.follow-btn {
  background: transparent;
  border: 3rpx solid #2EC4B6;
  color: #2EC4B6;
  padding: 12rpx 28rpx;
  border-radius: 9999rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.follow-btn.followed {
  background: #F5F5F5;
  border-color: #E0E0E0;
  color: #9E9E9E;
}

.reviews-section {
  padding-bottom: 40rpx;
}

.section-title {
  font-size: 26rpx;
  font-weight: 700;
  color: #424242;
  margin-bottom: 20rpx;
  display: block;
}

.comment-card {
  display: flex;
  gap: 20rpx;
  margin-bottom: 28rpx;
}

.comment-ava {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #D0F0EE, #B0E8E4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-name {
  font-size: 24rpx;
  font-weight: 600;
  color: #616161;
  display: block;
}

.comment-text {
  font-size: 24rpx;
  color: #757575;
  line-height: 1.5;
  margin-top: 6rpx;
  display: block;
}

.comment-time {
  font-size: 20rpx;
  color: #9E9E9E;
  margin-top: 6rpx;
  display: block;
}

.detail-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  border-top: 1rpx solid #F5F5F5;
  padding: 20rpx 28rpx 48rpx;
  display: flex;
  gap: 20rpx;
  align-items: center;
  z-index: 100;
}

.price-display {
  flex: 1;
}

.price {
  display: flex;
  align-items: baseline;
}

.price-num {
  font-size: 44rpx;
  font-weight: 800;
  color: #FF6B35;
}

.price-unit {
  font-size: 26rpx;
  font-weight: 500;
  color: #9E9E9E;
}

.price-label {
  font-size: 22rpx;
  color: #9E9E9E;
  display: block;
  margin-top: 4rpx;
}

.btn-chat {
  background: #F5F5F5;
  color: #616161;
  border: none;
  padding: 26rpx 32rpx;
  border-radius: 9999rpx;
  font-size: 40rpx;
}

.btn-large {
  flex: 2;
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
  border: none;
  padding: 26rpx;
  border-radius: 9999rpx;
  font-size: 30rpx;
  font-weight: 700;
  box-shadow: 0 8rpx 40rpx rgba(255, 107, 53, 0.35);
}
</style>
