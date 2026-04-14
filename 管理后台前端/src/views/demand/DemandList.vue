<template>
  <div class="demand-list">
    <el-card shadow="always">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="需求标题/描述"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable>
            <el-option label="保洁" :value="1" />
            <el-option label="烹饪" :value="2" />
            <el-option label="育儿嫂" :value="3" />
            <el-option label="老人照护" :value="4" />
            <el-option label="搬运" :value="5" />
            <el-option label="宠物服务" :value="6" />
            <el-option label="维修服务" :value="7" />
            <el-option label="家电清洗" :value="8" />
          </el-select>
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="城市名" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="招募中" :value="1" />
            <el-option label="已接单" :value="2" />
            <el-option label="进行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
            <el-option label="已过期" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="需求标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="服务类型" width="100">
          <template #default="{ row }">
            {{ getServiceTypeText(row.serviceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="expectedPrice" label="预期价格" width="100">
          <template #default="{ row }">
            ¥{{ row.expectedPrice }}/{{ row.priceUnit || '小时' }}
          </template>
        </el-table-column>
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === 1" 
              size="small" 
              type="warning"
              @click="handleOffline(row)"
            >
              下架
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 需求详情对话框 -->
    <DemandDetailDialog 
      v-model:visible="detailVisible" 
      :demand-id="selectedDemandId"
      @refresh="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'
import DemandDetailDialog from './DemandDetailDialog.vue'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const selectedDemandId = ref(null)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  city: '',
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await request.get('/admin/demands', { params })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载需求列表失败', error)
    ElMessage.error('加载需求列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.city = ''
  searchForm.status = null
  handleSearch()
}

const handleView = (row) => {
  selectedDemandId.value = row.id
  detailVisible.value = true
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架该需求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/admin/demands/${row.id}/offline`)
    ElMessage.success('下架成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该需求吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    await request.delete(`/admin/demands/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.demand-list {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
