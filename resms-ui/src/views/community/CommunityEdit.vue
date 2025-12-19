<template>
  <div class="community-edit-container">
    <div class="page-header">
      <div class="header-content">
        <h2 class="title">编辑小区</h2>
        <p class="subtitle">修改小区或楼盘信息，请确保信息准确完整</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          <el-icon class="el-icon--left"><Check /></el-icon>
          保存修改
        </el-button>
      </div>
    </div>

    <el-card shadow="hover" class="form-card" v-loading="loading">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        label-position="top"
        size="large"
      >
        <!-- 封面图上传 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><Picture /></el-icon></span>
          <span>封面图片</span>
        </div>
        <div class="upload-section">
            <el-upload
              class="cover-uploader"
              action="#"
              :show-file-list="false"
              :auto-upload="false"
              :on-change="handleCoverChange"
              accept="image/*"
            >
              <div v-if="coverUrlPreview" class="cover-preview">
                <el-image :src="coverUrlPreview" fit="cover" class="preview-image" />
                <div class="upload-mask">
                  <el-icon><Edit /></el-icon>
                  <span>更换封面</span>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传封面图</div>
                <div class="upload-tip">支持 JPG/PNG 格式，建议尺寸 800x600</div>
              </div>
            </el-upload>
        </div>

        <!-- 基础信息 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><House /></el-icon></span>
          <span>基础信息</span>
        </div>

        <el-row :gutter="24">
          <el-col :span="12">
             <el-form-item label="小区名称" prop="communityName">
              <el-input
                v-model="formData.communityName"
                placeholder="请输入小区名称"
                clearable
              >
                <template #prefix><el-icon><OfficeBuilding /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发商" prop="developer">
              <el-input
                v-model="formData.developer"
                placeholder="请输入开发商名称"
                clearable
              />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="建成年代" prop="buildYear">
              <el-input
                v-model.number="formData.buildYear"
                placeholder="请输入建成年代（如 2010）"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 位置信息 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><Location /></el-icon></span>
          <span>位置信息</span>
        </div>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="请输入省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
             <el-form-item label="区县" prop="district">
              <el-input v-model="formData.district" placeholder="请输入区县" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
             <el-form-item label="详细地址" prop="address">
              <el-input
                v-model="formData.address"
                placeholder="请输入详细地址"
                clearable
              >
                 <template #prefix><el-icon><MapLocation /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 详细参数 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><DataLine /></el-icon></span>
          <span>详细参数</span>
        </div>

        <el-row :gutter="24">
           <el-col :span="12">
            <el-form-item label="总户数" prop="totalHouseholds">
              <el-input-number
                v-model="formData.totalHouseholds"
                :min="0"
                :step="10"
                placeholder="请输入总户数"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物业费" prop="propertyFee">
              <el-input
                v-model.number="formData.propertyFee"
                placeholder="请输入物业费"
                clearable
              >
                <template #append>元/㎡/月</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 配套信息 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><Van /></el-icon></span>
          <span>配套信息</span>
        </div>

        <el-row :gutter="24">
           <el-col :span="12">
            <el-form-item label="最近地铁站" prop="metroStation">
              <el-input
                v-model="formData.metroStation"
                placeholder="请输入最近地铁站名称"
                clearable
              >
                <template #prefix><el-icon><Van /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="所属学区" prop="schoolDistrict">
              <el-input
                v-model="formData.schoolDistrict"
                placeholder="请输入所属学区"
                clearable
              >
                <template #prefix><el-icon><School /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { getCommunityById, updateCommunity, uploadCommunityCover } from '@/api/community'
import { Plus, Check, Edit, Picture, House, Location, OfficeBuilding, MapLocation, DataLine, Van, School } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const selectedCoverFile = ref<File | null>(null)
const coverUrlPreview = ref<string>('')

