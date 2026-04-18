<template>
  <div class="operation-logs">
    <el-card shadow="always">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作人员">
          <el-input
            v-model="searchForm.operator"
            placeholder="用户名/手机号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="查询" value="QUERY" />
            <el-option label="登录" value="LOGIN" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 240px"
          />
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
        <el-table-column prop="username" label="操作人员" width="120" />
        <el-table-column prop="action" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.action)">
              {{ getTypeText(row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="method" label="请求方法" width="100" />
        <el-table-column prop="ip" label="IP 地址" width="140" />
        <el-table-column prop="createdAt" label="操作时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleViewDetail(row)">详情</el-button>
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

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="操作日志详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志 ID">{{ selectedLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ selectedLog.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getTypeTag(selectedLog.action)">
            {{ getTypeText(selectedLog.action) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ selectedLog.module || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ selectedLog.method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求 URL">{{ selectedLog.url || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre v-if="selectedLog.requestData" style="max-height: 200px; overflow-y: auto; background: #f5f7fa; padding: 10px; border-radius: 4px;">{{ formatJson(selectedLog.requestData) }}</pre>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <pre v-if="selectedLog.responseData" style="max-height: 200px; overflow-y: auto; background: #f5f7fa; padding: 10px; border-radius: 4px;">{{ formatJson(selectedLog.responseData) }}</pre>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="响应状态码">{{ selectedLog.responseCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ selectedLog.duration || 0 }} ms</el-descriptions-item>
        <el-descriptions-item label="IP 地址">{{ selectedLog.ip || '-' }}</el-descriptions-item>
        <el-descriptions-item label="User Agent" :span="2">{{ selectedLog.userAgent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDate(selectedLog.createdAt) }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const selectedLog = ref({})

const searchForm = reactive({
  operator: '',
  operationType: '',
  startDate: '',
  endDate: ''
})

const dateRange = ref([])

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
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dayjs(dateRange.value[0]).format('YYYY-MM-DD HH:mm:ss')
      params.endDate = dayjs(dateRange.value[1]).format('YYYY-MM-DD HH:mm:ss')
    }
    
    const res = await request.get('/admin/logs', { params })
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载日志失败', error)
    ElMessage.error('加载日志失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.operator = ''
  searchForm.operationType = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  dateRange.value = []
  handleSearch()
}

const handleViewDetail = (row) => {
  selectedLog.value = { ...row }
  detailVisible.value = true
}

const getTypeTag = (type) => {
  const map = {
    CREATE: 'success',
    UPDATE: 'warning',
    DELETE: 'danger',
    QUERY: 'info',
    LOGIN: 'primary',
    OTHER: 'info'
  }
  return map[type] || 'info'
}

const getTypeText = (type) => {
  const map = {
    CREATE: '创建',
    UPDATE: '更新',
    DELETE: '删除',
    QUERY: '查询',
    LOGIN: '登录',
    OTHER: '其他'
  }
  return map[type] || type
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const formatJson = (jsonStr) => {
  if (!jsonStr) return '-'
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2)
  } catch (e) {
    return jsonStr
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.operation-logs {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

pre {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
}
</style>
