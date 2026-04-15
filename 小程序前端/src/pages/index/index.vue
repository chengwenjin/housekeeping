<template>
  <view class="home-page">
    <view class="home-header">
      <view class="home-topbar">
        <view class="home-loc" @click="selectCity">
          <text class="loc-icon">📍</text>
          <text class="loc-text">{{ currentCity }}</text>
          <text class="loc-arrow">▾</text>
        </view>
        <view class="home-avatar" @click="goToMine">
          <text v-if="userStore.userInfo?.avatarUrl" class="avatar-img">{{ userStore.userInfo.avatarUrl }}</text>
          <text v-else class="avatar-placeholder">😊</text>
        </view>
      </view>
      
      <view class="search-bar" @click="goToSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索家政服务需求...</text>
      </view>
    </view>
    
    <scroll-view class="home-cats" scroll-x>
      <view 
        v-for="cat in categories" 
        :key="cat.id" 
        class="cat-item"
        @click="selectCategory(cat)"
      >
        <view :class="['cat-icon-wrap', 'c' + (cat.id % 5 + 1)]">
          <text>{{ cat.icon }}</text>
        </view>
        <text class="cat-text">{{ cat.name }}</text>
      </view>
    </scroll-view>
    
    <view class="banner" v-if="homeData.banner">
      <view class="banner-text">
        <text class="banner-title">{{ homeData.banner.title }}</text>
        <text class="banner-subtitle">{{ homeData.banner.subtitle }}</text>
      </view>
      <view class="banner-btn" @click="goToDemands">去接单</view>
      <view class="banner-decor">🏠</view>
    </view>
    
    <view class="section-bar">
      <text class="section-title">附近需求 · {{ selectedCategory?.name || '全部' }}</text>
      <text class="section-filter" @click="showFilter = true">筛选 ▾</text>
    </view>
    
    <scroll-view 
      class="demand-list" 
      scroll-y 
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view 
        v-for="demand in demandList" 
        :key="demand.id" 
        class="demand-card"
        @click="goToDetail(demand.id)"
      >
        <view class="demand-card-top">
          <view class="demand-user">
            <view :class="['user-avatar', 'ua' + ((demand.publisher?.id || 1) % 4 + 1)]">
              <text>{{ getAvatarEmoji(demand.publisher?.id) }}</text>
            </view>
            <view class="user-info">
              <text class="uname">{{ demand.publisher?.nickname || '用户' }}</text>
              <text class="utime">{{ demand.createdAtText }} · {{ demand.location?.district || '' }}</text>
            </view>
          </view>
          <view :class="['demand-status', demand.status === 1 ? 'ds-open' : 'ds-taken']">
            <text>{{ demand.statusText }}</text>
          </view>
        </view>
        
        <text class="demand-title">{{ demand.title }}</text>
        <text class="demand-desc">{{ demand.description }}</text>
        
        <view class="demand-meta">
          <view class="meta-item" v-if="demand.location?.distance">
            <text>📍</text>
            <text>距您{{ demand.location.distance }}km</text>
          </view>
          <view class="meta-item" v-if="demand.serviceTimeDesc">
            <text>📅</text>
            <text>{{ demand.serviceTimeDesc }}</text>
          </view>
          <view class="meta-item" v-if="demand.minDuration">
            <text>⏱</text>
            <text>{{ demand.minDuration }}-{{ demand.maxDuration }}h</text>
          </view>
        </view>
        
        <view class="demand-footer">
          <view class="demand-price">
            <text class="price-num">¥{{ demand.expectedPrice }}</text>
            <text class="price-unit">/{{ demand.priceUnit || '小时' }}</text>
          </view>
          <button 
            v-if="demand.status === 1" 
            class="btn-take" 
            @click.stop="takeOrder(demand)"
          >
            立即接单
          </button>
          <button v-else class="btn-take-sm" disabled>
            {{ demand.statusText }}
          </button>
        </view>
      </view>
      
      <view v-if="loading" class="loading-text">
        <text>加载中...</text>
      </view>
      
      <view v-if="!loading && demandList.length === 0" class="empty-text">
        <text>暂无需求数据</text>
      </view>
      
      <view v-if="!loading && !hasMore && demandList.length > 0" class="no-more-text">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { demandApi, categoryApi, homeApi, orderApi } from '@/api'

const userStore = useUserStore()

