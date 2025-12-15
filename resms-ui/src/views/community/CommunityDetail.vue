<template>
  <div class="community-detail-container">
    <el-card shadow="never" class="detail-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button @click="handleBack" icon="ArrowLeft" link>返回</el-button>
            <span class="divider">|</span>
            <span class="title">小区详情</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="handleEdit" icon="Edit" v-if="canEdit">编辑小区</el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading" class="detail-content">
        <div v-if="communityData" class="content-wrapper">
          <!-- 顶部区域：左侧封面图，右侧关键信息 -->
          <div class="top-section">
            <div class="cover-image-wrapper">
              <el-image 
                v-if="communityData.coverUrl"
                :src="getImageUrl(communityData.coverUrl)"
                :preview-src-list="[getImageUrl(communityData.coverUrl)]"
                fit="cover"
                class="cover-image"
              />
              <div v-else class="no-cover">
                <el-icon :size="48" color="#dcdfe6"><Picture /></el-icon>
                <span>暂无封面图</span>
              </div>
            </div>
            
            <div class="main-info">
              <h1 class="community-name">{{ communityData.communityName }}</h1>
              <div class="tags">
                <el-tag v-if="communityData.status === 0" type="success">正常</el-tag>
                <el-tag v-else-if="communityData.status === 1" type="warning">待审核</el-tag>
                <el-tag v-else type="danger">已驳回</el-tag>
                <el-tag type="info" effect="plain">{{ communityData.buildYear }}年建</el-tag>
              </div>
              
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">参考均价</span>
                  <span class="value price">暂无数据</span>
                </div>
                <div class="info-item">
                  <span class="label">物业费</span>
                  <span class="value">{{ communityData.propertyFee ? communityData.propertyFee + ' 元/㎡/月' : '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">总户数</span>
                  <span class="value">{{ communityData.totalHouseholds || '-' }} 户</span>
                </div>
              </div>

               <div class="address-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ communityData.city }} - {{ communityData.district }} - {{ communityData.address }}</span>
              </div>
            </div>
          </div>

          <!-- 详细信息区域 -->
          <div class="detail-sections">
            <!-- 基础信息 -->
            <div class="section-item">
              <div class="section-title">
                 <span class="icon-wrapper"><el-icon><House /></el-icon></span>
                 <span>基础信息</span>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="开发商">{{ communityData.developer || '-' }}</el-descriptions-item>
                <el-descriptions-item label="建成年代">{{ communityData.buildYear || '-' }}</el-descriptions-item>
                <el-descriptions-item label="总户数">{{ communityData.totalHouseholds || '-' }}</el-descriptions-item>
                <el-descriptions-item label="物业费">{{ communityData.propertyFee ? communityData.propertyFee + ' 元/㎡/月' : '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 配套信息 -->
             <div class="section-item">
              <div class="section-title">
                 <span class="icon-wrapper"><el-icon><Van /></el-icon></span>
                 <span>配套信息</span>
              </div>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="最近地铁站">{{ communityData.metroStation || '-' }}</el-descriptions-item>
                <el-descriptions-item label="所属学区">{{ communityData.schoolDistrict || '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

        </div>
        <el-empty v-else description="暂无数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCommunityById } from '@/api/community'
import { ArrowLeft, Edit, Picture, Location, House, Van } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const communityData = ref<any>(null)
const userStore = useUserStore()
const roleType = computed(() => userStore.userInfo?.roleType)
const isAdmin = computed(() => roleType.value === 1)
const isManager = computed(() => roleType.value === 3)
const canEdit = computed(() => isAdmin.value || isManager.value)

// 使用统一图片工具

// 获取小区详情
const fetchCommunityDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('小区ID不存在')
    return
  }

  loading.value = true
  try {
    const res = await getCommunityById(Number(id))
    if (res.status) {
      communityData.value = res.data
    } else {
      ElMessage.error(res.message || '获取小区详情失败')
    }
  } catch (error: any) {
    console.error('获取小区详情失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '获取小区详情失败')
  } finally {
    loading.value = false
  }
}

// 编辑小区
const handleEdit = () => {
  router.push(`/community/edit/${route.params.id}`)
}

// 返回列表
const handleBack = () => {
  router.push('/community/list')
}

onMounted(() => {
  fetchCommunityDetail()
})
</script>

<style scoped lang="scss">
.community-detail-container {
  padding: 20px;
  background-color: #f6f8fa;
  min-height: calc(100vh - 84px);

  .detail-card {
    border: none;
    border-radius: 8px;
    
    :deep(.el-card__header) {
      padding: 16px 24px;
      border-bottom: 1px solid #f0f0f0;
    }

    :deep(.el-card__body) {
      padding: 24px;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    align-items: center;
    
    .divider {
      margin: 0 12px;
      color: #dcdfe6;
    }

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.top-section {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;

  .cover-image-wrapper {
    width: 400px;
    height: 300px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    background-color: #f5f7fa;
    border: 1px solid #ebeef5;

    .cover-image {
      width: 100%;
      height: 100%;
      display: block;
    }

    .no-cover {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #909399;
      
      span {
        margin-top: 12px;
        font-size: 14px;
      }
    }
  }

  .main-info {
    flex: 1;

    .community-name {
      margin: 0 0 16px 0;
      font-size: 24px;
      color: #1a1a1a;
      line-height: 1.4;
    }

    .tags {
      display: flex;
      gap: 8px;
      margin-bottom: 24px;
    }

    .info-grid {
      display: flex;
      gap: 48px;
      padding: 24px 0;
      border-top: 1px solid #ebeef5;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 24px;

      .info-item {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .label {
          font-size: 14px;
          color: #909399;
        }

        .value {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
          
          &.price {
            color: #f56c6c;
            font-size: 24px;
          }
        }
      }
    }

    .address-row {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #606266;
      font-size: 14px;
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
}

.section-item {
  margin-bottom: 24px;

  .section-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;

    .icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        background-color: #ecf5ff;
        border-radius: 4px;
        margin-right: 8px;
        color: #409eff;
        font-size: 14px;
    }
  }
}
</style>
