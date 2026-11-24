/**
 * 环境配置工具
 * 已迁移到运行时配置系统，此文件保留用于向后兼容
 * 新代码请使用 @/utils/runtimeConfig
 */

import runtimeConfig, {
  getApiBaseUrl as getRuntimeApiBaseUrl,
  getAppTitle as getRuntimeAppTitle,
  getAppVersion as getRuntimeAppVersion,
  isDevelopment,
  isProduction,
} from './runtimeConfig'

// 获取环境变量（已废弃，保留用于兼容）
export const getEnv = (): ImportMetaEnv => {
  return import.meta.env
}

// 获取应用标题
export const getAppTitle = (): string => {
  return getRuntimeAppTitle()
}

// 获取 API 基础 URL
export const getApiBaseUrl = (): string => {
  // 开发环境使用代理
  if (import.meta.env.DEV) {
    return runtimeConfig.API_URL_PREFIX
  }

  // 生产环境使用完整 URL
  return getRuntimeApiBaseUrl()
}

// 获取上传 URL
export const getUploadUrl = (): string => {
  return runtimeConfig.UPLOAD_URL
}

// 是否为开发环境
export const isDev = (): boolean => {
  return isDevelopment()
}

// 是否为生产环境
export const isProd = (): boolean => {
  return isProduction()
}

// 获取应用版本
export const getAppVersion = (): string => {
  return getRuntimeAppVersion()
}

// 是否启用 PWA
export const isPWAEnabled = (): boolean => {
  return runtimeConfig.ENABLE_PWA
}

// 获取当前环境名称
export const getEnvName = (): string => {
  return runtimeConfig.APP_ENV
}

// 环境配置对象
export const envConfig = {
  title: getAppTitle(),
  version: getAppVersion(),
  apiBaseUrl: getApiBaseUrl(),
  uploadUrl: getUploadUrl(),
  isDev: isDev(),
  isProd: isProd(),
  isPWAEnabled: isPWAEnabled(),
  envName: getEnvName(),
}

// 打印环境信息（仅开发环境）
if (isDev()) {
  console.log('🚀 DocLogic Environment Config:', envConfig)
}
