import axios from 'axios'

import { useUserStore } from '@/stores/useUserStore'
import { getToken } from '@/stores/modules/auth/helper'
import { tansParams } from '@/utils/ruoyi'
import cache from '@/utils/cache'
import { HttpStatus } from '@/utils/enums/RespEnum'
import { errorCode } from '@/utils/errorCode'
import { i18n } from '@/i18n'
import { addLanguageHeaders } from './language'

// 简单的消息提示函数，替代 UI 库
const showMessage = (message: string, type: 'success' | 'error' | 'warning' = 'error') => {
  console.log(`[${type.toUpperCase()}]: ${message}`)
  // 这里可以替换为你项目中使用的消息提示方式
  alert(`${type.toUpperCase()}: ${message}`)
}

// 简单的确认对话框，替代 UI 库
const showConfirm = (title: string, content: string): Promise<boolean> => {
  return new Promise((resolve) => {
    const result = confirm(`${title}\n${content}`)
    resolve(result)
  })
}

// 是否显示重新登录 | Whether to show re-login
export const isRelogin = { show: false };



(axios as any).defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建 axios 实例
const service = (axios as any).create({
  baseURL: import.meta.env.VITE_GLOB_API_URL,
  timeout: 50000,
})

// 请求拦截器 | Request interceptor
service.interceptors.request.use(
  (config: any) => {
    const isToken = (config.headers || {}).isToken === false
    // 是否需要防止数据重复提交 | Whether to prevent data from being submitted repeatedly
    const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
    if (getToken() && !isToken)
      config.headers.Authorization = `Bearer ${getToken()}` // 让每个请求携带自定义token 请根据实际情况自行修改 | Add custom token to each request, modify according to actual needs

    // 添加语言头部参数
    addLanguageHeaders(config)

    // get请求映射params参数 | Map params parameters for GET requests
    if (config.method === 'get' && config.params) {
      let url = `${config.url}?${tansParams(config.params)}`
      url = url.slice(0, -1)
      config.params = {}
      config.url = url
    }

    if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    //   if (config.headers && config.headers.skipRepeatSubmit) {
    //     // 如果调用接口时，在headers里手工设置了（skipRepeatSubmit: true）跳过重复提交检查，则直接返回config
    //     return config
    //   }
      const requestObj = {
        url: config.url,
        data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
        time: new Date().getTime(),
      }
      // TODO 取消重复提交
      const sessionObj = cache.session.getJSON('sessionObj')
    //   if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
        cache.session.setJSON('sessionObj', requestObj)
    //   }
    //   else {
    //     const s_url = sessionObj.url // 请求地址 | Request address
    //     const s_data = sessionObj.data // 请求数据 | Request data
    //     const s_time = sessionObj.time // 请求时间 | Request time
    //     const interval = 100 // 间隔时间(ms)，小于此时间视为重复提交 | Interval time (ms), less than this time is considered repeated submission
    //     if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url)
    //       return Promise.reject(new Error(t('error.processingData')))
    //     else
    //       cache.session.setJSON('sessionObj', requestObj)
    //   }
    }
    // FormData数据去请求头Content-Type | Remove Content-Type header for FormData
    if (config.data instanceof FormData)
      delete config.headers['Content-Type']

    // 获取hash路由中的inviteCode参数
    const hash = window.location.hash
    const queryString = hash.split('?')[1] || ''
    const urlParams = new URLSearchParams(queryString)
    const inviteCode = urlParams.get('inviteCode')
    // console.log('inviteCode:::', inviteCode)
    if (inviteCode) {
      config.data = config.data || {}
      config.data.inviteCode = inviteCode
    }

    // 注意：语言信息现在通过 content-language 头部传递，不再通过 data.language

    return config
  },
  (error: any) => {
    return Promise.reject(error)
  },
)

// 响应拦截器 | Response interceptor
service.interceptors.response.use(
  async (res: any) => {
    // 未设置状态码则默认成功状态 | Default success status if no status code is set
    const code = res.data.code || HttpStatus.SUCCESS
    // 获取错误信息 | Get error message
    const msg = errorCode[code] || res.data.msg || errorCode.default
    // 二进制数据则直接返回 | Return binary data directly
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer')
      return res.data

    if (code === 4001) {
      const confirmed = await showConfirm(
        '重新登录提示',
        '登录状态已过期，您可以继续留在该页面，或者重新登录'
      )
      if (confirmed) {
        // 确定登录,返回数据并跳转到首页
        await useUserStore().logout()
        // 在URL中添加一个参数，用于标识需要自动打开登录弹框
        location.href = '/#/login?autoOpen=true'
      } else {
        // 取消登录,跳转到登录页
        location.href = '/#/login'
      }
    }
    else if (code === 401) {
      showMessage('无效的会话，或者会话已过期，请重新登录。', 'error')
      // 退出登录 | Logout
      await useUserStore().logout()
      location.href = '/login'
    }
    else if (code === HttpStatus.SERVER_ERROR) {
      // 统一错误提示
      showMessage(msg, 'error')
      return Promise.reject(new Error(msg))
    }
    else if (code === HttpStatus.WARN) {
      showMessage(msg, 'warning')
      return Promise.reject(new Error(msg))
    }
    else if (code !== HttpStatus.SUCCESS) {
      showMessage(msg, 'error')
      return Promise.reject(new Error(msg))
    }
    else {
      return Promise.resolve(res.data)
    }
  },
  (error: any) => {
    let { message } = error
    if (message === 'Network Error')
      message = '网络错误'
    else if (message.includes('timeout'))
      message = '系统接口请求超时'
    else if (message.includes('Request failed with status code'))
      message = `系统接口${message.substr(message.length - 3)}异常`

    showMessage(message, 'error')
    return Promise.reject(error)
  },
)
// 导出 axios 实例 | Export axios instance
export default service
