<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-form">
        <div class="logo-section">
          <div class="logo">
            <el-icon size="48" class="logo-icon"><Management /></el-icon>
          </div>
          <h1>房产后台管理系统</h1>
        </div>

        <el-form
          ref="loginFormRef"
          :model="formData"
          :rules="rules"
          label-width="0"
          class="login-form-content"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="formData.username"
              placeholder="请输入用户名或邮箱"
              size="large"
              :prefix-icon="User"
              clearable
              @blur="validateUsername"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
              clearable
            >
              <template #suffix>
                <el-button
                  text
                  @click="showPassword = !showPassword"
                  :icon="showPassword ? Hide : View"
                  class="password-toggle-btn"
                />
              </template>
            </el-input>
          </el-form-item>

          <div class="form-options">
            <el-checkbox
              v-model="formData.rememberMe"
              class="remember-me"
            >
              记住我
            </el-checkbox>
            <el-link
              type="primary"
              @click="handleForgotPassword"
              class="forgot-password"
            >
              忘记密码？
            </el-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span v-if="loading">登录中...</span>
              <span v-else>登录</span>
            </el-button>
          </el-form-item>


          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            <el-alert
              :title="errorMessage"
              type="error"
              :closable="false"
              show-icon
              class="error-alert"
            />
          </div>
        </el-form>

        <!-- 开发环境信息 -->
        <div v-if="isDev" class="dev-info">
          <el-collapse class="dev-collapse">
            <el-collapse-item title="开发环境信息" name="1">
              <div class="dev-details">
                <p><strong>后端接口:</strong> http://localhost:8080/auth/login</p>
                <p><strong>测试账号:</strong> admin / 123456</p>
                <p><strong>安全特性:</strong> 已启用登录失败次数限制</p>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Lock,
  View,
  Hide,
  Management
} from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

// 路由和状态管理
const router = useRouter()
const userStore = useUserStore()

// 表单引用和状态
const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')

// 本地存储键名
const STORAGE_KEYS = {
  REMEMBER_ME: 'login_remember_me',
  USERNAME: 'login_username'
}

// 计算属性
// 已移除账户锁定逻辑，保留简洁的计算属性

const isDev = computed(() => {
  return import.meta.env.DEV
})

