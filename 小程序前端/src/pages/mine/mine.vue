<template>
  <view class="mine-page">
    <view class="mine-header">
      <view class="mine-user-row" v-if="userStore.isLoggedIn">
        <view class="mine-avatar">
          <text v-if="userStore.userInfo?.avatarUrl">{{ userStore.userInfo.avatarUrl }}</text>
          <text v-else>😊</text>
        </view>
        <view class="mine-info">
          <text class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</text>
          <text class="user-desc">
            {{ userStore.userInfo?.certificationStatus === 2 ? '已认证' : '未认证' }}
            {{ userStore.userInfo?.bio || '' }}
          </text>
        </view>
        <button class="mine-edit-btn" @click="goToEdit">
          <text>编辑</text>
        </button>
      </view>
      
      <view class="mine-user-row" v-else @click="goToLogin">
        <view class="mine-avatar">
          <text>😊</text>
        </view>
        <view class="mine-info">
          <text class="user-name">点击登录</text>
          <text class="user-desc">登录后享受更多功能</text>
        </view>
      </view>
      
      <view class="mine-stats" v-if="userStore.isLoggedIn">
        <view class="stat-item" @click="goToPublishedOrders">
          <text class="stat-num">{{ userStore.userInfo?.publishedCount || 0 }}</text>
          <text class="stat-label">发布需求</text>
        </view>
        <view class="stat-item" @click="goToTakenOrders">
          <text class="stat-num">{{ userStore.userInfo?.takenCount || 0 }}</text>
          <text class="stat-label">接单记录</text>
        </view>
        <view class="stat-item" @click="goToFollowing">
          <text class="stat-num">{{ userStore.userInfo?.followingCount || 0 }}</text>
          <text class="stat-label">关注</text>
        </view>
        <view class="stat-item" @click="goToFootprints">
          <text class="stat-num">{{ userStore.userInfo?.followerCount || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
      </view>
    </view>
    
    <view class="mine-body">
      <view class="mine-card" v-if="userStore.isLoggedIn">
        <view class="mine-card-header">
          <text class="card-title">📋 我发布的需求</text>
          <text class="card-more" @click="goToPublishedOrders">查看全部</text>
        </view>
        <view 
          v-for="order in publishedOrders" 
          :key="order.id" 
          class="order-item"
          @click="goToOrderDetail(order.id)"
        >
          <view :class="['order-icon', 'oi' + ((order.demand?.category?.id || 1) % 3 + 1)]">
            <text>{{ getCategoryIcon(order.demand?.category?.id) }}</text>
          </view>
          <view class="order-detail">
            <text class="order-title">{{ order.demand?.title || '需求' }}</text>
            <text class="order-sub">
              {{ order.taker?.nickname ? order.taker.nickname + ' 接单' : '尚未有人接单' }}
              {{ order.serviceTime ? ' · ' + formatTime(order.serviceTime) : '' }}
            </text>
          </view>
          <view class="order-right">
            <text class="order-price">¥{{ order.actualPrice || order.demand?.expectedPrice || 0 }}{{ order.priceUnit ? '/' + order.priceUnit : '' }}</text>
            <view :class="['order-status', getStatusClass(order.status)]">
              <text>{{ getStatusText(order.status) }}</text>
            </view>
          </view>
        </view>
        <view v-if="publishedOrders.length === 0" class="empty-order">
          <text>暂无发布的需求</text>
        </view>
      </view>
      
      <view class="mine-card" v-if="userStore.isLoggedIn">
        <view class="mine-card-header">
          <text class="card-title">🤝 我接的单</text>
          <text class="card-more" @click="goToTakenOrders">查看全部</text>
        </view>
        <view 
          v-for="order in takenOrders" 
          :key="order.id" 
          class="order-item"
          @click="goToOrderDetail(order.id)"
        >
          <view :class="['order-icon', 'oi' + ((order.demand?.category?.id || 1) % 3 + 1)]">
            <text>{{ getCategoryIcon(order.demand?.category?.id) }}</text>
          </view>
          <view class="order-detail">
            <text class="order-title">{{ order.demand?.title || '需求' }}</text>
            <text class="order-sub">
              {{ order.publisher?.nickname ? order.publisher.nickname + ' 发布' : '' }}
              {{ order.status === 4 ? ' · 已完成' : '' }}
            </text>
          </view>
          <view class="order-right">
            <text class="order-price">¥{{ order.actualPrice || order.demand?.expectedPrice || 0 }}{{ order.priceUnit ? '/' + order.priceUnit : '' }}</text>
            <view :class="['order-status', getStatusClass(order.status)]">
              <text>{{ getStatusText(order.status) }}</text>
            </view>
          </view>
        </view>
        <view v-if="takenOrders.length === 0" class="empty-order">
          <text>暂无接单记录</text>
        </view>
      </view>
      
      <view class="mine-card">
        <view class="menu-item" @click="goToFootprints">
          <view class="menu-icon-wrap mi1">
            <text>👣</text>
          </view>
          <text class="menu-label">我的足迹</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToFollowing">
          <view class="menu-icon-wrap mi2">
            <text>❤️</text>
          </view>
          <text class="menu-label">我的关注</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToReviews">
          <view class="menu-icon-wrap mi3">
            <text>⭐</text>
          </view>
          <text class="menu-label">我的评价</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToMessages">
          <view class="menu-icon-wrap mi4">
            <text>🔔</text>
          </view>
          <text class="menu-label">消息通知</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToSettings">
          <view class="menu-icon-wrap mi5">
            <text>⚙️</text>
          </view>
          <text class="menu-label">设置</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
      
      <view class="logout-btn" v-if="userStore.isLoggedIn" @click="handleLogout">
        <text>退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { orderApi } from '@/api'

const userStore = useUserStore()

const publishedOrders = ref([])
const takenOrders = ref([])
const loading = ref(false)

const getCategoryIcon = (id) => {
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
}

const getStatusClass = (status) => {
  switch (status) {
    case 1: return 'os-pending'
    case 2: return 'os-active'
    case 3: return 'os-pending'
    case 4: return 'os-done'
    default: return 'os-pending'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 1: return '待服务'
    case 2: return '进行中'
    case 3: return '待确认'
    case 4: return '已完成'
    default: return '待接单'
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  return `${month}月${day}日 ${hour}:${minute.toString().padStart(2, '0')}`
}

const loadOrders = async () => {
  if (!userStore.isLoggedIn) return
  
  loading.value = true
  try {
    const [publishedRes, takenRes] = await Promise.all([
      orderApi.getPublished({ page: 1, pageSize: 3 }),
      orderApi.getTaken({ page: 1, pageSize: 3 })
    ])
    
    if (publishedRes.code === 200) {
      publishedOrders.value = publishedRes.data?.list || []
    }
    if (takenRes.code === 200) {
      takenOrders.value = takenRes.data?.list || []
    }
  } catch (e) {
    console.error('加载订单失败', e)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  })
}

const goToEdit = () => {
  uni.showToast({
    title: '编辑功能开发中',
    icon: 'none'
  })
}

const goToPublishedOrders = () => {
  uni.navigateTo({
    url: '/pages/orders/orders?type=published'
  })
}

const goToTakenOrders = () => {
  uni.navigateTo({
    url: '/pages/orders/orders?type=taken'
  })
}

const goToFollowing = () => {
  uni.navigateTo({
    url: '/pages/follow/follow?tab=following'
  })
}

const goToFootprints = () => {
  uni.navigateTo({
    url: '/pages/follow/follow?tab=footprint'
  })
}

const goToReviews = () => {
  uni.showToast({
    title: '评价功能开发中',
    icon: 'none'
  })
}

const goToMessages = () => {
  uni.switchTab({
    url: '/pages/messages/messages'
  })
}

const goToSettings = () => {
  uni.showToast({
    title: '设置功能开发中',
    icon: 'none'
  })
}

const goToOrderDetail = (id) => {
  uni.navigateTo({
    url: `/pages/order-detail/order-detail?id=${id}`
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    loadOrders()
    userStore.refreshUserInfo()
  }
})
</script>

<style scoped>
.mine-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 120rpx;
}

.mine-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  padding: 80rpx 28rpx 48rpx;
}

