<template>
  <div class="user-list">
    <el-card shadow="always">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="昵称/手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select v-model="searchForm.certificationStatus" placeholder="全部" clearable>
            <el-option label="未认证" :value="0" />
            <el-option label="认证中" :value="1" />
            <el-option label="已认证" :value="2" />
            <el-option label="认证失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatarUrl" />
              <div class="info">
                <div class="nickname">{{ row.nickname }}</div>
                <div class="phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column label="认证状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCertificationType(row.certificationStatus)">
              {{ getCertificationText(row.certificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="totalOrders" label="订单数" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">详情</el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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

    <!-- 用户详情对话框 -->
    <UserDetailDialog 
      v-model:visible="detailVisible" 
      :user-id="selectedUserId"
      @refresh="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import UserDetailDialog from './UserDetailDialog.vue'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const selectedUserId = ref(null)

const searchForm = reactive({
  keyword: '',
  certificationStatus: null,
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 20,
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
    const res = await request.get('/admin/users', { params })
    console.log('用户列表响应数据:', res.data)
    // MyBatis-Plus Page 对象序列化后的字段是 records 和 total
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total || (res.data.pagination && res.data.pagination.total) || 0
  } catch (error) {
    console.error('加载用户列表失败', error)
    ElMessage.error('加载数据失败')
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
  searchForm.certificationStatus = null
  searchForm.status = null
  handleSearch()
}

const handleView = (row) => {
  selectedUserId.value = row.id
  detailVisible.value = true
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/admin/users/${row.id}/status`, {
      status: row.status === 1 ? 0 : 1
    })
    
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getCertificationType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const getCertificationText = (status) => {
  const texts = { 0: '未认证', 1: '认证中', 2: '已认证', 3: '认证失败' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  margin-right: 10px;
}

.info {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-weight: bold;
  color: #333;
}

.phone {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
