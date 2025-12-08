<template>
  <div class="community-edit-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">编辑小区</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-form-item label="小区名称" prop="communityName">
          <el-input
            v-model="formData.communityName"
            placeholder="请输入小区名称"
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

        <el-form-item label="建成年代" prop="buildYear">
          <el-input
            v-model.number="formData.buildYear"
            placeholder="请输入建成年代（如 2010）"
            clearable
          />
        </el-form-item>

        <el-form-item label="总户数" prop="totalHouseholds">
          <el-input
            v-model.number="formData.totalHouseholds"
            placeholder="请输入总户数"
            clearable
          />
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

        <el-divider content-position="left">配套信息</el-divider>

        <el-form-item label="最近地铁站" prop="metroStation">
          <el-input
            v-model="formData.metroStation"
            placeholder="请输入最近地铁站名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="所属学区" prop="schoolDistrict">
          <el-input
            v-model="formData.schoolDistrict"
            placeholder="请输入所属学区"
            clearable
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
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { getCommunityById, updateCommunity } from '@/api/community'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

// 表单数据
const formData = reactive({
  id: null as number | null,
  communityName: '',
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
    const res = await getCommunityById(Number(id))
    if (res.status) {
      Object.assign(formData, res.data)
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
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await updateCommunity(formData)
        if (res.status) {
          ElMessage.success('小区更新成功')
          router.push('/community/list')
        } else {
          ElMessage.error(res.message || '小区更新失败')
        }
      } catch (error: any) {
        console.error('更新小区失败:', error)
        ElMessage.error(error.response?.data?.message || error.message || '小区更新失败')
      } finally {
        submitting.value = false
      }
    } else {
      ElMessage.warning('请填写必填项')
    }
  })
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

<style scoped>
.community-edit-container {
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
</style>
