/**
 * API 配置文件
 */

// API 端点配置
export const API_ENDPOINTS = {
  // 认证相关
  AUTH: {
    LOGIN: '/auth/login',
    EMAIL_LOGIN: '/auth/emailLogin',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    RESET_PASSWORD: '/reset/password',
  },

  // 用户相关
  USER: {
    INFO: '/system/user/getInfo',
    UPDATE: '/user/update',
    AVATAR: '/user/avatar',
  },

  // 资源相关
  RESOURCE: {
    EMAIL_CODE: '/resource/email/code',
    UPLOAD: '/resource/upload',
  },

  // Telegram 相关
  TELEGRAM: {
    GENERATE_LINK: '/api/v1/telegram/generate-link',
    BIND_STATUS: '/api/v1/telegram/bind-status',
    UNBIND: '/api/v1/telegram/unbind',
    SEND_NOTIFICATION: '/api/v1/telegram/send-notification',
  },
} as const

// API 配置
export const API_CONFIG = {
  // 超时时间
  TIMEOUT: 20000,
  
  // 请求头
  HEADERS: {
    'Content-Type': 'application/json;charset=utf-8',
  },
  
  // 响应状态码
  SUCCESS_CODE: 200,
  
  // Token 相关
  TOKEN_KEY: 'ACCESS_TOKEN',
  TOKEN_PREFIX: 'Bearer ',
} as const

// 错误码配置
export const ERROR_CODES = {
  // 通用错误
  SUCCESS: 200,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500,
  
  // 业务错误码
  USER_NOT_FOUND: 1001,
  PASSWORD_ERROR: 1002,
  TOKEN_EXPIRED: 1003,
  PERMISSION_DENIED: 1004,
} as const

// 错误消息映射
export const ERROR_MESSAGES = {
  [ERROR_CODES.BAD_REQUEST]: '请求参数错误',
  [ERROR_CODES.UNAUTHORIZED]: '未授权，请重新登录',
  [ERROR_CODES.FORBIDDEN]: '访问被拒绝',
  [ERROR_CODES.NOT_FOUND]: '请求的资源不存在',
  [ERROR_CODES.INTERNAL_ERROR]: '服务器内部错误',
  [ERROR_CODES.USER_NOT_FOUND]: '用户不存在',
  [ERROR_CODES.PASSWORD_ERROR]: '密码错误',
  [ERROR_CODES.TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [ERROR_CODES.PERMISSION_DENIED]: '权限不足',
} as const

// 获取错误消息
export const getErrorMessage = (code: number): string => {
  return ERROR_MESSAGES[code] || '未知错误'
}

// 导出配置对象
export default {
  API_ENDPOINTS,
  API_CONFIG,
  ERROR_CODES,
  ERROR_MESSAGES,
  getErrorMessage,
}
