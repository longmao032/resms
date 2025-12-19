<template>
  <div class="community-add-container">
    <div class="page-header">
      <div class="header-content">
        <h2 class="title">新增小区</h2>
        <p class="subtitle">录入新的小区或楼盘信息，请确保信息准确完整</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          <el-icon class="el-icon--left"><Check /></el-icon>
          保存提交
        </el-button>
      </div>
    </div>

    <el-card shadow="hover" class="form-card">
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
              <el-date-picker
                v-model="formData.buildYear"
                type="year"
                placeholder="请选择建成年代"
                format="YYYY"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="小区状态" prop="status">
                <el-radio-group v-model="formData.status">
                  <el-radio :label="0">正常</el-radio>
                  <el-radio :label="1">待审核</el-radio>
                </el-radio-group>
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
          <el-col :span="12">
            <el-form-item label="最近地铁站" prop="metroStation">
              <el-input
                v-model="formData.metroStation"
                placeholder="请输入最近地铁站"
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { useRouter } from 'vue-router'
import { addCommunity, uploadCommunityCover } from '@/api/community'
import { Plus, Check, Edit, Picture, House, Location, OfficeBuilding, MapLocation, DataLine, Van, School } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const selectedCoverFile = ref<File | null>(null)
const coverUrlPreview = ref<string>('')

// 表单数据
const formData = reactive({
  communityName: '',
  coverUrl: '',
  developer: '',
  buildYear: '',
  province: '',
  city: '',
  district: '',
  address: '',
  totalHouseholds: undefined as number | undefined,
  propertyFee: undefined as number | undefined,
  metroStation: '',
  schoolDistrict: '',
  status: 1 // 默认待审核
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
    let finalCoverUrl = ''

    // 1. 如果有选择图片，先上传图片
    if (selectedCoverFile.value) {
      try {
        const uploadResp = await uploadCommunityCover(selectedCoverFile.value, formData.communityName)
        if (uploadResp.code === 200 && uploadResp.data) {
          finalCoverUrl = uploadResp.data
        } else {
          ElMessage.warning('封面上传失败，将使用默认封面')
        }
      } catch (err) {
        console.error('封面上传异常', err)
        ElMessage.warning('封面上传异常')
      }
    }

    // 2. 提交小区信息
    // 将年份字符串转换为整数
    const submitData = {
      ...formData,
      coverUrl: finalCoverUrl,
      buildYear: formData.buildYear ? parseInt(formData.buildYear) : undefined
    }

    const resAny: any = await addCommunity(submitData)
    const apiResp = resAny.data ?? resAny
    if (apiResp.code === 200 || apiResp.status) {
      ElMessage.success('小区添加成功')
      router.push('/community/list')
    } else {
      ElMessage.error(apiResp.message || '小区添加失败')
    }
  } catch (error) {
    console.error('添加小区失败:', error)
    ElMessage.error('小区添加失败')
  } finally {
    submitting.value = false
  }
}

// 取消
const handleCancel = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.community-add-container {
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
