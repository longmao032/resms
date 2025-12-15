<template>
  <div class="house-add-container">
    <el-page-header @back="goBack" content="新增房源" />

    <el-card shadow="never" style="margin-top: 20px;">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px" status-icon>
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <el-icon>
            <House />
          </el-icon>
          <span class="divider-text">基本信息</span>
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房源类型" prop="houseType">
              <el-radio-group v-model="formData.houseType" @change="handleHouseTypeChange">
                <el-radio :label="1">二手房</el-radio>
                <el-radio :label="2">新房</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select v-model="formData.projectId" placeholder="请选择项目" filterable clearable style="width: 100%">
                <el-option v-for="project in projectList" :key="project.id" :label="project.projectName"
                  :value="project.id" />
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
              <el-input-number v-model="formData.area" :min="0" :max="99999.99" :precision="2" :step="0.01"
                controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在楼层" prop="floor">
              <el-input-number v-model="formData.floor" :min="-5" :max="200" controls-position="right"
                style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input-number v-model="formData.totalFloor" :min="1" :max="200" controls-position="right"
                style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="挂牌价(元)" prop="price">
              <el-input-number v-model="formData.price" :min="0" :max="999999999.99" :precision="2" :step="10000"
                controls-position="right" style="width: 100%" />
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
              <el-input-number v-model="formData.buildingNo" :min="1" :max="999" controls-position="right"
                style="width: 100%" />
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
              <el-select v-model="formData.salesId" placeholder="请选择销售" filterable clearable style="width: 100%" :disabled="isSalesConsultant()">
                <el-option v-for="sales in salesList" :key="sales.id" :label="sales.realName" :value="sales.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="房源描述" prop="description">
              <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入房源描述，如：近地铁、学区房、采光好等"
                maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 图片上传 -->
        <el-divider content-position="left">
          <el-icon>
            <Picture />
          </el-icon>
          <span class="divider-text">房源图片</span>
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="房源图片">
              <el-upload v-model:file-list="fileList" action="#" list-type="picture-card" :auto-upload="false"
                :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :limit="10" accept="image/*">
                <el-icon>
                  <Plus />
                </el-icon>
              </el-upload>
              <el-dialog v-model="dialogVisible">
                <img w-full :src="dialogImageUrl" alt="预览图片" style="width: 100%" />
              </el-dialog>
              <div class="upload-tip">
                <el-icon>
                  <InfoFilled />
                </el-icon>
                <span>最多上传10张图片，第一张将作为封面图</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 新房扩展信息 -->
        <template v-if="formData.houseType === 2">
          <el-divider content-position="left">
            <el-icon>
              <OfficeBuilding />
            </el-icon>
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
                <el-input-number v-model="formData.newHouseInfo.recordPrice" :min="0" :precision="2" :step="10000"
                  controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="产权年限" prop="newHouseInfo.propertyRightYears">
                <el-input-number v-model="formData.newHouseInfo.propertyRightYears" :min="0" :max="100"
                  controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="得房率(%)" prop="newHouseInfo.actualAreaRate">
                <el-input-number v-model="formData.newHouseInfo.actualAreaRate" :min="0" :max="100" :precision="2"
                  controls-position="right" style="width: 100%" />
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
                <el-date-picker v-model="formData.newHouseInfo.estimatedDeliveryTime" type="date" placeholder="选择日期"
                  style="width: 100%" value-format="YYYY-MM-DD" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交付标准" prop="newHouseInfo.deliveryStandard">
                <el-select v-model="formData.newHouseInfo.deliveryStandard" placeholder="请选择" clearable
                  style="width: 100%">
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
            <el-icon>
              <House />
            </el-icon>
            <span class="divider-text">二手房扩展信息</span>
          </el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="建筑年代" prop="secondHouseInfo.buildYear">
                <el-date-picker v-model="formData.secondHouseInfo.buildYear" type="year" placeholder="选择年份"
                  style="width: 100%" value-format="YYYY" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="装修时间" prop="secondHouseInfo.decorationTime">
                <el-date-picker v-model="formData.secondHouseInfo.decorationTime" type="date" placeholder="选择日期"
                  style="width: 100%" value-format="YYYY-MM-DD" />
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
                <el-select v-model="formData.secondHouseInfo.houseUsage" placeholder="请选择" clearable
                  style="width: 100%">
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
                <el-input v-model="formData.secondHouseInfo.description" type="textarea" :rows="2"
                  placeholder="如：满五唯一、税费低、南北通透等" maxlength="500" show-word-limit />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 操作按钮 -->
        <el-form-item style="margin-top: 30px;">
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            <el-icon>
              <Check />
            </el-icon>
            <span>提交</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh />
            </el-icon>
            <span>重置</span>
          </el-button>
          <el-button @click="goBack">
            <el-icon>
              <Close />
            </el-icon>
            <span>取消</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile, type UploadUserFile } from 'element-plus'
