/**
 * 环境变量配置文件
 * 提供类型安全的环境变量访问和验证
 */

// 环境变量类型定义
export interface EnvConfig {
  // 应用基础信息
  APP_TITLE: string
  APP_VERSION: string
  APP_ENV: 'development' | 'production' | 'staging' | 'testing'
  
  // 服务器配置
  DEV_SERVER_PORT: number
  DEV_SERVER_HOST: string
  
  // API 配置
  GLOB_API_URL: string
  APP_API_BASE_URL: string
  
  // 功能开关
  GLOB_APP_PWA: boolean
  USE_LAZY_LOADING: boolean
  
  // 文件上传配置
  UPLOAD_URL: string
  
  // 第三方服务配置
  GOOGLE_CLIENT_ID: string
  
  // 构建配置
  BUILD_DROP_CONSOLE?: boolean
  BUILD_COMPRESS?: string
}

/**
 * 环境变量验证函数
 */
function validateEnvVar(key: string, value: any, required: boolean = true): any {
  if (required && (value === undefined || value === '')) {
    console.warn(`⚠️ 环境变量 ${key} 未设置或为空`)
    return undefined
  }
  return value
}

/**
 * 字符串转布尔值
 */
function stringToBoolean(value: string | undefined): boolean {
  return value === 'true'
}

/**
 * 字符串转数字
 */
function stringToNumber(value: string | undefined, defaultValue: number): number {
  const num = Number(value)
  return isNaN(num) ? defaultValue : num
}

/**
 * 获取环境变量配置
 */
function createEnvConfig(): EnvConfig {
  const env = import.meta.env
  
  return {
    // 应用基础信息
    APP_TITLE: validateEnvVar('VITE_APP_TITLE', env.VITE_APP_TITLE) || 'DocLogic',
    APP_VERSION: validateEnvVar('VITE_APP_VERSION', env.VITE_APP_VERSION) || '1.0.0',
    APP_ENV: validateEnvVar('VITE_APP_ENV', env.VITE_APP_ENV) || 'development',
    
    // 服务器配置
    DEV_SERVER_PORT: stringToNumber(env.VITE_DEV_SERVER_PORT, 5173),
    DEV_SERVER_HOST: validateEnvVar('VITE_DEV_SERVER_HOST', env.VITE_DEV_SERVER_HOST) || '0.0.0.0',
    
    // API 配置
    GLOB_API_URL: validateEnvVar('VITE_GLOB_API_URL', env.VITE_GLOB_API_URL) || '/api',
    APP_API_BASE_URL: validateEnvVar('VITE_APP_API_BASE_URL', env.VITE_APP_API_BASE_URL) || 'http://localhost:8500',
    
    // 功能开关
    GLOB_APP_PWA: stringToBoolean(env.VITE_GLOB_APP_PWA),
    USE_LAZY_LOADING: stringToBoolean(env.VITE_USE_LAZY_LOADING),
    
    // 文件上传配置
    UPLOAD_URL: validateEnvVar('VITE_UPLOAD_URL', env.VITE_UPLOAD_URL) || '/startTask',
    
    // 第三方服务配置
    GOOGLE_CLIENT_ID: validateEnvVar('VITE_GOOGLE_CLIENT_ID', env.VITE_GOOGLE_CLIENT_ID, false) || '',
    
    // 构建配置
    BUILD_DROP_CONSOLE: stringToBoolean(env.VITE_BUILD_DROP_CONSOLE),
    BUILD_COMPRESS: env.VITE_BUILD_COMPRESS,
  }
}

/**
 * 环境配置实例
 */
export const envConfig = createEnvConfig()

/**
 * 环境检查函数
 */
export const envChecks = {
  isDevelopment: () => envConfig.APP_ENV === 'development',
  isProduction: () => envConfig.APP_ENV === 'production',
  isStaging: () => envConfig.APP_ENV === 'staging',
  isTesting: () => envConfig.APP_ENV === 'testing',
}

/**
 * 打印环境信息（仅在开发环境）
 */
if (envChecks.isDevelopment()) {
  console.log('🔧 环境配置信息:', {
    环境: envConfig.APP_ENV,
    应用标题: envConfig.APP_TITLE,
    版本: envConfig.APP_VERSION,
    API地址: envConfig.APP_API_BASE_URL,
    PWA启用: envConfig.GLOB_APP_PWA,
  })
}

/**
 * 导出默认配置
 */
export default envConfig
