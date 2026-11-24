/**
 * Google 认证配置
 */

import { envConfig } from './env'

// 从环境变量获取 Google Client ID
export const GOOGLE_CLIENT_ID = envConfig.GOOGLE_CLIENT_ID

/**
 * 检查 Google 配置是否有效
 */
export const isGoogleConfigValid = (): boolean => {
  return Boolean(GOOGLE_CLIENT_ID && GOOGLE_CLIENT_ID.trim() !== '')
}

/**
 * Google 认证配置对象
 */
export const googleConfig = {
  clientId: GOOGLE_CLIENT_ID,
  scope: 'email profile',
  cookiePolicy: 'single_host_origin',
}

/**
 * Google 认证错误消息
 */
export const GOOGLE_AUTH_ERRORS = {
  CONFIG_INVALID: 'Google 认证配置无效',
  POPUP_BLOCKED: '弹窗被阻止，请允许弹窗后重试',
  NETWORK_ERROR: '网络错误，请检查网络连接',
  USER_CANCELLED: '用户取消了认证',
  UNKNOWN_ERROR: '未知错误，请稍后重试',
} as const

export type GoogleAuthError = keyof typeof GOOGLE_AUTH_ERRORS
