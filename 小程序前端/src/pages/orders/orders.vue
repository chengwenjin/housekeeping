<template>
  <view class="orders-page">
    <view class="orders-header">
      <text class="header-title">订单列表</text>
    </view>
    
    <view class="order-tabs">
      <view 
        :class="['tab-item', orderType === 'published' ? 'active' : '']"
        @click="switchOrderType('published')"
      >
        <text>我发布的</text>
      </view>
      <view 
        :class="['tab-item', orderType === 'taken' ? 'active' : '']"
        @click="switchOrderType('taken')"
      >
        <text>我接的单</text>
      </view>
    </view>
    
    <view class="status-tabs">
      <view 
        v-for="status in statusOptions" 
        :key="status.value"
        :class="['status-item', currentStatus === status.value ? 'active' : '']"
        @click="selectStatus(status.value)"
      >
        <text>{{ status.label }}</text>
      </view>
    </view>
    
    <scroll-view 
      class="orders-list" 
      scroll-y
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view 
        v-for="order in orderList" 
        :key="order.id" 
        class="order-card"
        @click="goToOrderDetail(order.id)"
      >
        <view class="order-card-header">
          <view class="order-info">
            <view :class="['order-icon', 'oi' + ((order.demand?.category?.id || 1) % 3 + 1)]">
              <text>{{ getCategoryIcon(order.demand?.category?.id) }}</text>
            </view>
            <view class="order-basic">
              <text class="order-title">{{ order.demand?.title || '需求' }}</text>
              <text class="order-no">订单号: {{ order.orderNo }}</text>
            </view>
          </view>
          <view :class="['order-status-badge', getStatusBadgeClass(order.status)]">
            <text>{{ getStatusText(order.status) }}</text>
          </view>
        </view>
        
        <view class="order-card-body">
          <view class="order-meta-row" v-if="orderType === 'published' && order.taker">
            <text class="meta-label">接单者:</text>
            <text class="meta-value">{{ order.taker?.nickname || '用户' }}</text>
          </view>
          <view class="order-meta-row" v-else-if="orderType === 'taken' && order.publisher">
            <text class="meta-label">发布者:</text>
            <text class="meta-value">{{ order.publisher?.nickname || '用户' }}</text>
          </view>
          
          <view class="order-meta-row" v-if="order.serviceTime">
            <text class="meta-label">服务时间:</text>
            <text class="meta-value">{{ formatTime(order.serviceTime) }}</text>
          </view>
          
          <view class="order-meta-row" v-if="order.location?.address">
            <text class="meta-label">服务地址:</text>
            <text class="meta-value">{{ order.location.district }} {{ order.location.address }}</text>
          </view>
        </view>
        
        <view class="order-card-footer">
          <view class="order-price">
            <text class="price-num">¥{{ order.actualPrice || order.demand?.expectedPrice || 0 }}</text>
            <text class="price-unit">/{{ order.priceUnit || order.demand?.priceUnit || '小时' }}</text>
          </view>
          <view class="order-actions">
            <button 
              v-if="order.status === 1 && orderType === 'taken'"
              class="action-btn primary"
              @click.stop="startService(order)"
            >
              <text>开始服务</text>
            </button>
            <button 
              v-if="order.status === 2 && orderType === 'taken'"
              class="action-btn primary"
              @click.stop="completeService(order)"
            >
              <text>确认完成</text>
            </button>
            <button 
              v-if="order.status === 3 && orderType === 'published'"
              class="action-btn primary"
              @click.stop="confirmComplete(order)"
            >
              <text>确认完成</text>
            </button>
            <button 
              v-if="order.status === 4"
              class="action-btn outline"
              @click.stop="goToReview(order)"
            >
              <text>去评价</text>
            </button>
            <button 
              v-if="order.status === 1 || order.status === 2"
              class="action-btn outline"
              @click.stop="cancelOrder(order)"
            >
              <text>取消订单</text>
            </button>
          </view>
        </view>
      </view>
      
      <view v-if="orderList.length === 0 && !loading" class="empty-text">
        <text>暂无订单</text>
      </view>
      
      <view v-if="loading" class="loading-text">
        <text>加载中...</text>
      </view>
      
      <view v-if="!loading && !hasMore && orderList.length > 0" class="no-more-text">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { orderApi } from '@/api'

const userStore = useUserStore()

const orderType = ref('published')
const currentStatus = ref(null)
const orderList = ref([])
const loading = ref(false)
const refreshing = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = ref(10)

const statusOptions = [
  { label: '全部', value: null },
  { label: '待服务', value: 1 },
  { label: '进行中', value: 2 },
  { label: '待确认', value: 3 },
  { label: '已完成', value: 4 }
]

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

const getStatusText = (status) => {
  const texts = {
    1: '待服务',
    2: '进行中',
    3: '待确认',
    4: '已完成'
  }
  return texts[status] || '未知'
}

