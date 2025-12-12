// stores/userStore.ts

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, Menu, loginForm, loginResp } from '@/api/user/type'
import { reqLogin, reqUserInfo } from '@/api/user/index'
import { ElMessage, ElMessageBox } from 'element-plus'

// 会话过期时间（24小时）
const SESSION_EXPIRY_TIME = 24 * 60 * 60 * 1000
let sessionCheckTimer: number | null = null

export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string | null>(null)
  const menus = ref<Menu[]>([])
  const permissions = ref<string[]>([])
  const isLoggedIn = computed(() => !!token.value && !isSessionExpired())
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const currentUser = computed(() => userInfo.value)
  const userMenus = computed(() => menus.value)
  const userPermissions = computed(() => permissions.value)
  const hasPermission = (permission: string) => permissions.value.includes(permission)

  // Actions
  const login = async (data: loginForm) => {
    loading.value = true
    error.value = null

    try {
      const response = await reqLogin(data)

      // 检查响应格式和状态
      if (!response || typeof response.code !== 'number') {
        throw new Error('服务器响应格式错误')
      }

      if (response.code === 200 && response.status && response.data) {
        // 保存用户信息
        userInfo.value = response.data
        token.value = response.data.token
        menus.value = response.data.menus || []
        permissions.value = response.data.permissions || []

        // 存储登录时间戳，用于会话过期检查
        const loginTime = Date.now()
        localStorage.setItem('loginTime', loginTime.toString())
        localStorage.setItem('token', response.data.token) // 手动存储token供request.ts使用

        // 设置会话过期检查定时器
        setupSessionExpiryCheck()

        return response
      } else {
        // 业务错误
        const errorMessage = response.message || '登录失败'
        throw new Error(errorMessage)
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '登录失败'
      error.value = errorMessage
      console.error('Failed to login:', err)

      // 如果是网络错误，添加更友好的提示
      if (!navigator.onLine) {
        error.value = '网络连接失败，请检查网络设置'
      } else if (errorMessage.includes('Network Error') || errorMessage.includes('ECONNABORTED')) {
        error.value = '无法连接到服务器，请检查后端服务是否运行'
      }

      throw err
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    // 清除状态
    userInfo.value = null
    token.value = null
    menus.value = []
    permissions.value = []

    // 清除登录时间戳和定时器
    localStorage.removeItem('loginTime')
    localStorage.removeItem('token') // 清除token
    if (sessionCheckTimer) {
      clearInterval(sessionCheckTimer)
      sessionCheckTimer = null
    }

    // 持久化插件会同步清除 storage 中的数据
  }

  const fetchUserInfo = async () => {
    if (!token.value) return

    loading.value = true
    error.value = null

    try {
      const response = await reqUserInfo()

      if (response.code === 200 && response.status) {
        if (response.data && !response.data.userId && response.data.id) {
          response.data.userId = response.data.id
        }
        userInfo.value = response.data
        menus.value = response.data.menus
        permissions.value = response.data.permissions

        return response.data
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取用户信息失败'
      console.error('Failed to fetch user info:', err)
      // 如果获取用户信息失败，可能是token过期，执行登出
      logout()
      throw err
    } finally {
      loading.value = false
    }
  }

  // 检查会话是否过期
  const isSessionExpired = () => {
    const loginTime = localStorage.getItem('loginTime')
    if (!loginTime) return true

    const timeDiff = Date.now() - parseInt(loginTime, 10)
    return timeDiff > SESSION_EXPIRY_TIME
  }

  // 设置会话过期检查定时器
  const setupSessionExpiryCheck = () => {
    if (sessionCheckTimer) {
      clearInterval(sessionCheckTimer)
    }

    // 每分钟检查一次会话状态
    sessionCheckTimer = window.setInterval(() => {
      if (isSessionExpired() && token.value) {
        ElMessage.warning('登录已过期，请重新登录')
        logout()
        // 跳转到登录页
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
    }, 60000) // 1分钟检查一次
  }

  // 初始化用户信息（插件会恢复持久化的数据；若只有 token，则尝试从服务器获取）
  const initializeUser = () => {
    if (token.value && !userInfo.value) {
      // 检查会话是否过期
      if (isSessionExpired()) {
        logout()
        return
      }
      fetchUserInfo()
      setupSessionExpiryCheck()
    }
  }

  // Mock login for demo purposes
  const mockLogin = () => {
    const mockUser = {
      userId: 1,
      username: 'zhangsan',
      realName: '张三',
      avatar: '/uploads/avatars/default.jpg',
      token: 'mock-jwt-token-123456',
      roleType: 1,
      menus: [],
      permissions: ['*']
    }

    userInfo.value = mockUser
    token.value = mockUser.token
    menus.value = mockUser.menus
    permissions.value = mockUser.permissions

    const loginTime = Date.now()
    localStorage.setItem('loginTime', loginTime.toString())
    setupSessionExpiryCheck()
  }

  return {
    // State
    userInfo,
    token,
    menus,
    permissions,
    isLoggedIn,
    loading,
    error,

    // Getters
    currentUser,
    userMenus,
    userPermissions,
    hasPermission,

    // Actions
    login,
    logout,
    fetchUserInfo,
    initializeUser,
    mockLogin
  }
}, {
  persist: true
})