<template>
  <view class="follow-page">
    <view class="follow-header">
      <view class="follow-nav-row">
        <view class="back-btn" @click="goBack">
          <text>‹</text>
        </view>
        <text class="header-title">足迹 / 关注</text>
      </view>
      <view class="follow-tabs">
        <view 
          :class="['ftab', activeTab === 'footprint' ? 'active' : '']"
          @click="switchTab('footprint')"
        >
          <text>我的足迹</text>
        </view>
        <view 
          :class="['ftab', activeTab === 'following' ? 'active' : '']"
          @click="switchTab('following')"
        >
          <text>我的关注</text>
        </view>
      </view>
    </view>
    
    <scroll-view 
      class="follow-body" 
      scroll-y
      @scrolltolower="loadMore"
    >
      <view v-if="activeTab === 'footprint'">
        <view class="date-group" v-for="group in footprintGroups" :key="group.date">
          <text class="date-label">{{ group.date }}</text>
          <view 
            v-for="item in group.items" 
            :key="item.id" 
            class="footprint-item"
            @click="goToDemand(item.id)"
          >
            <view class="fp-icon">
              <text>{{ getCategoryIcon(item.category?.id) }}</text>
            </view>
            <view class="fp-info">
              <text class="fp-title">{{ item.title }}</text>
              <text class="fp-meta">
                📍 {{ item.location?.district || '' }} · {{ item.browsedAtText || '' }}
              </text>
              <text class="fp-price">¥{{ item.expectedPrice || 0 }}/{{ item.priceUnit || '时' }}</text>
            </view>
          </view>
        </view>
        
        <view v-if="footprintList.length === 0 && !loading" class="empty-text">
          <text>暂无足迹记录</text>
        </view>
      </view>
      
      <view v-if="activeTab === 'following'">
        <view 
          v-for="user in followingList" 
          :key="user.id" 
          class="follow-user-item"
        >
          <view class="fu-avatar fua1" @click="goToUserProfile(user.id)">
            <text>{{ getAvatarEmoji(user.id) }}</text>
          </view>
          <view class="fu-info" @click="goToUserProfile(user.id)">
            <text class="fu-name">{{ user.nickname || '用户' }}</text>
            <text class="fu-meta">
              {{ user.bio || '暂无简介' }} · 接单{{ user.totalOrders || 0 }}次 ⭐{{ user.score || 5.0 }}
            </text>
          </view>
          <button 
            :class="['follow-action-btn', user.isFollowed ? 'followed' : '']"
            @click="toggleFollow(user)"
          >
            <text>{{ user.isFollowed ? '已关注' : '+ 关注' }}</text>
          </button>
        </view>
        
        <view v-if="followingList.length === 0 && !loading" class="empty-text">
          <text>暂无关注的用户</text>
        </view>
      </view>
      
      <view v-if="loading" class="loading-text">
        <text>加载中...</text>
      </view>
      
      <view v-if="!loading && !hasMore && (footprintList.length > 0 || followingList.length > 0)" class="no-more-text">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { footprintApi, userApi } from '@/api'

const userStore = useUserStore()

const activeTab = ref('footprint')
const footprintList = ref([])
const followingList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = ref(10)

const footprintGroups = computed(() => {
  const groups = {}
  const today = new Date().toDateString()
  const yesterday = new Date(Date.now() - 86400000).toDateString()
  
  footprintList.value.forEach(item => {
    const itemDate = new Date(item.browsedAt || item.createdAt).toDateString()
    let groupKey = '更早'
    
    if (itemDate === today) {
      groupKey = '今天'
    } else if (itemDate === yesterday) {
      groupKey = '昨天'
    }
    
    if (!groups[groupKey]) {
      groups[groupKey] = { date: groupKey, items: [] }
    }
    groups[groupKey].items.push(item)
  })
  
  return Object.values(groups)
})

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

