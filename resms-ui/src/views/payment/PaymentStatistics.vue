<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>累计收款金额</span>
            </div>
          </template>
          <div class="card-value text-gold">¥ {{ formatMoney(stats.totalPaymentAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日收款</span>
            </div>
          </template>
          <div class="card-value text-success">¥ {{ formatMoney(stats.todayPaymentAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待确认收款</span>
            </div>
          </template>
          <div class="card-value text-danger">{{ stats.pendingConfirmCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近30天收款趋势</span>
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
import { getPaymentStats } from '@/api/statistics';
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
  if (!trendChart || !stats.value.paymentTrend) return;

  const dates = stats.value.paymentTrend.map((item: any) => item.date);
  const amounts = stats.value.paymentTrend.map((item: any) => item.amount);

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'line'
      }
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '金额'
    },
    series: [
      {
        name: '收款金额',
        type: 'line',
        smooth: true,
        data: amounts,
        areaStyle: {},
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  });
};

const fetchData = async () => {
  try {
    const res = await getPaymentStats();
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
.text-success {
  color: #67c23a;
}
.text-danger {
  color: #f56c6c;
}
.text-gold {
  color: #faad14;
}
</style>