<template>
  <section class="search-area">
    <div class="search-container">
      <el-input
        v-model="searchInput"
        placeholder="请输入楼盘名称、地址或关键词"
        class="search-input"
        size="large"
      >
        <template #append>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
        </template>
      </el-input>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { Search } from '@element-plus/icons-vue'

const searchInput = ref('')

const handleSearch = () => {
  console.log('搜索内容：', searchInput.value)
  // 触发搜索事件
  emit('search', searchInput.value)
}

const emit = defineEmits<{
  search: [value: string]
}>()
</script>

<style lang="scss" scoped>
.search-area {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 24px;
  
  .search-container {
    max-width: 800px;
    margin: 0 auto;
  }
  
  .search-input {
    .el-input-group__append {
      background: #1890ff;
      border: none;
      
      .el-button {
        color: white;
        font-weight: 500;
      }
    }
  }
}

@media (max-width: 768px) {
  .search-area {
    padding: 40px 16px;
  }
}
</style>