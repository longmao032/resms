<template>
  <div class="community-list-container">
    <!-- 搜索筛选区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="小区名称">
          <el-input v-model="queryParams.communityName" placeholder="请输入小区名称" clearable style="width: 200px"
            @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="queryParams.city" placeholder="请输入城市" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="queryParams.district" placeholder="请输入区县" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="开发商">
          <el-input v-model="queryParams.developer" placeholder="请输入开发商" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="通过" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="建成年代">
          <el-date-picker v-model="buildYearRange" type="yearrange" range-separator="-" start-placeholder="开始年份"
            end-placeholder="结束年份" style="width: 220px" @change="handleBuildYearChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon>
              <Search />
            </el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh />
            </el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" style="margin-top: 20px">
      <!-- 操作按钮区 -->
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd" v-if="canAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增小区
        </el-button>
      </div>

      <el-table v-loading="loading" :data="communityList" stripe style="width: 100%">
        <el-table-column label="封面图" width="100" align="center">
          <template #default="{ row }">
            <el-image v-if="row.coverUrl" :src="getImageUrl(row.coverUrl)"
              :preview-src-list="[getImageUrl(row.coverUrl)]" fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer;" preview-teleported />
            <div v-else class="no-cover">
              <el-icon :size="24" color="#c0c4cc">
                <Picture />
              </el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="communityName" label="小区名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区县" width="100" />
        <el-table-column prop="address" label="小区地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success">通过</el-tag>
            <el-tag v-else-if="row.status === 1" type="danger">待审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">驳回</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-tooltip content="详情" placement="top" v-if="canView">
                <el-button link type="primary" :icon="View" @click="handleView(row)" />
              </el-tooltip>
              <el-tooltip content="编辑" placement="top" v-if="canEdit">
                <el-button link type="primary" :icon="Edit" @click="handleEdit(row)" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="canDelete">
                <el-button link type="danger" :icon="Delete" @click="handleDelete(row)" />
              </el-tooltip>
              <el-tooltip content="审核" placement="top" v-if="row.status === 1 && canAudit">
                <el-button link type="success" :icon="Check" @click="handleAudit(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end" @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" title="小区审核" width="600px" append-to-body destroy-on-close>
      <div v-loading="auditLoading" class="audit-container">
        <div v-if="auditData" class="community-info">
          <!-- 小区没有图片字段，仅显示基本信息 -->
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="小区名称">{{ auditData.communityName }}</el-descriptions-item>
            <el-descriptions-item label="开发商">{{ auditData.developer || '-' }}</el-descriptions-item>
            <el-descriptions-item label="区域">{{ auditData.city }} - {{ auditData.district }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ auditData.address }}</el-descriptions-item>
            <el-descriptions-item label="建成年代">{{ auditData.buildYear || '-' }}</el-descriptions-item>
            <el-descriptions-item label="物业费">{{ auditData.propertyFee || '-' }} 元/㎡/月</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider />

        <el-form ref="auditFormRef" :model="auditForm" label-width="80px">
          <el-form-item label="审核结果" required>
            <el-radio-group v-model="auditForm.status">
              <el-radio :label="0">通过</el-radio>
              <el-radio :label="2">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核备注" v-if="auditForm.status === 2" required>
            <el-input v-model="auditForm.reason" type="textarea" placeholder="请输入驳回原因" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit" :loading="auditLoading">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit, Plus, Check, Picture, Delete } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getCommunityPage, getCommunityById, auditCommunity, deleteCommunity } from '@/api/community'
import { useUserStore } from '@/stores/userStore'
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const userStore = useUserStore()

const roleType = computed(() => userStore.userInfo?.roleType)
const isAdmin = computed(() => roleType.value === 1)
const isManager = computed(() => roleType.value === 3)
const isConsultant = computed(() => roleType.value === 2)

const canAdd = computed(() => isAdmin.value || isManager.value)
const canEdit = computed(() => isAdmin.value || isManager.value)
const canDelete = computed(() => isAdmin.value)
const canAudit = computed(() => isAdmin.value)
const canView = computed(() => isAdmin.value || isConsultant.value)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  communityName: '',
  city: '',
  district: '',
  developer: '',
  status: undefined,
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

// 审核相关
const auditVisible = ref(false)
const auditLoading = ref(false)
const auditData = ref<any>(null)
const auditForm = reactive({
  id: 0,
  status: 0,
  reason: ''
})

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
    status: undefined,
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

// 审核
const handleAudit = async (row: any) => {
  auditVisible.value = true
  auditLoading.value = true
  auditForm.id = row.id
  auditForm.status = 0 // 默认通过
  auditForm.reason = ''
  auditData.value = null

  try {
    const res = await getCommunityById(row.id)
    if (res.status) {
      auditData.value = res.data
    } else {
      ElMessage.error('获取详情失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取详情失败')
  } finally {
    auditLoading.value = false
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认要删除该小区吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCommunity(row.id)
      ElMessage.success('删除成功')
      loadCommunityList()
    } catch (error: any) {
      console.error('删除失败', error)
    }
  }).catch(() => { })
}

// 提交审核
const submitAudit = async () => {
  if (auditForm.status === 2 && !auditForm.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  auditLoading.value = true
  try {
    await auditCommunity(auditForm.id, auditForm.status, auditForm.reason)
    ElMessage.success('审核完成')
    auditVisible.value = false
    loadCommunityList()
  } catch (error) {
    console.error('审核失败', error)
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
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

.audit-container {
  .community-info {
    margin-bottom: 20px;
  }
}

.no-cover {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  margin: 0 auto;
}
</style>