const currentCity = ref('北京市朝阳区')
const categories = ref([])
const selectedCategory = ref(null)
const homeData = ref({})
const demandList = ref([])
const loading = ref(false)
const refreshing = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = ref(10)
const showFilter = ref(false)

const getAvatarEmoji = (id) => {
  const emojis = ['👩', '👨', '👩‍🦳', '👨‍🦳', '👩‍🍳', '👨‍⚕️']
  return emojis[(id || 0) % emojis.length]
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getList()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (e) {
    console.error('加载分类失败', e)
    categories.value = [
      { id: 1, name: '保洁', icon: '🧹' },
      { id: 2, name: '烹饪', icon: '🍳' },
      { id: 3, name: '育儿', icon: '👶' },
      { id: 4, name: '老人', icon: '🧓' },
      { id: 5, name: '搬运', icon: '📦' },
      { id: 6, name: '宠物', icon: '🐾' },
      { id: 7, name: '维修', icon: '🔧' }
    ]
  }
}

const loadHomeData = async () => {
  try {
    const res = await homeApi.getData()
    if (res.code === 200) {
      homeData.value = res.data || {}
    }
  } catch (e) {
    console.error('加载首页数据失败', e)
    homeData.value = {
      banner: {
        title: '附近有3条新需求',
        subtitle: '快来抢单，收入翻倍！'
      }
    }
  }
}

const loadDemands = async (isRefresh = false) => {
  if (loading.value) return
  if (isRefresh) {
    page.value = 1
    hasMore.value = true
    demandList.value = []
  }
  
  if (!hasMore.value) return
  
  loading.value = true
  
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      status: 1
    }
    
    if (selectedCategory.value?.id) {
      params.categoryId = selectedCategory.value.id
    }
    
    const res = await demandApi.getList(params)
    
    if (res.code === 200) {
      const newList = res.data?.list || []
      if (isRefresh) {
        demandList.value = newList
      } else {
        demandList.value = [...demandList.value, ...newList]
      }
      
      const pagination = res.data?.pagination || {}
      hasMore.value = page.value < pagination.totalPages
      page.value++
    }
  } catch (e) {
    console.error('加载需求列表失败', e)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onRefresh = () => {
  refreshing.value = true
  loadDemands(true)
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadDemands()
  }
}

const selectCategory = (cat) => {
  selectedCategory.value = selectedCategory.value?.id === cat.id ? null : cat
  loadDemands(true)
}

const selectCity = () => {
  uni.showToast({
    title: '城市选择功能开发中',
    icon: 'none'
  })
}

const goToSearch = () => {
  uni.showToast({
    title: '搜索功能开发中',
    icon: 'none'
  })
}

const goToMine = () => {
  if (userStore.isLoggedIn) {
    uni.switchTab({
      url: '/pages/mine/mine'
    })
  } else {
    uni.navigateTo({
      url: '/pages/login/login'
    })
  }
}

const goToDemands = () => {
  uni.switchTab({
    url: '/pages/orders/orders'
  })
}

const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/demand/detail?id=${id}`
  })
}

const takeOrder = async (demand) => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  uni.showModal({
    title: '确认接单',
    content: `确定要接取"${demand.title}"这个需求吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '接单中...' })
          const result = await orderApi.takeOrder(demand.id, { remark: '' })
          uni.hideLoading()
          
          if (result.code === 200) {
            uni.showToast({
              title: '接单成功',
              icon: 'success'
            })
            loadDemands(true)
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
  loadCategories()
  loadHomeData()
  loadDemands(true)
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #FAFAFA;
  padding-bottom: 120rpx;
}

.home-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF9A3C 60%, #FFB347 100%);
  padding: 80rpx 28rpx 32rpx;
  position: sticky;
  top: 0;
  z-index: 100;
}

.home-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.home-loc {
  display: flex;
  align-items: center;
  gap: 8rpx;
  color: rgba(255, 255, 255, 0.9);
  font-size: 24rpx;
}

.loc-text {
  color: #FFFFFF;
  font-size: 26rpx;
  font-weight: 600;
}

.loc-arrow {
  font-size: 20rpx;
}

.home-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  border: 4rpx solid rgba(255, 255, 255, 0.6);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.search-bar {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 9999rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 18rpx 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.12);
}

.search-icon {
  font-size: 30rpx;
  color: #BDBDBD;
}

