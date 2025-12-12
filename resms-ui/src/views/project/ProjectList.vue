<template>
  <div class="project-list-container">
    <!-- 搜索筛选区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="项目名称">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
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
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="在售" :value="1" />
            <el-option label="售罄" :value="2" />
            <el-option label="待售" :value="3" />
            <el-option label="待审核" :value="4" />
            <el-option label="已驳回" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="物业类型">
          <el-select v-model="queryParams.propertyType" placeholder="请选择类型" clearable style="width: 120px">
            <el-option label="住宅" value="住宅" />
            <el-option label="公寓" value="公寓" />
            <el-option label="别墅" value="别墅" />
            <el-option label="商住" value="商住" />
          </el-select>
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
          新增项目
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="projectList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="projectNo" label="项目编号" width="120" />
        <el-table-column label="封面图" width="100" align="center">
          <template #default="{ row }">
            <el-image 
              v-if="row.coverUrl" 
              :src="getImageUrl(row.coverUrl)" 
              :preview-src-list="[getImageUrl(row.coverUrl)]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer;"
              preview-teleported
            />
            <div v-else class="no-cover">
              <el-icon :size="24" color="#c0c4cc"><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="developer" label="开发商" min-width="150" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区县" width="100" />
        <el-table-column prop="address" label="项目地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="priceAvg" label="均价(元/㎡)" width="120" align="right">
          <template #default="{ row }">
            <span v-if="row.priceAvg">{{ row.priceAvg.toLocaleString() }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="propertyType" label="物业类型" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">在售</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">售罄</el-tag>
            <el-tag v-else-if="row.status === 3" type="warning">待售</el-tag>
            <el-tag v-else-if="row.status === 4" type="danger">待审核</el-tag>
            <el-tag v-else-if="row.status === 5" type="danger">已驳回</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalHouseholds" label="总户数" width="100" />
        <el-table-column prop="openingTime" label="开盘时间" width="120">
          <template #default="{ row }">
            {{ row.openingTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 4" link type="success" @click="handleAudit(row)">
              <el-icon><Check /></el-icon>
              审核
            </el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditVisible"
      title="项目审核"
      width="600px"
      append-to-body
      destroy-on-close
    >
      <div v-loading="auditLoading" class="audit-container">
        <div v-if="auditData" class="project-info">
          <div v-if="auditData.coverUrl" class="cover-image">
            <img :src="getImageUrl(auditData.coverUrl)" alt="项目封面" />
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="项目名称">{{ auditData.projectName }}</el-descriptions-item>
            <el-descriptions-item label="开发商">{{ auditData.developer }}</el-descriptions-item>
            <el-descriptions-item label="区域">{{ auditData.city }} - {{ auditData.district }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ auditData.address }}</el-descriptions-item>
            <el-descriptions-item label="物业类型">{{ auditData.propertyType }}</el-descriptions-item>
            <el-descriptions-item label="均价">{{ auditData.priceAvg }} 元/㎡</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider />

        <el-form ref="auditFormRef" :model="auditForm" label-width="80px">
          <el-form-item label="审核结果" required>
            <el-radio-group v-model="auditForm.status">
              <el-radio :label="3">通过 (待售)</el-radio>
              <el-radio :label="5">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核备注" v-if="auditForm.status === 5" required>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit, Plus, Check, Picture } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getProjectPage, getProjectById, auditProject } from '@/api/project'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  projectName: '',
  city: '',
  district: '',
  developer: '',
  status: undefined,
  propertyType: '',
  sortField: '',
  sortOrder: 'DESC'
})

// 数据列表
const projectList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 审核相关
const auditVisible = ref(false)
const auditLoading = ref(false)
const auditData = ref<any>(null)
const auditForm = reactive({
  id: 0,
  status: 3,
  reason: ''
})

// 加载数据
const loadProjectList = async () => {
  loading.value = true
  try {
    const res = await getProjectPage(queryParams)
    if (res.status && res.data) {
      projectList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  loadProjectList()
}

// 重置
const handleReset = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    projectName: '',
    city: '',
    district: '',
    developer: '',
    status: undefined,
    propertyType: '',
    sortField: '',
    sortOrder: 'DESC'
  })
  loadProjectList()
}

// 分页大小变化
const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadProjectList()
}

// 页码变化
const handlePageChange = () => {
  loadProjectList()
}

// 查看详情
const handleView = (row: any) => {
  router.push(`/project/detail/${row.id}`)
}

// 编辑
const handleEdit = (row: any) => {
  router.push(`/project/edit/${row.id}`)
}

// 新增
const handleAdd = () => {
  router.push('/project/add')
}

// 审核
const handleAudit = async (row: any) => {
  auditVisible.value = true
  auditLoading.value = true
  auditForm.id = row.id
  auditForm.status = 3 // 默认通过
  auditForm.reason = ''
  auditData.value = null

  try {
    const res = await getProjectById(row.id)
    if (res.status) {
      auditData.value = res.data
    } else {
      ElMessage.error('获取项目详情失败')
    }
  } catch (error) {
    console.error('获取详情失败', error)
    ElMessage.error('获取详情失败')
  } finally {
    auditLoading.value = false
  }
}

// 提交审核
const submitAudit = async () => {
  if (auditForm.status === 5 && !auditForm.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  auditLoading.value = true
  try {
    await auditProject(auditForm.id, auditForm.status, auditForm.reason)
    ElMessage.success('审核完成')
    auditVisible.value = false
    loadProjectList()
  } catch (error) {
    console.error('审核失败', error)
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

const getImageUrl = (url: string) => {
    if (!url) return ''
    return `http://localhost:8080/uploads${url}`
}

// 组件挂载时加载数据
onMounted(() => {
  loadProjectList()
})
</script>

<style scoped lang="scss">
.project-list-container {
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

.no-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.audit-container {
    .project-info {
        margin-bottom: 20px;
        
        .cover-image {
            text-align: center;
            margin-bottom: 15px;
            
            img {
                max-width: 100%;
                max-height: 200px;
                object-fit: cover;
                border-radius: 4px;
            }
        }
    }
}
</style>
