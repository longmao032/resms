<template>
  <div class="menu-manage-container">
    <div class="page-header">
      <h2>菜单管理</h2>
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
      <el-table v-loading="loading" :data="menuTreeData" row-key="id" :default-expand-all="false"
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
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import {
  getMenuTree,
  updateMenuStatus
} from '@/api/menu'
import type { MenuTreeNode, MenuQueryParams } from '@/api/menu/type'

// 数据定义
const loading = ref(false)

// 查询参数
const queryParams = reactive<MenuQueryParams>({
  menuName: '',
  menuCode: '',
  menuType: undefined,
  status: undefined
})

// 菜单树数据
const menuTreeData = ref<MenuTreeNode[]>([])

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
