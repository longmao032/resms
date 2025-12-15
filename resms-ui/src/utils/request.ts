// utils/request.ts 示例配置
import axios, { type AxiosResponse, type AxiosInstance } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// CSRF token存储
let csrfToken: string | null = null

// 获取CSRF token的函数
const getCsrfToken = async (): Promise<string | null> => {
  if (csrfToken) return csrfToken

  try {
    const response: AxiosResponse = await axios.get('/api/auth/csrf-token', {
      withCredentials: true
    })

    if (response.data?.code === 200 && response.data?.status) {
      csrfToken = response.data.data.csrfToken
      return csrfToken
    }
  } catch (error) {
    console.warn('Failed to get CSRF token:', error)
  }

  return null
}

type RequestInstance = {
  <T = any, R = T, D = any>(config: any): Promise<R>
  get<T = any, R = T, D = any>(url: string, config?: any): Promise<R>
  post<T = any, R = T, D = any>(url: string, data?: D, config?: any): Promise<R>
  put<T = any, R = T, D = any>(url: string, data?: D, config?: any): Promise<R>
  delete<T = any, R = T, D = any>(url: string, config?: any): Promise<R>
  interceptors: AxiosInstance['interceptors']
  defaults: AxiosInstance['defaults']
}

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 使用相对路径，通过 Vite 代理转发到后端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true // 启用跨域请求时发送凭据
}) as unknown as RequestInstance

// 请求拦截器
request.interceptors.request.use(
  async (config: any) => {
    // 添加token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 如果是 FormData，删除默认的 Content-Type，让浏览器自动设置带 boundary 的值
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }

    return config
  },
  (error: any) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  async (response: any) => {

    const data = response.data

    if (response?.config?.responseType === 'blob' && data instanceof Blob) {
      if (data.type && data.type.includes('application/json')) {
        const text = await data.text()
        try {
          const json = JSON.parse(text)
          const error = new Error(json.message || '请求失败')
          ;(error as any).response = { data: json }
          throw error
        } catch {
          const error = new Error('请求失败')
          ;(error as any).response = { data: { message: text } }
          throw error
        }
      }
      return data
    }

    // 检查后端返回的业务状态码
    if (data && typeof data.code === 'number') {
      // 如果是业务错误，抛出错误让业务代码处理
      if (data.code !== 200 || !data.status) {
        const error = new Error(data.message || '请求失败')
        ;(error as any).response = { data }
        throw error
      }
    }

    return data
  },
  async (error: any) => {
    console.log('响应错误:', error)

    if (error.response) {
      const { status, data } = error.response

      // 处理不同状态码
      switch (status) {
        case 400:
          // 客户端错误
          if (data?.message) {
            ElMessage.error(data.message)
          } else {
            ElMessage.error('请求参数错误')
          }
          break
        case 401:
          // token过期或无效
          ElMessage.warning(data?.message || '登录已过期，请重新登录')
          // 清除本地存储的用户信息
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('loginTime')
          // pinia-plugin-persistedstate 默认以 store id 作为 key
          localStorage.removeItem('user')
          // 清除CSRF token
          csrfToken = null
          // 延迟跳转以显示消息
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          // 权限不足
          if (data?.message?.includes('CSRF')) {
            ElMessage.error('安全验证失败，请刷新页面重试')
            // 清除CSRF token，重新获取
            csrfToken = null
          } else {
            ElMessage.error('没有权限访问此功能')
          }
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 408:
          ElMessage.error('请求超时，请稍后重试')
          break
        case 429:
          // 太多次请求
          ElMessage.warning('请求过于频繁，请稍后再试')
          break
        case 500:
          ElMessage.error('服务器内部错误，请联系管理员')
          break
        case 502:
        case 503:
        case 504:
          ElMessage.error('服务器暂时不可用，请稍后重试')
          break
        default:
          // 其他错误
          const errorMessage = data?.message || `请求失败 (${status})`
          ElMessage.error(errorMessage)
      }
    } else if (error.code === 'ECONNABORTED') {
      // 请求超时
      ElMessage.error('请求超时，请检查网络连接')
    } else if (error.request) {
      // 网络错误
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      ElMessage.error(error.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default request