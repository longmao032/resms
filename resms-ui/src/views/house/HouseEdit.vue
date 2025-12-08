<template>
  <div class="house-edit-container">
    <el-page-header @back="goBack" content="编辑房源" />
    
    <el-card v-loading="loading" shadow="never" style="margin-top: 20px;">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        status-icon
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <el-icon><House /></el-icon>
          <span class="divider-text">基本信息</span>
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房源编号">
              <el-input v-model="formData.houseNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房源类型" prop="houseType">
              <el-radio-group v-model="formData.houseType" @change="handleHouseTypeChange">
                <el-radio :label="1">二手房</el-radio>
                <el-radio :label="2">新房</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select
                v-model="formData.projectId"
                placeholder="请选择项目"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="project in projectList"
                  :key="project.id"
                  :label="project.projectName"
                  :value="project.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房源状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
                <el-option label="待审核" :value="0" />
                <el-option label="在售" :value="1" />
                <el-option label="已预订" :value="2" />
                <el-option label="已成交" :value="3" />
                <el-option label="下架" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="户型" prop="layout">
              <el-input v-model="formData.layout" placeholder="如：3室2厅1卫" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="户型名称" prop="unitName">
              <el-input v-model="formData.unitName" placeholder="如：A户型" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="建筑面积(㎡)" prop="area">
              <el-input-number
                v-model="formData.area"
                :min="0"
                :max="99999.99"
                :precision="2"
                :step="0.01"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在楼层" prop="floor">
              <el-input-number
                v-model="formData.floor"
                :min="-5"
                :max="200"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input-number
                v-model="formData.totalFloor"
                :min="1"
                :max="200"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="挂牌价(元)" prop="price">
              <el-input-number
                v-model="formData.price"
                :min="0"
                :max="999999999.99"
                :precision="2"
                :step="10000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="formData.orientation" placeholder="请选择朝向" clearable style="width: 100%">
                <el-option label="东" value="东" />
                <el-option label="南" value="南" />
                <el-option label="西" value="西" />
                <el-option label="北" value="北" />
                <el-option label="东南" value="东南" />
                <el-option label="东北" value="东北" />
                <el-option label="西南" value="西南" />
                <el-option label="西北" value="西北" />
                <el-option label="南北通透" value="南北通透" />
                <el-option label="东西通透" value="东西通透" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="装修情况" prop="decoration">
              <el-select v-model="formData.decoration" placeholder="请选择装修情况" clearable style="width: 100%">
                <el-option label="毛坯" value="毛坯" />
                <el-option label="简装" value="简装" />
                <el-option label="精装修" value="精装修" />
                <el-option label="豪华装修" value="豪华装修" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼栋号" prop="buildingNo">
              <el-input-number
                v-model="formData.buildingNo"
                :min="1"
                :max="999"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房号" prop="roomNo">
              <el-input v-model="formData.roomNo" placeholder="如：101" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责销售" prop="salesId">
              <el-select
                v-model="formData.salesId"
                placeholder="请选择销售"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="sales in salesList"
                  :key="sales.id"
                  :label="sales.realName"
                  :value="sales.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="房源描述" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入房源描述，如：近地铁、学区房、采光好等"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 图片上传 -->
        <el-divider content-position="left">
          <el-icon><Picture /></el-icon>
          <span class="divider-text">房源图片</span>
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="房源图片">
              <el-upload
                v-model:file-list="fileList"
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
                :limit="10"
                accept="image/*"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <el-dialog v-model="dialogVisible">
                <img w-full :src="dialogImageUrl" alt="预览图片" style="width: 100%" />
              </el-dialog>
              <div class="upload-tip">
                <el-icon><InfoFilled /></el-icon>
                <span>最多上传10张图片，第一张将作为封面图。重新上传将替换所有旧图片</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 新房扩展信息 -->
        <template v-if="formData.houseType === 2">
          <el-divider content-position="left">
            <el-icon><OfficeBuilding /></el-icon>
            <span class="divider-text">新房扩展信息</span>
          </el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="预售证号" prop="newHouseInfo.preSaleLicenseNo">
                <el-input v-model="formData.newHouseInfo.preSaleLicenseNo" placeholder="请输入预售许可证号" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="备案价(元)" prop="newHouseInfo.recordPrice">
                <el-input-number
                  v-model="formData.newHouseInfo.recordPrice"
                  :min="0"
                  :precision="2"
                  :step="10000"
                  controls-position="right"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="产权年限" prop="newHouseInfo.propertyRightYears">
                <el-input-number
                  v-model="formData.newHouseInfo.propertyRightYears"
                  :min="0"
                  :max="100"
                  controls-position="right"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="得房率(%)" prop="newHouseInfo.actualAreaRate">
                <el-input-number
                  v-model="formData.newHouseInfo.actualAreaRate"
                  :min="0"
                  :max="100"
                  :precision="2"
                  controls-position="right"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="梯户比" prop="newHouseInfo.elevatorRatio">
                <el-input v-model="formData.newHouseInfo.elevatorRatio" placeholder="如：2梯4户" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="预计交房时间" prop="newHouseInfo.estimatedDeliveryTime">
                <el-date-picker
                  v-model="formData.newHouseInfo.estimatedDeliveryTime"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交付标准" prop="newHouseInfo.deliveryStandard">
                <el-select v-model="formData.newHouseInfo.deliveryStandard" placeholder="请选择" clearable style="width: 100%">
                  <el-option label="毛坯" value="毛坯" />
                  <el-option label="精装修" value="精装修" />
                  <el-option label="豪华装修" value="豪华装修" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 二手房扩展信息 -->
        <template v-if="formData.houseType === 1">
          <el-divider content-position="left">
            <el-icon><House /></el-icon>
            <span class="divider-text">二手房扩展信息</span>
          </el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="建筑年代" prop="secondHouseInfo.buildYear">
                <el-date-picker
                  v-model="formData.secondHouseInfo.buildYear"
                  type="year"
                  placeholder="选择年份"
                  style="width: 100%"
                  value-format="YYYY"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="装修时间" prop="secondHouseInfo.decorationTime">
                <el-date-picker
                  v-model="formData.secondHouseInfo.decorationTime"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="是否满二" prop="secondHouseInfo.isOverTwo">
                <el-radio-group v-model="formData.secondHouseInfo.isOverTwo">
                  <el-radio :label="0">否</el-radio>
                  <el-radio :label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="是否满五" prop="secondHouseInfo.isOverFive">
                <el-radio-group v-model="formData.secondHouseInfo.isOverFive">
                  <el-radio :label="0">否</el-radio>
                  <el-radio :label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="是否唯一" prop="secondHouseInfo.isOnlyHouse">
                <el-radio-group v-model="formData.secondHouseInfo.isOnlyHouse">
                  <el-radio :label="0">否</el-radio>
                  <el-radio :label="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="抵押状态" prop="secondHouseInfo.mortgageStatus">
                <el-radio-group v-model="formData.secondHouseInfo.mortgageStatus">
                  <el-radio :label="0">无抵押</el-radio>
                  <el-radio :label="1">已抵押</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="房屋用途" prop="secondHouseInfo.houseUsage">
                <el-select v-model="formData.secondHouseInfo.houseUsage" placeholder="请选择" clearable style="width: 100%">
                  <el-option label="住宅" value="住宅" />
                  <el-option label="商住" value="商住" />
                  <el-option label="商业" value="商业" />
                  <el-option label="办公" value="办公" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="房源特色" prop="secondHouseInfo.description">
                <el-input
                  v-model="formData.secondHouseInfo.description"
                  type="textarea"
                  :rows="2"
                  placeholder="如：满五唯一、税费低、南北通透等"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 操作按钮 -->
        <el-form-item style="margin-top: 30px;">
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            <span>保存</span>
          </el-button>
          <el-button @click="goBack">
            <el-icon><Close /></el-icon>
            <span>取消</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile, type UploadUserFile } from 'element-plus'