const getStatusBadgeClass = (status) => {
  const classes = {
    1: 'status-pending',
    2: 'status-active',
    3: 'status-confirm',
    4: 'status-done'
  }
  return classes[status] || 'status-pending'
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

const switchOrderType = (type) => {
  orderType.value = type
  page.value = 1
  hasMore.value = true
  orderList.value = []
  loadOrders()
}

const selectStatus = (status) => {
  currentStatus.value = status
  page.value = 1
  hasMore.value = true
  orderList.value = []
  loadOrders()
}

const loadOrders = async () => {
  if (!userStore.isLoggedIn) return
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    
    if (currentStatus.value !== null) {
      params.status = currentStatus.value
    }
    
    let res
    if (orderType.value === 'published') {
      res = await orderApi.getPublished(params)
    } else {
      res = await orderApi.getTaken(params)
    }
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      orderList.value = [...orderList.value, ...newList]
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载订单失败', e)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onRefresh = () => {
  refreshing.value = true
  page.value = 1
  hasMore.value = true
  orderList.value = []
  loadOrders()
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadOrders()
  }
}

const goToOrderDetail = (id) => {
  uni.navigateTo({
    url: `/pages/order-detail/order-detail?id=${id}`
  })
}

const startService = async (order) => {
  uni.showModal({
    title: '确认开始服务',
    content: '确定要开始服务吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(order.id, {
            status: 2,
            remark: '开始服务'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '已开始服务',
              icon: 'success'
            })
            onRefresh()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('开始服务失败', e)
        }
      }
    }
  })
}

const completeService = async (order) => {
  uni.showModal({
    title: '确认完成服务',
    content: '确定要确认完成服务吗？请等待雇主确认。',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(order.id, {
            status: 3,
            remark: '服务已完成'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '已提交确认',
              icon: 'success'
            })
            onRefresh()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('确认完成失败', e)
        }
      }
    }
  })
}

const confirmComplete = async (order) => {
  uni.showModal({
    title: '确认订单完成',
    content: '确定要确认订单完成吗？确认后可以进行评价。',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.updateStatus(order.id, {
            status: 4,
            remark: '订单已完成'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '订单已完成',
              icon: 'success'
            })
            onRefresh()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('确认订单失败', e)
        }
      }
    }
  })
}

const cancelOrder = async (order) => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '处理中...' })
          const result = await orderApi.cancel(order.id, {
            reason: '用户主动取消',
            cancelBy: orderType.value === 'published' ? 'publisher' : 'taker'
          })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '订单已取消',
              icon: 'success'
            })
            onRefresh()
          }
        } catch (e) {
          uni.hideLoading()
          console.error('取消订单失败', e)
        }
      }
    }
  })
}

const goToReview = (order) => {
  uni.showToast({
    title: '评价功能开发中',
    icon: 'none'
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const type = currentPage.options?.type
  
  if (type === 'taken') {
    orderType.value = 'taken'
  }
  
  if (userStore.isLoggedIn) {
    loadOrders()
  }
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 120rpx;
}

.orders-header {
  background: #FFFFFF;
  padding: 80rpx 28rpx 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #212121;
}

.order-tabs {
  display: flex;
  background: #FFFFFF;
  padding: 0 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 28rpx 0;
  font-size: 28rpx;
  color: #757575;
  position: relative;
}

.tab-item.active {
  color: #FF6B35;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background: #FF6B35;
  border-radius: 3rpx;
}

.status-tabs {
  display: flex;
  background: #FFFFFF;
  padding: 20rpx 28rpx;
  overflow-x: auto;
  white-space: nowrap;
  border-bottom: 1rpx solid #F5F5F5;
}

.status-item {
  flex-shrink: 0;
  padding: 12rpx 28rpx;
  margin-right: 16rpx;
  border-radius: 9999rpx;
  font-size: 24rpx;
  color: #757575;
  background: #F5F5F5;
}

.status-item.active {
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
}

.orders-list {
  height: calc(100vh - 400rpx);
  padding: 28rpx;
}

.order-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex: 1;
  min-width: 0;
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

.order-basic {
  flex: 1;
  min-width: 0;
}

.order-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #212121;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-no {
  font-size: 20rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
}

.order-status-badge {
  font-size: 22rpx;
  font-weight: 600;
  padding: 6rpx 16rpx;
  border-radius: 9999rpx;
  flex-shrink: 0;
}

.status-pending {
  background: #FFF8E1;
  color: #FF9800;
}

.status-active {
  background: #FFF0EB;
  color: #FF6B35;
}

.status-confirm {
  background: #E3F2FD;
  color: #2196F3;
}

.status-done {
  background: #E8F5E9;
  color: #4CAF50;
}

.order-card-body {
  padding: 20rpx 0;
  border-top: 1rpx solid #F5F5F5;
  border-bottom: 1rpx solid #F5F5F5;
}

.order-meta-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.order-meta-row:last-child {
  margin-bottom: 0;
}

.meta-label {
  font-size: 24rpx;
  color: #9E9E9E;
  width: 140rpx;
  flex-shrink: 0;
}

.meta-value {
  font-size: 24rpx;
  color: #424242;
  flex: 1;
}

.order-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
}

.order-price {
  display: flex;
  align-items: baseline;
}

.price-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #FF6B35;
}

.price-unit {
  font-size: 24rpx;
  color: #9E9E9E;
}

.order-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  padding: 12rpx 28rpx;
  border-radius: 9999rpx;
  font-size: 24rpx;
  font-weight: 600;
  border: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
}

.action-btn.outline {
  background: transparent;
  color: #FF6B35;
  border: 2rpx solid #FF6B35;
}

.empty-text,
.loading-text,
.no-more-text {
  text-align: center;
  padding: 80rpx 40rpx;
  color: #9E9E9E;
  font-size: 24rpx;
}
</style>
