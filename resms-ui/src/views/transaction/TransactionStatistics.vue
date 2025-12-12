<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>累计交易总额</span>
            </div>
          </template>
          <div class="card-value text-gold">¥ {{ formatMoney(stats.totalDealAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>累计交易单数</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalTransactionCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待审核交易</span>
            </div>
          </template>
          <div class="card-value text-danger">{{ stats.pendingAuditCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近30天交易趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { getTransactionStats } from '@/api/statistics';
import { ElMessage } from 'element-plus';

const stats = ref<any>({});
const trendChartRef = ref<HTMLElement | null>(null);
let trendChart: echarts.ECharts | null = null;

const formatMoney = (val: any) => {
  if (!val) return '0.00';
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

const initCharts = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
  }
};

const renderCharts = () => {
  if (!trendChart || !stats.value.recentTrend) return;

  const dates = stats.value.recentTrend.map((item: any) => item.date);
  const amounts = stats.value.recentTrend.map((item: any) => item.amount);
  const counts = stats.value.recentTrend.map((item: any) => item.count);

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['交易金额', '交易单数'],
      top: '5%'
    },
    xAxis: [
      {
        type: 'category',
        data: dates,
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '金额',
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '单数',
        min: 0,
        interval: 1,
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '交易金额',
        type: 'bar',
        tooltip: {
          valueFormatter: function (value: any) {
            return '¥' + value;
          }
        },
        data: amounts,
        itemStyle: {
          color: '#e6a23c'
        }
      },
      {
        name: '交易单数',
        type: 'line',
        yAxisIndex: 1,
        tooltip: {
          valueFormatter: function (value: any) {
            return value + ' 单';
          }
        },
        data: counts,
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  });
};

const fetchData = async () => {
  try {
    const res = await getTransactionStats();
    if (res.code === 200) {
      stats.value = res.data;
      renderCharts();
    } else {
      ElMessage.error(res.message);
    }
  } catch (error) {
    console.error(error);
  }
};

const handleResize = () => {
  trendChart?.resize();
};

onMounted(() => {
  initCharts();
  fetchData();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  trendChart?.dispose();
});
</script>

<style scoped>
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
.text-danger {
  color: #f56c6c;
}
.text-gold {
  color: #faad14;
}
</style>
