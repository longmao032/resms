<template>
  <div class="house-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" :inline="true" label-width="80px">
        <el-form-item label="房源编号">
          <el-input v-model="queryParams.houseNo" placeholder="请输入房源编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="户型">
          <el-input v-model="queryParams.layout" placeholder="请输入户型" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="房源类型">
          <el-select v-model="queryParams.houseType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="二手房" :value="1" />
            <el-option label="新房" :value="2" />
            <el-option label="租房" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待审核" :value="0" />
            <el-option label="在售" :value="1" />
            <el-option label="已预订" :value="2" />
            <el-option label="已成交" :value="3" />
            <el-option label="下架" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增房源</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" :icon="Check" :disabled="selectedIds.length === 0" @click="handleBatchAudit">
            批量审核
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="houseList"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="房源编号" prop="houseNo" width="140" />
        <el-table-column label="户型" prop="layout" width="100" />
        <el-table-column label="面积(㎡)" prop="area" width="100" align="right" />
        <el-table-column label="楼层" width="100" align="center">
          <template #default="{ row }">
            {{ row.floor }}/{{ row.totalFloor }}
          </template>
        </el-table-column>
        <el-table-column label="朝向" prop="orientation" width="100" />
        <el-table-column label="装修" prop="decoration" width="100" />
        <el-table-column label="价格(万)" width="120" align="right">
          <template #default="{ row }">
            <span class="price-text">{{ formatPrice(row.price) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="房源类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getHouseTypeTag(row.houseType)">{{ row.houseTypeText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="房源地址" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span :title="row.fullAddress">{{ row.fullAddress || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="负责销售" prop="salesName" width="120" />
        <el-table-column label="项目" prop="projectName" width="150" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" v-if="row.status === 0" @click="handleAudit(row)">审核</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="房源审核"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-loading="auditLoading" class="audit-dialog-content">
        <!-- 房源基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房源编号">{{ currentAuditHouse?.houseNo }}</el-descriptions-item>
          <el-descriptions-item label="房源类型">{{ currentAuditHouse?.houseTypeText }}</el-descriptions-item>
          <el-descriptions-item label="户型">{{ currentAuditHouse?.layout }}</el-descriptions-item>
          <el-descriptions-item label="建筑面积">{{ currentAuditHouse?.area }}㎡</el-descriptions-item>
          <el-descriptions-item label="楼层">
            {{ currentAuditHouse?.floor }}/{{ currentAuditHouse?.totalFloor }}层
          </el-descriptions-item>
          <el-descriptions-item label="朝向">{{ currentAuditHouse?.orientation }}</el-descriptions-item>
          <el-descriptions-item label="装修">{{ currentAuditHouse?.decoration }}</el-descriptions-item>
          <el-descriptions-item label="价格">
            <span class="price-text">{{ formatPrice(currentAuditHouse?.price || 0) }}万</span>
          </el-descriptions-item>
          <el-descriptions-item label="房源地址" :span="2">
            {{ currentAuditHouse?.fullAddress || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="房源描述" :span="2">
            {{ currentAuditHouse?.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 房源图片 -->
        <div class="audit-images-section" v-if="currentAuditHouse?.images && currentAuditHouse.images.length > 0">
          <h4>房源图片</h4>
          <div class="images-grid">
            <el-image
              v-for="(image, index) in currentAuditHouse.images"
              :key="index"
              :src="`http://localhost:8080/uploads${image}`"
              :preview-src-list="currentAuditHouse.images.map((img: string) => `http://localhost:8080/uploads${img}`)"
              :initial-index="index"
              fit="cover"
              class="audit-image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </div>

        <!-- 审核表单 -->
        <el-form :model="auditForm" ref="auditFormRef" :rules="auditRules" label-width="100px" class="audit-form">
          <el-form-item label="审核结果" prop="approved">
            <el-radio-group v-model="auditForm.approved">
              <el-radio :label="true">通过</el-radio>
              <el-radio :label="false">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item 
            label="审核意见" 
            prop="reason"
            :rules="auditForm.approved === false ? [{ required: true, message: '请输入审核意见', trigger: 'blur' }] : []"
          >
            <el-input
              v-model="auditForm.reason"
              type="textarea"
              :rows="3"
              :placeholder="auditForm.approved ? '可选：说明通过原因' : '必填：说明不通过原因'"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAuditSubmit" :loading="auditSubmitting">
            提交审核
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="HouseList">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete, Search, Refresh, View, Edit, Check, Picture } from '@element-plus/icons-vue'
import { getHouseList, deleteHouse, batchDeleteHouse, auditHouse, getHouseById } from '@/api/house'
import type { HouseDetail, HouseQueryParams } from '@/api/house/type'
import { useRouter } from 'vue-router'

const router = useRouter()

// 数据
const loading = ref(false)
const houseList = ref<HouseDetail[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 审核对话框相关
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditSubmitting = ref(false)
const currentAuditHouse = ref<HouseDetail | null>(null)
const auditFormRef = ref<FormInstance>()
const auditForm = reactive({
  approved: true,
  reason: ''
})
const auditRules: FormRules = {
  approved: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ]
}

// 查询参数
const queryParams = reactive<HouseQueryParams>({
  pageNum: 1,
  pageSize: 10
})

// 生命周期
onMounted(() => {
  loadHouseList()
})

// 加载房源列表
const loadHouseList = async () => {
  loading.value = true
  try {
    const res = await getHouseList(queryParams)
    // 兼容不同 axios/request 返回结构：
    // - 可能是 AxiosResponse<ApiResponse<PageResult>> (res.data 为 ApiResponse)
    // - 或拦截器已返回 ApiResponse<PageResult>（res 即为 ApiResponse）
    const anyRes: any = res
    const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
    // 尝试从不同位置读取状态和分页结果
    const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status

    if (statusFlag) {
      const page = apiResp.data ?? apiResp
      houseList.value = page.list || []
      total.value = page.total || 0
    } else {
      ElMessage.error((apiResp && apiResp.message) || '查询失败')
    }
  } catch (error) {
    console.error('加载房源列表失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索查询（重置到第1页）
const handleQuery = () => {
  queryParams.pageNum = 1
  loadHouseList()
}

// 分页大小变化（重置到第1页）
const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadHouseList()
}

// 页码变化（不重置页码）
const handlePageChange = () => {
  loadHouseList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10 })
  loadHouseList()
}

// 选择变化
const handleSelectionChange = (selection: HouseDetail[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增房源
const handleAdd = () => {
  router.push('/house/add')
}

// 编辑房源
const handleEdit = (row: HouseDetail) => {
  router.push(`/house/edit/${row.id}`)
}

// 查看详情
const handleView = (row: HouseDetail) => {
  router.push(`/house/detail/${row.id}`)
}

// 删除房源
const handleDelete = async (row: HouseDetail) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除房源「${row.houseNo}」吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteHouse(row.id)
    const anyRes: any = res
    const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
    const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status

    if (statusFlag) {
      ElMessage.success('删除成功')
      loadHouseList()
    } else {
      ElMessage.error((apiResp && apiResp.message) || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除房源失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 条房源吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await batchDeleteHouse(selectedIds.value)
    const anyRes: any = res
    const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
    const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status

    if (statusFlag) {
      // deleted count might be in apiResp.data or apiResp
      const payload = apiResp.data ?? apiResp
      const deletedCount = typeof payload === 'number' ? payload : (payload?.data ?? payload)
      ElMessage.success(`成功删除 ${deletedCount || 0} 条记录`)
      selectedIds.value = []
      loadHouseList()
    } else {
      ElMessage.error((apiResp && apiResp.message) || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败，请稍后重试')
    }
  }
}

// 审核房源 - 修改为打开对话框
const handleAudit = async (row: HouseDetail) => {
  // 打开对话框并加载详细信息
  auditDialogVisible.value = true
  auditLoading.value = true
  currentAuditHouse.value = null
  
  // 重置表单
  auditForm.approved = true
  auditForm.reason = ''
  
  try {
    const res = await getHouseById(row.id)
    if (res.status && res.data) {
      currentAuditHouse.value = res.data
    } else {
      ElMessage.error('加载房源信息失败')
      auditDialogVisible.value = false
    }
  } catch (error) {
    console.error('加载房源详情失败:', error)
    ElMessage.error('加载房源信息失败')
    auditDialogVisible.value = false
  } finally {
    auditLoading.value = false
  }
}

// 提交审核
const handleAuditSubmit = async () => {
  if (!auditFormRef.value) return
  
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        auditSubmitting.value = true
        
        const res = await auditHouse(
          currentAuditHouse.value!.id,
          auditForm.approved,
          auditForm.reason || undefined
        )
        
        const anyRes: any = res
        const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
        const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status
        
        if (statusFlag) {
          ElMessage.success(auditForm.approved ? '审核通过' : '审核已拒绝')
          auditDialogVisible.value = false
          loadHouseList()
        } else {
          ElMessage.error((apiResp && apiResp.message) || '审核失败')
        }
      } catch (error: any) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败，请稍后重试')
      } finally {
        auditSubmitting.value = false
      }
    }
  })
}

// 批量审核
const handleBatchAudit = () => {
  ElMessage.info('批量审核功能待实现')
}

// 格式化价格（元转万元）
const formatPrice = (price: number) => {
  return (price / 10000).toFixed(2)
}

// 房源类型标签
const getHouseTypeTag = (type: number) => {
  const tagMap: Record<number, string> = {
    1: '',
    2: 'success',
    3: 'warning'
  }
  return tagMap[type] || ''
}

// 状态标签
const getStatusTag = (status: number) => {
  const tagMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: '',
    4: 'danger'
  }
  return tagMap[status] || 'info'
}
</script>

<style scoped lang="scss">
.house-list-container {
  padding: 20px;

  .search-card,
  .toolbar-card,
  .table-card {
    margin-bottom: 20px;
  }

  .price-text {
    color: #f56c6c;
    font-weight: bold;
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
  
  // 审核对话框样式
  .audit-dialog-content {
    .audit-images-section {
      margin-top: 20px;
      
      h4 {
        margin-bottom: 15px;
        font-size: 14px;
        font-weight: 600;
      }
      
      .images-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 15px;
        
        .audit-image {
          width: 100%;
          height: 150px;
          border-radius: 4px;
          cursor: pointer;
          
          .image-slot {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: #f5f7fa;
            color: #909399;
            
            .el-icon {
              font-size: 30px;
              margin-bottom: 5px;
            }
          }
        }
      }
    }
    
    .audit-form {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;
    }
  }
}
</style>
