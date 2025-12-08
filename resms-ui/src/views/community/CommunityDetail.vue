<template>
  <div class="community-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">小区详情</span>
          <div class="button-group">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>

      <div v-if="communityData" class="detail-content">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="小区名称">{{ communityData.communityName }}</el-descriptions-item>
          <el-descriptions-item label="开发商">{{ communityData.developer || '-' }}</el-descriptions-item>
          <el-descriptions-item label="建成年代">{{ communityData.buildYear || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总户数">{{ communityData.totalHouseholds || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物业费">
            {{ communityData.propertyFee ? communityData.propertyFee + ' 元/㎡/月' : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 位置信息 -->
        <el-divider content-position="left">位置信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="省份">{{ communityData.province || '-' }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{ communityData.city }}</el-descriptions-item>
          <el-descriptions-item label="区县">{{ communityData.district }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ communityData.address }}</el-descriptions-item>
        </el-descriptions>

        <!-- 配套信息 -->
        <el-divider content-position="left">配套信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="最近地铁站">{{ communityData.metroStation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属学区">{{ communityData.schoolDistrict || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCommunityById } from '@/api/community'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const communityData = ref<any>(null)

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

<style scoped>
.community-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.button-group {
  display: flex;
  gap: 10px;
}

.detail-content {
  padding: 20px 0;
}
</style>
