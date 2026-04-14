<template>
  <el-dialog
    v-model="dialogVisible"
    title="订单详情"
    width="900px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-descriptions title="基本信息" :column="2" border>
      <el-descriptions-item label="订单 ID">{{ orderData.id }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ orderData.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="标题" :span="2">{{ orderData.title }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(orderData.status)">
          {{ getStatusText(orderData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="支付状态">
        <el-tag :type="orderData.paymentStatus === 1 ? 'success' : 'info'">
          {{ orderData.paymentStatus === 1 ? '已支付' : '未支付' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="订单金额">¥{{ orderData.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="服务时长">{{ orderData.actualDuration || '-' }} 小时</el-descriptions-item>
      <el-descriptions-item label="服务类型">
        {{ getServiceTypeText(orderData.serviceType) }}
      </el-descriptions-item>
      <el-descriptions-item label="服务地址" :span="2">
        {{ orderData.province }}{{ orderData.city }}{{ orderData.district }}{{ orderData.address }}
      </el-descriptions-item>
      <el-descriptions-item label="联系人">{{ orderData.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ orderData.contactPhone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(orderData.createdAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="支付时间">
        {{ formatDate(orderData.paidAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="开始服务时间">
        {{ formatDate(orderData.startTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="结束服务时间">
        {{ formatDate(orderData.endTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="完成时间">
        {{ formatDate(orderData.completedAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="取消时间">
        {{ formatDate(orderData.cancelledAt) }}
      </el-descriptions-item>
      <el-descriptions-item v-if="orderData.cancelReason" label="取消原因" :span="2">
        {{ orderData.cancelReason }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单描述</el-divider>
    <div style="padding: 10px; background: #f5f7fa; border-radius: 4px; min-height: 80px;">
      {{ orderData.description || '暂无描述' }}
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button 
        v-if="orderData.status === 1 || orderData.status === 2" 
        type="warning" 
        @click="handleCancel"
      >
        取消订单
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
  orderId: Number
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const orderData = ref({})

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.orderId) {
    loadOrderDetail()
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const loadOrderDetail = async () => {
  try {
    const res = await request.get(`/admin/orders/${props.orderId}`)
    orderData.value = res.data || {}
  } catch (error) {
    console.error('加载订单详情失败', error)
    ElMessage.error('加载订单详情失败')
  }
}

const handleCancel = async () => {
  try {
    const reason = await ElMessageBox.prompt('请输入取消原因', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因',
      type: 'warning'
    })
    
    await request.post(`/admin/orders/${props.orderId}/force-cancel`, null, {
      params: { reason: reason.value }
    })
    ElMessage.success('取消成功')
    emit('refresh')
    dialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败', error)
    }
  }
}

const handleClosed = () => {
  orderData.value = {}
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const getServiceTypeText = (type) => {
  const map = { 1: '小时工', 2: '天工', 3: '包月' }
  return map[type] || '未知'
}

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    1: '待接单',
    2: '服务中',
    3: '已完成',
    4: '已取消',
    5: '已关闭'
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.el-descriptions {
  margin-bottom: 15px;
}
</style>
