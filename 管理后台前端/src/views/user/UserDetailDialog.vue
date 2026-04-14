<template>
  <el-dialog
    v-model="dialogVisible"
    title="用户详情"
    width="800px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-descriptions title="基本信息" :column="2" border>
      <el-descriptions-item label="用户 ID">{{ userData.id }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ userData.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="头像">
        <el-avatar :size="60" :src="userData.avatarUrl" />
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ getGenderText(userData.gender) }}
      </el-descriptions-item>
      <el-descriptions-item label="手机号">{{ userData.phone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="真实姓名">{{ userData.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="认证状态">
        <el-tag :type="getCertificationType(userData.certificationStatus)">
          {{ getCertificationText(userData.certificationStatus) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="评分">
        <el-rate v-model="userData.score" disabled show-score text-color="#ff9900" />
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="userData.status === 1 ? 'success' : 'danger'">
          {{ userData.status === 1 ? '正常' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="注册时间">
        {{ formatDate(userData.createdAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="最后登录">
        {{ formatDate(userData.lastLoginAt) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计数据</el-divider>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-statistic title="累计订单数" :value="userData.totalOrders" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="发布需求数" :value="userData.publishedCount" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="粉丝数" :value="userData.followerCount" />
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="订单记录" name="orders">
        <el-table :data="orderList" border stripe>
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="title" label="订单标题" min-width="200" />
          <el-table-column prop="totalAmount" label="金额" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="需求记录" name="demands">
        <el-table :data="demandList" border stripe>
          <el-table-column prop="title" label="需求标题" min-width="200" />
          <el-table-column prop="expectedPrice" label="预期价格" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getDemandStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="评价记录" name="reviews">
        <el-table :data="reviewList" border stripe>
          <el-table-column prop="rating" label="评分" width="80">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" min-width="250" />
          <el-table-column prop="helpfulCount" label="有用数" width="80" />
          <el-table-column prop="createdAt" label="时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button 
        v-if="userData.status === 1" 
        type="warning" 
        @click="handleToggleStatus(0)"
      >
        禁用账号
      </el-button>
      <el-button 
        v-else 
        type="success" 
        @click="handleToggleStatus(1)"
      >
        启用账号
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'

const props = defineProps({
  visible: Boolean,
  userId: Number
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const userData = ref({})
const activeTab = ref('orders')
const orderList = ref([])
const demandList = ref([])
const reviewList = ref([])

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.userId) {
    loadUserData()
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const loadUserData = async () => {
  try {
    const res = await request.get(`/admin/users/${props.userId}`)
    userData.value = res.data
    // 加载相关数据
    loadOrders()
    loadDemands()
    loadReviews()
  } catch (error) {
    console.error('加载用户详情失败', error)
  }
}

const loadOrders = async () => {
  try {
    const res = await request.get('/admin/orders', {
      params: { userId: props.userId, page: 1, pageSize: 10 }
    })
    orderList.value = res.data.records || res.data.list || []
  } catch (error) {
    console.error('加载订单失败', error)
  }
}

const loadDemands = async () => {
  try {
    const res = await request.get('/admin/demands', {
      params: { userId: props.userId, page: 1, pageSize: 10 }
    })
    demandList.value = res.data.records || res.data.list || []
  } catch (error) {
    console.error('加载需求失败', error)
  }
}

const loadReviews = async () => {
  try {
    const res = await request.get('/admin/reviews', {
      params: { userId: props.userId, page: 1, pageSize: 10 }
    })
    reviewList.value = res.data.records || res.data.list || []
  } catch (error) {
    console.error('加载评价失败', error)
  }
}

const handleToggleStatus = async (newStatus) => {
  try {
    await request.put(`/admin/users/${props.userId}/status`, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    emit('refresh')
    dialogVisible.value = false
  } catch (error) {
    console.error('更新状态失败', error)
  }
}

const handleClosed = () => {
  userData.value = {}
  orderList.value = []
  demandList.value = []
  reviewList.value = []
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const getGenderText = (gender) => {
  const map = { 0: '未知', 1: '男', 2: '女' }
  return map[gender] || '未知'
}

const getCertificationType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getCertificationText = (status) => {
  const map = { 
    0: '未认证', 
    1: '认证中', 
    2: '已认证', 
    3: '认证失败' 
  }
  return map[status] || '未知'
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

const getDemandStatusText = (status) => {
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
  margin-bottom: 20px;
}

.el-statistic {
  text-align: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