.mine-user-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.mine-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  border: 6rpx solid rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  flex-shrink: 0;
}

.mine-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-name {
  color: #FFFFFF;
  font-size: 34rpx;
  font-weight: 700;
  display: block;
}

.user-desc {
  color: rgba(255, 255, 255, 0.8);
  font-size: 24rpx;
  margin-top: 4rpx;
  display: block;
}

.mine-edit-btn {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.25);
  color: #FFFFFF;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  padding: 10rpx 24rpx;
  border-radius: 9999rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.mine-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin-top: 32rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 32rpx;
  overflow: hidden;
}

.stat-item {
  padding: 20rpx 0;
  text-align: center;
  border-right: 1rpx solid rgba(255, 255, 255, 0.2);
}

.stat-item:last-child {
  border-right: none;
}

.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
}

.stat-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 2rpx;
  display: block;
}

.mine-body {
  padding: 28rpx;
}

.mine-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.mine-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.card-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #424242;
}

.card-more {
  font-size: 24rpx;
  color: #FF6B35;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.order-item:last-child {
  border-bottom: none;
}

.order-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}

.oi1 { background: linear-gradient(135deg, #FFE0D0, #FFCDB0); }
.oi2 { background: linear-gradient(135deg, #D0F0EE, #B0E8E4); }
.oi3 { background: linear-gradient(135deg, #D0E8FF, #B0D4FF); }

.order-detail {
  flex: 1;
  min-width: 0;
}

.order-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-sub {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-right {
  text-align: right;
  flex-shrink: 0;
}

.order-price {
  font-size: 28rpx;
  font-weight: 700;
  color: #FF6B35;
  display: block;
}

.order-status {
  font-size: 20rpx;
  margin-top: 4rpx;
  padding: 4rpx 14rpx;
  border-radius: 9999rpx;
  display: inline-block;
}

.os-pending {
  background: #FFF8E1;
  color: #FF9800;
}

.os-done {
  background: #E8F5E9;
  color: #4CAF50;
}

.os-active {
  background: #FFF0EB;
  color: #FF6B35;
}

.empty-order {
  padding: 40rpx;
  text-align: center;
  color: #9E9E9E;
  font-size: 24rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  flex-shrink: 0;
}

.mi1 { background: linear-gradient(135deg, #FFE0D0, #FFCDB0); }
.mi2 { background: linear-gradient(135deg, #D0F0EE, #B0E8E4); }
.mi3 { background: linear-gradient(135deg, #D0E8FF, #B0D4FF); }
.mi4 { background: linear-gradient(135deg, #F0D0FF, #E0B0FF); }
.mi5 { background: linear-gradient(135deg, #D0FFE0, #B0F0C0); }

.menu-label {
  flex: 1;
  font-size: 28rpx;
  color: #424242;
  font-weight: 500;
}

.menu-arrow {
  font-size: 32rpx;
  color: #E0E0E0;
}

.logout-btn {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  text-align: center;
  color: #F44336;
  font-size: 28rpx;
  font-weight: 600;
  margin-top: 28rpx;
}
</style>