import {
  House,
  OfficeBuilding,
  Picture,
  Plus,
  Check,
  Refresh,
  Close,
  InfoFilled
} from '@element-plus/icons-vue'
import { addHouse, uploadHouseImages } from '@/api/house'
import { getProjectList } from '@/api/project'
import { getSalesList } from '@/api/user'
import type { HouseFormData } from '@/api/house/type'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const isSalesConsultant = () => (userStore.currentUser?.roleType === 2)
const isFinance = () => (userStore.currentUser?.roleType === 4)

// 表单引用
const formRef = ref<FormInstance>()

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
  newHouseInfo: any
  secondHouseInfo: any
}>({
  houseType: 1,
  layout: '',
  area: null,
  floor: null,
  totalFloor: null,
  price: null,
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
  buildingNo: [
    { required: true, message: '请输入楼栋号', trigger: 'blur' }
  ],
  roomNo: [
    { required: true, message: '请输入房号', trigger: 'blur' }
  ]
})

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
    if (isSalesConsultant()) {
      // 销售顾问新增房源后端会强制绑定自己，这里前端也同步默认值
      formData.salesId = (userStore.currentUser?.userId || userStore.currentUser?.id) as any
      salesList.value = []
      return
    }
    const res = await getSalesList()
    const anyRes: any = res

    // 兼容不同的响应结构
    if (Array.isArray(anyRes)) {
      salesList.value = anyRes
    } else if (anyRes.data) {
      salesList.value = Array.isArray(anyRes.data) ? anyRes.data : []
    } else {
      salesList.value = []
    }
  } catch (error) {
    console.error('加载销售列表失败:', error)
    ElMessage.error('加载销售列表失败')
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

  try {
    await formRef.value.validate()
  } catch (err) {
    ElMessage.warning('请填写必填项')
    return false
  }

  try {
    submitLoading.value = true

    // 处理图片上传
    let imageUrls: string[] = []
    if (fileList.value.length > 0) {
      imageUrls = await uploadImages()
    }

    // 构建提交数据
    const submitData = {
      ...formData,
      images: imageUrls
    }

    const res = await addHouse(submitData)
    const anyRes: any = res
    const apiResp = anyRes.data ?? anyRes

    if (apiResp.status) {
      ElMessage.success('新增房源成功')
      // 跳转到列表页
      router.push('/house/list')
    } else {
      ElMessage.error(apiResp.message || '新增房源失败')
    }
  } catch (error: any) {
    console.error('新增房源失败:', error)
    ElMessage.error(error.message || '新增房源失败')
  } finally {
    submitLoading.value = false
  }
}


// 上传图片
const uploadImages = async (): Promise<string[]> => {
  try {
    const files = fileList.value
      .filter((file) => file.raw)
      .map((file) => file.raw!)

    if (files.length === 0) return []

    // 调用上传接口
    const res = await uploadHouseImages(files)

    // request.ts 已处理响应数据，直接返回
    // 注意：request.ts 拦截器返回的是 data，如果是 ApiResponse，需要根据实际返回结构调整
    // 查看 request.ts: const data = response.data; return data;
    // 所以 res 是 ApiResponse<string[]>

    // 这里需要根据 request.ts 的返回值来处理
    // 通常 request<T>返回 Promise<T>
    const anyRes: any = res
    // 兼容可能的不一致返回结构 (some apis return data directly, some return ApiResponse)
    const imgUrls = anyRes.data || anyRes

    if (Array.isArray(imgUrls)) {
      return imgUrls
    }

    if (anyRes.status && anyRes.data) {
      return anyRes.data
    }

    throw new Error(anyRes.message || '图片上传失败')

  } catch (error: any) {
    console.error('上传图片失败:', error)
    throw new Error(error.message || '图片上传失败')
  }
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  fileList.value = []
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
  if (isFinance()) {
    ElMessage.error('无权限新增房源')
    router.push('/house/list')
    return
  }
  loadProjectList()
  loadSalesList()
})
</script>

<style scoped lang="scss">
.house-add-container {
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
