<template>
  <div class="dashboard">
    <div class="welcome-section">
      <h1>欢迎使用后台管理系统</h1>
      <p>您好，{{ userStore.currentUser?.realName }}！今天是 {{ currentDate }}</p>
    </div>

    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
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
        <el-col :span="6">
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
        <el-col :span="6">
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
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32" color="#F56C6C"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.commissions }}</div>
                <div class="stat-label">佣金总额</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="quick-actions">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>快捷操作</span>
          </div>
        </template>
        <div class="actions-grid">
          <el-button
            v-for="action in quickActions"
            :key="action.key"
            :type="action.type"
            :icon="action.icon"
            class="action-btn"
            @click="handleQuickAction(action.key)"
          >
            {{ action.label }}
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="recent-activities">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近活动</span>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.timestamp"
          >
            {{ activity.description }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  House,
  HomeFilled,
  Sell,
  Money,
  Coin,
  Wallet,
  Plus,
  Search,
  Setting,
  TrendCharts
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
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

// 统计数据（模拟）
const stats = ref({
  users: 1250,
  houses: 856,
  transactions: 324,
  commissions: '¥2,450,000'
})

// 快捷操作
const quickActions = ref([
  {
    key: 'add-house',
    label: '添加房源',
    type: 'primary' as const,
    icon: markRaw(Plus),
    path: '/house/list'
  },
  {
    key: 'view-customers',
    label: '查看客户',
    type: 'success' as const,
    icon: markRaw(User),
    path: '/customer/list'
  },
  {
    key: 'transaction-audit',
    label: '交易审核',
    type: 'warning' as const,
    icon: markRaw(Coin),
    path: '/transaction/audit'
  },
  {
    key: 'commission-calc',
    label: '佣金核算',
    type: 'danger' as const,
    icon: markRaw(Wallet),
    path: '/commission/calculate'
  },
  {
    key: 'system-settings',
    label: '系统设置',
    type: 'info' as const,
    icon: markRaw(Setting),
    path: '/system/user'
  },
  {
    key: 'view-reports',
    label: '查看报表',
    type: 'primary' as const,
    icon: markRaw(TrendCharts),
    path: '/house/statistics'
  }
])

// 最近活动（模拟）
const recentActivities = ref([
  {
    id: 1,
    description: '新增房源：三室两厅，面积120㎡',
    timestamp: '2024-12-04 10:30'
  },
  {
    id: 2,
    description: '客户张三提交购房意向',
    timestamp: '2024-12-04 09:15'
  },
  {
    id: 3,
    description: '交易订单#20241204001审核通过',
    timestamp: '2024-12-04 08:45'
  },
  {
    id: 4,
    description: '系统用户李四登录',
    timestamp: '2024-12-04 08:30'
  },
  {
    id: 5,
    description: '佣金核算完成，本月总佣金¥185,000',
    timestamp: '2024-12-03 17:20'
  }
])

// 处理快捷操作
const handleQuickAction = (actionKey: string) => {
  const action = quickActions.value.find(a => a.key === actionKey)
  if (action) {
    if (action.path) {
      router.push(action.path)
    } else {
      ElMessage.info(`${action.label}功能开发中`)
    }
  }
}

// 组件挂载
onMounted(() => {
  // 可以在这里加载真实的统计数据
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
