<template>
  <div class="page-container">
    <div class="page-header">
      <h2>角色管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>
    </div>

    <div class="page-content">
      <!-- 搜索表单 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="角色名称">
            <el-input
              v-model="searchForm.roleName"
              placeholder="请输入角色名称"
              clearable
              style="width: 140px"
            />
          </el-form-item>
          <el-form-item label="角色编码">
            <el-input
              v-model="searchForm.roleCode"
              placeholder="请输入角色编码"
              clearable
              style="width: 140px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="启用" :value="1" />
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

      <!-- 角色列表 -->
      <el-card>
        <template #header>
          <div class="card-header">
            <span>角色列表</span>
          </div>
        </template>

        <el-table
          :data="roleList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          v-loading="loading"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="roleName" label="角色名称" width="150" />
          <el-table-column prop="roleCode" label="角色编码" width="150" />
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column prop="dataScopeText" label="数据权限" width="120" />
          <el-table-column prop="userCount" label="用户数量" width="100" />
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
              <el-button size="small" type="success" @click="handleAssignPermissions(scope.row)">权限配置</el-button>
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input
                v-model="roleForm.roleName"
                placeholder="请输入角色名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色编码" prop="roleCode">
              <el-input
                v-model="roleForm.roleCode"
                placeholder="请输入角色编码"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            placeholder="请输入角色描述"
            :rows="3"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据权限" prop="dataScope">
              <el-select
                v-model="roleForm.dataScope"
                placeholder="请选择数据权限范围"
                style="width: 100%"
              >
                <el-option
                  v-for="scope in dataScopeOptions"
                  :key="scope.value"
                  :label="scope.label"
                  :value="scope.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select
                v-model="roleForm.status"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- 权限配置对话框 -->
    <el-dialog
      title="权限配置"
      v-model="permissionDialogVisible"
      width="800px"
      @close="handlePermissionDialogClose"
    >
      <div class="permission-content">
        <div class="role-info">
          <h4>{{ currentRole?.roleName }} ({{ currentRole?.roleCode }})</h4>
          <p>{{ currentRole?.description }}</p>
        </div>

        <div class="permission-tree">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :props="treeProps"
            :default-checked-keys="checkedPermissionIds"
            @check-change="handlePermissionCheckChange"
          />
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermissions" :loading="permissionSubmitLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import {
  reqRolePage,
  reqRoleDetail,
  reqRoleSave,
  reqRoleUpdate,
  reqRoleDelete,
  reqRoleBatchDelete,
  reqRoleAssignPermissions,
  reqRolePermissions
} from '@/api/role'
import { reqPermissionTree } from '@/api/permission'
import type {
  RoleQueryParams,
  RoleVO,
  RolePageVO,
  RoleSaveParams,
  RolePermissionParams,
  DataScopeOption
} from '@/api/role/type'
import type { PermissionVO } from '@/api/permission/type'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const permissionSubmitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const roleList = ref<RoleVO[]>([])
const selectedRoles = ref<RoleVO[]>([])

// 搜索表单
const searchForm = reactive<RoleQueryParams>({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleCode: '',
  status: undefined
})

// 数据权限范围选项
const dataScopeOptions: DataScopeOption[] = [
  { value: 1, label: '全部数据' },
  { value: 2, label: '本部门数据' },
  { value: 3, label: '本部门及以下数据' },
  { value: 4, label: '仅本人数据' }
]

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const isEdit = ref(false)
const roleFormRef = ref<FormInstance>()

const roleForm = reactive<RoleSaveParams>({
  roleName: '',
  roleCode: '',
  description: '',
  dataScope: 1,
  status: 1
})

