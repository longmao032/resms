<template>
  <div class="project-add-container">
    <div class="page-header">
      <div class="header-content">
        <h2 class="title">新增项目</h2>
        <p class="subtitle">录入新的楼盘项目信息，请确保信息准确完整</p>
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
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
              accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
            >
              <div v-if="coverImageUrl" class="cover-preview">
                <el-image :src="coverImageUrl" fit="cover" class="preview-image" />
                <div class="upload-mask">
                  <el-icon><Edit /></el-icon>
                  <span>更换封面</span>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传封面图</div>
                <div class="upload-tip">支持 JPG/PNG 格式，最大 5MB</div>
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
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="formData.projectName" placeholder="请输入项目名称" clearable>
                 <template #prefix><el-icon><OfficeBuilding /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发商" prop="developer">
              <el-input v-model="formData.developer" placeholder="请输入开发商名称" clearable />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="物业公司" prop="propertyCompany">
              <el-input v-model="formData.propertyCompany" placeholder="请输入物业公司名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
               <el-select v-model="formData.status" placeholder="请选择项目状态" style="width: 100%">
                <el-option label="在售" :value="1" />
                <el-option label="售罄" :value="2" />
                <el-option label="待售" :value="3" />
               </el-select>
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
              <el-input v-model="formData.address" placeholder="请输入详细地址" clearable>
                 <template #prefix><el-icon><MapLocation /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 项目参数 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><DataLine /></el-icon></span>
          <span>项目参数</span>
        </div>

        <el-row :gutter="24">
          <el-col :span="8">
             <el-form-item label="总建筑面积" prop="totalArea">
               <el-input v-model.number="formData.totalArea" placeholder="请输入" type="number">
                  <template #append>㎡</template>
               </el-input>
             </el-form-item>
          </el-col>
           <el-col :span="8">
             <el-form-item label="容积率" prop="plotRatio">
               <el-input v-model.number="formData.plotRatio" placeholder="请输入" type="number" />
             </el-form-item>
          </el-col>
           <el-col :span="8">
             <el-form-item label="绿化率" prop="greeningRate">
               <el-input v-model.number="formData.greeningRate" placeholder="请输入" type="number">
                  <template #append>%</template>
               </el-input>
             </el-form-item>
          </el-col>
           <el-col :span="8">
             <el-form-item label="车位比" prop="parkingRatio">
               <el-input v-model="formData.parkingRatio" placeholder="如 1:1.2" />
             </el-form-item>
          </el-col>
           <el-col :span="8">
             <el-form-item label="总户数" prop="totalHouseholds">
               <el-input v-model="formData.totalHouseholds" placeholder="请输入" />
             </el-form-item>
          </el-col>
           <el-col :span="8">
             <el-form-item label="物业类型" prop="propertyType">
              <el-select v-model="formData.propertyType" placeholder="请选择" style="width: 100%">
                <el-option label="住宅" value="住宅" />
                <el-option label="公寓" value="公寓" />
                <el-option label="别墅" value="别墅" />
                <el-option label="商住" value="商住" />
              </el-select>
             </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="平均价格" prop="priceAvg">
               <el-input v-model.number="formData.priceAvg" placeholder="请输入" type="number">
                  <template #append>元/㎡</template>
               </el-input>
             </el-form-item>
          </el-col>
           <el-col :span="12">
             <el-form-item label="物业费" prop="propertyFee">
               <el-input v-model.number="formData.propertyFee" placeholder="请输入" type="number">
                  <template #append>元/㎡/月</template>
               </el-input>
             </el-form-item>
          </el-col>
        </el-row>

        <!-- 时间信息 -->
        <div class="section-title">
          <span class="icon-wrapper"><el-icon><Calendar /></el-icon></span>
          <span>时间信息</span>
        </div>
        <el-row :gutter="24">
            <el-col :span="12">
                <el-form-item label="开盘时间" prop="openingTime">
                  <el-date-picker
                    v-model="formData.openingTime"
                    type="date"
                    placeholder="请选择开盘时间"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="交房时间" prop="completionTime">
                  <el-date-picker
                    v-model="formData.completionTime"
                    type="datetime"
                    placeholder="请选择交房时间"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    style="width: 100%"
                  />
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
                    <el-input v-model="formData.metroStation" placeholder="请输入" clearable />
                </el-form-item>
             </el-col>
             <el-col :span="12">
                <el-form-item label="地铁站距离" prop="metroDistance">
                   <el-input v-model.number="formData.metroDistance" placeholder="请输入距离" type="number">
                        <template #append>米</template>
                   </el-input>
                </el-form-item>
             </el-col>
             <el-col :span="12">
                <el-form-item label="所属学区" prop="schoolDistrict">
                  <el-input v-model="formData.schoolDistrict" placeholder="请输入" clearable>
                     <template #prefix><el-icon><School /></el-icon></template>
                  </el-input>
                </el-form-item>
             </el-col>
             <el-col :span="12">
                <el-form-item label="所属商圈" prop="businessDistrict">
                  <el-input v-model="formData.businessDistrict" placeholder="请输入" clearable />
                </el-form-item>
             </el-col>
             <el-col :span="24">
                 <el-form-item label="项目描述" prop="description">
                  <el-input
                    v-model="formData.description"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入项目描述"
                  />
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
import { Plus, Check, Edit, Picture, House, Location, OfficeBuilding, MapLocation, DataLine, Van, School, Calendar } from '@element-plus/icons-vue'
import { addProject } from '@/api/project'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const coverImageUrl = ref('')
const coverImageFile = ref<File | null>(null)

// 表单数据
const formData = reactive({
  projectName: '',
  developer: '',
  propertyCompany: '',
  province: '',
  city: '',
  district: '',
  address: '',
  totalArea: undefined as number | undefined,
  plotRatio: undefined as number | undefined,
  greeningRate: undefined as number | undefined,
  parkingRatio: '',
  totalHouseholds: '',
  priceAvg: undefined as number | undefined,
  propertyFee: undefined as number | undefined,
  propertyType: '',
  status: 1,
  openingTime: '',
  completionTime: '',
  metroStation: '',
  metroDistance: undefined as number | undefined,
  schoolDistrict: '',
  businessDistrict: '',
  description: ''
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
    // 滚动到第一个错误
    const errorInputs = document.querySelectorAll('.is-error')
    if (errorInputs.length > 0) {
      errorInputs[0].scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
    return
  }

  submitting.value = true
  try {
    // 准备FormData
    const formDataToSend = new FormData()

    // 添加项目信息（作为JSON字符串）
    formDataToSend.append('project', new Blob([JSON.stringify(formData)], { type: 'application/json' }))

    // 添加封面图片（如果有）
    if (coverImageFile.value) {
      formDataToSend.append('coverImage', coverImageFile.value)
    }

    const apiResp: any = await addProject(formDataToSend)
    if (apiResp.status) {
      ElMessage.success('项目添加成功')
      router.push('/project/list')
    } else {
      ElMessage.error(apiResp.message || '项目添加失败')
    }
  } catch (error: any) {
    console.error('添加项目失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '项目添加失败')
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
.project-add-container {
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
