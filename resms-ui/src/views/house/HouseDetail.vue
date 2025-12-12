<template>
  <div class="house-detail-container">
    <el-page-header @back="goBack" content="房源详情" />
    
    <el-card v-loading="loading" class="detail-card">
      <!-- 房源基础信息 -->
      <template #header>
        <div class="card-header">
          <span class="house-title">
            <el-tag :type="getHouseTypeTagType(houseDetail.houseType)">
              {{ houseDetail.houseTypeText }}
            </el-tag>
            {{ houseDetail.projectName }} {{ houseDetail.buildingNo }}栋{{ houseDetail.roomNo }}室
          </span>
          <el-tag :type="getStatusTagType(houseDetail.status)">
            {{ houseDetail.statusText }}
          </el-tag>
        </div>
      </template>

      <!-- 图片轮播 -->
      <div class="image-section" v-if="formattedImages.length > 0">
        <el-carousel :interval="4000" type="card" height="400px" indicator-position="outside">
          <el-carousel-item v-for="(image, index) in formattedImages" :key="index">
            <el-image 
              :src="image" 
              :preview-src-list="formattedImages"
              :initial-index="index"
              fit="cover"
              class="carousel-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </el-carousel-item>
        </el-carousel>
        <div class="image-count">共 {{ formattedImages.length }} 张图片</div>
      </div>
      <el-empty v-else description="暂无房源图片" :image-size="200" />

      <!-- 基本信息 -->
      <el-divider content-position="left">
        <el-icon><House /></el-icon>
        <span class="divider-text">基本信息</span>
      </el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="房源编号">{{ houseDetail.houseNo }}</el-descriptions-item>
        <el-descriptions-item label="房源类型">
          <el-tag :type="getHouseTypeTagType(houseDetail.houseType)" size="small">
            {{ houseDetail.houseTypeText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="户型">{{ houseDetail.layout }}</el-descriptions-item>
        <el-descriptions-item label="建筑面积">{{ houseDetail.area }} ㎡</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ houseDetail.floor }}/{{ houseDetail.totalFloor }}层</el-descriptions-item>
        <el-descriptions-item label="朝向">{{ houseDetail.orientation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="装修情况">{{ houseDetail.decoration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="挂牌价格">
          <span class="price-text">¥ {{ formatPrice(houseDetail.price) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="负责销售">{{ houseDetail.salesName || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="房源位置" :span="3">
          <el-icon><Location /></el-icon>
          {{ houseDetail.fullAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="房源描述" :span="3">
          {{ houseDetail.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 新房扩展信息 -->
      <template v-if="houseDetail.houseType === 2 && houseDetail.newHouseInfo">
        <el-divider content-position="left">
          <el-icon><OfficeBuilding /></el-icon>
          <span class="divider-text">新房信息</span>
        </el-divider>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="楼盘名称">{{ houseDetail.newHouseInfo.estateName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开盘时间">{{ houseDetail.newHouseInfo.openingDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交房时间">{{ houseDetail.newHouseInfo.deliveryDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="销售状态">
            <el-tag :type="getSaleStatusTagType(houseDetail.newHouseInfo.saleStatus)" size="small">
              {{ houseDetail.newHouseInfo.saleStatusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ houseDetail.newHouseInfo.remark || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </template>

      <!-- 二手房扩展信息 -->
      <template v-if="houseDetail.houseType === 1 && houseDetail.secondHouseInfo">
        <el-divider content-position="left">
          <el-icon><House /></el-icon>
          <span class="divider-text">二手房信息</span>
        </el-divider>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="小区名称">{{ houseDetail.secondHouseInfo.communityName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房龄">{{ houseDetail.secondHouseInfo.houseAge || '-' }} 年</el-descriptions-item>
          <el-descriptions-item label="产权年限">{{ houseDetail.secondHouseInfo.propertyRight || '-' }} 年</el-descriptions-item>
          <el-descriptions-item label="上次交易时间">{{ houseDetail.secondHouseInfo.lastTradeDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="抵押状态">
            <el-tag :type="houseDetail.secondHouseInfo.mortgageStatus === 1 ? 'warning' : 'success'" size="small">
              {{ houseDetail.secondHouseInfo.mortgageStatusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="房本状态">
            <el-tag :type="houseDetail.secondHouseInfo.certificateStatus === 1 ? 'success' : 'info'" size="small">
              {{ houseDetail.secondHouseInfo.certificateStatusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="3">
            {{ houseDetail.secondHouseInfo.remark || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </template>

      <!-- 项目/小区信息 -->
      <template v-if="houseDetail.projectInfo">
        <el-divider content-position="left">
          <el-icon><School /></el-icon>
          <span class="divider-text">项目/小区信息</span>
        </el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ houseDetail.projectInfo.projectName }}</el-descriptions-item>
          <el-descriptions-item label="项目编号">{{ houseDetail.projectInfo.projectNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开发商">{{ houseDetail.projectInfo.developer || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物业公司">{{ houseDetail.projectInfo.propertyCompany || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目地址" :span="2">
            <el-icon><Location /></el-icon>
            {{ getFullProjectAddress(houseDetail.projectInfo) }}
          </el-descriptions-item>
          <el-descriptions-item label="占地面积">{{ houseDetail.projectInfo.landArea || '-' }} ㎡</el-descriptions-item>
          <el-descriptions-item label="容积率">{{ houseDetail.projectInfo.plotRatio || '-' }}</el-descriptions-item>
          <el-descriptions-item label="绿化率">{{ houseDetail.projectInfo.greeningRate ? houseDetail.projectInfo.greeningRate + '%' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="车位配比">{{ houseDetail.projectInfo.parkingRatio || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总户数">{{ houseDetail.projectInfo.totalHouses || '-' }} 户</el-descriptions-item>
          <el-descriptions-item label="物业费">{{ houseDetail.projectInfo.propertyFee ? '¥' + houseDetail.projectInfo.propertyFee + '/㎡/月' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="建筑类型">{{ houseDetail.projectInfo.buildingType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开盘时间">{{ houseDetail.projectInfo.openingDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交房时间">{{ houseDetail.projectInfo.deliveryDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最近地铁站">
            {{ houseDetail.projectInfo.nearestMetro || '-' }}
            <span v-if="houseDetail.projectInfo.metroDistance">
              （距离约 {{ houseDetail.projectInfo.metroDistance }}米）
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="学区信息" :span="2">{{ houseDetail.projectInfo.schoolDistrict || '-' }}</el-descriptions-item>
          <el-descriptions-item label="商圈信息" :span="2">{{ houseDetail.projectInfo.businessCircle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目描述" :span="2">{{ houseDetail.projectInfo.description || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>

      <!-- 操作记录 -->
      <el-divider content-position="left">
        <el-icon><Clock /></el-icon>
        <span class="divider-text">操作记录</span>
      </el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="创建时间">{{ houseDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ houseDetail.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHouseDetail } from '@/api/house'
import type { HouseDetail, ProjectInfo } from '@/api/house/type'
import { 
  House, 
  Picture, 
  Location, 
  OfficeBuilding, 
  School, 
  Clock 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const houseDetail = ref<HouseDetail>({} as HouseDetail)

// 获取房源详情
const fetchHouseDetail = async () => {
  const id = route.params.id as string
  if (!id) {
    ElMessage.error('房源ID不能为空')
    goBack()
    return
  }

  loading.value = true
  try {
    const res = await getHouseDetail(Number(id))
    houseDetail.value = res.data
  } catch (error) {
    console.error('获取房源详情失败:', error)
    ElMessage.error('获取房源详情失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 格式化价格
const formatPrice = (price: number | undefined | null): string => {
  // 空值检查：如果价格为 undefined、null 或非数字，返回默认值
  if (price === undefined || price === null || isNaN(price)) {
    return '0.00'
  }
  return price.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取房源类型标签类型
const getHouseTypeTagType = (type: number | undefined): string => {
  if (type === undefined || type === null) return 'info'
  const typeMap: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取状态标签类型
const getStatusTagType = (status: number | undefined): string => {
  if (status === undefined || status === null) return 'info'
  const statusMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取销售状态标签类型
const getSaleStatusTagType = (status: number | undefined): string => {
  if (status === undefined) return 'info'
  const statusMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取完整项目地址
const getFullProjectAddress = (project: ProjectInfo): string => {
  const parts = [
    project.province,
    project.city,
    project.district,
    project.address
  ].filter(Boolean)
  return parts.join('') || '-'
}

const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 处理已包含 /uploads 的情况
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`
  // 处理以 / 开头的情况 (如 /project/1.jpg)
  if (url.startsWith('/')) return `http://localhost:8080/uploads${url}`
  // 处理相对路径 (如 temp_...)
  return `http://localhost:8080/uploads/${url}`
}

import { computed } from 'vue'

const formattedImages = computed(() => {
  if (!houseDetail.value.images) return []
  return houseDetail.value.images.map(img => getImageUrl(img))
})

onMounted(() => {
  fetchHouseDetail()
})
</script>

<style scoped lang="scss">
.house-detail-container {
  padding: 20px;

  .detail-card {
    margin-top: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .house-title {
        font-size: 18px;
        font-weight: bold;
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }

    .image-section {
      margin-bottom: 30px;
      position: relative;

      .carousel-image {
        width: 100%;
        height: 100%;
        border-radius: 8px;
        overflow: hidden;

        .image-error {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 100%;
          background-color: #f5f7fa;
          color: #909399;

          i {
            font-size: 48px;
            margin-bottom: 10px;
          }
        }
      }

      .image-count {
        position: absolute;
        bottom: 10px;
        right: 20px;
        background-color: rgba(0, 0, 0, 0.5);
        color: #fff;
        padding: 5px 15px;
        border-radius: 15px;
        font-size: 14px;
      }
    }

    .divider-text {
      margin-left: 8px;
      font-weight: bold;
      font-size: 16px;
    }

    .price-text {
      color: #f56c6c;
      font-size: 20px;
      font-weight: bold;
    }

    :deep(.el-descriptions__label) {
      width: 150px;
      font-weight: 600;
    }
  }
}
</style>