import { 
  House, 
  OfficeBuilding, 
  Picture, 
  Plus, 
  Check, 
  Close, 
  InfoFilled
} from '@element-plus/icons-vue'
import { getHouseById, updateHouse } from '@/api/house'
import { getProjectList } from '@/api/project'
import type { HouseFormData } from '@/api/house/type'

const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)

// 提交加载状态
const submitLoading = ref(false)

// 项目列表
const projectList = ref<any[]>([])

// 销售列表
const salesList = ref<any[]>([])

// 图片上传相关
const fileList = ref<UploadUserFile[]>([])
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

// 表单数据
const formData = reactive<HouseFormData & {
  id: number
  houseNo: string
  status: number
  newHouseInfo: any
  secondHouseInfo: any
}>({
  id: 0,
  houseNo: '',
  houseType: 1,
  layout: '',
  area: null,
  floor: null,
  totalFloor: null,
  price: null,
  status: 0,
  unitName: '',
  orientation: '',
  decoration: '',
  buildingNo: undefined,
  projectId: undefined,
  roomNo: '',
  salesId: undefined,
  description: '',
  newHouseInfo: {
    preSaleLicenseNo: '',
    recordPrice: null,
    propertyRightYears: 70,
    estimatedDeliveryTime: '',
    deliveryStandard: '毛坯',
    elevatorRatio: '',
    actualAreaRate: null
  },
  secondHouseInfo: {
    buildYear: '',
    decorationTime: '',
    isOverTwo: 0,
    isOverFive: 0,
    isOnlyHouse: 0,
    mortgageStatus: 0,
    houseUsage: '住宅',
    description: ''
  }
})

