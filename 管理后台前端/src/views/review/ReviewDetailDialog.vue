<template>
  <el-dialog
    v-model="dialogVisible"
    title="评价详情"
    width="800px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-descriptions title="基本信息" :column="2" border>
      <el-descriptions-item label="评价 ID">{{ reviewData.id }}</el-descriptions-item>
      <el-descriptions-item label="类型">
        {{ reviewData.reviewType === 1 ? '需求评价' : '订单评价' }}
      </el-descriptions-item>
      <el-descriptions-item label="评分">
        <el-rate v-model="reviewData.rating" disabled show-score text-color="#ff9900" />
      </el-descriptions-item>
      <el-descriptions-item label="有用数">{{ reviewData.helpfulCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="是否匿名">
        <el-tag :type="reviewData.isAnonymous === 1 ? 'warning' : 'info'">
          {{ reviewData.isAnonymous === 1 ? '匿名' : '实名' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(reviewData.createdAt) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">评价内容</el-divider>
    <div style="padding: 15px; background: #f5f7fa; border-radius: 4px; min-height: 100px; line-height: 1.6;">
      {{ reviewData.content || '暂无内容' }}
    </div>

    <el-divider v-if="reviewData.images && reviewData.images.length > 0" content-position="left">
      评价图片
    </el-divider>
    <div v-if="reviewData.images && reviewData.images.length > 0" style="display: flex; gap: 10px; flex-wrap: wrap;">
      <el-image
        v-for="(img, index) in reviewData.images"
        :key="index"
        :src="img"
        :preview-src-list="reviewData.images"
        :initial-index="index"
        fit="cover"
        style="width: 150px; height: 150px; cursor: pointer;"
      />
    </div>

    <el-divider v-if="reviewData.replyContent" content-position="left">官方回复</el-divider>
    <div v-if="reviewData.replyContent" style="padding: 15px; background: #e6f7ff; border-radius: 4px; min-height: 60px; line-height: 1.6;">
      {{ reviewData.replyContent }}
    </div>
    <div v-else style="padding: 15px; background: #fffbe6; border-radius: 4px; color: #999;">
      暂无官方回复
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleReply">回复评价</el-button>
      <el-button type="danger" @click="handleDelete">删除评价</el-button>
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
  reviewId: Number
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const reviewData = ref({})

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.reviewId) {
    loadReviewDetail()
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const loadReviewDetail = async () => {
  try {
    const res = await request.get(`/admin/reviews/${props.reviewId}`)
    reviewData.value = res.data || {}
    
    // 解析图片数组（如果是字符串）
    if (typeof reviewData.value.images === 'string') {
      try {
        reviewData.value.images = JSON.parse(reviewData.value.images)
      } catch (e) {
        reviewData.value.images = []
      }
    }
  } catch (error) {
    console.error('加载评价详情失败', error)
    ElMessage.error('加载评价详情失败')
  }
}

const handleReply = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入回复内容', '回复评价', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入回复内容',
      type: 'textarea',
      inputPlaceholder: '请输入回复内容...'
    })
    
    await request.post(`/admin/reviews/${props.reviewId}/reply`, null, {
      params: { content: value }
    })
    ElMessage.success('回复成功')
    loadReviewDetail()
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回复失败', error)
    }
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    await request.delete(`/admin/reviews/${props.reviewId}`)
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
  reviewData.value = {}
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
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
