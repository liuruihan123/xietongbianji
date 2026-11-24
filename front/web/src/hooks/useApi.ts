import { ref, computed, type Ref } from 'vue'
import axios from 'axios'
import { useAuthStoreWithout } from '@/stores'

export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

export interface ApiError {
  code: number
  message: string
  details?: any
}

export interface UseApiOptions {
  immediate?: boolean
  onSuccess?: (data: any) => void
  onError?: (error: ApiError) => void
  showLoading?: boolean
  showError?: boolean
}

export interface UseApiReturn<T> {
  data: Ref<T | null>
  loading: Ref<boolean>
  error: Ref<ApiError | null>
  execute: (...args: any[]) => Promise<T>
  reset: () => void
}

// 创建axios实例
const createApiInstance = () => {
  const instance = (axios as any).create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // 请求拦截器
  instance.interceptors.request.use(
    (config) => {
      const authStore = useAuthStoreWithout()
      
      // 添加认证token
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`
      }

      // 添加语言头
      const language = localStorage.getItem('app-locale') || 'zh-CN'
      config.headers['Accept-Language'] = language

      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  instance.interceptors.response.use(
    (response: any) => {
      const { data } = response
      
      // 检查业务状态码
      if (data.code === 200) {
        return response
      } else {
        // 业务错误
        const error: ApiError = {
          code: data.code,
          message: data.msg || '请求失败',
          details: data
        }
        return Promise.reject(error)
      }
    },
    (error: any) => {
      const authStore = useAuthStoreWithout()
      
      // 处理HTTP错误
      let apiError: ApiError = {
        code: 0,
        message: '网络错误'
      }

      if (error.response) {
        const { status, data } = error.response
        
        switch (status) {
          case 401:
            apiError = {
              code: 401,
              message: '登录已过期，请重新登录'
            }
            // 清除用户状态
            authStore.clearUserState()
            // 重定向到登录页
            if (typeof window !== 'undefined') {
              window.location.href = '/signin'
            }
            break
          case 403:
            apiError = {
              code: 403,
              message: '权限不足'
            }
            break
          case 404:
            apiError = {
              code: 404,
              message: '请求的资源不存在'
            }
            break
          case 500:
            apiError = {
              code: 500,
              message: '服务器内部错误'
            }
            break
          default:
            apiError = {
              code: status,
              message: (data as any)?.msg || `请求失败 (${status})`
            }
        }
      } else if (error.request) {
        apiError = {
          code: 0,
          message: '网络连接失败，请检查网络设置'
        }
      } else {
        apiError = {
          code: 0,
          message: error.message || '请求配置错误'
        }
      }

      return Promise.reject(apiError)
    }
  )

  return instance
}

// 全局axios实例
const apiInstance = createApiInstance()

// 通用API请求hook
export function useApi<T = any>(
  apiFunction: (...args: any[]) => Promise<any>,
  options: UseApiOptions = {}
): UseApiReturn<T> {
  const {
    immediate = false,
    onSuccess,
    onError,
    showLoading = true,
    showError = true
  } = options

  const data = ref<T | null>(null)
  const loading = ref(false)
  const error = ref<ApiError | null>(null)

  const execute = async (...args: any[]): Promise<T> => {
    try {
      if (showLoading) {
        loading.value = true
      }
      error.value = null

      const response = await apiFunction(...args)
      const result = response.data.data

      data.value = result
      
      if (onSuccess) {
        onSuccess(result)
      }

      return result
    } catch (err) {
      const apiError = err as ApiError
      error.value = apiError

      if (showError) {
        console.error('API Error:', apiError)
        // 这里可以集成消息提示组件
        // message.error(apiError.message)
      }

      if (onError) {
        onError(apiError)
      }

      throw apiError
    } finally {
      if (showLoading) {
        loading.value = false
      }
    }
  }

  const reset = () => {
    data.value = null
    loading.value = false
    error.value = null
  }

  // 如果设置了immediate，立即执行
  if (immediate) {
    execute()
  }

  return {
    data: data as any as Ref<T | null>,
    loading,
    error,
    execute,
    reset
  }
}

// 简化的API请求方法
export const api = {
  get: <T = any>(url: string, config?: any) =>
    apiInstance.get(url, config),

  post: <T = any>(url: string, data?: any, config?: any) =>
    apiInstance.post(url, data, config),

  put: <T = any>(url: string, data?: any, config?: any) =>
    apiInstance.put(url, data, config),

  delete: <T = any>(url: string, config?: any) =>
    apiInstance.delete(url, config),

  patch: <T = any>(url: string, data?: any, config?: any) =>
    apiInstance.patch(url, data, config)
}

// 文件上传hook
export function useFileUpload() {
  const uploading = ref(false)
  const progress = ref(0)
  const error = ref<ApiError | null>(null)

  const upload = async (file: File, url: string = '/upload'): Promise<string> => {
    try {
      uploading.value = true
      progress.value = 0
      error.value = null

      const formData = new FormData()
      formData.append('file', file)

      const response = await apiInstance.post(
        url,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            if (progressEvent.total) {
              progress.value = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              )
            }
          }
        }
      )

      return response.data.data.url
    } catch (err) {
      const apiError = err as ApiError
      error.value = apiError
      throw apiError
    } finally {
      uploading.value = false
    }
  }

  const reset = () => {
    uploading.value = false
    progress.value = 0
    error.value = null
  }

  return {
    uploading,
    progress,
    error,
    upload,
    reset
  }
}

// 分页数据hook
export function usePagination<T = any>(
  apiFunction: (params: any) => Promise<any>,
  initialParams: any = {}
) {
  const data = ref<T[]>([])
  const loading = ref(false)
  const error = ref<ApiError | null>(null)
  const total = ref(0)
  const current = ref(1)
  const pageSize = ref(10)

  const params = ref({ ...initialParams })

  const fetchData = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await apiFunction({
        ...params.value,
        page: current.value,
        pageSize: pageSize.value
      })

      const result = response.data.data
      data.value = result.list
      total.value = result.total
    } catch (err) {
      const apiError = err as ApiError
      error.value = apiError
      console.error('Pagination Error:', apiError)
    } finally {
      loading.value = false
    }
  }

  const changePage = (page: number) => {
    current.value = page
    fetchData()
  }

  const changePageSize = (size: number) => {
    pageSize.value = size
    current.value = 1
    fetchData()
  }

  const updateParams = (newParams: any) => {
    params.value = { ...params.value, ...newParams }
    current.value = 1
    fetchData()
  }

  const refresh = () => {
    fetchData()
  }

  const reset = () => {
    data.value = []
    total.value = 0
    current.value = 1
    error.value = null
    params.value = { ...initialParams }
  }

  return {
    data,
    loading,
    error,
    total,
    current,
    pageSize,
    params,
    fetchData,
    changePage,
    changePageSize,
    updateParams,
    refresh,
    reset
  }
}
