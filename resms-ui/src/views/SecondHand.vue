<template>
  <div class="home-view">
    <Header :current-city="filters.region" @city-change="city => filters.region = city" />
    <el-main>
      <SearchArea />
      <FilterArea 
        v-model:region="filters.region" 
        v-model:houseType="filters.houseType"
        v-model:priceRange="filters.priceRange" 
        v-model:area="filters.area"
      />
      
      
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from "vue"
import Header from '@/components/layout/Header.vue'
import SearchArea from '@/components/house/SearchArea.vue'
import FilterArea from '@/components/house/FilterArea.vue'
import HouseList from '@/components/house/HouseList.vue'
import { ElMessage } from 'element-plus'
import { debounce } from 'lodash-es'


// 筛选条件
const filters = reactive({
  region: '不限',
  houseType: '不限',
  priceRange: '不限',
  area: '不限'
})



  
  // 重置筛选条件，但保留城市
  Object.assign(filters, {
    region: '不限',
    houseType: '不限',
    priceRange: '不限', 
    area: '不限'
  })




// 监听筛选条件变化 - 优化版本
const debouncedUpdateFilters = debounce(async () => {
  console.log('执行筛选更新:', filters)
  try {
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



</script>
<style scoped>

</style>