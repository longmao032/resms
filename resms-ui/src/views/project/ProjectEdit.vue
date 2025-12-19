<template>
  <div class="project-edit-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">编辑项目</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-divider content-position="left">基础信息</el-divider>
        
        <el-form-item label="项目编号">
          <el-input v-model="formData.projectNo" disabled />
        </el-form-item>

        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="formData.projectName"
            placeholder="请输入项目名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="开发商" prop="developer">
          <el-input
            v-model="formData.developer"
            placeholder="请输入开发商名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="物业公司" prop="propertyCompany">
          <el-input
            v-model="formData.propertyCompany"
            placeholder="请输入物业公司名称"
            clearable
          />
        </el-form-item>

        <el-divider content-position="left">位置信息</el-divider>

        <el-form-item label="省份" prop="province">
          <el-input
            v-model="formData.province"
            placeholder="请输入省份"
            clearable
          />
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-input
            v-model="formData.city"
            placeholder="请输入城市"
            clearable
          />
        </el-form-item>

        <el-form-item label="区县" prop="district">
          <el-input
            v-model="formData.district"
            placeholder="请输入区县"
            clearable
          />
        </el-form-item>

        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="formData.address"
            placeholder="请输入详细地址"
            clearable
          />
        </el-form-item>

        <el-divider content-position="left">项目参数</el-divider>

        <el-form-item label="总建筑面积" prop="totalArea">
          <el-input
            v-model.number="formData.totalArea"
            placeholder="请输入总建筑面积（㎡）"
            clearable
          >
            <template #append>㎡</template>
          </el-input>
        </el-form-item>

        <el-form-item label="容积率" prop="plotRatio">
          <el-input
            v-model.number="formData.plotRatio"
            placeholder="请输入容积率"
            clearable
          />
        </el-form-item>

        <el-form-item label="绿化率" prop="greeningRate">
          <el-input
            v-model.number="formData.greeningRate"
            placeholder="请输入绿化率"
            clearable
          >
            <template #append>%</template>
          </el-input>
        </el-form-item>

        <el-form-item label="车位比" prop="parkingRatio">
          <el-input
            v-model="formData.parkingRatio"
            placeholder="请输入车位比（如 1:1.2）"
            clearable
          />
        </el-form-item>

        <el-form-item label="总户数" prop="totalHouseholds">
          <el-input
            v-model="formData.totalHouseholds"
            placeholder="请输入总户数"
            clearable
          />
        </el-form-item>

        <el-form-item label="物业类型" prop="propertyType">
          <el-select v-model="formData.propertyType" placeholder="请选择物业类型" clearable>
            <el-option label="住宅" value="住宅" />
            <el-option label="公寓" value="公寓" />
            <el-option label="别墅" value="别墅" />
            <el-option label="商住两用" value="商住两用" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">价格信息</el-divider>

        <el-form-item label="平均价格" prop="priceAvg">
          <el-input
            v-model.number="formData.priceAvg"
            placeholder="请输入平均价格"
            clearable
          >
            <template #append>元/㎡</template>
          </el-input>
        </el-form-item>

        <el-form-item label="物业费" prop="propertyFee">
          <el-input
            v-model.number="formData.propertyFee"
            placeholder="请输入物业费"
            clearable
          >
            <template #append>元/㎡/月</template>
          </el-input>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="在售" :value="1" />
            <el-option label="售罄" :value="2" />
            <el-option label="待售" :value="3" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">时间信息</el-divider>

        <el-form-item label="开盘时间" prop="openingTime">
          <el-date-picker
            v-model="formData.openingTime"
            type="date"
            placeholder="请选择开盘时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="交房时间" prop="completionTime">
          <el-date-picker
            v-model="formData.completionTime"
            type="datetime"
            placeholder="请选择交房时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-divider content-position="left">配套信息</el-divider>

        <el-form-item label="项目封面" prop="coverUrl">
          <el-upload
            class="cover-uploader"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
          >
            <img v-if="coverImageUrl" :src="coverImageUrl" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持jpg/png/gif/webp格式，最大5MB</div>
        </el-form-item>

        <el-form-item label="最近地铁站" prop="metroStation">
          <el-input
            v-model="formData.metroStation"
            placeholder="请输入最近地铁站名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="地铁站距离" prop="metroDistance">
          <el-input
            v-model.number="formData.metroDistance"
            placeholder="请输入地铁站距离"
            clearable
          >
            <template #append>米</template>
          </el-input>
        </el-form-item>

        <el-form-item label="所属学区" prop="schoolDistrict">
          <el-input
            v-model="formData.schoolDistrict"
            placeholder="请输入所属学区"
            clearable
          />
        </el-form-item>

        <el-form-item label="所属商圈" prop="businessDistrict">
          <el-input
            v-model="formData.businessDistrict"
            placeholder="请输入所属商圈"
            clearable
          />
        </el-form-item>

        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            保存
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { getProjectById, updateProject } from '@/api/project'
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const coverImageUrl = ref('')
const coverImageFile = ref<File | null>(null)
const originalCoverUrl = ref('')

