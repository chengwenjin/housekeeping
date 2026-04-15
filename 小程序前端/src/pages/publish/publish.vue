<template>
  <view class="publish-page">
    <view class="publish-header">
      <view class="back-btn" @click="goBack">
        <text>‹</text>
      </view>
      <text class="header-title">发布家政需求</text>
    </view>
    
    <scroll-view class="publish-form" scroll-y>
      <view class="form-section">
        <text class="section-title">📌 服务类型</text>
        <view class="cat-select-grid">
          <view 
            v-for="cat in categories" 
            :key="cat.id" 
            :class="['cat-sel-item', selectedCategory?.id === cat.id ? 'selected' : '']"
            @click="selectCategory(cat)"
          >
            <text class="cat-sel-icon">{{ cat.icon }}</text>
            <text class="cat-sel-text">{{ cat.name }}</text>
          </view>
        </view>
      </view>
      
      <view class="form-section">
        <text class="section-title">📝 需求描述</text>
        
        <view class="form-item">
          <view class="form-label">
            <text>标题</text>
            <text class="required">*</text>
          </view>
          <input 
            class="form-input" 
            type="text" 
            placeholder="简要描述您的需求，如：保洁3小时"
            v-model="formData.title"
          />
        </view>
        
        <view class="form-item">
          <view class="form-label">
            <text>详细说明</text>
          </view>
          <textarea 
            class="form-textarea" 
            placeholder="描述具体要求、注意事项等..."
            v-model="formData.description"
          />
        </view>
      </view>
      
      <view class="form-section">
        <text class="section-title">💰 报酬设置</text>
        
        <view class="form-item">
          <view class="form-label">
            <text>预期报酬</text>
            <text class="required">*</text>
          </view>
          <view class="price-row">
            <text class="price-prefix">¥</text>
            <input 
              class="price-input" 
              type="number" 
              placeholder="80"
              v-model="formData.expectedPrice"
            />
            <picker 
              :value="priceUnitIndex" 
              :range="priceUnits" 
              range-key="name"
              @change="onPriceUnitChange"
            >
              <view class="price-unit">
                <text>{{ priceUnits[priceUnitIndex].name }}</text>
                <text> ▾</text>
              </view>
            </picker>
          </view>
        </view>
        
        <view class="form-item">
          <view class="form-label">
            <text>预计时长</text>
          </view>
          <view class="duration-row">
            <input 
              class="duration-input" 
              type="number" 
              placeholder="2"
              v-model="formData.minDuration"
            />
            <text class="duration-sep">-</text>
            <input 
              class="duration-input" 
              type="number" 
              placeholder="3"
              v-model="formData.maxDuration"
            />
            <text class="duration-unit">小时</text>
          </view>
        </view>
      </view>
      
      <view class="form-section">
        <text class="section-title">📅 时间地点</text>
        
        <view class="form-item">
          <view class="form-label">
            <text>服务时间</text>
          </view>
          <picker 
            mode="multiSelector" 
            :value="timeValue" 
            :range="timeRange"
            @change="onTimeChange"
          >
            <view class="form-input picker-input">
              <text v-if="formData.serviceTimeDesc">{{ formData.serviceTimeDesc }}</text>
              <text v-else style="color: #BDBDBD;">请选择服务时间</text>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <view class="form-label">
            <text>服务地址</text>
            <text class="required">*</text>
          </view>
          <view class="address-row">
            <view class="address-picker" @click="selectLocation">
              <text v-if="formData.district">{{ formData.city }} {{ formData.district }}</text>
              <text v-else style="color: #BDBDBD;">选择地区</text>
              <text> ▾</text>
            </view>
          </view>
          <input 
            class="form-input" 
            type="text" 
            placeholder="详细地址，如：建国路88号"
            v-model="formData.address"
            style="margin-top: 16rpx;"
          />
        </view>
      </view>
      
      <view class="form-section">
        <text class="section-title">📷 上传图片（选填）</text>
        <view class="photo-upload">
          <view 
            v-for="(img, index) in imageList" 
            :key="index" 
            class="photo-thumb"
          >
            <text>{{ getRandomEmoji() }}</text>
          </view>
          <view class="photo-add" @click="chooseImage" v-if="imageList.length < 9">
            <text class="add-icon">📷</text>
            <text class="add-text">添加</text>
          </view>
        </view>
      </view>
      
      <view class="form-section" style="margin-bottom: 200rpx;">
        <text class="section-title">📞 联系方式</text>
        
        <view class="form-item">
          <view class="form-label">
            <text>联系人</text>
          </view>
          <input 
            class="form-input" 
            type="text" 
            placeholder="您的称呼"
            v-model="formData.contactName"
          />
        </view>
        
        <view class="form-item">
          <view class="form-label">
            <text>联系电话</text>
          </view>
          <input 
            class="form-input" 
            type="number" 
            placeholder="请输入手机号"
            v-model="formData.contactPhone"
          />
        </view>
      </view>
    </scroll-view>
    
    <view class="publish-btn-wrap safe-area-bottom">
      <button class="publish-btn" @click="submitForm" :disabled="submitting">
        <text v-if="submitting">发布中...</text>
        <text v-else>🚀 立即发布需求</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { demandApi, categoryApi, uploadApi } from '@/api'

