<template>
  <div class="project-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">项目详情</span>
          <div class="button-group">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>

      <div v-if="projectData" class="detail-content">
        <!-- 封面图片 -->
        <div class="cover-section" v-if="projectData.coverUrl">
          <img :src="`http://localhost:8080/uploads${projectData.coverUrl}`" class="cover-image" />
        </div>

        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目编号">{{ projectData.projectNo }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ projectData.projectName }}</el-descriptions-item>
          <el-descriptions-item label="开发商">{{ projectData.developer }}</el-descriptions-item>
          <el-descriptions-item label="物业公司">{{ projectData.propertyCompany || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="projectData.status === 1" type="success">在售</el-tag>
            <el-tag v-else-if="projectData.status === 2" type="info">售罄</el-tag>
            <el-tag v-else type="warning">待售</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 位置信息 -->
        <el-divider content-position="left">位置信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="省份">{{ projectData.province || '-' }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{ projectData.city }}</el-descriptions-item>
          <el-descriptions-item label="区县">{{ projectData.district }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ projectData.address }}</el-descriptions-item>
        </el-descriptions>

        <!-- 项目参数 -->
        <el-divider content-position="left">项目参数</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="总建筑面积">
            {{ projectData.totalArea ? projectData.totalArea + ' ㎡' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="容积率">{{ projectData.plotRatio || '-' }}</el-descriptions-item>
          <el-descriptions-item label="绿化率">
            {{ projectData.greeningRate ? projectData.greeningRate + '%' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="车位比">{{ projectData.parkingRatio || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总户数">{{ projectData.totalHouseholds || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物业类型">{{ projectData.propertyType || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 价格信息 -->
        <el-divider content-position="left">价格信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="平均价格">
            {{ projectData.priceAvg ? projectData.priceAvg + ' 元/㎡' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="物业费">
            {{ projectData.propertyFee ? projectData.propertyFee + ' 元/㎡/月' : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 时间信息 -->
        <el-divider content-position="left">时间信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="开盘时间">{{ projectData.openingTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交房时间">{{ projectData.completionTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 配套信息 -->
        <el-divider content-position="left">配套信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="最近地铁站">{{ projectData.metroStation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地铁站距离">
            {{ projectData.metroDistance ? projectData.metroDistance + ' 米' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="所属学区">{{ projectData.schoolDistrict || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属商圈">{{ projectData.businessDistrict || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 项目描述 -->
        <el-divider content-position="left">项目描述</el-divider>
        <div class="description">
          {{ projectData.description || '暂无描述' }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProjectById } from '@/api/project'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const projectData = ref<any>(null)

// 获取项目详情
const fetchProjectDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('项目ID不存在')
    return
  }

  loading.value = true
  try {
    const res = await getProjectById(Number(id))
    if (res.status) {
      projectData.value = res.data
    } else {
      ElMessage.error(res.message || '获取项目详情失败')
    }
  } catch (error: any) {
    console.error('获取项目详情失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '获取项目详情失败')
  } finally {
    loading.value = false
  }
}

// 编辑项目
const handleEdit = () => {
  router.push(`/project/edit/${route.params.id}`)
}

// 返回列表
const handleBack = () => {
  router.push('/project/list')
}

onMounted(() => {
  fetchProjectDetail()
})
</script>

<style scoped>
.project-detail-container {
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

.cover-section {
  margin-bottom: 20px;
  text-align: center;
}

.cover-image {
  max-width: 600px;
  max-height: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.description {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
