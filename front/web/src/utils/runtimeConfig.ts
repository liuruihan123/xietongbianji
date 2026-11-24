/**
 * 运行时配置工具
 * 用于读取 window.__APP_CONFIG__ 中的配置
 * 支持 Docker 容器部署时动态修改配置
 */

// 运行时配置类型定义
export interface RuntimeConfig {
  // 应用基础信息
  APP_TITLE: string
  APP_VERSION: string
  APP_ENV: 'development' | 'production' | 'staging' | 'testing'

  // API 配置
  API_BASE_URL: string
  API_URL_PREFIX: string

  // WebSocket 配置
  WS_URL: string

  // 文件上传配置
  UPLOAD_URL: string

  // OnlyOffice Document Server 配置
  ONLYOFFICE_URL: string

  // 第三方服务配置
  GOOGLE_CLIENT_ID: string

  // 功能开关
  ENABLE_PWA: boolean
  ENABLE_LAZY_LOADING: boolean
  ENABLE_CONSOLE: boolean

  // 调试配置
  DEBUG: {
    ENABLED: boolean
    LOG_REQUEST: boolean
    LOG_RESPONSE: boolean
  }

  // 超时配置
  TIMEOUT: {
    REQUEST: number
    WEBSOCKET: number
  }
}

// 扩展 Window 接口
declare global {
  interface Window {
    __APP_CONFIG__?: RuntimeConfig
  }
}

/**
 * 检查字符串是否为未替换的占位符
 * @param value 要检查的值
 * @returns 是否为占位符
 */
function isPlaceholder(value: any): boolean {
  return typeof value === 'string' && /^\$\{.+\}$/.test(value)
}

/**
 * 获取环境变量的默认配置
 */
function getEnvDefaults(): RuntimeConfig {
  return {
    APP_TITLE: import.meta.env.VITE_APP_TITLE || '协同编辑系统',
    APP_VERSION: import.meta.env.VITE_APP_VERSION || '1.0.0',
    APP_ENV: (import.meta.env.VITE_APP_ENV as any) || 'development',

    API_BASE_URL: import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8500',
    API_URL_PREFIX: import.meta.env.VITE_GLOB_API_URL || '/api',

    // SockJS 需要使用 http/https 协议，不能使用 ws/wss
    // SockJS 会自动处理 WebSocket 协议升级
    WS_URL: `${import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8500'}/ws`,
      // WS_URL: `${import.meta.env.VITE_SERVER_IP+ ':' + import.meta.env.VITE_DEV_SERVER_PORT+import.meta.env.VITE_GLOB_API_URL || 'http://localhost:8500'}/ws`,

    UPLOAD_URL: import.meta.env.VITE_UPLOAD_URL || '/startTask',

    ONLYOFFICE_URL: import.meta.env.VITE_ONLYOFFICE_URL || 'http://34.200.2.14:9000/',

    GOOGLE_CLIENT_ID: import.meta.env.VITE_GOOGLE_CLIENT_ID || '',

    ENABLE_PWA: import.meta.env.VITE_GLOB_APP_PWA === 'true',
    ENABLE_LAZY_LOADING: import.meta.env.VITE_USE_LAZY_LOADING === 'true',
    ENABLE_CONSOLE: import.meta.env.VITE_BUILD_DROP_CONSOLE !== 'true',

    DEBUG: {
      ENABLED: import.meta.env.DEV,
      LOG_REQUEST: import.meta.env.DEV,
      LOG_RESPONSE: import.meta.env.DEV,
    },

    TIMEOUT: {
      REQUEST: 15000,
      WEBSOCKET: 10000,
    },
  }
}

/**
 * 获取运行时配置
 * 优先使用 window.__APP_CONFIG__，如果不存在则回退到环境变量
 * 如果配置值是未替换的占位符(如 ${ONLYOFFICE_URL})，则使用环境变量替换
 */
function getRuntimeConfig(): RuntimeConfig {
  const envDefaults = getEnvDefaults()

  // 如果不存在运行时配置，使用环境变量
  if (!window.__APP_CONFIG__) {
    console.warn('⚠️ 运行时配置未找到，使用环境变量作为回退方案')
    return envDefaults
  }

  // 存在运行时配置，但需要检查并替换占位符
  const config = window.__APP_CONFIG__
  const mergedConfig: any = {}

  // 遍历配置项，替换占位符
  for (const key in config) {
    const value = (config as any)[key]

    // 处理嵌套对象
    if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
      mergedConfig[key] = {}
      for (const subKey in value) {
        const subValue = value[subKey]
        if (isPlaceholder(subValue)) {
          // 使用环境变量默认值替换占位符
          mergedConfig[key][subKey] = (envDefaults as any)[key]?.[subKey] ?? subValue
          console.warn(`⚠️ 配置项 ${key}.${subKey} 为占位符，已替换为环境变量值:`, mergedConfig[key][subKey])
        } else {
          mergedConfig[key][subKey] = subValue
        }
      }
    } else if (isPlaceholder(value)) {
      // 使用环境变量默认值替换占位符
      mergedConfig[key] = (envDefaults as any)[key] ?? value
      console.warn(`⚠️ 配置项 ${key} 为占位符，已替换为环境变量值:`, mergedConfig[key])
    } else {
      mergedConfig[key] = value
    }
  }

  return mergedConfig as RuntimeConfig
}

/**
 * 运行时配置实例（单例）
 */
export const runtimeConfig = getRuntimeConfig()

/**
 * 获取 API 基础 URL
 */
export function getApiBaseUrl(): string {
  return runtimeConfig.API_BASE_URL
}

/**
 * 获取 OnlyOffice URL
 */
export function getOnlyOfficeUrl(): string {
  return runtimeConfig.ONLYOFFICE_URL
}

/**
 * 获取 WebSocket URL
 */
export function getWebSocketUrl(): string {
  return runtimeConfig.WS_URL
}

/**
 * 获取应用标题
 */
export function getAppTitle(): string {
  return runtimeConfig.APP_TITLE
}

/**
 * 获取应用版本
 */
export function getAppVersion(): string {
  return runtimeConfig.APP_VERSION
}

/**
 * 是否为生产环境
 */
export function isProduction(): boolean {
  return runtimeConfig.APP_ENV === 'production'
}

/**
 * 是否为开发环境
 */
export function isDevelopment(): boolean {
  return runtimeConfig.APP_ENV === 'development'
}

/**
 * 打印配置信息（仅开发环境）
 */
if (runtimeConfig.DEBUG.ENABLED) {
  console.log('🔧 运行时配置信息:', {
    环境: runtimeConfig.APP_ENV,
    应用标题: runtimeConfig.APP_TITLE,
    版本: runtimeConfig.APP_VERSION,
    API地址: runtimeConfig.API_BASE_URL,
    OnlyOffice地址: runtimeConfig.ONLYOFFICE_URL,
    WebSocket地址: runtimeConfig.WS_URL,
  })
}

export default runtimeConfig