const userStore = useUserStore()

const categories = ref([])
const selectedCategory = ref(null)
const imageList = ref([])
const submitting = ref(false)
const priceUnitIndex = ref(0)

const priceUnits = [
  { id: 1, name: '元/小时', value: '小时' },
  { id: 2, name: '元/次', value: '次' },
  { id: 3, name: '元/天', value: '天' },
  { id: 4, name: '元/月', value: '月' }
]

const timeRange = [
  ['今天', '明天', '后天', '本周', '下周'],
  ['上午', '下午', '晚上', '全天']
]
const timeValue = ref([0, 0])

const formData = reactive({
  categoryId: null,
  title: '',
  description: '',
  expectedPrice: '',
  priceUnit: '小时',
  minDuration: '',
  maxDuration: '',
  serviceTimeDesc: '',
  province: '北京市',
  city: '北京市',
  district: '',
  address: '',
  latitude: 39.9042,
  longitude: 116.4074,
  contactName: '',
  contactPhone: '',
  imageUrls: []
})

const getRandomEmoji = () => {
  const emojis = ['🏠', '🛁', '🍳', '🧹', '👶', '🧓']
  return emojis[Math.floor(Math.random() * emojis.length)]
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
      { id: 7, name: '维修', icon: '🔧' },
      { id: 8, name: '更多', icon: '➕' }
    ]
  }
}

const selectCategory = (cat) => {
  selectedCategory.value = selectedCategory.value?.id === cat.id ? null : cat
  formData.categoryId = selectedCategory.value?.id || null
}

const onPriceUnitChange = (e) => {
  priceUnitIndex.value = e.detail.value
  formData.priceUnit = priceUnits[e.detail.value].value
}

const onTimeChange = (e) => {
  timeValue.value = e.detail.value
  const day = timeRange[0][e.detail.value[0]]
  const period = timeRange[1][e.detail.value[1]]
  formData.serviceTimeDesc = `${day} ${period}`
}

const selectLocation = () => {
  uni.showActionSheet({
    itemList: ['朝阳区', '海淀区', '西城区', '东城区', '丰台区', '石景山区'],
    success: (res) => {
      const districts = ['朝阳区', '海淀区', '西城区', '东城区', '丰台区', '石景山区']
      formData.district = districts[res.tapIndex]
    }
  })
}

const chooseImage = () => {
  uni.chooseImage({
    count: 9 - imageList.value.length,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFilePaths = res.tempFilePaths
      
      for (const path of tempFilePaths) {
        try {
          imageList.value.push(path)
        } catch (e) {
          console.error('上传图片失败', e)
        }
      }
    }
  })
}

const goBack = () => {
  uni.navigateBack()
}

const validateForm = () => {
  if (!formData.categoryId) {
    uni.showToast({ title: '请选择服务类型', icon: 'none' })
    return false
  }
  if (!formData.title.trim()) {
    uni.showToast({ title: '请输入需求标题', icon: 'none' })
    return false
  }
  if (!formData.expectedPrice) {
    uni.showToast({ title: '请输入预期报酬', icon: 'none' })
    return false
  }
  if (!formData.district) {
    uni.showToast({ title: '请选择服务地区', icon: 'none' })
    return false
  }
  if (!formData.address.trim()) {
    uni.showToast({ title: '请输入详细地址', icon: 'none' })
    return false
  }
  return true
}

