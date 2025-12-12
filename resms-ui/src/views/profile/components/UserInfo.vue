<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="100px"
    class="user-info-form"
  >
    <el-form-item label="用户头像" prop="avatar">
      <el-upload
        class="avatar-uploader"
        action="http://localhost:8080/common/upload"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        name="file"
      >
        <img v-if="form.avatar" :src="getImageUrl(form.avatar)" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <div class="upload-tip">支持 jpg、png 格式，大小不超过 2MB</div>
    </el-form-item>

    <el-form-item label="用户名" prop="username">
      <el-input v-model="form.username" disabled />
    </el-form-item>

    <el-form-item label="真实姓名" prop="realName">
      <el-input v-model="form.realName" maxlength="20" />
    </el-form-item>

    <el-form-item label="手机号码" prop="phone">
      <el-input v-model="form.phone" maxlength="11" />
    </el-form-item>

    <el-form-item label="电子邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" />
    </el-form-item>

    <el-form-item label="性别" prop="sex">
      <el-radio-group v-model="form.sex">
        <el-radio :label="1">男</el-radio>
        <el-radio :label="2">女</el-radio>
        <el-radio :label="0">未知</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="个人简介" prop="remark">
      <el-input
        v-model="form.remark"
        type="textarea"
        :rows="3"
        maxlength="200"
        show-word-limit
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" :loading="loading" @click="handleSubmit">保存修改</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { reqUserUpdate } from '@/api/user'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  id: 0,
  username: '',
  realName: '',
  phone: '',
  email: '',
  sex: 0,
  avatar: '',
  remark: ''
})

const rules = reactive<FormRules>({
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

// 图片 URL 处理
const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`
  if (url.startsWith('/')) return `http://localhost:8080/uploads${url}`
  return `http://localhost:8080/uploads/${url}`
}

onMounted(() => {
  initForm()
})

const initForm = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(form, {
      id: userInfo.id,
      username: userInfo.username,
      realName: userInfo.realName,
      phone: userInfo.phone,
      email: userInfo.email,
      sex: userInfo.sex || 0,
      avatar: userInfo.avatar,
      remark: userInfo.remark
    })
  }
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.status) {
    form.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像必须是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res: any = await reqUserUpdate(form)
        const apiResp = res.data ?? res
        
        if (apiResp.status) {
          ElMessage.success('个人资料更新成功')
          // 更新 store 中的用户信息
          await userStore.getUserInfo()
        } else {
          ElMessage.error(apiResp.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  initForm()
  formRef.value?.clearValidate()
}
</script>

<style scoped lang="scss">
.user-info-form {
  max-width: 600px;
  padding: 20px;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    
    &:hover {
      border-color: var(--el-color-primary);
    }
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.5;
}
</style>
