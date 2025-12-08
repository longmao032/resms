<template>
  <section class="filter-area">
    <div class="filter-container">
      <slot name="filter-items">
        <!-- 默认插槽，用于注入筛选条件项 -->
      </slot>
    </div>

    <!-- 已选条件区域 -->
    <section class="selected-filters" v-if="hasSelectedFilters">
      <div class="filters-container">
        <slot name="selected-filters">
          <!-- 默认插槽，用于注入已选条件 -->
        </slot>
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed } from "vue"

const props = defineProps<{
  hasSelectedFilters?: boolean
}>()

const emit = defineEmits<{
  'clear-all': []
}>()

// 默认值处理
const hasSelectedFilters = computed(() => props.hasSelectedFilters || false)
</script>

<style lang="scss" scoped>
.filter-area {
  background: white;
  border-bottom: 1px solid #e8e8e8;

  .filter-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
  }

  .filter-section {
    display: flex;
    align-items: center;
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }

    .filter-title {
      min-width: 80px;
      font-weight: 600;
      color: #333;
      font-size: 14px;
    }

    .filter-options {
      flex: 1;

      .el-radio-group {
        .el-radio-button {
          .el-radio-button__inner {
            border: 1px solid #e8e8e8;
            border-radius: 6px;
            margin-right: 8px;
            padding: 8px 16px;

            &:hover {
              color: #1890ff;
              border-color: #1890ff;
            }
          }

          &.is-active {
            .el-radio-button__inner {
              background: #1890ff;
              border-color: #1890ff;
              color: white;
            }
          }
        }
      }
    }
  }
}

.selected-filters {
  background: #f8f9fa;
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;

  .filters-container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
  }

  .filters-label {
    font-size: 14px;
    color: #666;
    margin-right: 16px;
    white-space: nowrap;
  }

  .filters-tags {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .clear-all-btn {
    margin-left: 8px;
    color: #666;

    &:hover {
      color: #1890ff;
    }
  }
}

@media (max-width: 768px) {
  .filter-container {
    padding: 16px;

    .filter-section {
      flex-direction: column;
      align-items: flex-start;

      .filter-title {
        margin-bottom: 8px;
      }
    }
  }

  .selected-filters {
    padding: 16px;
  }
}
</style>