// 表单验证规则
const formRules = reactive<FormRules>({
  houseType: [
    { required: true, message: '请选择房源类型', trigger: 'change' }
  ],
  layout: [
    { required: true, message: '请输入户型', trigger: 'blur' },
    { max: 20, message: '户型长度不能超过20个字符', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入建筑面积', trigger: 'blur' }
  ],
  floor: [
    { required: true, message: '请输入所在楼层', trigger: 'blur' }
  ],
  totalFloor: [
    { required: true, message: '请输入总楼层', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入挂牌价', trigger: 'blur' }
  ],
  projectId: [
    { required: true, message: '请选择所属项目', trigger: 'change' }
  ],
  buildingNo:[
     { required: true, message: '请输入楼栋号', trigger: 'blur' }
  ],
  roomNo: [
    { required: true, message: '请输入房号', trigger: 'blur' }
  ]
})

// 加载房源详情
const loadHouseDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('房源ID不存在')
    router.push('/house/list')
    return
  }

  loading.value = true
  try {
    const res = await getHouseById(Number(id))
    if (res.status && res.data) {
      const house = res.data
      
      // 填充基本信息
      Object.assign(formData, {
        id: house.id,
        houseNo: house.houseNo,
        houseType: house.houseType,
        layout: house.layout,
        area: house.area,
        floor: house.floor,
        totalFloor: house.totalFloor,
        price: house.price,
        status: house.status,
        unitName: house.unitName,
        orientation: house.orientation,
        decoration: house.decoration,
        buildingNo: house.buildingNo,
        projectId: house.projectId,
        roomNo: house.roomNo,
        salesId: house.salesId,
        description: house.description
      })

      // 填充扩展信息
      if (house.houseType === 2 && house.newHouseInfo) {
        formData.newHouseInfo = {
          preSaleLicenseNo: house.newHouseInfo.preSaleLicenseNo || '',
          recordPrice: house.newHouseInfo.recordPrice || null,
          propertyRightYears: house.newHouseInfo.propertyRightYears || 70,
          estimatedDeliveryTime: house.newHouseInfo.estimatedDeliveryTime || '',
          deliveryStandard: house.newHouseInfo.deliveryStandard || '毛坯',
          elevatorRatio: house.newHouseInfo.elevatorRatio || '',
          actualAreaRate: house.newHouseInfo.actualAreaRate || null
        }
      } else if (house.houseType === 1 && house.secondHouseInfo) {
        formData.secondHouseInfo = {
          buildYear: house.secondHouseInfo.buildYear?.toString() || '',
          decorationTime: house.secondHouseInfo.decorationTime || '',
          isOverTwo: house.secondHouseInfo.isOverTwo ?? 0,
          isOverFive: house.secondHouseInfo.isOverFive ?? 0,
          isOnlyHouse: house.secondHouseInfo.isOnlyHouse ?? 0,
          mortgageStatus: house.secondHouseInfo.mortgageStatus ?? 0,
          houseUsage: house.secondHouseInfo.houseUsage || '住宅',
          description: house.secondHouseInfo.description || ''
        }
      }

      // 加载现有图片
      if (house.images && house.images.length > 0) {
        fileList.value = house.images.map((url: string, index: number) => ({
          name: `image-${index + 1}`,
          url: `http://localhost:8080/uploads${url}`,
          uid: Date.now() + index,
          status: 'success'
        }))
      }
    } else {
      ElMessage.error(res.message || '加载房源详情失败')
      router.push('/house/list')
    }
  } catch (error) {
    console.error('加载房源详情失败:', error)
    ElMessage.error('加载房源详情失败')
    router.push('/house/list')
  } finally {
    loading.value = false
  }
}

