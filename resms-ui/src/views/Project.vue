<template>
  <div class="home-view">
    <Header :current-city="currentCity" @city-change="handleCityChange" />
    <el-main>
      <SearchArea />
      
      <!-- 使用新的插槽式FilterArea组件 -->
      <FilterArea :has-selected-filters="hasSelectedFilters">
        <!-- 筛选条件项 -->
        <template #filter-items>
          <div class="filter-section">
            <div class="filter-title">区域</div>
            <div class="filter-options">
              <el-radio-group v-model="filters.region">
                <el-radio-button label="不限" />
                <el-radio-button 
                  v-for="district in houseStore.distinctDistricts" 
                  :key="district" 
                  :label="district" 
                />
              </el-radio-group>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">面积</div>
            <div class="filter-options">
              <el-radio-group v-model="filters.area">
                <el-radio-button label="不限" />
                <el-radio-button label="50㎡以下" />
                <el-radio-button label="50-100㎡" />
                <el-radio-button label="100-150㎡" />
                <el-radio-button label="150-200㎡" />
                <el-radio-button label="200㎡以上" />
              </el-radio-group>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">户型</div>
            <div class="filter-options">
              <el-radio-group v-model="filters.houseType">
                <el-radio-button label="不限" />
                <el-radio-button label="一室" />
                <el-radio-button label="两室" />
                <el-radio-button label="三室" />
                <el-radio-button label="四室" />
                <el-radio-button label="五室及以上" />
              </el-radio-group>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-title">单价</div>
            <div class="filter-options">
              <el-radio-group v-model="filters.priceRange">
                <el-radio-button label="不限" />
                <el-radio-button label="5千以下" />
                <el-radio-button label="5千-1万" />
                <el-radio-button label="1-1.5万" />
                <el-radio-button label="1.5-2万" />
                <el-radio-button label="2万以上" />
              </el-radio-group>
            </div>
          </div>
        </template>
        
        <!-- 已选条件 -->
        <template #selected-filters>
          <span class="filters-label">已选条件：</span>
          <div class="filters-tags">
            <el-tag v-if="filters.region !== '不限'" closable @close="filters.region = '不限'" type="primary" size="large">
              区域：{{ filters.region }}
            </el-tag>
            <el-tag v-if="filters.houseType !== '不限'" closable @close="filters.houseType = '不限'" type="primary" size="large">
              户型：{{ filters.houseType }}
            </el-tag>
            <el-tag v-if="filters.priceRange !== '不限'" closable @close="filters.priceRange = '不限'" type="primary" size="large">
              单价：{{ filters.priceRange }}
            </el-tag>
            <el-tag v-if="filters.area !== '不限'" closable @close="filters.area = '不限'" type="primary" size="large">
              面积：{{ filters.area }}
            </el-tag>
            <el-button type="link" @click="clearAllFilters" class="clear-all-btn">
              清空所有条件
            </el-button>
          </div>
        </template>
      </FilterArea>
      
      <div v-if="houseStore.loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="houseStore.error" class="error-container">
        <el-alert
          :title="houseStore.error"
          type="error"
          show-icon
          closable
        />
      </div>
      
      <!-- 使用新的插槽式HouseList组件 -->
      <HouseList 
        v-else
        :house-list="transformedHouseList" 
        :current-page="houseStore.currentPage" 
        :page-size="houseStore.pagination.size"
        :total="houseStore.totalProjects" 
        @page-change="handlePageChange" 
        @size-change="handleSizeChange"
        :has-data="houseStore.projects.length > 0"
      >
        <!-- 房源列表内容 -->
        <template #header>
          <div class="list-header">
            <h2 class="list-title">推荐房源</h2>
            <div class="result-count">
              共找到 {{ houseStore.totalProjects }} 个符合条件的楼盘
            </div>
            <div class="list-actions">
              <el-button text :icon="Sort">排序</el-button>
              <el-button text :icon="Grid">网格</el-button>
            </div>
          </div>
        </template>
        
        <!-- 房源卡片列表 -->
        <template #default="{ houseList }">
          <HouseCard 
            v-for="item in houseList" 
            :key="item.id"
            :house="item"
          />
        </template>
        
        <!-- 空状态 -->
        <template #empty>
          <div class="empty-state">
            <el-empty description="暂无符合条件的房源" />
          </div>
        </template>
      </HouseList>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from "vue"
import Header from '@/components/layout/Header.vue'
import SearchArea from '@/components/house/SearchArea.vue'
import FilterArea from '@/components/house/FilterArea.vue'
import HouseList from '@/components/house/HouseList.vue'
import HouseCard from '@/components/house/HouseCard.vue'
import { useHouseProjectStore } from '@/stores/projectsStores'
import { ElMessage } from 'element-plus'
import { debounce } from 'lodash-es'
import { Sort, Grid } from '@element-plus/icons-vue'

// 使用 Pinia store
const houseStore = useHouseProjectStore()

// 筛选条件
const filters = reactive({
  region: '不限',
  houseType: '不限',
  priceRange: '不限',
  area: '不限'
})

// 使用store中的全局城市状态
const currentCity = computed(() => houseStore.currentCity)

// 处理城市变更
const handleCityChange = async (newCity: string) => {
  // 更新 store 中的城市参数，store会自动更新全局城市状态
  await houseStore.updateQueryParams({ 
    city: newCity,
    pageNum: 1
  })
  
  // 重置筛选条件，但保留城市
  Object.assign(filters, {
    region: '不限',
    houseType: '不限',
    priceRange: '不限', 
    area: '不限'
  })
}

// 将后端数据转换为前端展示格式
const transformedHouseList = computed(() => {
  return houseStore.projects.map(project => ({
    id: project.id,
    name: project.projectName,
    status: '销售中',
    statusType: 'success' as const,
    address: project.address,
    img: project.coverImg,
    roomType: project.layoutConcat || '户型待定',
    openingTime: project.openingTime || '待定',
    features: [
      project.businessDistrict,
      project.description?.replace(/，/g, '、') || '配套完善',
      '交通便利'
    ].filter(Boolean).slice(0, 3),
    price: project.priceAvg ? project.priceAvg.toLocaleString() : '暂无报价'
  }))
})

// 监听筛选条件变化 - 优化版本
const debouncedUpdateFilters = debounce(async () => {
  console.log('执行筛选更新:', filters)
  try {
    await houseStore.updateFilterParams(filters)
    console.log('筛选更新完成')
  } catch (error) {
    console.error('筛选更新失败:', error)
  }
}, 300)
// 监听所有筛选条件
watch(
  () => filters,
  (newFilters) => {
    console.log('筛选条件变化:', newFilters)
    debouncedUpdateFilters()
  },
  { deep: true, immediate: true }
)

// 计算是否有选中的筛选条件
const hasSelectedFilters = computed(() => {
  return filters.region !== '不限' || 
         filters.houseType !== '不限' || 
         filters.priceRange !== '不限' || 
         filters.area !== '不限'
})

// 清空所有筛选条件
const clearAllFilters = () => {
  Object.assign(filters, {
    region: '不限',
    houseType: '不限',
    priceRange: '不限',
    area: '不限'
  })
}

// 分页处理
const handleSizeChange = async (val: number) => {
  await houseStore.updateQueryParams({ 
    pageSize: val,
    pageNum: 1
  })
}

const handlePageChange = async (val: number) => {
  await houseStore.goToPage(val)
}

// 组件挂载时获取数据
onMounted(async () => {
  await houseStore.initialize()
})
</script>
<style scoped>

</style>