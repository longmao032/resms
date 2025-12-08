<template>
  <div class="page-container">
    <div class="page-header">
      <h2>权限管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增权限
        </el-button>
        <el-button type="warning" @click="handleBatchEnable" :disabled="selectedPermissions.length === 0">
          <el-icon><Check /></el-icon>
          批量启用
        </el-button>
        <el-button type="danger" @click="handleBatchDisable" :disabled="selectedPermissions.length === 0">
          <el-icon><Close /></el-icon>
          批量禁用
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedPermissions.length === 0">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <div class="page-content">
      <!-- 搜索表单 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="权限名称">
            <el-input
              v-model="searchForm.permissionName"
              placeholder="请输入权限名称"
              clearable
              style="width: 140px"
            />
          </el-form-item>
          <el-form-item label="权限编码">
            <el-input
              v-model="searchForm.permissionCode"
              placeholder="请输入权限编码"
              clearable
              style="width: 140px"
            />
          </el-form-item>
          <el-form-item label="权限类型">
            <el-select
              v-model="searchForm.type"
              placeholder="请选择权限类型"
              clearable
              style="width: 130px"
            >
              <el-option
                v-for="typeOption in typeOptions"
                :key="typeOption.value"
                :label="typeOption.label"
                :value="typeOption.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 100px"
            >
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
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

      <!-- 权限列表 -->
      <el-card>
        <template #header>
          <div class="card-header">
            <span>权限列表</span>
          </div>
        </template>

        <el-table
          :data="permissionList"
          style="width: 100%"
          row-key="id"
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
          @selection-change="handleSelectionChange"
          v-loading="loading"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="permissionName" label="权限名称" width="180" />
          <el-table-column prop="permissionCode" label="权限编码" width="180" />
          <el-table-column prop="typeText" label="权限类型" width="100" />
          <el-table-column prop="path" label="路由地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="component" label="组件路径" min-width="200" show-overflow-tooltip />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button
                size="small"
                :type="scope.row.status === 1 ? 'danger' : 'success'"
                @click="handleToggleStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑权限对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionFormRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限名称" prop="permissionName">
              <el-input
                v-model="permissionForm.permissionName"
                placeholder="请输入权限名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="permissionCode">
              <el-input
                v-model="permissionForm.permissionCode"
                placeholder="请输入权限编码"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限类型" prop="type">
              <el-select
                v-model="permissionForm.type"
                placeholder="请选择权限类型"
                style="width: 100%"
              >
                <el-option
                  v-for="typeOption in typeOptions"
                  :key="typeOption.value"
                  :label="typeOption.label"
                  :value="typeOption.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="父权限">
              <el-tree-select
                v-model="permissionForm.parentId"
                :data="permissionTree"
                :props="treeSelectProps"
                placeholder="请选择父权限"
                style="width: 100%"
                clearable
                filterable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="permissionForm.type === 1">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input
                v-model="permissionForm.path"
                placeholder="请输入路由地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input
                v-model="permissionForm.component"
                placeholder="请输入组件路径"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="permissionForm.type === 1">
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input
                v-model="permissionForm.icon"
                placeholder="请输入图标"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="permissionForm.sort"
                :min="0"
                :max="999"
                placeholder="请输入排序号"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-select
            v-model="permissionForm.status"
            placeholder="请选择状态"
            style="width: 200px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Check, Close, Delete } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import {
  reqPermissionPage,
  reqPermissionDetail,
  reqPermissionTree,
  reqPermissionSave,
  reqPermissionUpdate,
  reqPermissionDelete,
  reqPermissionBatchDelete,
  reqPermissionStatusUpdate,
  reqCheckPermissionName,
  reqCheckPermissionCode
} from '@/api/permission'
import type {
  PermissionQueryParams,
  PermissionVO,
  PermissionPageVO,
  PermissionSaveParams,
  PermissionStatusParams,
  PermissionTypeOption,
  ParentPermissionOption
} from '@/api/permission/type'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const permissionList = ref<PermissionVO[]>([])
const selectedPermissions = ref<PermissionVO[]>([])

// 搜索表单
const searchForm = reactive<PermissionQueryParams>({
  pageNum: 1,
  pageSize: 10,
  permissionName: '',
  permissionCode: '',
  type: undefined,
  status: undefined
})

// 权限类型选项
const typeOptions: PermissionTypeOption[] = [
  { value: 1, label: '菜单' },
  { value: 2, label: '按钮/接口' }
]

// 权限树（用于父权限选择）
const permissionTree = ref<ParentPermissionOption[]>([])

// 树形选择器配置
const treeSelectProps = {
  children: 'children',
  label: 'permissionName',
  value: 'id'
}

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const isEdit = ref(false)
const permissionFormRef = ref<FormInstance>()

