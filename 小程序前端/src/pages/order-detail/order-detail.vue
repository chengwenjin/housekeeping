<template>
  <view class="order-detail-page">
    <view class="order-header">
      <view class="order-nav" @click="goBack">
        <text class="back-icon">‹</text>
        <text class="nav-title">订单详情</text>
      </view>
    </view>
    
    <view class="status-section">
      <view class="status-icon">
        <text>{{ getStatusIcon(order.status) }}</text>
      </view>
      <text class="status-text">{{ getStatusText(order.status) }}</text>
      <text class="status-desc">{{ getStatusDesc(order.status) }}</text>
    </view>
    
    <view class="info-section">
      <view class="section-title">
        <text>📋 需求信息</text>
      </view>
      <view class="info-item">
        <text class="info-label">需求标题</text>
        <text class="info-value">{{ order.demand?.title || '暂无' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">需求描述</text>
        <text class="info-value">{{ order.demand?.description || '暂无' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">服务类型</text>
        <text class="info-value">{{ order.demand?.category?.name || '暂无' }}</text>
      </view>
    </view>
    
    <view class="info-section">
      <view class="section-title">
        <text>💰 费用信息</text>
      </view>
      <view class="info-item">
        <text class="info-label">预期价格</text>
        <text class="info-value price">¥{{ order.actualPrice || order.demand?.expectedPrice || 0 }}/{{ order.priceUnit || order.demand?.priceUnit || '小时' }}</text>
      </view>
      <view class="info-item" v-if="order.totalAmount">
        <text class="info-label">实际金额</text>
        <text class="info-value price">¥{{ order.totalAmount }}</text>
      </view>
    </view>
    
    <view class="info-section">
      <view class="section-title">
        <text>📍 服务信息</text>
      </view>
      <view class="info-item">
        <text class="info-label">服务时间</text>
        <text class="info-value">{{ order.serviceTime ? formatTime(order.serviceTime) : '待定' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">服务地址</text>
        <text class="info-value">{{ order.location?.district || '' }} {{ order.location?.address || '暂无' }}</text>
      </view>
    </view>
    
    <view class="info-section">
      <view class="section-title">
        <text>👥 双方信息</text>
      </view>
      <view class="user-card" v-if="order.publisher">
        <view class="user-avatar">
          <text>{{ getAvatarEmoji(order.publisher?.id) }}</text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ order.publisher?.nickname || '用户' }}</text>
          <text class="user-role">发布者</text>
        </view>
        <view class="user-contact" v-if="order.publisher?.phone" @click="callPhone(order.publisher.phone)">
          <text>📞</text>
        </view>
      </view>
      <view class="user-card" v-if="order.taker">
        <view class="user-avatar">
          <text>{{ getAvatarEmoji(order.taker?.id) }}</text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ order.taker?.nickname || '用户' }}</text>
          <text class="user-role">接单者</text>
        </view>
        <view class="user-contact" v-if="order.taker?.phone" @click="callPhone(order.taker.phone)">
          <text>📞</text>
        </view>
      </view>
    </view>
    
    <view class="info-section" v-if="order.remark">
      <view class="section-title">
        <text>📝 备注信息</text>
      </view>
      <view class="info-item">
        <text class="info-value">{{ order.remark }}</text>
      </view>
    </view>
    
    <view class="bottom-actions safe-area-bottom">
      <button 
        v-if="order.status === 1 && isTaker"
        class="action-btn primary"
        @click="startService"
      >
        <text>开始服务</text>
      </button>
      <button 
        v-if="order.status === 2 && isTaker"
        class="action-btn primary"
        @click="completeService"
      >
        <text>确认完成</text>
      </button>
      <button 
        v-if="order.status === 3 && isPublisher"
        class="action-btn primary"
        @click="confirmComplete"
      >
        <text>确认完成</text>
      </button>
      <button 
        v-if="order.status === 4 && !hasReviewed"
        class="action-btn primary"
        @click="goToReview"
      >
        <text>去评价</text>
      </button>
      <button 
        v-if="order.status === 1 || order.status === 2"
        class="action-btn outline"
        @click="cancelOrder"
      >
        <text>取消订单</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { orderApi } from '@/api'

const userStore = useUserStore()

const orderId = ref(null)
const order = ref({})
const loading = ref(false)
const hasReviewed = ref(false)

const isPublisher = computed(() => {
  return order.value.publisher?.id === userStore.userInfo?.id
})

const isTaker = computed(() => {
  return order.value.taker?.id === userStore.userInfo?.id
})

const getStatusIcon = (status) => {
  const icons = {
    1: '⏳',
    2: '🔄',
    3: '✅',
    4: '🎉'
  }
  return icons[status] || '📋'
}

const getStatusText = (status) => {
  const texts = {
    1: '待服务',
    2: '进行中',
    3: '待确认',
    4: '已完成'
  }
  return texts[status] || '未知状态'
}

const getStatusDesc = (status) => {
  const descs = {
    1: '等待服务者开始服务',
    2: '服务进行中',
    3: '等待发布者确认完成',
    4: '订单已完成'
  }
  return descs[status] || ''
}

const getAvatarEmoji = (id) => {
  const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
  return emojis[(id || 0) % emojis.length]
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  return `${year}年${month}月${day}日 ${hour}:${minute.toString().padStart(2, '0')}`
}

const loadOrderDetail = async () => {
  if (!orderId.value) return
  
  loading.value = true
  try {
    const res = await orderApi.getDetail(orderId.value)
    if (res.code === 200) {
      order.value = res.data || {}
    }
  } catch (e) {
    console.error('加载订单详情失败', e)
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

const callPhone = (phone) => {
  uni.makePhoneCall({
    phoneNumber: phone.replace(/\*/g, '0')
  })
}

const startService = async () => {
  uni.showModal({
    title: '确认开始服务',
    content: '确定要开始服务吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(orderId.value, {
            status: 2,
            remark: '开始服务'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '已开始服务',
              icon: 'success'
            })
            loadOrderDetail()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('开始服务失败', e)
        }
      }
    }
  })
}