// 加载项目列表
const loadProjectList = async () => {
  try {
    const res = await getProjectList()
    if (res.status && res.data) {
      projectList.value = res.data
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  }
}

// 加载销售列表
const loadSalesList = async () => {
  try {
    // TODO: 调用销售列表API
    // const res = await getSalesList()
    // salesList.value = res.data
    
    // 模拟数据
    salesList.value = [
      { id: 4, realName: '张三' },
      { id: 5, realName: '李四' },
      { id: 6, realName: '王五' }
    ]
  } catch (error) {
    console.error('加载销售列表失败:', error)
  }
}

// 房源类型变化
const handleHouseTypeChange = () => {
  // 切换房源类型时，重置扩展信息
  if (formData.houseType === 2) {
    formData.newHouseInfo = {
      preSaleLicenseNo: '',
      recordPrice: null,
      propertyRightYears: 70,
      estimatedDeliveryTime: '',
      deliveryStandard: '毛坯',
      elevatorRatio: '',
      actualAreaRate: null
    }
  } else if (formData.houseType === 1) {
    formData.secondHouseInfo = {
      buildYear: '',
      decorationTime: '',
      isOverTwo: 0,
      isOverFive: 0,
      isOnlyHouse: 0,
      mortgageStatus: 0,
      houseUsage: '住宅',
      description: ''
    }
  }
}

// 图片预览
const handlePictureCardPreview = (file: UploadFile) => {
  dialogImageUrl.value = file.url!
  dialogVisible.value = true
}

// 移除图片
const handleRemove = (file: UploadFile) => {
  console.log('移除图片:', file.name)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true

        // 处理图片上传
        let imageUrls: string[] = []
        const hasNewImages = fileList.value.some(file => file.raw)
        
        if (hasNewImages) {
          // 有新上传的图片
          imageUrls = await uploadImages()
        } else if (fileList.value.length > 0) {
          // 保持原有图片
          imageUrls = fileList.value.map(file => {
            // 从完整URL中提取相对路径
            if (file.url && file.url.includes('/uploads')) {
              return file.url.substring(file.url.indexOf('/uploads') + 8)
            }
            return ''
          }).filter(url => url)
        }

        // 构建提交数据
        const submitData = {
          ...formData,
          images: imageUrls.length > 0 ? imageUrls : undefined
        }

        const res = await updateHouse(submitData)
        
        if (res.status) {
          ElMessage.success('更新房源成功')
          // 跳转到列表页
          router.push('/house/list')
        } else {
          ElMessage.error(res.message || '更新房源失败')
        }
      } catch (error: any) {
        console.error('更新房源失败:', error)
        ElMessage.error(error.message || '更新房源失败')
      } finally {
        submitLoading.value = false
      }
    } else {
      ElMessage.warning('请填写必填项')
      return false
    }
  })
}

// 上传图片
const uploadImages = async (): Promise<string[]> => {
  try {
    const formDataUpload = new FormData()
    
    // 只添加新上传的图片文件
    fileList.value.forEach((file) => {
      if (file.raw) {
        formDataUpload.append('files', file.raw)
      }
    })

    // 调用上传接口
    const response = await fetch('http://localhost:8080/house/upload', {
      method: 'POST',
      body: formDataUpload
    })

    const result = await response.json()
    
    if (result.status && result.data) {
      return result.data
    } else {
      throw new Error(result.message || '图片上传失败')
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    throw new Error('图片上传失败')
  }
}

// 返回上一页
const goBack = () => {
  ElMessageBox.confirm(
    '确定要离开吗？未保存的数据将丢失',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    router.back()
  }).catch(() => {
    // 取消操作
  })
}

onMounted(() => {
  loadProjectList()
  loadSalesList()
  loadHouseDetail()
})
</script>

<style scoped lang="scss">
.house-edit-container {
  padding: 20px;

  .divider-text {
    margin-left: 10px;
    font-weight: 600;
    font-size: 16px;
  }

  .upload-tip {
    color: #909399;
    font-size: 12px;
    margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
  }
}
</style>
