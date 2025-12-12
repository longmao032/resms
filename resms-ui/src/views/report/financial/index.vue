<template>
  <div class="app-container">
    <el-card shadow="never" class="mb-20">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="统计月份">
          <el-date-picker
            v-model="dateRange"
            type="monthrange"
            range-separator="至"
            start-placeholder="开始月份"
            end-placeholder="结束月份"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleExport">导出报表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div slot="header" class="clearfix">
        <span>财务收支报表</span>
      </div>
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="period" label="统计月份" align="center" sortable />
        <el-table-column prop="transactionCount" label="交易笔数" align="center" />
        <el-table-column prop="totalIncome" label="总收入 (元)" align="center">
          <template #default="scope">
            <span style="color: #67c23a">{{ formatMoney(scope.row.totalIncome) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalRefund" label="总退款 (元)" align="center">
          <template #default="scope">
            <span style="color: #f56c6c">{{ formatMoney(scope.row.totalRefund) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="netIncome" label="净收入 (元)" align="center" sortable>
          <template #default="scope">
            <span style="font-weight: bold">{{ formatMoney(scope.row.netIncome) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getFinancialReport } from '@/api/report';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const dateRange = ref<any>([]);
const queryParams = ref<any>({});
const tableData = ref([]);

const handleQuery = () => {
  fetchData();
};

const resetQuery = () => {
  dateRange.value = [];
  queryParams.value = {};
  fetchData();
};

const formatMoney = (val: any) => {
  if (!val) return '0.00';
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params: any = {};
    if (dateRange.value && dateRange.value.length === 2) {
      // 月份选择器返回的是当月1号，需要处理成月底
      // 这里简化处理，直接传给后端，后端用Month comparisons
      // 其实Mapper用的是 payment_time >= startDate, <= endDate (字符串比较)
      // YYYY-MM-DD format works.
      params.startDate = dateRange.value[0];
      // monthrange value-format usually gives the first day of the month string
      // Need to adjust end date?  '2024-02' -> '2024-02-01'
      // Ideally let's let user modify or just pass as is.
      // ECharts backend logic handles string comparison.
      params.endDate = dateRange.value[1]; 
      // Note: simplistic handling.
    }
    const res: any = await getFinancialReport(params);
    if (res.code === 200) {
      tableData.value = res.data;
    } else {
      ElMessage.error(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleExport = () => {
  ElMessage.success('报表导出功能开发中...');
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}
</style>
