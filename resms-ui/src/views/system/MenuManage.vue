<template>
  <div class="menu-manage-container">
    <div class="page-header">
      <h2>菜单管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增菜单
        </el-button>
        <el-button @click="handleExpandAll">
          <el-icon>
            <Expand />
          </el-icon>
          {{ expandAll ? '折叠全部' : '展开全部' }}
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="菜单编码">
          <el-input v-model="queryParams.menuCode" placeholder="请输入菜单编码" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-select v-model="queryParams.menuType" placeholder="请选择菜单类型" clearable style="width: 150px">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="数据权限" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon>
              <Search />
            </el-icon>
            查询
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

    <!-- 菜单树表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="menuTreeData" row-key="id" :default-expand-all="expandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" border stripe>
        <el-table-column prop="menuName" label="菜单名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="menuCode" label="菜单编码" min-width="180" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" :size="18">
              <component :is="getIconComponent(row.icon)" />
            </el-icon>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuTypeText" label="菜单类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 1" type="primary">{{ row.menuTypeText }}</el-tag>
            <el-tag v-else-if="row.menuType === 2" type="success">{{ row.menuTypeText }}</el-tag>
            <el-tag v-else type="info">{{ row.menuTypeText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="path" label="路由路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
              <el-tooltip content="新增" placement="top">
                <el-button link type="primary" :icon="Plus" @click="handleAdd(row)" />
              </el-tooltip>
              <el-tooltip content="编辑" placement="top">
                <el-button link type="primary" :icon="Edit" @click="handleEdit(row)" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="danger" :icon="Delete" @click="handleDelete(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 菜单编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false"
      @close="handleDialogClose">
      <el-form ref="menuFormRef" :model="menuForm" :rules="menuRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select v-model="menuForm.parentId" :data="parentMenuOptions"
                :props="{ label: 'menuName', value: 'id', children: 'children' }" check-strictly
                :render-after-expand="false" placeholder="请选择上级菜单" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单编码" prop="menuCode">
              <el-input v-model="menuForm.menuCode" placeholder="请输入菜单编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="menuForm.menuType">
                <el-radio :value="1">菜单</el-radio>
                <el-radio :value="2">按钮</el-radio>
                <el-radio :value="3">数据权限</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sortOrder">
              <el-input-number v-model="menuForm.sortOrder" :min="0" :max="9999" controls-position="right"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="menuForm.menuType === 1">
          <el-col :span="12">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单图标" prop="icon">
              <el-input v-model="menuForm.icon" placeholder="请输入图标名称">
                <template #prefix>
                  <el-icon v-if="menuForm.icon">
                    <component :is="getIconComponent(menuForm.icon)" />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
              <el-radio-group v-model="menuForm.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="菜单描述" prop="description">
              <el-input v-model="menuForm.description" type="textarea" :rows="3" placeholder="请输入菜单描述" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Edit,
  Delete,
  Search,
  Refresh,
  Expand
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import {
  getMenuTree,
  getMenuById,
  getParentMenuTree,
  saveMenu,
  updateMenu,
  deleteMenu,
  updateMenuStatus
} from '@/api/menu'
import type { MenuTreeNode, MenuFormData, MenuQueryParams } from '@/api/menu/type'

// 数据定义
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const isEdit = ref(false)
const expandAll = ref(false)
const menuFormRef = ref<FormInstance>()

// 查询参数
const queryParams = reactive<MenuQueryParams>({
  menuName: '',
  menuCode: '',
  menuType: undefined,
  status: undefined
})

// 菜单树数据
const menuTreeData = ref<MenuTreeNode[]>([])

// 父菜单选项
const parentMenuOptions = ref<MenuTreeNode[]>([])

// 菜单表单
const menuForm = reactive<MenuFormData>({
  id: undefined,
  menuName: '',
  menuCode: '',
  parentId: 0,
  menuType: 1,
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  description: '',
  status: 1
})

// 表单验证规则
const validateMenuPath = (_rule: any, value: any, callback: any) => {
  if (menuForm.menuType === 1) {
    if (!value || String(value).trim().length === 0) {
      callback(new Error('请输入路由路径'))
      return
    }
  }
  callback()
}

const menuRules = reactive<FormRules<MenuFormData>>({
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  menuCode: [
    { required: true, message: '请输入菜单编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_:]*$/, message: '编码必须以字母开头，只能包含字母、数字、下划线和冒号', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择上级菜单', trigger: 'change' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  path: [
    { validator: validateMenuPath, trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入显示排序', trigger: 'blur' }
  ]
})

const userStore = useUserStore()

// 图标映射（与 AdminLayout 保持一致）
const iconMap: Record<string, any> = {
  setting: 'ElIconSetting',
  user: 'ElIconUser',
  home: 'ElIconHouse',
  team: 'ElIconAvatar',
  apartment: 'ElIconOfficeBuilding',
  menu: 'ElIconMenu',
  book: 'ElIconReading',
  table: 'ElIconGrid',
  audit: 'ElIconCheck',
  'bar-chart': 'ElIconTrendCharts',
  'customer-service': 'ElIconService',
  form: 'ElIconDocument',
  interaction: 'ElIconChatDotRound',
  transaction: 'ElIconCoin',
  'money-collect': 'ElIconWallet',
  calculator: 'ElIconManagement',
  trophy: 'ElIconTrophy'
}

const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || 'ElIconSetting'
}

// 方法定义

/**
 * 加载菜单树
 */
const loadMenuTree = async () => {
  loading.value = true
  try {
    const response = await getMenuTree(queryParams)
    if (response.status) {
      menuTreeData.value = response.data || []
    } else {
      ElMessage.error(response.message || '加载菜单树失败')
    }
  } catch (error: any) {
    console.error('加载菜单树失败:', error)
    ElMessage.error(error.message || '加载菜单树失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载父菜单树
 */
const loadParentMenuTree = async (excludeId?: number) => {
  try {
    const response = await getParentMenuTree(excludeId)
    if (response.status) {
      parentMenuOptions.value = response.data || []
    }
  } catch (error: any) {
    console.error('加载父菜单树失败:', error)
    ElMessage.error('加载父菜单树失败')
  }
}

/**
 * 查询
 */
const handleQuery = () => {
  loadMenuTree()
}

/**
 * 重置查询
 */
const handleReset = () => {
  queryParams.menuName = ''
  queryParams.menuCode = ''
  queryParams.menuType = undefined
  queryParams.status = undefined
  loadMenuTree()
}

/**
 * 展开/折叠全部
 */
const handleExpandAll = () => {
  expandAll.value = !expandAll.value
}

/**
 * 新增菜单
 */
const handleAdd = async (row?: MenuTreeNode) => {
  isEdit.value = false
  dialogTitle.value = '新增菜单'

  // 重置表单
  resetForm()

  // 如果有父级菜单，设置父级ID
  if (row) {
    menuForm.parentId = row.id
  }

  // 加载父菜单树
  await loadParentMenuTree()

  dialogVisible.value = true
}

/**
 * 编辑菜单
 */
const handleEdit = async (row: MenuTreeNode) => {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'

  // 加载父菜单树（排除当前菜单及其子孙菜单）
  await loadParentMenuTree(row.id)

  // 加载菜单详情
  try {
    const response = await getMenuById(row.id)
    if (response.status) {
      const data = response.data
      menuForm.id = data.id
      menuForm.menuName = data.menuName
      menuForm.menuCode = data.menuCode
      menuForm.parentId = data.parentId
      menuForm.menuType = data.menuType
      menuForm.path = data.path || ''
      menuForm.component = data.component || ''
      menuForm.icon = data.icon || ''
      menuForm.sortOrder = data.sortOrder
      menuForm.description = data.description || ''
      menuForm.status = data.status
    } else {
      ElMessage.error(response.message || '加载菜单详情失败')
      return
    }
  } catch (error: any) {
    console.error('加载菜单详情失败:', error)
    ElMessage.error('加载菜单详情失败')
    return
  }

  dialogVisible.value = true
}

/**
 * 删除菜单
 */
const handleDelete = (row: MenuTreeNode) => {
  ElMessageBox.confirm(
    `确定要删除菜单"${row.menuName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteMenu(row.id)
      if (response.status) {
        ElMessage.success('删除成功')
        await loadMenuTree()
        await userStore.fetchUserInfo()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error: any) {
      console.error('删除菜单失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 状态切换
 */
const handleStatusChange = async (row: MenuTreeNode) => {
  try {
    const response = await updateMenuStatus({
      ids: [row.id],
      status: row.status
    })
    if (response.status) {
      ElMessage.success('状态更新成功')
      await userStore.fetchUserInfo()
    } else {
      // 恢复原状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(response.message || '状态更新失败')
    }
  } catch (error: any) {
    console.error('更新菜单状态失败:', error)
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!menuFormRef.value) return
  try {
    await menuFormRef.value.validate()
  } catch (err) {
    return
  }

  submitLoading.value = true
  try {
    const responseAny: any = isEdit.value
      ? await updateMenu(menuForm)
      : await saveMenu(menuForm)
    const apiResp = responseAny.data ?? responseAny

    if (apiResp.status) {
      ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
      dialogVisible.value = false
      await loadMenuTree()
      await userStore.fetchUserInfo()
    } else {
      ElMessage.error(apiResp.message || (isEdit.value ? '更新失败' : '新增失败'))
    }
  } catch (error: any) {
    console.error('保存菜单失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  menuForm.id = undefined
  menuForm.menuName = ''
  menuForm.menuCode = ''
  menuForm.parentId = 0
  menuForm.menuType = 1
  menuForm.path = ''
  menuForm.component = ''
  menuForm.icon = ''
  menuForm.sortOrder = 0
  menuForm.description = ''
  menuForm.status = 1

  menuFormRef.value?.resetFields()
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  resetForm()
}

// 生命周期
onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped lang="scss">
.menu-manage-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 0 20px 0;
  border-bottom: 1px solid #e4e7ed;

  h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 500;
    color: #303133;
  }

  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.search-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 20px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.table-card {
  :deep(.el-card__body) {
    padding: 20px;
  }

  :deep(.el-table) {
    .el-table__header {
      th {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: 500;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
