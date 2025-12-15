# 前端代码规范与目录结构

## 目录结构规范

```
resms-ui/
├── src/
│   ├── api/             # API接口层
│   │   ├── [module]/    # 业务模块目录（kebab-case或camelCase）
│   │   │   ├── index.ts # API方法定义
│   │   │   └── type.ts  # TypeScript类型定义
│   │   └── *.ts         # 简单模块可直接使用单文件
│   │
│   ├── assets/          # 静态资源
│   │
│   ├── components/      # 公共组件（PascalCase）
│   │
│   ├── router/          # 路由配置
│   │   └── index.ts
│   │
│   ├── stores/          # Pinia状态管理
│   │   └── *Store.ts    # 命名规范: xxxStore.ts
│   │
│   ├── utils/           # 工具函数
│   │
│   └── views/           # 页面组件
│       ├── [module]/    # 业务模块目录（kebab-case）
│       │   ├── [Module]List.vue      # 列表页
│       │   ├── [Module]Add.vue       # 新增页
│       │   ├── [Module]Edit.vue      # 编辑页
│       │   ├── [Module]Detail.vue    # 详情页
│       │   ├── [Module]Audit.vue     # 审核页（可选）
│       │   ├── [Module]Statistics.vue # 统计页（可选）
│       │   └── components/           # 模块私有组件
│       └── *.vue        # 顶级页面（Login, Dashboard等）
```

---

## 命名规范

### 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| Vue组件 | **PascalCase** | `CustomerList.vue`, `HouseEdit.vue` |
| API文件 | **camelCase** | `viewRecord/index.ts` |
| 工具文件 | **camelCase** | `request.ts`, `websocket.js` |
| Store文件 | **camelCase + Store后缀** | `userStore.ts`, `noticeStore.ts` |

### 变量/函数命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 变量 | camelCase | `queryParams`, `houseList` |
| 函数 | camelCase + 动词开头 | `handleQuery`, `loadData`, `formatPrice` |
| 常量 | UPPER_SNAKE_CASE | `API_BASE_URL`, `DEFAULT_PAGE_SIZE` |
| 组件name | PascalCase | `name="HouseList"` |

---

## Vue组件规范

### Script Setup 结构顺序

```vue
<script setup lang="ts">
// 1. 导入
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getXxxList, deleteXxx } from '@/api/xxx'
import type { XxxDetail, XxxQueryParams } from '@/api/xxx/type'

// 2. Router/Store
const router = useRouter()
const store = useXxxStore()

// 3. 响应式数据
const loading = ref(false)
const dataList = ref<XxxDetail[]>([])
const total = ref(0)
const queryParams = reactive<XxxQueryParams>({
  pageNum: 1,
  pageSize: 10
})

// 4. 计算属性
const isAdmin = computed(() => userStore.roleType === 1)

// 5. 方法定义（按功能分组）
// -- 数据加载
const loadData = async () => { ... }

// -- 事件处理
const handleQuery = () => { ... }
const handleReset = () => { ... }
const handleAdd = () => { ... }
const handleEdit = (row: XxxDetail) => { ... }
const handleDelete = (row: XxxDetail) => { ... }

// -- 工具函数
const formatPrice = (price: number) => { ... }

// 6. 生命周期
onMounted(() => {
  loadData()
})
</script>
```

### Template 结构规范

```vue
<template>
  <div class="xxx-list-container">
    <!-- 搜索筛选区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true">
        ...
      </el-form>
    </el-card>

    <!-- 工具栏（可选） -->
    <el-card shadow="never" class="toolbar-card">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="dataList" border stripe>
        ...
      </el-table>
      
      <!-- 分页 -->
      <el-pagination ... />
    </el-card>

    <!-- 弹窗组件 -->
    <el-dialog v-model="dialogVisible" ...>
      ...
    </el-dialog>
  </div>
</template>
```

---

## API 规范

### 目录结构

```
api/
├── house/
│   ├── index.ts    # API方法
│   └── type.ts     # 类型定义
├── customer/
│   ├── index.ts
│   └── type.ts
└── notice.ts       # 简单模块可用单文件
```

### API 方法命名

| 操作 | 命名规范 | 示例 |
|------|----------|------|
| 列表查询 | `getXxxList`, `getXxxPage` | `getHouseList`, `getCustomerPage` |
| 详情查询 | `getXxxById`, `getXxxDetail` | `getHouseById`, `getCustomerDetail` |
| 新增 | `addXxx`, `createXxx` | `addHouse`, `createCustomer` |
| 更新 | `updateXxx` | `updateHouse`, `updateCustomer` |
| 删除 | `deleteXxx` | `deleteHouse`, `deleteCustomer` |
| 批量操作 | `batchXxx` | `batchDeleteHouse` |
| 审核 | `auditXxx` | `auditHouse`, `auditCommunity` |

### type.ts 规范

```typescript
// 查询参数
export interface XxxQueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: number
}

// 详情对象
export interface XxxDetail {
  id: number
  name: string
  status: number
  createTime: string
}

// 新增/编辑表单
export interface XxxForm {
  id?: number
  name: string
  status: number
}
```

---

## 样式规范

### Scoped SCSS 结构

```scss
<style scoped lang="scss">
.xxx-list-container {
  padding: 20px;

  .search-card,
  .toolbar-card,
  .table-card {
    margin-bottom: 20px;
  }

  // 深度选择器覆盖 Element Plus 样式
  :deep(.el-card__body) {
    padding: 16px;
  }

  // 功能区块样式
  .price-text {
    color: #f56c6c;
    font-weight: bold;
  }
}
</style>
```

### CSS 类命名

- 使用 **kebab-case** (短横线分隔)
- 容器类: `xxx-container`, `xxx-list-container`
- 功能类: `search-card`, `table-card`, `dialog-footer`
- 状态类: `is-active`, `is-loading`, `has-error`

---

## 当前目录差异分析

### views 目录

| 模块 | 当前状态 | 建议 |
|------|----------|------|
| `house/` | ✅ 6个文件,结构完整 | - |
| `customer/` | ⚠️ 3个文件+1子目录 | 建议将 `appointment/` 内容迁移 |
| `community/` | ✅ 4个文件 | - |
| `transaction/` | ✅ 3个文件 | - |
| `work-notice/` | ⚠️ 使用 `list/` 子目录 | 建议拍平为 `NoticeList.vue` |

### api 目录

| 模块 | 当前状态 | 建议 |
|------|----------|------|
| `house/` | ✅ index.ts + type.ts | - |
| `customer/` | ✅ index.ts + type.ts | - |
| `viewRecord/` | ✅ camelCase 目录 | - |
| `notice.ts` | ⚠️ 单文件 | 建议迁移到 `notice/index.ts` |
| `appointment.ts` | ⚠️ 单文件 | 建议迁移到 `appointment/index.ts` |

---

## 推荐的 ESLint/Prettier 配置

```json
// .eslintrc.cjs 推荐规则
{
  "rules": {
    "vue/multi-word-component-names": "off",
    "vue/component-name-in-template-casing": ["error", "PascalCase"],
    "@typescript-eslint/no-explicit-any": "warn",
    "no-console": "warn"
  }
}
```

```json
// .prettierrc 推荐配置
{
  "semi": false,
  "singleQuote": true,
  "printWidth": 120,
  "tabWidth": 2,
  "trailingComma": "none"
}
```
