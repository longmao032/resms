<template>
  <div class="trend-chart">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>业务趋势</span>
          <el-radio-group v-model="days" size="small" @change="loadData">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="14">近14天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getTrendStats } from '@/api/statistics'
import type { EChartsOption } from 'echarts'

const chartRef = ref<HTMLDivElement>()
const days = ref(7)
let chartInstance: echarts.ECharts | null = null

const loadData = async () => {
  try {
    const res = await getTrendStats(days.value)
    if (res.code === 200 && res.data) {
      renderChart(res.data)
    }
  } catch (error) {
    console.error('Failed to load trend data:', error)
  }
}

const renderChart = (data: any) => {
  if (!chartInstance) return

  const dailyStats = data.dailyStats || []
  const dates = dailyStats.map((item: any) => item.date)
  const newCustomers = dailyStats.map((item: any) => item.newCustomers || 0)
  const newTransactions = dailyStats.map((item: any) => item.newTransactions || 0)
  const paymentAmounts = dailyStats.map((item: any) => item.paymentAmount || 0)

  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['新增客户', '新增交易', '收款金额']
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
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left'
      },
      {
        type: 'value',
        name: '金额（万元）',
        position: 'right',
        axisLabel: {
          formatter: (value: number) => (value / 10000).toFixed(2)
        }
      }
    ],
    series: [
      {
        name: '新增客户',
        type: 'line',
        smooth: true,
        data: newCustomers,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '新增交易',
        type: 'line',
        smooth: true,
        data: newTransactions,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '收款金额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: paymentAmounts,
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }

  chartInstance.setOption(option)
}

onMounted(() => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
    window.addEventListener('resize', () => {
      chartInstance?.resize()
    })
    loadData()
  }
})

onUnmounted(() => {
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style scoped>
.trend-chart {
  margin: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
