<template>
  <view class="user-profile-page">
    <view class="profile-header">
      <view class="profile-nav" @click="goBack">
        <text class="back-icon">‹</text>
        <text class="nav-title">用户主页</text>
      </view>
    </view>
    
    <view class="user-info-section">
      <view class="user-avatar-large">
        <text>{{ getAvatarEmoji(userInfo.id) }}</text>
      </view>
      <text class="user-name">{{ userInfo.nickname || '用户' }}</text>
      <text class="user-bio">{{ userInfo.bio || '暂无简介' }}</text>
      
      <view class="user-stats">
        <view class="stat-item">
          <text class="stat-num">{{ userInfo.totalOrders || 0 }}</text>
          <text class="stat-label">接单次数</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ userInfo.score || 5.0 }}</text>
          <text class="stat-label">评分</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ userInfo.followerCount || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
      </view>
      
      <view class="action-buttons" v-if="userStore.isLoggedIn && userInfo.id !== userStore.userInfo?.id">
        <button 
          :class="['follow-btn', isFollowed ? 'followed' : '']"
          @click="toggleFollow"
        >
          <text>{{ isFollowed ? '已关注' : '+ 关注' }}</text>
        </button>
        <button class="chat-btn" @click="handleChat">
          <text>💬 私信</text>
        </button>
      </view>
    </view>
    
    <view class="tabs-section">
      <view 
        :class="['tab-item', activeTab === 'reviews' ? 'active' : '']"
        @click="switchTab('reviews')"
      >
        <text>用户评价</text>
      </view>
      <view 
        :class="['tab-item', activeTab === 'categories' ? 'active' : '']"
        @click="switchTab('categories')"
      >
        <text>服务类型</text>
      </view>
    </view>
    
    <scroll-view 
      class="content-section" 
      scroll-y
      @scrolltolower="loadMore"
    >
      <view v-if="activeTab === 'reviews'">
        <view 
          v-for="review in reviewList" 
          :key="review.id" 
          class="review-card"
        >
          <view class="review-header">
            <view class="reviewer-info">
              <view class="reviewer-avatar">
                <text>{{ getAvatarEmoji(review.reviewer?.id) }}</text>
              </view>
              <view>
                <text class="reviewer-name">{{ review.reviewer?.nickname || '用户' }}</text>
                <view class="review-rating">
                  <text v-for="i in 5" :key="i" :class="['star', i <= review.rating ? 'active' : '']">★</text>
                </view>
              </view>
            </view>
            <text class="review-time">{{ formatTime(review.createdAt) }}</text>
          </view>
          <text class="review-content">{{ review.content }}</text>
          <view class="review-images" v-if="review.images?.length > 0">
            <view 
              v-for="(img, index) in review.images" 
              :key="index" 
              class="review-image"
            >
              <text>📷</text>
            </view>
          </view>
          <view class="review-reply" v-if="review.replyContent">
            <text class="reply-label">回复:</text>
            <text class="reply-content">{{ review.replyContent }}</text>
          </view>
        </view>
        
        <view v-if="reviewList.length === 0 && !loading" class="empty-text">
          <text>暂无评价</text>
        </view>
      </view>
      
      <view v-if="activeTab === 'categories'">
        <view class="categories-grid">
          <view 
            v-for="cat in categories" 
            :key="cat.id" 
            class="category-item"
          >
            <view class="category-icon">
              <text>{{ cat.icon }}</text>
            </view>
            <text class="category-name">{{ cat.name }}</text>
          </view>
        </view>
        
        <view v-if="categories.length === 0" class="empty-text">
          <text>暂无服务类型</text>
        </view>
      </view>
      
      <view v-if="loading" class="loading-text">
        <text>加载中...</text>
      </view>
      
      <view v-if="!loading && !hasMore && reviewList.length > 0" class="no-more-text">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { userApi, reviewApi, categoryApi } from '@/api'

const userStore = useUserStore()

const userId = ref(null)
const userInfo = ref({})
const activeTab = ref('reviews')
const reviewList = ref([])
const categories = ref([])
const isFollowed = ref(false)
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = ref(10)

