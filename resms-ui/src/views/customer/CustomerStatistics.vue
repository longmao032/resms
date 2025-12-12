<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>客户总数</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalCustomerCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日新增</span>
            </div>
          </template>
          <div class="card-value text-success">{{ stats.todayNewCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月新增</span>
            </div>
          </template>
          <div class="card-value text-primary">{{ stats.monthNewCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>客户意向等级</span>
            </div>
          </template>
          <div ref="intentionChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>客户来源分布</span>
            </div>
          </template>
          <div ref="sourceChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { getCustomerStats } from '@/api/statistics';
import { ElMessage } from 'element-plus';

const stats = ref<any>({});
const intentionChartRef = ref<HTMLElement | null>(null);
const sourceChartRef = ref<HTMLElement | null>(null);
let intentionChart: echarts.ECharts | null = null;
let sourceChart: echarts.ECharts | null = null;

const initCharts = () => {
  if (intentionChartRef.value) {
    intentionChart = echarts.init(intentionChartRef.value);
  }
  if (sourceChartRef.value) {
    sourceChart = echarts.init(sourceChartRef.value);
  }
};

const renderCharts = () => {
  if (intentionChart && stats.value.intentionLevelStats) {
    intentionChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: '意向等级',
          type: 'pie',
          radius: ['40%', '70%'],
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          data: stats.value.intentionLevelStats
        }
      ]
    });
  }

  if (sourceChart && stats.value.sourceStats) {
    const sourceData = stats.value.sourceStats;
    const categories = sourceData.map((item: any) => item.name);
    const data = sourceData.map((item: any) => item.value);

    sourceChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: categories,
        axisLabel: {
          interval: 0,
          rotate: 30
        }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '客户数量',
          type: 'bar',
          data: data,
          itemStyle: {
            color: '#409eff'
          }
        }
      ]
    });
  }
};

const fetchData = async () => {
  try {
    const res = await getCustomerStats();
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
  intentionChart?.resize();
  sourceChart?.resize();
};

onMounted(() => {
  initCharts();
  fetchData();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  intentionChart?.dispose();
  sourceChart?.dispose();
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
.text-primary {
  color: #409eff;
}
</style>