// 表单数据
const formData = reactive({
  id: null as number | null,
  communityName: '',
  coverUrl: '',
  developer: '',
  buildYear: null as number | null,
  totalHouseholds: null as number | null,
  propertyFee: null as number | null,
  province: '',
  city: '',
  district: '',
  address: '',
  metroStation: '',
  schoolDistrict: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  communityName: [
    { required: true, message: '请输入小区名称', trigger: 'blur' }
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

// 处理封面图片选择
const handleCoverChange = (uploadFile: UploadFile) => {
  if (uploadFile.raw) {
    const isImage = uploadFile.raw.type.startsWith('image/')
    const isLt5M = uploadFile.raw.size / 1024 / 1024 < 5

    if (!isImage) {
      ElMessage.error('请上传图片文件!')
      return false
    }
    if (!isLt5M) {
      ElMessage.error('图片大小不能超过 5MB!')
      return false
    }

    selectedCoverFile.value = uploadFile.raw
    coverUrlPreview.value = URL.createObjectURL(uploadFile.raw)
  }
}

// 获取小区详情
const fetchCommunityDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('小区ID不存在')
    router.push('/community/list')
    return
  }

  loading.value = true
  try {
    const res = await getCommunityById(Number(id)) as any
    if (res.status || res.code === 200) {
      Object.assign(formData, res.data)
      if (formData.coverUrl) {
          coverUrlPreview.value = '/uploads' + formData.coverUrl
      }
    } else {
      ElMessage.error(res.message || '获取小区详情失败')
      router.push('/community/list')
    }
  } catch (error: any) {
    console.error('获取小区详情失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '获取小区详情失败')
    router.push('/community/list')
  } finally {
    loading.value = false
  }
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
    let finalCoverUrl = formData.coverUrl

    // 1. 如果有选择新图片，先上传图片
    if (selectedCoverFile.value) {
      try {
        const uploadResp: any = await uploadCommunityCover(selectedCoverFile.value, formData.communityName)
        if (uploadResp.code === 200 && uploadResp.data) {
          finalCoverUrl = uploadResp.data
        } else {
          ElMessage.warning('封面上传失败，将使用原封面')
        }
      } catch (err) {
        console.error('封面上传异常', err)
        ElMessage.warning('封面上传异常')
      }
    }

    const submitData = {
      ...formData,
      coverUrl: finalCoverUrl
    }

    const resAny: any = await updateCommunity(submitData)
    const apiResp = resAny.data ?? resAny
    if (apiResp.status || apiResp.code === 200) {
      ElMessage.success('小区更新成功')
      router.push('/community/list')
    } else {
      ElMessage.error(apiResp.message || '小区更新失败')
    }
  } catch (error: any) {
    console.error('更新小区失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '小区更新失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  fetchCommunityDetail()
}

// 取消编辑
const handleCancel = () => {
  router.push('/community/list')
}

onMounted(() => {
  fetchCommunityDetail()
})
</script>

<style scoped lang="scss">
.community-edit-container {
  padding: 24px;
  background-color: #f6f8fa;
  min-height: calc(100vh - 84px);

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-content {
      .title {
        font-size: 24px;
        font-weight: 600;
        color: #1a1a1a;
        margin: 0 0 8px 0;
      }
      .subtitle {
        font-size: 14px;
        color: #606266;
        margin: 0;
      }
    }
  }

  .form-card {
    border-radius: 8px;
    border: none;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    :deep(.el-card__body) {
      padding: 32px 48px;
    }
  }

  .section-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 24px 0 16px 0;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;

    &:first-child {
      margin-top: 0;
    }

    .icon-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 28px;
      height: 28px;
      background-color: #ecf5ff;
      border-radius: 6px;
      margin-right: 12px;
      color: #409eff;
    }
  }

  .upload-section {
    margin-bottom: 30px;
    display: flex;
    justify-content: flex-start;
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #dcdfe6;
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
      width: 320px;
      height: 180px;
      background-color: #fafafa;

      &:hover {
        border-color: #409eff;
        .upload-icon {
          color: #409eff;
        }
      }
    }
  }

  .cover-preview {
    width: 100%;
    height: 100%;
    position: relative;
    
    .preview-image {
        width: 100%;
        height: 100%;
        display: block;
    }

    .upload-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #fff;
        opacity: 0;
        transition: opacity 0.3s;
        
        .el-icon {
            font-size: 24px;
            margin-bottom: 8px;
        }
    }

    &:hover .upload-mask {
        opacity: 1;
    }
  }

  .upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    
    .upload-icon {
      font-size: 32px;
      color: #8c939d;
      margin-bottom: 12px;
      transition: color 0.3s;
    }
    
    .upload-text {
      font-size: 14px;
      color: #606266;
      margin-bottom: 8px;
    }
    
    .upload-tip {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
