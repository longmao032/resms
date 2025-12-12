<template>
  <div class="page-container">
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <template #header>
              <div class="card-header">
                <span>总目标业绩</span>
                <el-tag type="info">本期</el-tag>
              </div>
            </template>
            <div class="card-value">¥ {{ formatMoney(summary.totalTarget) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <template #header>
              <div class="card-header">
                <span>实际总业绩</span>
                <el-tag type="success">完成</el-tag>
              </div>
            </template>
            <div class="card-value highlight">¥ {{ formatMoney(summary.totalActual) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <template #header>
              <div class="card-header">
                <span>整体完成率</span>
                <el-icon><TrendCharts /></el-icon>
              </div>
            </template>
            <div class="card-value">
              <el-progress 
                :percentage="summary.avgCompletionRate" 
                :status="getProgressStatus(summary.avgCompletionRate)"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <template #header>
              <div class="card-header">
                <span>总成交单数</span>
                <el-icon><List /></el-icon>
              </div>
            </template>
            <div class="card-value">{{ summary.totalTransactions }} 单</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="统计周期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-time="defaultTime"
            @change="handleDateChange"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item label="团队名称">
          <el-input
            v-model="queryParams.teamName"
            placeholder="请输入团队名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 统计
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon> 导出
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header-flex">
          <span class="title">团队业绩排行榜</span>
          <span class="subtitle">数据实时更新</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        :default-sort="{ prop: 'actualPerformance', order: 'descending' }"
      >
        <el-table-column type="index" label="排名" width="80" align="center">
          <template #default="{ $index }">
            <div class="rank-badge" :class="'rank-' + ($index + 1)">
              {{ $index + 1 }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="teamName" label="团队名称" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="managerName" label="团队经理" width="120" />
        
        <el-table-column prop="memberCount" label="人数" width="80" align="center" />
        
        <el-table-column prop="performanceTarget" label="目标业绩(元)" min-width="140" align="right" sortable>
          <template #default="{ row }">
            {{ formatMoney(row.performanceTarget) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="actualPerformance" label="实际业绩(元)" min-width="140" align="right" sortable>
          <template #default="{ row }">
            <span class="money-text">{{ formatMoney(row.actualPerformance) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="completionRate" label="完成率" min-width="180" sortable>
          <template #default="{ row }">
            <div class="progress-wrapper">
              <el-progress 
                :percentage="Number(row.completionRate.toFixed(1))" 
                :status="getProgressStatus(row.completionRate)"
                :stroke-width="10"
              />
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="transactionCount" label="成交单数" width="120" align="center" sortable />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, TrendCharts, List, Download } from '@element-plus/icons-vue'
import { getTeamPerformance } from '@/api/team'
import type { TeamPerformanceQuery, TeamPerformanceVO, PerformanceSummary } from '@/api/team/type'

// 状态定义
const loading = ref(false)
const tableData = ref<TeamPerformanceVO[]>([])
const dateRange = ref<[string, string] | null>(null)

// 默认时间：当月第一天到当前时间
const defaultTime = [
  new Date(2000, 1, 1, 0, 0, 0),
  new Date(2000, 2, 1, 23, 59, 59),
]

const queryParams = reactive<TeamPerformanceQuery>({
  teamName: '',
  startTime: '',
  endTime: ''
})

// 计算概览数据
const summary = computed<PerformanceSummary>(() => {
  if (tableData.value.length === 0) {
    return { totalTarget: 0, totalActual: 0, avgCompletionRate: 0, totalTransactions: 0 }
  }
  
  const totalTarget = tableData.value.reduce((sum, item) => sum + (Number(item.performanceTarget) || 0), 0)
  const totalActual = tableData.value.reduce((sum, item) => sum + (Number(item.actualPerformance) || 0), 0)
  const totalTrans = tableData.value.reduce((sum, item) => sum + (Number(item.transactionCount) || 0), 0)
  
  // 计算整体完成率
  let rate = 0
  if (totalTarget > 0) {
    rate = Math.min((totalActual / totalTarget) * 100, 100)
  }
  
  return {
    totalTarget,
    totalActual,
    avgCompletionRate: Number(rate.toFixed(1)),
    totalTransactions: totalTrans
  }
})

// 初始化方法
onMounted(() => {
  // 默认查询当月
  initDateRange()
  handleSearch()
})

const initDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(1) // 本月1号
  start.setHours(0, 0, 0, 0)
  
  // 格式化为 YYYY-MM-DD HH:mm:ss
  const format = (d: Date) => d.toISOString().replace('T', ' ').substring(0, 19)
  
  queryParams.startTime = format(start)
  queryParams.endTime = format(end)
  dateRange.value = [queryParams.startTime, queryParams.endTime]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getTeamPerformance(queryParams)
    if (res.status) {
      tableData.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取业绩数据失败')
    }
  } catch (error) {
    console.error('获取业绩数据失败:', error)
    ElMessage.error('获取业绩数据失败')
  } finally {
    loading.value = false
  }
}

// 事件处理
const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  queryParams.teamName = ''
  initDateRange()
  handleSearch()
}

const handleDateChange = (val: [string, string] | null) => {
  if (val) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
}

const handleExport = () => {
  ElMessage.success('导出功能开发中...')
}

// 辅助函数
const formatMoney = (val: number | string) => {
  if (!val) return '0.00'
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getProgressStatus = (rate: number) => {
  if (rate >= 100) return 'success'
  if (rate >= 80) return 'warning'
  return 'exception'
}
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.statistics-cards {
  margin-bottom: 20px;
  
  .stat-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      color: #606266;
    }
    
    .card-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
      margin-top: 10px;
      
      &.highlight {
        color: #409eff;
      }
    }
  }
}

.search-card {
  margin-bottom: 20px;
  .search-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.table-card {
  .card-header-flex {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .title {
      font-size: 16px;
      font-weight: bold;
    }
    
    .subtitle {
      font-size: 12px;
      color: #909399;
      margin-left: 10px;
    }
  }
  
  .money-text {
    color: #f56c6c;
    font-weight: bold;
  }
  
  .rank-badge {
    width: 24px;
    height: 24px;
    line-height: 24px;
    border-radius: 50%;
    background-color: #f0f2f5;
    color: #606266;
    margin: 0 auto;
    font-weight: bold;
    font-size: 12px;
    
    &.rank-1 {
      background-color: #f56c6c;
      color: white;
    }
    &.rank-2 {
      background-color: #e6a23c;
      color: white;
    }
    &.rank-3 {
      background-color: #409eff;
      color: white;
    }
  }
}
</style>