<template>
  <div class="statistics">
    <!-- 时间筛选 -->
    <el-card shadow="always" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadData"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">新增用户</div>
              <div class="stat-value">{{ stats.newUsers }}</div>
              <div class="stat-trend" :class="stats.userTrend > 0 ? 'up' : 'down'">
                {{ stats.userTrend > 0 ? '+' : '' }}{{ stats.userTrend }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon demand">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">新增需求</div>
              <div class="stat-value">{{ stats.newDemands }}</div>
              <div class="stat-trend" :class="stats.demandTrend > 0 ? 'up' : 'down'">
                {{ stats.demandTrend > 0 ? '+' : '' }}{{ stats.demandTrend }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon order">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">成交订单</div>
              <div class="stat-value">{{ stats.completedOrders }}</div>
              <div class="stat-trend" :class="stats.orderTrend > 0 ? 'up' : 'down'">
                {{ stats.orderTrend > 0 ? '+' : '' }}{{ stats.orderTrend }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon amount">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">营业收入</div>
              <div class="stat-value">¥{{ stats.revenue }}</div>
              <div class="stat-trend" :class="stats.revenueTrend > 0 ? 'up' : 'down'">
                {{ stats.revenueTrend > 0 ? '+' : '' }}{{ stats.revenueTrend }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card shadow="always">
          <template #header>
            <div class="card-header">
              <span>订单趋势分析</span>
            </div>
          </template>
          <div ref="orderTrendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="always">
          <template #header>
            <div class="card-header">
              <span>收入构成分析</span>
            </div>
          </template>
          <div ref="revenueChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类排行 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="24">
        <el-card shadow="always">
          <template #header>
            <div class="card-header">
              <span>服务分类热度排行</span>
            </div>
          </template>
          <div ref="categoryRankChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { User, List, Document, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const dateRange = ref([])
const stats = ref({
  newUsers: 0,
  userTrend: 0,
  newDemands: 0,
  demandTrend: 0,
  completedOrders: 0,
  orderTrend: 0,
  revenue: 0,
  revenueTrend: 0
})

const orderTrendChartRef = ref(null)
const revenueChartRef = ref(null)
const categoryRankChartRef = ref(null)
let orderTrendChart = null
let revenueChart = null
let categoryRankChart = null

const loadData = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    const res = await request.get('/admin/statistics/detail', { params })
    stats.value = res.data || stats.value
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 初始化订单趋势图
const initOrderTrendChart = () => {
  if (!orderTrendChartRef.value) return
  
  orderTrendChart = echarts.init(orderTrendChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['订单数', '成交额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1 月', '2 月', '3 月', '4 月', '5 月', '6 月', '7 月']
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数'
      },
      {
        type: 'value',
        name: '金额',
        axisLabel: {
          formatter: '¥{value}'
        }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: [820, 932, 901, 934, 1290, 1330, 1320],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '成交额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [82000, 93200, 90100, 93400, 129000, 133000, 132000],
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  
  orderTrendChart.setOption(option)
}

// 初始化收入构成图
const initRevenueChart = () => {
  if (!revenueChartRef.value) return
  
  revenueChart = echarts.init(revenueChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '收入来源',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 1048, name: '保洁服务' },
          { value: 735, name: '烹饪服务' },
          { value: 580, name: '育儿嫂' },
          { value: 484, name: '老人照护' },
          { value: 300, name: '其他服务' }
        ]
      }
    ]
  }
  
  revenueChart.setOption(option)
}

// 初始化分类排行图
const initCategoryRankChart = () => {
  if (!categoryRankChartRef.value) return
  
  categoryRankChart = echarts.init(categoryRankChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['其他', '维修服务', '宠物服务', '搬运', '老人照护', '育儿嫂', '烹饪', '保洁']
    },
    series: [
      {
        name: '订单数',
        type: 'bar',
        data: [100, 150, 200, 300, 400, 500, 600, 800],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  categoryRankChart.setOption(option)
}

onMounted(() => {
  loadData()
  nextTick(() => {
    initOrderTrendChart()
    initRevenueChart()
    initCategoryRankChart()
  })
  
  window.addEventListener('resize', () => {
    orderTrendChart?.resize()
    revenueChart?.resize()
    categoryRankChart?.resize()
  })
})
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  margin-right: 15px;
}

.stat-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.demand {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.order {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 5px 0;
}

.stat-trend {
  font-size: 12px;
}

.stat-trend.up {
  color: #f56c6c;
}

.stat-trend.down {
  color: #67c23a;
}

.charts-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
