<template>
  <div class="app-container">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item label="佣金状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="(label, value) in CommissionStatusMap" :key="value" :label="label" :value="Number(value)" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="roleType !== 2" label="销售" prop="salesId">
          <el-select v-model="queryParams.salesId" placeholder="全部" clearable filterable style="width: 180px">
            <el-option v-for="item in salesOptions" :key="item.id" :label="item.realName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>累计佣金金额</span>
            </div>
          </template>
          <div class="card-value text-gold">¥ {{ formatMoney(stats.totalCommissionAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>累计佣金笔数</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalCommissionCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>已发放金额</span>
            </div>
          </template>
          <div class="card-value text-success">¥ {{ formatMoney(stats.issuedAmount) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>佣金状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 360px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>佣金趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 360px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import * as echarts from 'echarts'
import { getCommissionStats } from '@/api/statistics'
import { getSalesList } from '@/api/appointment'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const roleType = computed(() => userStore.userInfo?.roleType)

const CommissionStatusMap: Record<number, string> = { 0: '待核算', 1: '已核算', 2: '已发放' }

const queryFormRef = ref<FormInstance>()
const queryParams = reactive<any>({
  dateRange: undefined,
  status: undefined,
  salesId: undefined
})

const salesOptions = ref<any[]>([])
const stats = ref<any>({})

const statusChartRef = ref<HTMLElement | null>(null)
const trendChartRef = ref<HTMLElement | null>(null)
let statusChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

const formatMoney = (val: any) => {
  if (!val) return '0.00'
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const initCharts = () => {
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
  }
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
  }
}

const renderStatusChart = () => {
  if (!statusChart) return
  const data = Array.isArray(stats.value.statusStats) ? stats.value.statusStats : []
  statusChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '佣金状态',
        type: 'pie',
        radius: ['40%', '70%'],
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        data
      }
    ]
  })
}

const renderTrendChart = () => {
  if (!trendChart) return
  const trend = Array.isArray(stats.value.recentTrend) ? stats.value.recentTrend : []
  const dates = trend.map((item: any) => item.date)
  const amounts = trend.map((item: any) => item.amount)
  const counts = trend.map((item: any) => item.count)
  trendChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['佣金金额', '佣金笔数'], top: '5%' },
    xAxis: [{ type: 'category', data: dates }],
    yAxis: [
      { type: 'value', name: '金额' },
      { type: 'value', name: '笔数', min: 0, interval: 1 }
    ],
    series: [
      { name: '佣金金额', type: 'bar', data: amounts, itemStyle: { color: '#e6a23c' } },
      { name: '佣金笔数', type: 'line', yAxisIndex: 1, data: counts, itemStyle: { color: '#409eff' } }
    ]
  })
}

const fetchData = async () => {
  const params: any = {}
  if (Array.isArray(queryParams.dateRange) && queryParams.dateRange.length === 2) {
    params.startDate = queryParams.dateRange[0]
    params.endDate = queryParams.dateRange[1]
  } else {
    params.days = 30
  }
  if (queryParams.status !== undefined && queryParams.status !== null && queryParams.status !== '') {
    params.status = queryParams.status
  }
  if (roleType.value !== 2 && queryParams.salesId) {
    params.salesId = queryParams.salesId
  }

  try {
    const res: any = await getCommissionStats(params)
    if (res.code === 200) {
      stats.value = res.data || {}
      renderStatusChart()
      renderTrendChart()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.error(e)
  }
}

const loadSales = async () => {
  if (roleType.value === 2) {
    salesOptions.value = []
    return
  }
  try {
    const res: any = await getSalesList()
    if (res?.code === 200) {
      salesOptions.value = Array.isArray(res.data) ? res.data : (res.data?.records || [])
    }
  } catch {
  }
}

const handleQuery = () => {
  fetchData()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.dateRange = undefined
  queryParams.status = undefined
  queryParams.salesId = undefined
  fetchData()
}

const handleResize = () => {
  statusChart?.resize()
  trendChart?.resize()
}

onMounted(() => {
  initCharts()
  loadSales()
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  padding: 10px 0;
}

.text-success {
  color: #67c23a;
}

.text-gold {
  color: #faad14;
}
</style>