const getAvatarEmoji = (id) => {
  const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
  return emojis[(id || 0) % emojis.length]
}

const switchTab = (tab) => {
  activeTab.value = tab
  page.value = 1
  hasMore.value = true
  if (tab === 'footprint') {
    footprintList.value = []
    loadFootprints()
  } else {
    followingList.value = []
    loadFollowing()
  }
}

const loadFootprints = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    const res = await footprintApi.getList({
      page: page.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      footprintList.value = [...footprintList.value, ...newList]
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载足迹失败', e)
  } finally {
    loading.value = false
  }
}

const loadFollowing = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    const res = await userApi.getFollowing({
      page: page.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      followingList.value = [...followingList.value, ...newList.map(item => ({ ...item, isFollowed: true }))]
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载关注列表失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    if (activeTab.value === 'footprint') {
      loadFootprints()
    } else {
      loadFollowing()
    }
  }
}

const goBack = () => {
  uni.navigateBack()
}

const goToDemand = (id) => {
  uni.navigateTo({
    url: `/pages/demand/detail?id=${id}`
  })
}

const goToUserProfile = (id) => {
  uni.navigateTo({
    url: `/pages/user-profile/user-profile?id=${id}`
  })
}

const toggleFollow = async (user) => {
  try {
    if (user.isFollowed) {
      const res = await userApi.unfollow(user.id)
      if (res.code === 200) {
        user.isFollowed = false
        uni.showToast({
          title: '已取消关注',
          icon: 'success'
        })
      }
    } else {
      const res = await userApi.follow(user.id)
      if (res.code === 200) {
        user.isFollowed = true
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

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const tab = currentPage.options?.tab
  
  if (tab === 'following') {
    activeTab.value = 'following'
    loadFollowing()
  } else {
    activeTab.value = 'footprint'
    loadFootprints()
  }
})
</script>

<style scoped>
.follow-page {
  min-height: 100vh;
  background: #FAFAFA;
}

.follow-header {
  background: #FFFFFF;
  padding: 80rpx 28rpx 0;
  border-bottom: 1rpx solid #E0E0E0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.follow-nav-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding-bottom: 0;
}

.back-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  flex-shrink: 0;
  margin-bottom: 0;
}

.header-title {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 0;
}

.follow-tabs {
  display: flex;
  margin-top: 20rpx;
}

.ftab {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 26rpx;
  font-weight: 500;
  color: #9E9E9E;
  border-bottom: 4rpx solid transparent;
}

.ftab.active {
  color: #FF6B35;
  font-weight: 700;
  border-bottom: 4rpx solid #FF6B35;
}

.follow-body {
  padding: 28rpx;
  height: calc(100vh - 300rpx);
}

.date-group {
  margin-bottom: 28rpx;
}

.date-label {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-bottom: 16rpx;
  display: block;
}

.footprint-item {
  display: flex;
  gap: 20rpx;
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.fp-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  flex-shrink: 0;
}

.fp-info {
  flex: 1;
  min-width: 0;
}

.fp-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fp-meta {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fp-price {
  font-size: 26rpx;
  font-weight: 700;
  color: #FF6B35;
  margin-top: 6rpx;
  display: block;
}

.follow-user-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.fu-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  flex-shrink: 0;
}

.fua1 { background: linear-gradient(135deg, #FFE0D0, #FFCDB0); }
.fua2 { background: linear-gradient(135deg, #D0F0EE, #B0E8E4); }
.fua3 { background: linear-gradient(135deg, #D0E8FF, #B0D4FF); }

.fu-info {
  flex: 1;
  min-width: 0;
}

.fu-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
  display: block;
}

.fu-meta {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.follow-action-btn {
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
  border: none;
  padding: 12rpx 28rpx;
  border-radius: 9999rpx;
  font-size: 22rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.follow-action-btn.followed {
  background: #F5F5F5;
  color: #9E9E9E;
  box-shadow: none;
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
