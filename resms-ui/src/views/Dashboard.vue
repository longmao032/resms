<template>
  <div class="dashboard" v-loading="loading">
    <div class="welcome-section">
      <h1>欢迎使用后台管理系统</h1>
      <p>您好，{{ userStore.currentUser?.realName }}！今天是 {{ currentDate }}</p>
    </div>

    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6" v-if="visibleCards.includes('users')">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32" color="#409EFF"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.users }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" v-if="visibleCards.includes('houses')">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32" color="#67C23A"><HomeFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.houses }}</div>
                <div class="stat-label">房源数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" v-if="visibleCards.includes('transactions')">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32" color="#E6A23C"><Sell /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.transactions }}</div>
                <div class="stat-label">交易数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" v-if="visibleCards.includes('commissions')">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32" color="#F56C6C"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ formatCurrency(stats.commissions) }}</div>
                <div class="stat-label">佣金总额</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 趋势图表 - 所有角色可见 -->
    <TrendChart v-if="shouldShowTrends" />

    <!-- 待办事项 - 根据角色显示不同待办 -->
    <TodoList />
  
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  HomeFilled,
  Sell,
  Money
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { reqUserPage } from '@/api/user'
import { getHouseStats, getTransactionStats, getCommissionStats } from '@/api/statistics'
import TrendChart from '@/components/TrendChart.vue'
import TodoList from '@/components/TodoList.vue'

const userStore = useUserStore()

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 角色个性化 - 判断是否显示趋势图表
const shouldShowTrends = computed(() => {
  const roleType = userStore.currentUser?.roleType
  // 所有角色都可以看趋势图表
  return roleType !== undefined
})

// 角色个性化 - 根据角色显示不同的统计卡片
const visibleCards = computed(() => {
  const roleType = userStore.currentUser?.roleType
  
  if (roleType === 1) {
    // 管理员 - 显示全部统计
    return ['users', 'houses', 'transactions', 'commissions']
  } else if (roleType === 2 || roleType === 3) {
    // 销售顾问/销售经理 - 显示房源、交易、佣金
    return ['houses', 'transactions', 'commissions']
  } else if (roleType === 4) {
    // 财务 - 显示交易、佣金
    return ['transactions', 'commissions']
  }
  
  // 默认显示全部
  return ['users', 'houses', 'transactions', 'commissions']
})

const loading = ref(false)

const stats = ref({
  users: 0,
  houses: 0,
  transactions: 0,
  commissions: 0
})

const formatCurrency = (val: any) => {
  const num = Number(val || 0)
  const text = Number.isFinite(num)
    ? num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    : '0.00'
  return `¥${text}`
}

const fetchDashboardStats = async () => {
  loading.value = true
  try {
    const [userRes, houseRes, txRes, commissionRes] = await Promise.allSettled([
      reqUserPage({ pageNum: 1, pageSize: 1 }),
      getHouseStats(),
      getTransactionStats(),
      getCommissionStats({ days: 30 })
    ])

    if (userRes.status === 'fulfilled' && userRes.value?.code === 200) {
      stats.value.users = Number(userRes.value?.data?.total || 0)
    }
    if (houseRes.status === 'fulfilled' && houseRes.value?.code === 200) {
      stats.value.houses = Number(houseRes.value?.data?.totalHouseCount || 0)
    }
    if (txRes.status === 'fulfilled' && txRes.value?.code === 200) {
      stats.value.transactions = Number(txRes.value?.data?.totalTransactionCount || 0)
    }
    if (commissionRes.status === 'fulfilled' && commissionRes.value?.code === 200) {
      stats.value.commissions = Number(commissionRes.value?.data?.totalCommissionAmount || 0)
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}


// 组件挂载
onMounted(() => {
  fetchDashboardStats()
})
</script>

<style scoped lang="scss">
.dashboard {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 112px);
}

.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 30px;

  h1 {
    margin: 0 0 10px 0;
    font-size: 28px;
    font-weight: 600;
  }

  p {
    margin: 0;
    font-size: 16px;
    opacity: 0.9;
  }
}

.stats-cards {
  margin-bottom: 30px;

  .stat-card {
    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 15px;

    .stat-icon {
      flex-shrink: 0;
    }

    .stat-info {
      flex: 1;

      .stat-number {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

.quick-actions {
  margin-bottom: 30px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .actions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 15px;

    .action-btn {
      height: 80px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;

      .el-icon {
        font-size: 24px;
      }
    }
  }
}

.recent-activities {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  :deep(.el-timeline) {
    padding: 20px 0;

    .el-timeline-item {
      .el-timeline-item__content {
        color: #666;
      }

      .el-timeline-item__timestamp {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }

  .welcome-section {
    padding: 20px;

    h1 {
      font-size: 24px;
    }
  }

  .stats-cards .el-col {
    margin-bottom: 15px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);

    .action-btn {
      height: 70px;

      span {
        font-size: 12px;
      }
    }
  }
}
</style>
