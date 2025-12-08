<template>
  <div class="city-selector">
    <el-button class="location-btn" @click="visible = true" :loading="loading">
      <el-icon><Location /></el-icon>
      {{ props.currentCity }}
    </el-button>

    <el-dialog 
      v-model="visible" 
      title="选择城市" 
      width="40%" 
      :before-close="handleClose"
      class="city-dialog"
    >
      <div class="city-content">
        <div class="current-city">
          <div class="current-label">当前城市</div>
          <div class="current-name">{{ props.currentCity }}</div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-container">
          <el-alert
            :title="error"
            type="error"
            show-icon
            closable
            @close="error = ''"
          />
          <el-button type="primary" @click="fetchCityData" class="retry-btn">
            重新加载
          </el-button>
        </div>

        <!-- 正常显示 -->
        <div v-else class="city-grid">
          <div class="city-column">
            <div
              v-for="(cities, letter) in leftColumnCities"
              :key="letter"
              class="city-group"
            >
              <div class="group-title">{{ letter }}</div>
              <div class="city-items">
                <span
                  v-for="city in cities"
                  :key="city"
                  class="city-item"
                  :class="{ active: getCityName(city) === props.currentCity }"
                  @click="selectCity(city)"
                >
                  {{ city }}
                </span>
              </div>
            </div>
          </div>

          <div class="city-column">
            <div
              v-for="(cities, letter) in rightColumnCities"
              :key="letter"
              class="city-group"
            >
              <div class="group-title">{{ letter }}</div>
              <div class="city-items">
                <span
                  v-for="city in cities"
                  :key="city"
                  class="city-item"
                  :class="{ active: getCityName(city) === props.currentCity }"
                  @click="selectCity(city)"
                >
                  {{ city }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElDialog, ElButton, ElIcon, ElMessage } from 'element-plus';
import { Location } from '@element-plus/icons-vue';
import { reqGetCity } from '@/api/house/index';
import type { CityData } from '@/api/house/type';

interface Props {
  currentCity: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'city-change': [city: string]
}>()

// 响应式数据
const loading = ref(false);
const error = ref('');
const cityData = ref<CityData>({});
const visible = ref(false);

// 获取城市数据
const fetchCityData = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    const response = await reqGetCity();
    if (response.code === 200) {
      cityData.value = response.data;
    } else {
      throw new Error(response.message || '获取城市数据失败');
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '获取城市数据失败';
    console.error('Failed to fetch city data:', err);
    ElMessage.error('获取城市列表失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 监听对话框显示状态，当打开时获取数据
watch(visible, (newVal) => {
  if (newVal && Object.keys(cityData.value).length === 0) {
    fetchCityData();
  }
});

// 监听当前城市变化
watch(() => props.currentCity, (newCity) => {
  console.log('CitySelector received new city:', newCity);
});

// 获取城市名称（移除括号内容）
const getCityName = (city: string) => {
  return city.replace(/（.*?）/, '').trim()
}

// 按字母排序的城市列表
const sortedLetters = computed(() => {
  return Object.keys(cityData.value).sort();
});

// 左侧列城市数据
const leftColumnCities = computed(() => {
  const leftGroups: Record<string, string[]> = {};
  const midIndex = Math.ceil(sortedLetters.value.length / 2);
  sortedLetters.value.slice(0, midIndex).forEach(letter => {
    leftGroups[letter] = cityData.value[letter];
  });
  return leftGroups;
});

// 右侧列城市数据
const rightColumnCities = computed(() => {
  const rightGroups: Record<string, string[]> = {};
  const midIndex = Math.ceil(sortedLetters.value.length / 2);
  sortedLetters.value.slice(midIndex).forEach(letter => {
    rightGroups[letter] = cityData.value[letter];
  });
  return rightGroups;
});

// 选择城市
const selectCity = (city: string) => {
  const selectedCity = getCityName(city)
  emit('city-change', selectedCity)
  visible.value = false;
};

// 关闭对话框
const handleClose = (done: () => void) => {
  done();
};
</script>

<style lang="scss" scoped>
.city-selector {
  display: inline-block;
}

.location-btn {
  background: linear-gradient(135deg, #52b5ee, #52b5ee);
  border: none;
  color: white;
  font-weight: 500;
  padding: 10px 20px;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(238, 90, 82, 0.3);
  
  &:hover {
    background: linear-gradient(135deg, #52b5ee, #52b5ee);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(238, 90, 82, 0.4);
  }
}

:deep(.city-dialog) {
  .el-dialog {
    border-radius: 16px;
    max-width: 800px;
  }
  
  .el-dialog__header {
    text-align: center;
    padding: 20px 20px 10px;
    margin: 0;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }
  
  .el-dialog__headerbtn {
    top: 20px;
    right: 20px;
  }
}

.city-content {
  padding: 0 20px 20px;
}

.current-city {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
  
  .current-label {
    font-size: 14px;
    color: #666;
    margin-right: 12px;
  }
  
  .current-name {
    font-size: 18px;
    font-weight: 600;
    color: #52b5ee;
  }
}

.loading-container,
.error-container {
  padding: 40px 0;
  text-align: center;
}

.error-container {
  .retry-btn {
    margin-top: 16px;
  }
}

.city-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }
}

.city-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.city-group {
  .group-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
    padding-left: 4px;
    border-left: 3px solid #52b5ee;
  }
  
  .city-items {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}

.city-item {
  padding: 6px 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: #52b5ee;
    color: white;
    border-color: #52b5ee;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(238, 90, 82, 0.3);
  }
  
  &.active {
    background: #52b5ee;
    color: white;
    border-color: #52b5ee;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .city-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  :deep(.city-dialog) {
    .el-dialog {
      width: 95%;
      margin: 20px auto;
    }
  }
}
</style>