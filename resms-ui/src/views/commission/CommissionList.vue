<template>
  <div class="app-container">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="交易编号" prop="transactionNo">
          <el-input v-model="queryParams.transactionNo" placeholder="请输入" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="销售姓名" prop="salesName">
          <el-input v-model="queryParams.salesName" placeholder="请输入" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="待核算" :value="0" />
            <el-option label="已核算" :value="1" />
            <el-option label="已发放" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="transactionNo" label="交易编号" min-width="140" />
        <el-table-column prop="salesName" label="销售顾问" width="120" />
        <el-table-column prop="dealPrice" label="成交价(元)" min-width="120">
          <template #default="{ row }">{{ formatMoney(row.dealPrice) }}</template>
        </el-table-column>
        <el-table-column prop="commissionRate" label="提成比例(%)" width="110" align="center" />
        <el-table-column prop="amount" label="佣金金额(元)" min-width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">{{ formatMoney(row.amount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="financeName" label="核算/发放人" width="120" />
        <el-table-column prop="updateTime" label="更新时间" width="160" />

        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="canOperate && row.status === 0" 
              link type="primary" 
              icon="Calculator" 
              @click="handleCalculate(row)"
            >核算</el-button>
            <el-button 
              v-if="canOperate && row.status === 1" 
              link type="success" 
              icon="Wallet" 
              @click="handleIssue(row)"
            >发放</el-button>
            <span v-if="row.status === 2" style="color: #909399; font-size: 12px">已完成</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { reqCommissionList, reqCalculateCommission, reqIssueCommission } from '@/api/commission';
import type { CommissionQuery, CommissionVO } from '@/api/commission/type';
import { useUserStore } from '@/stores/userStore'
import { computed } from 'vue'

const loading = ref(false);
const total = ref(0);
const tableData = ref<CommissionVO[]>([]);
const queryFormRef = ref();

const userStore = useUserStore()
const roleType = computed(() => userStore.userInfo?.roleType)
const canOperate = computed(() => roleType.value === 1 || roleType.value === 4)

const queryParams = reactive<CommissionQuery>({
  pageNum: 1,
  pageSize: 10,
  transactionNo: '',
  salesName: '',
  status: undefined
});

// 获取数据
const getList = async () => {
  loading.value = true;
  try {
    const res = await reqCommissionList(queryParams);
    if (res.code === 200) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    }
  } finally {
    loading.value = false;
  }
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.status = undefined;
  handleQuery();
};

// 业务操作：核算
const handleCalculate = (row: CommissionVO) => {
  if (!canOperate.value) return
  ElMessageBox.confirm(`确认对交易 [${row.transactionNo}] 进行佣金核算吗？`, '核算确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await reqCalculateCommission(row.id);
    if (res.code === 200) {
      ElMessage.success('核算成功');
      getList();
    } else {
      ElMessage.error(res.message);
    }
  });
};

// 业务操作：发放
const handleIssue = (row: CommissionVO) => {
  if (!canOperate.value) return
  ElMessageBox.confirm(`确认发放佣金 ${row.amount} 元给 [${row.salesName}] 吗？`, '发放确认', {
    confirmButtonText: '确定发放',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    const res = await reqIssueCommission(row.id);
    if (res.code === 200) {
      ElMessage.success('发放成功');
      getList();
    } else {
      ElMessage.error(res.message);
    }
  });
};

// 辅助函数
const formatMoney = (val: number) => val ? `¥ ${val.toLocaleString()}` : '0';
const getStatusLabel = (status: number) => ['待核算', '已核算', '已发放'][status] || '未知';
const getStatusTag = (status: number) => ['info', 'warning', 'success'][status] || '';

onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card { min-height: 500px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>