// 表单数据
const formData = reactive({
  username: 'zhangsan',
  password: '123456',
  rememberMe: false
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!value) return callback()
        // 支持用户名或邮箱格式
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
        if (!emailRegex.test(value) && !usernameRegex.test(value)) {
          callback(new Error('请输入有效的用户名或邮箱地址'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 从本地存储恢复记住的用户名
const loadRememberedCredentials = () => {
  const remembered = localStorage.getItem(STORAGE_KEYS.REMEMBER_ME) === 'true'
  const savedUsername = localStorage.getItem(STORAGE_KEYS.USERNAME)

  if (remembered && savedUsername) {
    formData.username = savedUsername
    formData.rememberMe = true
  }
}

// 保存记住的凭据
const saveRememberedCredentials = () => {
  if (formData.rememberMe) {
    localStorage.setItem(STORAGE_KEYS.REMEMBER_ME, 'true')
    localStorage.setItem(STORAGE_KEYS.USERNAME, formData.username)
  } else {
    localStorage.removeItem(STORAGE_KEYS.REMEMBER_ME)
    localStorage.removeItem(STORAGE_KEYS.USERNAME)
  }
}


// 不再需要登录尝试次数的持久化




// 验证用户名格式
const validateUsername = () => {
  if (loginFormRef.value) {
    loginFormRef.value.validateField('username')
  }
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  // 清除之前的错误信息
  errorMessage.value = ''

  try {
    await loginFormRef.value.validate()

    loading.value = true

    // 调用登录API
    await userStore.login(formData)

    // 登录成功，保存记住的凭据（如果选择）
    saveRememberedCredentials()

    ElMessage.success('登录成功，欢迎使用系统！')
    router.push('/')

  } catch (err: any) {
    console.error('登录失败:', err)
    errorMessage.value = err?.message || '登录失败，请重试'
    ElMessage.error(errorMessage.value)
  } finally {
    loading.value = false
  }
}

// 处理忘记密码
const handleForgotPassword = async () => {
  try {
    await ElMessageBox.alert(
      '请联系系统管理员重置密码，或通过企业邮箱申请密码重置。',
      '忘记密码',
      {
        confirmButtonText: '知道了',
        type: 'info',
        customClass: 'forgot-password-dialog'
      }
    )
  } catch (error) {
    // 用户点击了关闭按钮
  }
}

// 生命周期钩子
onMounted(() => {
  loadRememberedCredentials()
})
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-form-wrapper {
  position: relative;
  z-index: 10;
}

.login-form {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  padding: 48px 40px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.logo-section {
  margin-bottom: 40px;

  .logo {
    margin-bottom: 16px;
    display: flex;
    justify-content: center;

    .logo-icon {
      color: #667eea;
      filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.3));
    }
  }

  h1 {
    margin: 0 0 12px 0;
    color: #2c3e50;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: -0.5px;
  }

  .welcome-text {
    margin: 0;
    color: #6c757d;
    font-size: 15px;
    line-height: 1.5;
  }
}

.login-form-content {
  margin-bottom: 32px;

  .el-form-item {
    margin-bottom: 24px;

    :deep(.el-input) {
      border-radius: 8px;
      border: 2px solid #e9ecef;

      &:hover {
        border-color: #667eea;
      }

      &.is-focus {
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
    }

    :deep(.el-input__wrapper) {
      border-radius: 6px;
      box-shadow: none;
    }

    :deep(.el-input__inner) {
      font-size: 15px;
      padding: 12px 16px;

      &::placeholder {
        color: #adb5bd;
      }
    }
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .remember-me {
    :deep(.el-checkbox__label) {
      font-size: 14px;
      color: #6c757d;
    }
  }

  .forgot-password {
    font-size: 14px;
    font-weight: 500;
  }
}

.password-toggle-btn {
  color: #6c757d;

  &:hover {
    color: #667eea;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
}

.login-attempts-warning {
  margin-bottom: 16px;

  .attempts-alert {
    text-align: left;

    :deep(.el-alert__title) {
      font-weight: 600;
    }
  }

  .attempts-text {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #e6a23c;
    font-size: 14px;
    font-weight: 500;

    .el-icon {
      font-size: 16px;
    }
  }
}

.error-message {
  margin-bottom: 16px;

  .error-alert {
    :deep(.el-alert__title) {
      font-weight: 600;
    }
  }
}

.dev-info {
  margin-top: 32px;

  .dev-collapse {
    :deep(.el-collapse-item__header) {
      font-size: 14px;
      color: #6c757d;
      border-bottom: 1px solid #e9ecef;
    }

    :deep(.el-collapse-item__content) {
      padding: 16px 0;
    }
  }

  .dev-details {
    p {
      margin: 0 0 8px 0;
      font-size: 13px;
      color: #6c757d;
      line-height: 1.5;

      &:last-child {
        margin-bottom: 0;
      }

      strong {
        color: #495057;
      }
    }
  }
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;

  &.bg-circle-1 {
    width: 300px;
    height: 300px;
    top: -150px;
    right: -150px;
    animation-delay: 0s;
  }

  &.bg-circle-2 {
    width: 200px;
    height: 200px;
    bottom: -100px;
    left: -100px;
    animation-delay: 2s;
  }

  &.bg-circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    left: -75px;
    animation-delay: 4s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 16px;
  }

  .login-form {
    padding: 32px 24px;
    max-width: 360px;
  }

  .logo-section h1 {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .form-options {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .login-form {
    padding: 24px 20px;
    border-radius: 12px;
  }

  .logo-section {
    margin-bottom: 32px;

    .logo {
      margin-bottom: 12px;

      .logo-icon {
        font-size: 40px;
      }
    }

    h1 {
      font-size: 22px;
    }
  }

  .bg-circle {
    &.bg-circle-1 {
      width: 200px;
      height: 200px;
      top: -100px;
      right: -100px;
    }

    &.bg-circle-2 {
      width: 150px;
      height: 150px;
      bottom: -75px;
      left: -75px;
    }

    &.bg-circle-3 {
      display: none;
    }
  }
}

// 自定义对话框样式
.forgot-password-dialog {
  :deep(.el-messagebox__message) {
    color: #495057;
    line-height: 1.6;
  }
}
</style>
