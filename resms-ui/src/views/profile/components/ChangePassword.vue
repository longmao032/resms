<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="120px"
    class="password-form"
  >
    <el-alert
      title="修改密码后需要重新登录"
      type="warning"
      show-icon
      :closable="false"
      style="margin-bottom: 20px"
    />

    <el-form-item label="原密码" prop="oldPassword">
      <el-input
        v-model="form.oldPassword"
        type="password"
        show-password
        placeholder="请输入原密码"
      />
    </el-form-item>

    <el-form-item label="新密码" prop="newPassword">
      <el-input
        v-model="form.newPassword"
        type="password"
        show-password
        placeholder="请输入新密码"
      />
    </el-form-item>

    <el-form-item label="确认新密码" prop="confirmNewPassword">
      <el-input
        v-model="form.confirmNewPassword"
        type="password"
        show-password
        placeholder="请再次输入新密码"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认修改</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { reqUserPasswordUpdate } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const userId = userStore.userInfo?.id
        if (!userId) {
          ElMessage.error('获取用户信息失败')
          return
        }

        const res: any = await reqUserPasswordUpdate({
          userId,
          ...form
        })
        const apiResp = res.data ?? res
        
        if (apiResp.status) {
          ElMessage.success('密码修改成功，请重新登录')
          // 退出登录，清理状态
          await userStore.logout()
          // 强制刷新并跳转登录页，确保所有状态清除
          // window.location.href = '/login' // userStore.logout通常会处理或我们需要手动push
          router.push('/login')
        } else {
          ElMessage.error(apiResp.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.password-form {
  max-width: 500px;
  padding: 20px;
}
</style>
