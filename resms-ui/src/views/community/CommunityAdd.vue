<template>
  <div class="community-add-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">新增小区</span>
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
          <el-date-picker
            v-model="formData.buildYear"
            type="year"
            placeholder="请选择建成年代"
            format="YYYY"
            value-format="YYYY"
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

        <el-divider content-position="left">小区参数</el-divider>

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
            提交
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { addCommunity } from '@/api/community'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const formData = reactive({
  communityName: '',
  developer: '',
  buildYear: '',
  province: '',
  city: '',
  district: '',
  address: '',
  totalHouseholds: undefined as number | undefined,
  propertyFee: undefined as number | undefined,
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
  ]
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 将年份字符串转换为整数
        const submitData = {
          ...formData,
          buildYear: formData.buildYear ? parseInt(formData.buildYear) : undefined
        }
        
        const res = await addCommunity(submitData)
        if (res.status) {
          ElMessage.success('小区添加成功')
          router.push('/community/list')
        } else {
          ElMessage.error(res.message || '小区添加失败')
        }
      } catch (error) {
        console.error('添加小区失败:', error)
        ElMessage.error('小区添加失败')
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
  formRef.value?.resetFields()
}

// 取消
const handleCancel = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.community-add-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: bold;
    }
  }

  :deep(.el-divider__text) {
    font-weight: bold;
    color: #409eff;
  }
}
</style>
