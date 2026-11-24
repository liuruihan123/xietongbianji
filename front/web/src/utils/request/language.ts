/**
 * 请求语言头部工具函数
 * 统一管理前后端语言代码映射和头部设置
 */

// 语言代码映射：前端格式 -> 后端格式
const LANGUAGE_MAP: Record<string, string> = {
  'zh-CN': 'zh_CN',
  'en-US': 'en_US', 
  'ja-JP': 'ja_JP'
}

/**
 * 获取当前语言对应的后端格式
 * @returns 后端语言代码 (zh_CN, en_US, ja_JP)
 */
export const getContentLanguage = (): string => {
  try {
    // 从 localStorage 获取当前语言设置
    const currentLocale = localStorage.getItem('app-locale') || 'zh-CN'
    
    // 映射前端语言代码到后端格式
    return LANGUAGE_MAP[currentLocale] || 'zh_CN'
  } catch {
    return 'zh_CN'
  }
}

/**
 * 获取包含语言信息的默认请求头
 * @param customHeaders 自定义头部
 * @returns 合并后的请求头
 */
export const getDefaultHeaders = (customHeaders: Record<string, any> = {}): Record<string, any> => {
  return {
    'content-language': getContentLanguage(),
    'Content-Type': 'application/json',
    ...customHeaders
  }
}

/**
 * 为 axios 配置对象添加语言头部
 * @param config axios 配置对象
 * @returns 添加了语言头部的配置对象
 */
export const addLanguageHeaders = (config: any): any => {
  if (config.headers) {
    config.headers['content-language'] = getContentLanguage()
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
  }
  return config
}

/**
 * 监听语言变化，用于在语言切换时更新请求头
 * @param callback 语言变化时的回调函数
 */
export const onLanguageChange = (callback: (newLanguage: string) => void): void => {
  window.addEventListener('language-changed', (event: any) => {
    const newLanguage = getContentLanguage()
    callback(newLanguage)
  })
}
