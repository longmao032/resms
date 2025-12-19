<template>
  <div class="login-container">
    <div class="login-card">
      <div class="logo-section">
        <div class="logo-circle">
          <el-icon size="32" class="logo-icon">
            <Management />
          </el-icon>
        </div>
        <h1>登录</h1>
        <p class="subtitle">使用您的房产后台账号</p>
      </div>

      <el-form ref="loginFormRef" :model="formData" :rules="rules" label-position="top" class="login-form"
        @submit.prevent="handleLogin">
        <el-form-item prop="username" label="用户名或邮箱">
          <el-input v-model="formData.username" placeholder="" size="large" clearable @blur="validateUsername" />
        </el-form-item>

        <el-form-item prop="password" label="密码">
          <el-input v-model="formData.password" :type="showPassword ? 'text' : 'password'" placeholder="" size="large"
            @keyup.enter="handleLogin" clearable>
            <template #suffix>
              <el-button text @click="showPassword = !showPassword" :icon="showPassword ? Hide : View"
                class="password-toggle-btn" />
            </template>
          </el-input>
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="formData.rememberMe" class="remember-me">
            记住我
          </el-checkbox>
          <el-link type="primary" @click="handleForgotPassword" class="forgot-password">
            忘记密码？
          </el-link>
        </div>

        <div v-if="errorMessage" class="error-message">
          <el-alert :title="errorMessage" type="error" :closable="false" show-icon />
        </div>

        <div class="action-section">
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </div>
      </el-form>

      <div class="footer">
        <p>© 2025 房产后台管理系统</p>
      </div>
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
  --g-blue: #1a73e8;
  --g-blue-dark: #174ea6;
  --g-blue-light: #e8f0fe;
  --g-gray-bg: #f8f9fa;
  --g-border: #dadce0;
  --g-text-primary: #202124;
  --g-text-secondary: #5f6368;
  --g-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.3), 0 1px 3px 1px rgba(60, 64, 67, 0.15);

  min-height: 100vh;
  background-color: var(--g-gray-bg);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  font-family: 'Roboto', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.login-card {
  width: 100%;
  max-width: 448px;
  background: #ffffff;
  border: 1px solid var(--g-border);
  border-radius: 24px;
  padding: 48px 40px 36px;
  box-shadow: none;
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: var(--g-shadow);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;

  .logo-circle {
    width: 64px;
    height: 64px;
    background-color: var(--g-blue-light);
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto 16px;

    .logo-icon {
      color: var(--g-blue);
    }
  }

  h1 {
    margin: 0 0 8px;
    color: var(--g-text-primary);
    font-size: 24px;
    font-weight: 500;
  }

  .subtitle {
    margin: 0;
    color: var(--g-text-secondary);
    font-size: 16px;
  }
}

.login-form {

  :deep(.el-form-item__label) {
    font-weight: 500;
    padding-bottom: 8px;
    color: var(--g-text-primary);
    display: block;
  }

  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px var(--g-border) inset;
    border-radius: 8px;
    padding: 8px 12px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: #fff;

    &:hover {
      box-shadow: 0 0 0 1px var(--g-text-secondary) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px var(--g-blue) inset !important;
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;

    .remember-me {
      height: auto;

      :deep(.el-checkbox__label) {
        color: var(--g-text-secondary);
      }

      :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
        background-color: var(--g-blue);
        border-color: var(--g-blue);
      }
    }

    .forgot-password {
      font-size: 14px;
      font-weight: 500;
      color: var(--g-blue);
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.error-message {
  margin-bottom: 24px;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;

  .login-btn {
    min-width: 120px;
    height: 44px;
    border-radius: 22px;
    font-weight: 500;
    font-size: 15px;
    background-color: var(--g-blue);
    border: none;
    padding: 0 24px;
    color: #ffffff;
    cursor: pointer;
    transition: background-color 0.2s, box-shadow 0.2s;

    &:hover {
      background-color: var(--g-blue-dark);
      box-shadow: 0 1px 3px 1px rgba(60, 64, 67, 0.15), 0 1px 2px 0 rgba(60, 64, 67, 0.3);
    }

    &:active {
      background-color: var(--g-blue-dark);
    }

    &:disabled {
      background-color: #f1f3f4;
      color: #9aa0a6;
      cursor: not-allowed;
      box-shadow: none;
    }
  }
}

.footer {
  margin-top: 48px;
  text-align: center;

  p {
    font-size: 12px;
    color: var(--g-text-secondary);
  }
}


@media (max-width: 480px) {
  .login-container {
    background-color: #ffffff;
    align-items: flex-start;
    padding-top: 40px;
  }

  .login-card {
    border: none;
    padding: 0 16px;
    box-shadow: none !important;
  }

  .action-section {
    flex-direction: column;

    .login-btn {
      width: 100%;
    }
  }
}

.forgot-password-dialog {
  border-radius: 12px;

  :deep(.el-messagebox__message) {
    color: var(--g-text-secondary);
    line-height: 1.6;
  }

  :deep(.el-button--primary) {
    background-color: var(--g-blue);
    border-radius: 20px;
  }
}
</style>