const submitForm = async () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({
      url: '/pages/login/login'
    })
    return
  }
  
  if (!validateForm()) return
  
  submitting.value = true
  
  try {
    uni.showLoading({ title: '发布中...' })
    
    const submitData = {
      categoryId: formData.categoryId,
      title: formData.title.trim(),
      description: formData.description.trim(),
      expectedPrice: parseFloat(formData.expectedPrice) || 0,
      priceUnit: formData.priceUnit,
      minDuration: formData.minDuration ? parseFloat(formData.minDuration) : null,
      maxDuration: formData.maxDuration ? parseFloat(formData.maxDuration) : null,
      serviceTimeDesc: formData.serviceTimeDesc || '待定',
      province: formData.province,
      city: formData.city,
      district: formData.district,
      address: formData.address.trim(),
      latitude: formData.latitude,
      longitude: formData.longitude,
      contactName: formData.contactName.trim() || '用户',
      contactPhone: formData.contactPhone.trim() || '',
      imageUrls: formData.imageUrls
    }
    
    const res = await demandApi.create(submitData)
    uni.hideLoading()
    
    if (res.code === 200) {
      uni.showToast({
        title: '发布成功',
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
    submitting.value = false
    console.error('发布失败', e)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: #FAFAFA;
  display: flex;
  flex-direction: column;
}

.publish-header {
  background: #FFFFFF;
  padding: 80rpx 28rpx 28rpx;
  border-bottom: 1rpx solid #F5F5F5;
  display: flex;
  align-items: center;
  gap: 20rpx;
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
}

.header-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #212121;
}

.publish-form {
  flex: 1;
  padding: 28rpx;
  height: calc(100vh - 300rpx);
}

.form-section {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.07);
}

.section-title {
  font-size: 26rpx;
  font-weight: 700;
  color: #616161;
  margin-bottom: 24rpx;
  display: block;
}

.cat-select-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
}

.cat-sel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 8rpx;
  border-radius: 24rpx;
  border: 3rpx solid #E0E0E0;
  transition: all 0.15s;
}

.cat-sel-item.selected {
  border-color: #FF6B35;
  background: #FFF0EB;
}

.cat-sel-icon {
  font-size: 36rpx;
}

.cat-sel-text {
  font-size: 20rpx;
  color: #757575;
  font-weight: 500;
}

.cat-sel-item.selected .cat-sel-text {
  color: #FF6B35;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  font-size: 24rpx;
  color: #757575;
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.required {
  color: #F44336;
}

.form-input {
  width: 100%;
  border: 3rpx solid #E0E0E0;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  font-size: 26rpx;
  background: #FAFAFA;
  color: #424242;
}

.form-input:focus {
  border-color: #FF6B35;
  background: #FFFFFF;
}

.picker-input {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-textarea {
  width: 100%;
  border: 3rpx solid #E0E0E0;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  font-size: 26rpx;
  background: #FAFAFA;
  resize: none;
  height: 144rpx;
}

.price-row {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.price-prefix {
  font-size: 32rpx;
  font-weight: 700;
  color: #FF6B35;
}

.price-input {
  flex: 1;
  border: 3rpx solid #E0E0E0;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #FF6B35;
  background: #FAFAFA;
}

.price-unit {
  font-size: 24rpx;
  color: #9E9E9E;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.duration-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.duration-input {
  width: 120rpx;
  border: 3rpx solid #E0E0E0;
  border-radius: 24rpx;
  padding: 16rpx 20rpx;
  font-size: 26rpx;
  text-align: center;
  background: #FAFAFA;
}

.duration-sep {
  font-size: 28rpx;
  color: #9E9E9E;
}

.duration-unit {
  font-size: 24rpx;
  color: #757575;
}

.address-row {
  display: flex;
  gap: 16rpx;
}

.address-picker {
  flex: 1;
  border: 3rpx solid #E0E0E0;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  font-size: 26rpx;
  background: #FAFAFA;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-upload {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
  margin-top: 12rpx;
}

.photo-thumb {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #FFE0D0, #FFCDB0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
}

.photo-add {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  border: 3rpx dashed #E0E0E0;
  background: #FAFAFA;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
}

.add-icon {
  font-size: 40rpx;
  color: #BDBDBD;
}

.add-text {
  font-size: 18rpx;
  color: #BDBDBD;
}

.publish-btn-wrap {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 28rpx 32rpx;
  background: #FFFFFF;
  border-top: 1rpx solid #F5F5F5;
  z-index: 100;
}

.publish-btn {
  width: 100%;
  background: linear-gradient(135deg, #FF6B35, #FFB347);
  color: #FFFFFF;
  border: none;
  padding: 28rpx;
  border-radius: 9999rpx;
  font-size: 32rpx;
  font-weight: 700;
  box-shadow: 0 8rpx 40rpx rgba(255, 107, 53, 0.35);
}

.publish-btn[disabled] {
  opacity: 0.6;
}
</style>