// 表单验证规则
const roleFormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { max: 50, message: '角色名称长度不能超过50个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { max: 50, message: '角色编码长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '角色编码只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '角色描述长度不能超过200个字符', trigger: 'blur' }
  ],
  dataScope: [
    { required: true, message: '请选择数据权限范围', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 权限配置相关
const permissionDialogVisible = ref(false)
const currentRole = ref<RoleVO | null>(null)
const permissionTree = ref<any[]>([])
const checkedPermissionIds = ref<number[]>([])
const permissionTreeRef = ref()

const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 获取角色列表
const getRoleList = async () => {
  try {
    loading.value = true
    const params = {
      ...searchForm,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await reqRolePage(params)
    if (response.code === 200) {
      const pageData: RolePageVO = response.data
      roleList.value = pageData.records
      total.value = pageData.total
    } else {
      ElMessage.error(response.message || '获取角色列表失败')
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getRoleList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    roleName: '',
    roleCode: '',
    status: undefined
  })
  currentPage.value = 1
  getRoleList()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  getRoleList()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getRoleList()
}

// 选择变化
const handleSelectionChange = (selection: RoleVO[]) => {
  selectedRoles.value = selection
}

// 新增角色
const handleAdd = () => {
  dialogTitle.value = '新增角色'
  isEdit.value = false
  Object.assign(roleForm, {
    roleName: '',
    roleCode: '',
    description: '',
    dataScope: 1,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = async (row: RoleVO) => {
  try {
    const response = await reqRoleDetail(row.id)
    if (response.code === 200) {
      dialogTitle.value = '编辑角色'
      isEdit.value = true
      const roleData = response.data
      Object.assign(roleForm, {
        id: roleData.id,
        roleName: roleData.roleName,
        roleCode: roleData.roleCode,
        description: roleData.description,
        dataScope: roleData.dataScope,
        status: roleData.status
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取角色信息失败')
    }
  } catch (error) {
    ElMessage.error('获取角色信息失败')
    console.error(error)
  }
}

// 删除角色
const handleDelete = async (row: RoleVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${row.roleName}" 吗？此操作不可恢复！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqRoleDelete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除角色成功')
      getRoleList()
    } else {
      ElMessage.error(response.message || '删除角色失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除角色失败')
      console.error(error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return

  try {
    await roleFormRef.value.validate()
    submitLoading.value = true

    let response
    if (isEdit.value) {
      response = await reqRoleUpdate(roleForm)
    } else {
      response = await reqRoleSave(roleForm)
    }

    if (response.code === 200) {
      ElMessage.success(`${isEdit.value ? '编辑' : '新增'}角色成功`)
      dialogVisible.value = false
      getRoleList()
    } else {
      ElMessage.error(response.message || `${isEdit.value ? '编辑' : '新增'}角色失败`)
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  roleFormRef.value?.resetFields()
  dialogVisible.value = false
}

// 配置权限
const handleAssignPermissions = async (row: RoleVO) => {
  currentRole.value = row
  permissionDialogVisible.value = true

  try {
    // 获取角色的权限
    const rolePermissionsResponse = await reqRolePermissions(row.id)
    if (rolePermissionsResponse.code === 200) {
      checkedPermissionIds.value = rolePermissionsResponse.data || []
    }

    // 获取权限树数据
    const permissionTreeResponse = await reqPermissionTree()
    if (permissionTreeResponse.code === 200) {
      permissionTree.value = permissionTreeResponse.data
    }
  } catch (error) {
    ElMessage.error('获取权限数据失败')
    console.error(error)
  }
}

// 保存权限配置
const handleSavePermissions = async () => {
  if (!currentRole.value) return

  try {
    permissionSubmitLoading.value = true

    // 获取选中的权限ID
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const allCheckedKeys = [...new Set([...checkedKeys, ...halfCheckedKeys])]

    const params: RolePermissionParams = {
      roleId: currentRole.value.id,
      permissionIds: allCheckedKeys
    }

    const response = await reqRoleAssignPermissions(params)
    if (response.code === 200) {
      ElMessage.success('权限配置成功')
      permissionDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '权限配置失败')
    }
  } catch (error) {
    ElMessage.error('权限配置失败')
    console.error(error)
  } finally {
    permissionSubmitLoading.value = false
  }
}

// 权限树选择变化
const handlePermissionCheckChange = () => {
  // 可以在这里处理权限选择变化的逻辑
}

// 关闭权限配置对话框
const handlePermissionDialogClose = () => {
  permissionDialogVisible.value = false
  currentRole.value = null
  checkedPermissionIds.value = []
}

// 组件挂载
onMounted(() => {
  getRoleList()
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

.permission-content {
  .role-info {
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;

    h4 {
      margin: 0 0 8px 0;
      color: #303133;
    }

    p {
      margin: 0;
      color: #606266;
      font-size: 14px;
    }
  }

  .permission-tree {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;

    :deep(.el-tree) {
      background-color: transparent;
    }
  }
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

  .el-dialog {
    width: 95% !important;
    margin: 5vh auto;
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

  .permission-content .permission-tree {
    max-height: 300px;
  }
}
</style>