const getAvatarEmoji = (id) => {
  const emojis = ['👩', '👨', '👩‍🍳', '👨‍⚕️', '🧹', '👶', '👩‍🦳', '👨‍🦳']
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
  
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}年${month}月${day}日`
}

const loadUserInfo = async () => {
  if (!userId.value) return
  
  try {
    const res = await userApi.getUserInfo(userId.value)
    if (res.code === 200) {
      userInfo.value = res.data || {}
      isFollowed.value = res.data?.isFollowed || false
      
      if (res.data?.categories?.length > 0) {
        categories.value = res.data.categories.map((name, index) => ({
          id: index + 1,
          name: name,
          icon: getCategoryIcon(name)
        }))
      }
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const getCategoryIcon = (name) => {
  const icons = {
    '保洁': '🧹',
    '烹饪': '🍳',
    '育儿': '👶',
    '老人': '🧓',
    '搬运': '📦',
    '宠物': '🐾',
    '维修': '🔧'
  }
  return icons[name] || '🏠'
}

const loadReviews = async () => {
  if (!userId.value) return
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    const res = await reviewApi.getUserReviews(userId.value, {
      page: page.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      reviewList.value = [...reviewList.value, ...newList]
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载评价失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value && activeTab.value === 'reviews') {
    loadReviews()
  }
}

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'reviews' && reviewList.value.length === 0) {
    page.value = 1
    hasMore.value = true
    loadReviews()
  }
}

const goBack = () => {
  uni.navigateBack()
}

const toggleFollow = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  try {
    if (isFollowed.value) {
      const res = await userApi.unfollow(userId.value)
      if (res.code === 200) {
        isFollowed.value = false
        uni.showToast({
          title: '已取消关注',
          icon: 'success'
        })
      }
    } else {
      const res = await userApi.follow(userId.value)
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
    title: '私信功能开发中',
    icon: 'none'
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  userId.value = currentPage.options?.id
  
  if (userId.value) {
    loadUserInfo()
    loadReviews()
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  background: #FAFAFA;
}

.profile-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  padding: 80rpx 28rpx 40rpx;
}

.profile-nav {
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

.user-info-section {
  background: #FFFFFF;
  border-radius: 0 0 40rpx 40rpx;
  padding: 40rpx 28rpx;
  text-align: center;
  margin-bottom: 24rpx;
}

.user-avatar-large {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
  margin: 0 auto 24rpx;
  border: 6rpx solid #FFFFFF;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #212121;
  display: block;
}

.user-bio {
  font-size: 26rpx;
  color: #757575;
  margin-top: 12rpx;
  display: block;
}

.user-stats {
  display: flex;
  justify-content: center;
  gap: 80rpx;
  margin-top: 32rpx;
  padding: 28rpx 0;
  border-top: 1rpx solid #F5F5F5;
  border-bottom: 1rpx solid #F5F5F5;
}

.stat-item {
  text-align: center;
}

.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #212121;
  display: block;
}

.stat-label {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 4rpx;
  display: block;
}

.action-buttons {
  display: flex;
  gap: 24rpx;
  margin-top: 28rpx;
  justify-content: center;
}

.follow-btn {
  padding: 16rpx 48rpx;
  border-radius: 9999rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
}

.follow-btn.followed {
  background: #F5F5F5;
  color: #9E9E9E;
}

.chat-btn {
  padding: 16rpx 48rpx;
  border-radius: 9999rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: 3rpx solid #2EC4B6;
  background: transparent;
  color: #2EC4B6;
}

.tabs-section {
  display: flex;
  background: #FFFFFF;
  margin-bottom: 24rpx;
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

.content-section {
  height: calc(100vh - 600rpx);
  padding: 0 28rpx;
}

.review-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.reviewer-avatar {
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

.reviewer-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
  display: block;
}

.review-rating {
  margin-top: 4rpx;
}

.star {
  font-size: 20rpx;
  color: #E0E0E0;
}

.star.active {
  color: #FFD700;
}

.review-time {
  font-size: 22rpx;
  color: #9E9E9E;
}

.review-content {
  font-size: 26rpx;
  color: #424242;
  line-height: 1.6;
  display: block;
}

.review-images {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
  flex-wrap: wrap;
}

.review-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 16rpx;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
}

.review-reply {
  margin-top: 16rpx;
  padding: 16rpx;
  background: #FAFAFA;
  border-radius: 16rpx;
}

.reply-label {
  font-size: 22rpx;
  color: #FF6B35;
  font-weight: 600;
}

.reply-content {
  font-size: 24rpx;
  color: #757575;
  margin-left: 8rpx;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 28rpx 16rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.category-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
}

.category-name {
  font-size: 24rpx;
  color: #424242;
  font-weight: 500;
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
