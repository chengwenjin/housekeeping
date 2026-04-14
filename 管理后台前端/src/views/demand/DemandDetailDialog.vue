<template>
  <el-dialog
    v-model="dialogVisible"
    title="需求详情"
    width="900px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-descriptions title="基本信息" :column="2" border>
      <el-descriptions-item label="需求 ID">{{ demandData.id }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(demandData.status)">
          {{ getStatusText(demandData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="标题" :span="2">{{ demandData.title }}</el-descriptions-item>
      <el-descriptions-item label="分类">{{ demandData.categoryName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务类型">
        {{ getServiceTypeText(demandData.serviceType) }}
      </el-descriptions-item>
      <el-descriptions-item label="预期价格">¥{{ demandData.expectedPrice }}/{{ demandData.priceUnit || '小时' }}</el-descriptions-item>
      <el-descriptions-item label="服务时长">
        {{ demandData.minDuration }} - {{ demandData.maxDuration }} 小时
      </el-descriptions-item>
      <el-descriptions-item label="联系人">{{ demandData.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ demandData.contactPhone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务时间">
        {{ formatDate(demandData.serviceTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="发布用户">
        <el-link type="primary" @click="viewPublisher">{{ demandData.publisherName || '-' }}</el-link>
      </el-descriptions-item>
      <el-descriptions-item label="发布时间">
        {{ formatDate(demandData.createdAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="浏览量">{{ demandData.viewCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="足迹数">{{ demandData.footprintCount || 0 }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">详细描述</el-divider>
    <div style="padding: 10px; background: #f5f7fa; border-radius: 4px; min-height: 100px;">
      {{ demandData.description || '暂无描述' }}
    </div>

    <el-divider content-position="left">服务地址</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="省份">{{ demandData.province || '-' }}</el-descriptions-item>
      <el-descriptions-item label="城市">{{ demandData.city || '-' }}</el-descriptions-item>
      <el-descriptions-item label="区县">{{ demandData.district || '-' }}</el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">{{ demandData.address || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider v-if="demandData.images && demandData.images.length > 0" content-position="left">
      图片展示
    </el-divider>
    <div v-if="demandData.images && demandData.images.length > 0" style="display: flex; gap: 10px; flex-wrap: wrap;">
      <el-image
        v-for="(img, index) in demandData.images"
        :key="index"
        :src="img"
        :preview-src-list="demandData.images"
        :initial-index="index"
        fit="cover"
        style="width: 150px; height: 150px; cursor: pointer;"
      />
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button 
        v-if="demandData.status === 1" 
        type="warning" 
        @click="handleOffline"
      >
        下架需求
      </el-button>
      <el-button 
        type="danger" 
        @click="handleDelete"
      >
        删除需求
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'

const props = defineProps({
  visible: Boolean,
  demandId: Number
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const demandData = ref({})

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.demandId) {
    loadDemandDetail()
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const loadDemandDetail = async () => {
  try {
    const res = await request.get(`/admin/demands/${props.demandId}`)
    demandData.value = res.data || {}
    
    // 解析图片数组（如果是字符串）
    if (typeof demandData.value.images === 'string') {
      try {
        demandData.value.images = JSON.parse(demandData.value.images)
      } catch (e) {
        demandData.value.images = []
      }
    }
  } catch (error) {
    console.error('加载需求详情失败', error)
    ElMessage.error('加载需求详情失败')
  }
}

const viewPublisher = () => {
  // TODO: 跳转到用户详情页
  ElMessage.info('查看发布者信息功能开发中')
}

const handleOffline = async () => {
  try {
    await ElMessageBox.confirm('确定要下架该需求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/admin/demands/${props.demandId}/offline`)
    ElMessage.success('下架成功')
    emit('refresh')
    dialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败', error)
    }
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该需求吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    await request.delete(`/admin/demands/${props.demandId}`)
    ElMessage.success('删除成功')
    emit('refresh')
    dialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

const handleClosed = () => {
  demandData.value = {}
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const getServiceTypeText = (type) => {
  const map = { 1: '小时工', 2: '长期工', 3: '临时工' }
  return map[type] || '未知'
}

const getStatusType = (status) => {
  const map = {
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'info',
    5: 'danger',
    6: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    1: '招募中',
    2: '已接单',
    3: '进行中',
    4: '已完成',
    5: '已取消',
    6: '已过期'
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.el-descriptions {
  margin-bottom: 15px;
}

.el-image {
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}
</style>
