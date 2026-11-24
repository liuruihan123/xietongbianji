import axios from 'axios'
import { getToken } from '@/stores/modules/auth/helper'
import { addLanguageHeaders } from './language'

// 获取环境变量
const { VITE_GLOB_API_URL, VITE_APP_API_BASE_URL } = import.meta.env

// 根据环境决定 baseURL
const getBaseURL = () => {
  // 开发环境使用代理
  if (import.meta.env.DEV) {
    return VITE_GLOB_API_URL || '/api'
  }
  // 生产环境使用完整 URL
  return VITE_APP_API_BASE_URL || '/api'
}

const service = (axios as any).create({
  baseURL: getBaseURL(),
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加语言头部参数
    addLanguageHeaders(config)

    return config
  },
  (error) => {
    return Promise.reject(error.response)
  },
)

service.interceptors.response.use(
  (response: any): any => {
    if (response.status === 200)
      return response

    throw new Error(response.status.toString())
  },
  (error: any) => {
    // 处理认证失败的情况
    if (error.response) {
      const { status } = error.response

      // 401 未授权 - token 失效或未登录
      if (status === 401) {
        handleAuthError('登录已过期，请重新登录')
        return Promise.reject(new Error('登录已过期'))
      }

      // 403 禁止访问 - 权限不足
      if (status === 403) {
        handleAuthError('权限不足，访问被拒绝')
        return Promise.reject(new Error('权限不足'))
      }

      // 其他错误状态码
      const errorMessage = error.response.data?.msg || error.response.data?.message || `请求失败 (${status})`
      return Promise.reject(new Error(errorMessage))
    }

    // 网络错误或其他错误
    if (error.code === 'NETWORK_ERROR' || !error.response) {
      return Promise.reject(new Error('网络连接失败，请检查网络'))
    }

    return Promise.reject(error)
  },
)

// 处理认证错误
function handleAuthError(message: string) {
  // 动态导入以避免循环依赖
  import('@/stores').then(({ useAuthStore }) => {
    const authStore = useAuthStore()

    // 清理用户状态
    authStore.clearUserState()

    // 显示错误消息
    import('ant-design-vue').then(({ message: antMessage }) => {
      antMessage.error(message)
    })

    // 跳转到首页
    import('vue-router').then(({ useRouter }) => {
      const router = useRouter()
      router.push('/')
    }).catch(() => {
      // 如果无法获取 router，直接刷新页面
      window.location.href = '/'
    })
  })
}

export default service
