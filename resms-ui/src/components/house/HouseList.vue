<template>
  <section class="house-list-area">
    <slot name="header">
      <div class="list-header">
        <h2 class="list-title">推荐房源</h2>
        <div class="result-count">
          共找到 {{ props.total }} 个符合条件的楼盘
        </div>
        <div class="list-actions">
          <el-button text :icon="Sort">排序</el-button>
          <el-button text :icon="Grid">网格</el-button>
        </div>
      </div>
    </slot>

    <slot name="empty" v-if="props.total === 0 || !props.hasData">
      <div class="empty-state">
        <el-empty description="暂无符合条件的房源" />
      </div>
    </slot>

    <div v-else class="house-list">
      <slot :houseList="props.houseList">
        <!-- 默认插槽，用于注入房源卡片 -->
      </slot>
    </div>

    <!-- 分页区域 -->
    <div v-if="props.total > 0" class="pagination-container">
      <el-pagination 
        background 
        layout="prev, pager, next" 
        :total="props.total"
        :current-page="props.currentPage"
        :page-size="props.pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { Sort, Grid } from '@element-plus/icons-vue'

interface Props {
  houseList: any[]
  currentPage: number
  pageSize: number
  total: number
  hasData?: boolean
}

// 使用 defineProps 接收属性
const props = withDefaults(defineProps<Props>(), {
  hasData: true
})

const emit = defineEmits<{
  'page-change': [page: number]
  'size-change': [size: number]
}>()

const handleSizeChange = (val: number) => {
  emit('size-change', val)
}

const handleCurrentChange = (val: number) => {
  emit('page-change', val)
}
</script>

<style lang="scss" scoped>
.house-list-area {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  
  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .list-title {
      font-size: 24px;
      font-weight: 600;
      color: #1f2937;
      margin: 0;
    }
    
    .list-actions {
      display: flex;
      gap: 8px;
    }
  }
}

.house-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 32px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32px;
  padding: 20px 0;
  
  :deep(.el-pagination) {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .house-list-area {
    padding: 16px;
  }
  
  .pagination-container {
    padding: 16px 0;
    
    :deep(.el-pagination) {
      .el-pagination__sizes,
      .el-pagination__jump {
        display: none;
      }
    }
  }
}
</style>