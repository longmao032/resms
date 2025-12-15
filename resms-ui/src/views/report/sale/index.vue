<template>
  <div class="app-container">
    <el-card shadow="never" class="mb-20">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="统计时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
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
        <span>销售业绩报表</span>
      </div>
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="staffName" label="销售人员" align="center" />
        <el-table-column prop="teamName" label="所属团队" align="center" />
        <el-table-column prop="dealCount" label="成交单数" align="center" sortable />
        <el-table-column prop="totalDealAmount" label="成交总金额 (元)" align="center" sortable>
          <template #default="scope">
            {{ formatMoney(scope.row.totalDealAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCommissionAmount" label="佣金总金额 (元)" align="center" sortable>
          <template #default="scope">
            {{ formatMoney(scope.row.totalCommissionAmount) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getSaleReport, exportSaleReport } from '@/api/report';
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
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    const res = await getSaleReport(params);
    if (res.code === 200) {
      tableData.value = res.data;
    }
  } catch (error: any) {
    console.error(error);
    const msg = error?.response?.data?.message || error?.message || '加载失败';
    ElMessage.error(msg);
  } finally {
    loading.value = false;
  }
};

const handleExport = () => {
  doExport();
};

const doExport = async () => {
  try {
    const params: any = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    const blob: any = await exportSaleReport(params);
    const fileBlob = blob instanceof Blob ? blob : new Blob([blob]);
    const url = window.URL.createObjectURL(fileBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `销售报表_${Date.now()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  } catch (error: any) {
    const msg = error?.response?.data?.message || error?.message || '导出失败';
    ElMessage.error(msg);
  }
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
