<template>
  <div class="community-list-container">
    <!-- 搜索筛选区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="小区名称">
          <el-input
            v-model="queryParams.communityName"
            placeholder="请输入小区名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="城市">
          <el-input
            v-model="queryParams.city"
            placeholder="请输入城市"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="区县">
          <el-input
            v-model="queryParams.district"
            placeholder="请输入区县"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="开发商">
          <el-input
            v-model="queryParams.developer"
            placeholder="请输入开发商"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="建成年代">
          <el-date-picker
            v-model="buildYearRange"
            type="yearrange"
            range-separator="-"
            start-placeholder="开始年份"
            end-placeholder="结束年份"
            style="width: 220px"
            @change="handleBuildYearChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" style="margin-top: 20px">
      <!-- 操作按钮区 -->
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增小区
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="communityList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="communityName" label="小区名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区县" width="100" />
        <el-table-column prop="address" label="小区地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="buildYear" label="建成年代" width="100">
          <template #default="{ row }">
            {{ row.buildYear ? row.buildYear + '年' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="developer" label="开发商" min-width="150" show-overflow-tooltip />
        <el-table-column prop="totalHouseholds" label="总户数" width="100" />
        <el-table-column prop="propertyFee" label="物业费(元/㎡/月)" width="140" align="right">
          <template #default="{ row }">
            {{ row.propertyFee || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="metroStation" label="最近地铁" width="120" show-overflow-tooltip />
        <el-table-column prop="schoolDistrict" label="所属学区" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View, Edit, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getCommunityPage } from '@/api/community'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  communityName: '',
  city: '',
  district: '',
  developer: '',
  minBuildYear: undefined as number | undefined,
  maxBuildYear: undefined as number | undefined,
  sortField: '',
  sortOrder: 'DESC'
})

// 建成年代范围
const buildYearRange = ref<any>(null)

// 数据列表
const communityList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 处理建成年代范围变化
const handleBuildYearChange = (val: any) => {
  if (val && val.length === 2) {
    queryParams.minBuildYear = val[0].getFullYear()
    queryParams.maxBuildYear = val[1].getFullYear()
  } else {
    queryParams.minBuildYear = undefined
    queryParams.maxBuildYear = undefined
  }
}

// 加载数据
const loadCommunityList = async () => {
  loading.value = true
  try {
    const res = await getCommunityPage(queryParams)
    if (res.status && res.data) {
      communityList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载小区列表失败:', error)
    ElMessage.error('加载小区列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  loadCommunityList()
}

// 重置
const handleReset = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    communityName: '',
    city: '',
    district: '',
    developer: '',
    minBuildYear: undefined,
    maxBuildYear: undefined,
    sortField: '',
    sortOrder: 'DESC'
  })
  buildYearRange.value = null
  loadCommunityList()
}

// 分页大小变化
const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadCommunityList()
}

// 页码变化
const handlePageChange = () => {
  loadCommunityList()
}

// 查看详情
const handleView = (row: any) => {
  router.push(`/community/detail/${row.id}`)
}

// 编辑
const handleEdit = (row: any) => {
  router.push(`/community/edit/${row.id}`)
}

// 新增
const handleAdd = () => {
  router.push('/community/add')
}

// 组件挂载时加载数据
onMounted(() => {
  loadCommunityList()
})
</script>

<style scoped lang="scss">
.community-list-container {
  padding: 20px;

  .search-card {
    :deep(.el-card__body) {
      padding-bottom: 0;
    }

    .el-form-item {
      margin-bottom: 20px;
    }
  }
}
</style>
