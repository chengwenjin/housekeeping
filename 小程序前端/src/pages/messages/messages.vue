<template>
  <view class="messages-page">
    <view class="messages-header">
      <text class="header-title">消息通知</text>
      <view class="header-action" v-if="unreadCount > 0" @click="markAllRead">
        <text>一键已读</text>
      </view>
    </view>
    
    <view class="message-tabs">
      <view 
        :class="['tab-item', activeTab === 'all' ? 'active' : '']"
        @click="switchTab('all')"
      >
        <text>全部</text>
        <view v-if="unreadCount > 0" class="tab-badge">{{ unreadCount }}</view>
      </view>
      <view 
        :class="['tab-item', activeTab === 'system' ? 'active' : '']"
        @click="switchTab('system')"
      >
        <text>系统</text>
      </view>
      <view 
        :class="['tab-item', activeTab === 'order' ? 'active' : '']"
        @click="switchTab('order')"
      >
        <text>订单</text>
      </view>
      <view 
        :class="['tab-item', activeTab === 'review' ? 'active' : '']"
        @click="switchTab('review')"
      >
        <text>评价</text>
      </view>
    </view>
    
    <scroll-view 
      class="messages-list" 
      scroll-y
      @scrolltolower="loadMore"
    >
      <view 
        v-for="msg in messageList" 
        :key="msg.id" 
        :class="['message-item', !msg.isRead ? 'unread' : '']"
        @click="handleMessageClick(msg)"
      >
        <view class="message-icon">
          <text>{{ getMessageIcon(msg.type) }}</text>
        </view>
        <view class="message-content">
          <view class="message-header">
            <text class="message-title">{{ msg.title }}</text>
            <text class="message-time">{{ formatTime(msg.createdAt) }}</text>
          </view>
          <text class="message-desc">{{ msg.content }}</text>
        </view>
        <view v-if="!msg.isRead" class="unread-dot"></view>
      </view>
      
      <view v-if="messageList.length === 0 && !loading" class="empty-text">
        <text>暂无消息</text>
      </view>
      
      <view v-if="loading" class="loading-text">
        <text>加载中...</text>
      </view>
      
      <view v-if="!loading && !hasMore && messageList.length > 0" class="no-more-text">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { messageApi } from '@/api'

const userStore = useUserStore()

const activeTab = ref('all')
const messageList = ref([])
const unreadCount = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = ref(10)

const getMessageIcon = (type) => {
  const icons = {
    1: '🔔',
    2: '📋',
    3: '⭐',
    4: '💬'
  }
  return icons[type] || '🔔'
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
  
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

const switchTab = (tab) => {
  activeTab.value = tab
  page.value = 1
  hasMore.value = true
  messageList.value = []
  loadMessages()
}

const loadMessages = async () => {
  if (!userStore.isLoggedIn) return
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    
    if (activeTab.value !== 'all') {
      params.type = activeTab.value
    }
    
    const res = await messageApi.getList(params)
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      messageList.value = [...messageList.value, ...newList]
      
      unreadCount.value = res.data?.unreadCount || 0
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载消息失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadMessages()
  }
}

const markAllRead = async () => {
  try {
    const res = await messageApi.markAllRead()
    if (res.code === 200) {
      unreadCount.value = 0
      messageList.value.forEach(msg => {
        msg.isRead = true
      })
      uni.showToast({
        title: '已全部标记为已读',
        icon: 'success'
      })
    }
  } catch (e) {
    console.error('标记已读失败', e)
  }
}

const handleMessageClick = async (msg) => {
  if (!msg.isRead) {
    try {
      await messageApi.markRead(msg.id)
      msg.isRead = true
      if (unreadCount.value > 0) {
        unreadCount.value--
      }
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  
  if (msg.relatedType === 'order' && msg.relatedId) {
    uni.navigateTo({
      url: `/pages/order-detail/order-detail?id=${msg.relatedId}`
    })
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    loadMessages()
  }
})
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 120rpx;
}

.messages-header {
  background: #FFFFFF;
  padding: 80rpx 28rpx 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #F5F5F5;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #212121;
}

.header-action {
  font-size: 26rpx;
  color: #FF6B35;
  font-weight: 500;
}

.message-tabs {
  display: flex;
  background: #FFFFFF;
  padding: 0 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 26rpx;
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
  width: 48rpx;
  height: 6rpx;
  background: #FF6B35;
  border-radius: 3rpx;
}

.tab-badge {
  position: absolute;
  top: 16rpx;
  right: 20rpx;
  background: #F44336;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 9999rpx;
  min-width: 32rpx;
}

.messages-list {
  height: calc(100vh - 320rpx);
}

.message-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 28rpx;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F5F5F5;
  position: relative;
}

.message-item.unread {
  background: #FFF9F5;
}

.message-icon {
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

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.message-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #212121;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 400rpx;
}

.message-time {
  font-size: 22rpx;
  color: #9E9E9E;
  flex-shrink: 0;
}

.message-desc {
  font-size: 24rpx;
  color: #757575;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}

.unread-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #F44336;
  flex-shrink: 0;
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
