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
        <span>客户转化报表</span>
      </div>
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="staffName" label="销售人员" align="center" />
        <el-table-column prop="newCustomerCount" label="新增客户数" align="center" sortable />
        <el-table-column prop="viewCount" label="带看次数" align="center" sortable />
        <el-table-column prop="dealCount" label="成交客户数" align="center" sortable />
        <el-table-column prop="conversionRate" label="成交转化率 (%)" align="center" sortable>
          <template #default="scope">
            <el-progress 
              :percentage="Number(scope.row.conversionRate)" 
              :status="getRateStatus(scope.row.conversionRate)"
            >
              {{ scope.row.conversionRate }}%
            </el-progress>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCustomerReport, exportCustomerReport } from '@/api/report';
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

const getRateStatus = (rate: number) => {
  if (rate >= 30) return 'success';
  if (rate >= 10) return 'warning';
  return 'exception';
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params: any = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    const res: any = await getCustomerReport(params);
    if (res.code === 200) {
      tableData.value = res.data;
    } else {
      ElMessage.error(res.message);
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
    const blob: any = await exportCustomerReport(params);
    const fileBlob = blob instanceof Blob ? blob : new Blob([blob]);
    const url = window.URL.createObjectURL(fileBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `客户报表_${Date.now()}.xlsx`;
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
