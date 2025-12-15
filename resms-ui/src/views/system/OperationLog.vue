<template>
  <div class="app-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="模块">
          <el-input v-model="query.module" placeholder="模块" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="query.operationType" placeholder="操作类型" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="query.riskLevel" placeholder="全部" clearable style="width: 120px">
            <el-option label="高危" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="普通" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
        </div>
      </template>

      <el-table :data="list" style="width: 100%" v-loading="loading" row-key="id">
        <el-table-column type="expand">
          <template #default="props">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="请求URL">{{ props.row.requestUrl }}</el-descriptions-item>
              <el-descriptions-item label="请求方法">{{ props.row.requestMethod }}</el-descriptions-item>
              <el-descriptions-item label="IP">{{ props.row.ipAddress }}</el-descriptions-item>
              <el-descriptions-item label="耗时(ms)">{{ props.row.executeTime }}</el-descriptions-item>
              <el-descriptions-item label="请求参数" :span="2">
                <el-input type="textarea" :rows="4" :model-value="props.row.requestParams" readonly />
              </el-descriptions-item>
              <el-descriptions-item label="响应结果" :span="2">
                <el-input type="textarea" :rows="4" :model-value="props.row.responseResult" readonly />
              </el-descriptions-item>
              <el-descriptions-item v-if="props.row.errorMessage" label="错误信息" :span="2">
                <el-input type="textarea" :rows="2" :model-value="props.row.errorMessage" readonly />
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </el-table-column>

        <el-table-column prop="operationTime" label="时间" width="180" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operationType" label="类型" width="140" show-overflow-tooltip />
        <el-table-column prop="operationDesc" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="riskLevel" label="风险" width="90">
          <template #default="scope">
            <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLabel(scope.row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskDimension" label="维度" width="160" show-overflow-tooltip />
        <el-table-column prop="userRealName" label="操作人" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="耗时(ms)" width="110" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

type OperationLog = {
  id: number
  module: string
  operationType: string
  operationDesc: string
  riskLevel: number
  riskDimension: string
  userRealName: string
  ipAddress: string
  requestUrl: string
  requestMethod: string
  requestParams: string
  responseResult: string
  status: number
  errorMessage: string
  executeTime: number
  operationTime: string
}

const loading = ref(false)
const list = ref<OperationLog[]>([])
const total = ref(0)

const timeRange = ref<[string, string] | null>(null)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  module: '',
  operationType: '',
  riskLevel: undefined as undefined | number,
  riskDimension: '',
  status: undefined as undefined | number,
  beginTime: '',
  endTime: ''
})

const riskLabel = (v: number) => {
  if (v === 1) return '高危'
  if (v === 2) return '中等'
  if (v === 3) return '普通'
  return '-'
}

const riskTagType = (v: number) => {
  if (v === 1) return 'danger'
  if (v === 2) return 'warning'
  return 'info'
}

const handleQuery = async () => {
  loading.value = true
  try {
    query.beginTime = timeRange.value?.[0] || ''
    query.endTime = timeRange.value?.[1] || ''

    const res: any = await request({
      url: '/operation-log/page',
      method: 'get',
      params: query
    })

    const pageData = res?.data
    list.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch (e: any) {
    ElMessage.error(e?.message || '获取操作日志失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  query.pageNum = 1
  query.pageSize = 10
  query.module = ''
  query.operationType = ''
  query.riskLevel = undefined
  query.riskDimension = ''
  query.status = undefined
  query.beginTime = ''
  query.endTime = ''
  timeRange.value = null
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
