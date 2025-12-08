// stores/projectStore.ts

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type {
  HouseProject,
  ProjectQueryParams,
} from '@/api/house/type'
import { reqGetDistricts, reqFiltered } from '@/api/house/index'

export const useHouseProjectStore = defineStore('houseProject', () => {
  // State
  const projects = ref<HouseProject[]>([])
  const districts = ref<string[]>([]) // 存储区县列表
  const loading = ref(false)
  const districtsLoading = ref(false) // 区县加载状态
  const error = ref<string | null>(null)
  const districtsError = ref<string | null>(null) // 区县错误信息
  const pagination = ref({
    total: 0,
    size: 4,
    current: 1,
    pages: 0
  })
  
  // 全局城市状态
  const currentCity = ref('南宁')

  // 筛选条件 - 确保有默认城市
  const queryParams = ref<ProjectQueryParams>({
    city: currentCity.value, // 使用全局城市状态
    pageNum: 1,
    pageSize: 10
  })

  // Getters
  const totalProjects = computed(() => pagination.value.total)
  const currentPage = computed(() => pagination.value.current)
  const totalPages = computed(() => pagination.value.pages)
  const hasNextPage = computed(() => pagination.value.current < pagination.value.pages)
  const hasPrevPage = computed(() => pagination.value.current > 1)

  // 按价格排序的项目
  const projectsByPrice = computed(() => {
    return [...projects.value].sort((a, b) => a.priceAvg - b.priceAvg)
  })

  // 按面积排序的项目
  const projectsByArea = computed(() => {
    return [...projects.value].sort((a, b) => a.minArea - b.minArea)
  })

  // 获取区县列表的 Action
  const fetchDistricts = async (city?: string) => {
    districtsLoading.value = true
    districtsError.value = null

    try {
      const targetCity = city || queryParams.value.city || '南宁'
      const response = await reqGetDistricts(targetCity)

      if (response.code === 200) {
        districts.value = response.data
      } else {
        throw new Error(response.message || '获取区县列表失败')
      }
    } catch (err) {
      districtsError.value = err instanceof Error ? err.message : '获取区县列表失败'
      console.error('Failed to fetch districts:', err)
    } finally {
      districtsLoading.value = false
    }
  }

  // Actions - 项目相关
  const fetchProjects = async (params?: ProjectQueryParams) => {
    loading.value = true
    error.value = null

    try {
      if (params) {
        queryParams.value = {
          ...queryParams.value,
          ...params
        }
      }

      // 确保城市参数存在
      if (!queryParams.value.city) {
        queryParams.value.city = '南宁'
      }

      console.log('发送查询参数:', queryParams.value)

      // 使用新的筛选接口
      const response = await reqFiltered(queryParams.value)

      if (response.code === 200) {
        projects.value = response.data.records || []
        pagination.value = {
          total: response.data.total || 0,
          size: response.data.size || 4,
          current: response.data.current || 1,
          pages: response.data.pages || 0
        }
        console.log('成功获取项目数据:', projects.value.length, '条')
      } else {
        throw new Error(response.message || '获取项目列表失败')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取项目列表失败'
      console.error('Failed to fetch projects:', err)
    } finally {
      loading.value = false
    }
  }

  const nextPage = async () => {
    if (hasNextPage.value) {
      await fetchProjects({
        ...queryParams.value,
        pageNum: pagination.value.current + 1
      })
    }
  }

  const prevPage = async () => {
    if (hasPrevPage.value) {
      await fetchProjects({
        ...queryParams.value,
        pageNum: pagination.value.current - 1
      })
    }
  }

  const goToPage = async (page: number) => {
    if (page >= 1 && page <= pagination.value.pages) {
      await fetchProjects({
        ...queryParams.value,
        pageNum: page
      })
    }
  }

  const updateQueryParams = async (params: Partial<ProjectQueryParams>) => {
    // 创建新的查询参数对象，保留当前城市和分页大小
    const newParams = {
      ...queryParams.value,
      pageNum: 1, // 重置到第一页
      city: queryParams.value.city || '南宁', // 始终保留城市
      ...params // 直接扩展参数，包括清除筛选条件（设置为undefined）
    }

    // 如果城市发生变化，先更新全局城市状态
    if (params.city && params.city !== currentCity.value) {
      currentCity.value = params.city
    }

    console.log('最终查询参数:', newParams)
    await fetchProjects(newParams)

    // 如果城市发生变化，重新获取区县列表
    if (params.city && params.city !== queryParams.value.city) {
      await fetchDistricts(params.city)
    }
  }

  const resetQueryParams = async () => {
    // 使用全局城市状态
    queryParams.value = {
      city: currentCity.value || '南宁', // 使用当前城市或默认值
      pageNum: 1,
      pageSize: 10
    }

    await fetchProjects()
    await fetchDistricts(currentCity.value) // 使用当前城市获取区县
  }

  // 根据 ID 获取单个项目
  const getProjectById = (id: number) => {
    return projects.value.find(project => project.id === id)
  }

  // 初始化时获取区县列表
  const initialize = async () => {
    // 确保城市参数存在并与全局城市状态同步
    if (!currentCity.value) {
      currentCity.value = '南宁'
    }
    queryParams.value.city = currentCity.value

    await Promise.all([
      fetchProjects(),
      fetchDistricts(currentCity.value)
    ])
  }

  const updateFilterParams = async (filters: {
    region: string;
    houseType: string;
    priceRange: string;
    area: string;
  }) => {
    console.log('开始处理筛选条件:', filters)

    const filterParams: Partial<ProjectQueryParams> = {}  // 改名为 filterParams 避免冲突

    // 始终设置城市参数，使用当前选择的城市
    filterParams.city = queryParams.value.city || '南宁'
    console.log('设置城市:', filterParams.city)

    // 处理区域
    if (filters.region !== '不限') {
      filterParams.district = filters.region
      console.log('设置区域:', filters.region)
    } else {
      filterParams.district = undefined
      console.log('清除区域筛选')
    }

    // 处理价格
    if (filters.priceRange !== '不限') {
      const priceMap = {
        '5千以下': { minPrice: 0, maxPrice: 5000 },
        '5千-1万': { minPrice: 5000, maxPrice: 10000 },
        '1-1.5万': { minPrice: 10000, maxPrice: 15000 },
        '1.5-2万': { minPrice: 15000, maxPrice: 20000 },
        '2万以上': { minPrice: 20000, maxPrice: undefined }
      }
      const priceConfig = priceMap[filters.priceRange as keyof typeof priceMap]
      if (priceConfig) {
        filterParams.minPrice = priceConfig.minPrice
        filterParams.maxPrice = priceConfig.maxPrice
        console.log('设置价格范围:', priceConfig)
      }
    } else {
      filterParams.minPrice = undefined
      filterParams.maxPrice = undefined
      console.log('清除价格筛选')
    }

    // 处理面积
    if (filters.area !== '不限') {
      const areaMap = {
        '50㎡以下': { minArea: 0, maxArea: 50 },
        '50-100㎡': { minArea: 50, maxArea: 100 },
        '100-150㎡': { minArea: 100, maxArea: 150 },
        '150-200㎡': { minArea: 150, maxArea: 200 },
        '200㎡以上': { minArea: 200, maxArea: undefined }
      }
      const areaConfig = areaMap[filters.area as keyof typeof areaMap]
      if (areaConfig) {
        filterParams.minArea = areaConfig.minArea
        filterParams.maxArea = areaConfig.maxArea
        console.log('设置面积范围:', areaConfig)
      }
    } else {
      filterParams.minArea = undefined
      filterParams.maxArea = undefined
      console.log('清除面积筛选')
    }

    // 处理户型
    if (filters.houseType !== '不限') {
      const layoutMap = {
        '一室': '1室',
        '两室': '2室',
        '三室': '3室',
        '四室': '4室',
        '五室及以上': '5室'
      }
      const layout = layoutMap[filters.houseType as keyof typeof layoutMap]
      if (layout) {
        filterParams.layout = layout
        console.log('设置户型:', layout)
      }
    } else {
      filterParams.layout = undefined
      console.log('清除户型筛选')
    }

    console.log('最终查询参数:', filterParams)
    await updateQueryParams(filterParams)  // 传递 filterParams
  }

  return {
    // State
    projects,
    districts, // 新增区县列表
    loading,
    districtsLoading, // 新增区县加载状态
    error,
    districtsError, // 新增区县错误信息
    pagination,
    currentCity, // 新增全局城市状态
    queryParams,

    // Getters
    totalProjects,
    currentPage,
    totalPages,
    hasNextPage,
    hasPrevPage,
    projectsByPrice,
    projectsByArea,
    distinctDistricts: computed(() => districts.value), // 使用后端返回的区县列表

    // Actions
    fetchProjects,
    fetchDistricts, // 新增获取区县方法
    nextPage,
    prevPage,
    goToPage,
    updateQueryParams,
    resetQueryParams,
    getProjectById,
    initialize, // 新增初始化方法
    updateFilterParams
  }
})