const completeService = async () => {
  uni.showModal({
    title: '确认完成服务',
    content: '确定要确认完成服务吗？请等待雇主确认。',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(orderId.value, {
            status: 3,
            remark: '服务已完成'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '已提交确认',
              icon: 'success'
            })
            loadOrderDetail()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('确认完成失败', e)
        }
      }
    }
  })
}

const confirmComplete = async () => {
  uni.showModal({
    title: '确认订单完成',
    content: '确定要确认订单完成吗？确认后可以进行评价。',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(orderId.value, {
            status: 4,
            remark: '订单已完成'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '订单已完成',
              icon: 'success'
            })
            loadOrderDetail()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('确认订单失败', e)
        }
      }
    }
  })
}

const cancelOrder = async () => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.cancel(orderId.value, {
            reason: '用户主动取消',
            cancelBy: isPublisher.value ? 'publisher' : 'taker'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '订单已取消',
              icon: 'success'
            })
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          }
        } catch (e) {
          uni.hideLoading()
          console.error('取消订单失败', e)
        }
      }
    }
  })
}

const goToReview = () => {
  uni.showToast({
    title: '评价功能开发中',
    icon: 'none'
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  orderId.value = currentPage.options?.id
  
  if (orderId.value) {
    loadOrderDetail()
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 160rpx;
}

.order-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  padding: 80rpx 28rpx 40rpx;
}

.order-nav {
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

.status-section {
  background: #FFFFFF;
  padding: 40rpx 28rpx;
  text-align: center;
  border-radius: 0 0 40rpx 40rpx;
  margin-bottom: 24rpx;
}

.status-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.status-text {
  font-size: 36rpx;
  font-weight: 700;
  color: #212121;
  display: block;
}

.status-desc {
  font-size: 24rpx;
  color: #9E9E9E;
  margin-top: 8rpx;
  display: block;
}

.info-section {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  margin-left: 28rpx;
  margin-right: 28rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #424242;
  margin-bottom: 24rpx;
  display: block;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F5F5F5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 26rpx;
  color: #9E9E9E;
  flex-shrink: 0;
  width: 160rpx;
}

.info-value {
  font-size: 26rpx;
  color: #424242;
  text-align: right;
  flex: 1;
  line-height: 1.5;
}

.info-value.price {
  color: #FF6B35;
  font-weight: 600;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F5F5F5;
}

.user-card:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #424242;
  display: block;
}

.user-role {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
}

.user-contact {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #E8F5E9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  flex-shrink: 0;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 20rpx 28rpx 48rpx;
  display: flex;
  gap: 20rpx;
  border-top: 1rpx solid #F5F5F5;
}

.action-btn {
  flex: 1;
  padding: 26rpx;
  border-radius: 9999rpx;
  font-size: 30rpx;
  font-weight: 600;
  border: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
  box-shadow: 0 8rpx 40rpx rgba(255, 107, 53, 0.35);
}

.action-btn.outline {
  background: transparent;
  color: #FF6B35;
  border: 3rpx solid #FF6B35;
}
</style>