.search-placeholder {
  color: #BDBDBD;
  font-size: 26rpx;
  flex: 1;
}

.home-cats {
  background: #FFFFFF;
  padding: 28rpx;
  display: flex;
  gap: 0;
  white-space: nowrap;
}

.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  min-width: 104rpx;
  margin-right: 20rpx;
}

.cat-icon-wrap {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
}

.c1 { background: linear-gradient(135deg, #FFE0D0, #FFCDB0); }
.c2 { background: linear-gradient(135deg, #D0F0EE, #B0E8E4); }
.c3 { background: linear-gradient(135deg, #D0E8FF, #B0D4FF); }
.c4 { background: linear-gradient(135deg, #F0D0FF, #E0B0FF); }
.c5 { background: linear-gradient(135deg, #D0FFE0, #B0F0C0); }

.cat-text {
  font-size: 22rpx;
  color: #757575;
  font-weight: 500;
}

.banner {
  margin: 24rpx 28rpx;
  border-radius: 32rpx;
  overflow: hidden;
  height: 180rpx;
  background: linear-gradient(135deg, #FF6B35, #FF9A3C, #FFB347);
  position: relative;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
}

.banner-text {
  flex: 1;
}

.banner-title {
  display: block;
  color: #FFFFFF;
  font-size: 30rpx;
  font-weight: 700;
}

.banner-subtitle {
  display: block;
  color: rgba(255, 255, 255, 0.85);
  font-size: 22rpx;
  margin-top: 6rpx;
}

.banner-btn {
  position: absolute;
  right: 32rpx;
  background: #FFFFFF;
  color: #FF6B35;
  padding: 12rpx 28rpx;
  border-radius: 9999rpx;
  font-size: 22rpx;
  font-weight: 700;
}

.banner-decor {
  position: absolute;
  right: 160rpx;
  bottom: -20rpx;
  font-size: 128rpx;
  opacity: 0.2;
}

.section-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 28rpx 16rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #212121;
}

.section-filter {
  font-size: 24rpx;
  color: #FF6B35;
}

.demand-list {
  height: calc(100vh - 600rpx);
}

.demand-card {
  margin: 0 28rpx 24rpx;
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.demand-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.demand-user {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.user-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  flex-shrink: 0;
}

.ua1 { background: linear-gradient(135deg, #FFE0D0, #FFCDB0); }
.ua2 { background: linear-gradient(135deg, #D0F0EE, #B0E8E4); }
.ua3 { background: linear-gradient(135deg, #D0E8FF, #B0D4FF); }
.ua4 { background: linear-gradient(135deg, #F0D0FF, #E0B0FF); }

.user-info {
  display: flex;
  flex-direction: column;
}

.uname {
  font-size: 26rpx;
  font-weight: 600;
  color: #424242;
}

.utime {
  font-size: 22rpx;
  color: #9E9E9E;
  margin-top: 2rpx;
}

.demand-status {
  font-size: 22rpx;
  font-weight: 600;
  padding: 6rpx 16rpx;
  border-radius: 9999rpx;
}

.ds-open {
  background: #FFF0EB;
  color: #FF6B35;
}

.ds-taken {
  background: #E8F5E9;
  color: #4CAF50;
}

.demand-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #212121;
  margin-bottom: 12rpx;
  display: block;
}

.demand-desc {
  font-size: 24rpx;
  color: #757575;
  line-height: 1.5;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.demand-meta {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 22rpx;
  color: #757575;
}

.demand-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #F5F5F5;
}

.demand-price {
  display: flex;
  align-items: baseline;
}

.price-num {
  font-size: 32rpx;
  font-weight: 700;
  color: #FF6B35;
}

.price-unit {
  font-size: 24rpx;
  font-weight: 500;
  color: #757575;
}

.btn-take {
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
  border: none;
  padding: 14rpx 32rpx;
  border-radius: 9999rpx;
  font-size: 24rpx;
  font-weight: 700;
  box-shadow: 0 8rpx 40rpx rgba(255, 107, 53, 0.35);
}

.btn-take-sm {
  background: transparent;
  color: #2EC4B6;
  border: 3rpx solid #2EC4B6;
  padding: 10rpx 24rpx;
  border-radius: 9999rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.loading-text,
.empty-text,
.no-more-text {
  text-align: center;
  padding: 40rpx;
  color: #9E9E9E;
  font-size: 24rpx;
}
</style>