const permissionForm = reactive<PermissionSaveParams>({
  permissionName: '',
  permissionCode: '',
  parentId: undefined,
  type: 2,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const permissionFormRules = reactive({
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { max: 50, message: '权限名称长度不能超过50个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { max: 50, message: '权限编码长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_:]+$/, message: '权限编码只能包含字母、数字、下划线和冒号', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  path: [
    {
      validator: (rule: any, value: string, callback: any) => {
        if (permissionForm.type === 1 && !value) {
          callback(new Error('菜单类型权限必须填写路由地址'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取权限列表
const getPermissionList = async () => {
  try {
    loading.value = true
    const params = {
      ...searchForm,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await reqPermissionPage(params)
    if (response.code === 200) {
      const pageData: PermissionPageVO = response.data
      permissionList.value = pageData.records
      total.value = pageData.total
    } else {
      ElMessage.error(response.message || '获取权限列表失败')
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取权限树
const getPermissionTree = async () => {
  try {
    const response = await reqPermissionTree()
    if (response.code === 200) {
      permissionTree.value = response.data
    }
  } catch (error) {
    console.error('获取权限树失败', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getPermissionList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    permissionName: '',
    permissionCode: '',
    type: undefined,
    status: undefined
  })
  currentPage.value = 1
  getPermissionList()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  getPermissionList()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getPermissionList()
}

// 选择变化
const handleSelectionChange = (selection: PermissionVO[]) => {
  selectedPermissions.value = selection
}

// 新增权限
const handleAdd = () => {
  dialogTitle.value = '新增权限'
  isEdit.value = false
  Object.assign(permissionForm, {
    permissionName: '',
    permissionCode: '',
    parentId: undefined,
    type: 2,
    path: '',
    component: '',
    icon: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = async (row: PermissionVO) => {
  try {
    const response = await reqPermissionDetail(row.id)
    if (response.code === 200) {
      dialogTitle.value = '编辑权限'
      isEdit.value = true
      const permissionData = response.data
      Object.assign(permissionForm, {
        id: permissionData.id,
        permissionName: permissionData.permissionName,
        permissionCode: permissionData.permissionCode,
        parentId: permissionData.parentId,
        type: permissionData.type,
        path: permissionData.path || '',
        component: permissionData.component || '',
        icon: permissionData.icon || '',
        sort: permissionData.sort,
        status: permissionData.status
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取权限信息失败')
    }
  } catch (error) {
    ElMessage.error('获取权限信息失败')
    console.error(error)
  }
}

// 删除权限
const handleDelete = async (row: PermissionVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${row.permissionName}" 吗？此操作不可恢复！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqPermissionDelete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除权限成功')
      getPermissionList()
    } else {
      ElMessage.error(response.message || '删除权限失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除权限失败')
      console.error(error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    const permissionIds = selectedPermissions.value.map(permission => permission.id)
    await ElMessageBox.confirm(
      `确定要删除选中的 ${permissionIds.length} 个权限吗？此操作不可恢复！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqPermissionBatchDelete({ permissionIds })
    if (response.code === 200) {
      ElMessage.success('批量删除权限成功')
      selectedPermissions.value = []
      getPermissionList()
    } else {
      ElMessage.error(response.message || '批量删除权限失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除权限失败')
      console.error(error)
    }
  }
}

// 批量启用
const handleBatchEnable = async () => {
  await handleBatchStatusUpdate(1, '批量启用权限成功')
}

// 批量禁用
const handleBatchDisable = async () => {
  await handleBatchStatusUpdate(0, '批量禁用权限成功')
}

// 批量状态更新
const handleBatchStatusUpdate = async (status: number, successMessage: string) => {
  try {
    const permissionIds = selectedPermissions.value.map(permission => permission.id)
    const params: PermissionStatusParams = {
      permissionIds,
      status
    }

    const response = await reqPermissionStatusUpdate(params)
    if (response.code === 200) {
      ElMessage.success(successMessage)
      selectedPermissions.value = []
      getPermissionList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

// 切换权限状态
const handleToggleStatus = async (row: PermissionVO) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const params: PermissionStatusParams = {
      permissionIds: [row.id],
      status: newStatus
    }

    const response = await reqPermissionStatusUpdate(params)
    if (response.code === 200) {
      ElMessage.success(`${newStatus === 1 ? '启用' : '禁用'}权限成功`)
      getPermissionList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!permissionFormRef.value) return

  try {
    await permissionFormRef.value.validate()
    submitLoading.value = true

    let response
    if (isEdit.value) {
      response = await reqPermissionUpdate(permissionForm)
    } else {
      response = await reqPermissionSave(permissionForm)
    }

    if (response.code === 200) {
      ElMessage.success(`${isEdit.value ? '编辑' : '新增'}权限成功`)
      dialogVisible.value = false
      getPermissionList()
      getPermissionTree() // 重新获取权限树
    } else {
      ElMessage.error(response.message || `${isEdit.value ? '编辑' : '新增'}权限失败`)
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  permissionFormRef.value?.resetFields()
  dialogVisible.value = false
}

// 组件挂载
onMounted(() => {
  getPermissionList()
  getPermissionTree()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    margin: 0;
    color: #333;
  }

  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.search-card {
  margin-bottom: 20px;

  .search-form {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}

.page-content {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;

    .header-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }

  .search-card .search-form {
    .el-form-item {
      display: block;
      margin-right: 0;

      .el-input,
      .el-select {
        width: 100% !important;
      }
    }
  }

  .el-table {
    font-size: 12px;

    .el-table__cell {
      padding: 8px 0;
    }
  }
}

@media (max-width: 480px) {
  .header-actions {
    flex-direction: column;
    width: 100%;

    .el-button {
      width: 100%;
      margin-bottom: 5px;
    }
  }

  .el-dialog {
    width: 95% !important;
    margin: 5vh auto;
  }
}
</style>