const getCoverUrl = (row: any) => {
  const base = getImageUrl(row?.coverUrl)
  if (!base || base.startsWith('data:image')) return base
  const v = row?.updateTime || row?.createTime || Date.now()
  const sep = base.includes('?') ? '&' : '?'
  return `${base}${sep}v=${encodeURIComponent(String(v))}`
}

// 表单数据
const formData = reactive({
  id: null as number | null,
  projectNo: '',
  projectName: '',
  developer: '',
  propertyCompany: '',
  province: '',
  city: '',
  district: '',
  address: '',
  totalArea: null as number | null,
  plotRatio: null as number | null,
  greeningRate: null as number | null,
  parkingRatio: '',
  totalHouseholds: '',
  priceAvg: null as number | null,
  propertyFee: null as number | null,
  propertyType: '',
  openingTime: '',
  completionTime: '',
  metroStation: '',
  metroDistance: null as number | null,
  schoolDistrict: '',
  businessDistrict: '',
  description: '',
  status: 1,
  coverUrl: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  developer: [
    { required: true, message: '请输入开发商名称', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
})

// 获取项目详情
const fetchProjectDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('项目ID不存在')
    router.push('/project/list')
    return
  }

  loading.value = true
  try {
    const res = await getProjectById(Number(id))
    if (res.status) {
      Object.assign(formData, res.data)
      originalCoverUrl.value = res.data.coverUrl || ''
      if (res.data.coverUrl) {
        coverImageUrl.value = getCoverUrl(res.data)
      }
    } else {
      ElMessage.error(res.message || '获取项目详情失败')
      router.push('/project/list')
    }
  } catch (error: any) {
    console.error('获取项目详情失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '获取项目详情失败')
    router.push('/project/list')
  } finally {
    loading.value = false
  }
}

// 处理文件选择
const handleFileChange = (uploadFile: UploadFile) => {
  const file = uploadFile.raw
  if (!file) return

  // 验证文件类型
  const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isImage) {
    ElMessage.error('只能上传jpg/png/gif/webp格式的图片！')
    return
  }

  // 验证文件大小
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return
  }

  // 保存文件
  coverImageFile.value = file

  // 预览图片
  const reader = new FileReader()
  reader.onload = (e) => {
    coverImageUrl.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (err) {
    ElMessage.warning('请填写必填项')
    return
  }

  submitting.value = true
  try {
    // 准备FormData
    const formDataToSend = new FormData()

    // 添加项目信息（作为JSON字符串）
    formDataToSend.append('project', new Blob([JSON.stringify(formData)], { type: 'application/json' }))

    // 添加封面图片（如果有新上传）
    if (coverImageFile.value) {
      formDataToSend.append('coverImage', coverImageFile.value)
    }

    const apiResp: any = await updateProject(formDataToSend)
    if (apiResp.status) {
      ElMessage.success('项目更新成功')
      router.push('/project/list')
    } else {
      ElMessage.error(apiResp.message || '项目更新失败')
    }
  } catch (error: any) {
    console.error('更新项目失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '项目更新失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  fetchProjectDetail()
  coverImageFile.value = null
}

// 取消编辑
const handleCancel = () => {
  router.push('/project/list')
}

onMounted(() => {
  fetchProjectDetail()
})
</script>

<style scoped>
.project-edit-container {
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

.cover-uploader {
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  text-align: center;
  line-height: 200px;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>
