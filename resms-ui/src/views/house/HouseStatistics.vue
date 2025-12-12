<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房源总数</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalHouseCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>在售房源</span>
            </div>
          </template>
          <div class="card-value text-success">{{ stats.onSaleCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>已成交</span>
            </div>
          </template>
          <div class="card-value text-primary">{{ stats.dealCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待审核</span>
            </div>
          </template>
          <div class="card-value text-warning">{{ stats.auditCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房源状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房源类型分布</span>
            </div>
          </template>
          <div ref="typeChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { getHouseStats } from '@/api/statistics';
import { ElMessage } from 'element-plus';

const stats = ref<any>({});
const statusChartRef = ref<HTMLElement | null>(null);
const typeChartRef = ref<HTMLElement | null>(null);
let statusChart: echarts.ECharts | null = null;
let typeChart: echarts.ECharts | null = null;

const initCharts = () => {
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value);
  }
  if (typeChartRef.value) {
    typeChart = echarts.init(typeChartRef.value);
  }
};

const renderCharts = () => {
  if (statusChart && stats.value.statusStats) {
    statusChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: '房源状态',
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
          labelLine: {
            show: false
          },
          data: stats.value.statusStats
        }
      ]
    });
  }

  if (typeChart && stats.value.typeStats) {
    typeChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: '房源类型',
          type: 'pie',
          radius: '50%',
          data: stats.value.typeStats,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    });
  }
};

const fetchData = async () => {
  try {
    const res = await getHouseStats();
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
  statusChart?.resize();
  typeChart?.resize();
};

onMounted(() => {
  initCharts();
  fetchData();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  statusChart?.dispose();
  typeChart?.dispose();
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
.text-warning {
  color: #e6a23c;
}
</style>
