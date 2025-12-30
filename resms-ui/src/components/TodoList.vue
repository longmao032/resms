<template>
  <div class="todo-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>待办事项</span>
          <el-badge :value="totalCount" :hidden="totalCount === 0">
            <el-icon><Finished /></el-icon>
          </el-badge>
        </div>
      </template>
      
      <el-empty v-if="loading" description="加载中..." :image-size="80" />
      <el-empty v-else-if="todoItems.length === 0" description="暂无待办事项" :image-size="100" />
      
      <el-space v-else direction="vertical" :fill="true" style="width: 100%">
        <el-card 
          v-for="item in todoItems" 
          :key="item.key"
          :body-style="{ padding: '12px' }"
          shadow="hover"
        >
          <div class="todo-item">
            <div class="todo-icon">
              <el-icon :size="20" :color="item.color">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="todo-content">
              <div class="todo-title">{{ item.label }}</div>
              <div class="todo-count">
                <el-tag :type="item.count > 0 ? 'warning' : 'info'" size="small">
                  {{ item.count }} 项
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Finished, Document, HomeFilled, Shop, User, Money, Check } from '@element-plus/icons-vue'
import { getTodoStats } from '@/api/statistics'
import { useUserStore } from '@/stores/userStore'

interface TodoItem {
  key: string
  label: string
  count: number
  icon: any
  color: string
}

const userStore = useUserStore()
const loading = ref(false)
const todoData = ref<any>(null)

const todoItems = computed<TodoItem[]>(() => {
  if (!todoData.value) return []
  
  const roleType = userStore.currentUser?.roleType
  const items: TodoItem[] = []
  
  // 管理员待办
  if (roleType === 1) {
    if (todoData.value.pendingTransactionAudits !== null) {
      items.push({
        key: 'pendingTransactionAudits',
        label: '待审核交易',
        count: todoData.value.pendingTransactionAudits || 0,
        icon: Document,
        color: '#409EFF'
      })
    }
    if (todoData.value.pendingHouseAudits !== null) {
      items.push({
        key: 'pendingHouseAudits',
        label: '待审核房源',
        count: todoData.value.pendingHouseAudits || 0,
        icon: HomeFilled,
        color: '#67C23A'
      })
    }
    if (todoData.value.pendingProjectAudits !== null) {
      items.push({
        key: 'pendingProjectAudits',
        label: '待审核项目',
        count: todoData.value.pendingProjectAudits || 0,
        icon: Shop,
        color: '#E6A23C'
      })
    }
  }
  
  // 销售经理待办
  if (roleType === 3) {
    if (todoData.value.teamPendingTransactions !== null) {
      items.push({
        key: 'teamPendingTransactions',
        label: '团队待审核交易',
        count: todoData.value.teamPendingTransactions || 0,
        icon: Document,
        color: '#409EFF'
      })
    }
    if (todoData.value.teamFollowUpCustomers !== null) {
      items.push({
        key: 'teamFollowUpCustomers',
        label: '团队待跟进客户',
        count: todoData.value.teamFollowUpCustomers || 0,
        icon: User,
        color: '#67C23A'
      })
    }
  }
  
  // 销售顾问待办
  if (roleType === 2) {
    if (todoData.value.myPendingTransactions !== null) {
      items.push({
        key: 'myPendingTransactions',
        label: '我的待审核交易',
        count: todoData.value.myPendingTransactions || 0,
        icon: Document,
        color: '#409EFF'
      })
    }
    if (todoData.value.myFollowUpCustomers !== null) {
      items.push({
        key: 'myFollowUpCustomers',
        label: '我的待跟进客户',
        count: todoData.value.myFollowUpCustomers || 0,
        icon: User,
        color: '#67C23A'
      })
    }
  }
  
  // 财务待办
  if (roleType === 4) {
    if (todoData.value.pendingPaymentConfirms !== null) {
      items.push({
        key: 'pendingPaymentConfirms',
        label: '待确认收款',
        count: todoData.value.pendingPaymentConfirms || 0,
        icon: Money,
        color: '#F56C6C'
      })
    }
    if (todoData.value.pendingCommissions !== null) {
      items.push({
        key: 'pendingCommissions',
        label: '待发放佣金',
        count: todoData.value.pendingCommissions || 0,
        icon: Money,
        color: '#E6A23C'
      })
    }
    if (todoData.value.pendingFinanceConfirms !== null) {
      items.push({
        key: 'pendingFinanceConfirms',
        label: '待处理交易完成',
        count: todoData.value.pendingFinanceConfirms || 0,
        icon: Check,
        color: '#67C23A'
      })
    }
  }
  
  return items
})

const totalCount = computed(() => {
  return todoItems.value.reduce((sum, item) => sum + item.count, 0)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTodoStats()
    if (res.code === 200 && res.data) {
      todoData.value = res.data
    }
  } catch (error) {
    console.error('Failed to load todo stats:', error)
    ElMessage.error('加载待办事项失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.todo-list {
  margin: 20px 0;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .todo-item {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .todo-icon {
      flex-shrink: 0;
    }
    
    .todo-content {
      flex: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .todo-title {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
      }
    }
  }
}
</style>
