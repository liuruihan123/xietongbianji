/**
 * API 响应辅助工具
 * 用于处理 ApiResponse<T> 类型的响应
 */

import type { ApiResponse } from '@/types/auth'

/**
 * 解包 API 响应，提取 data 字段
 * @param response API 响应对象
 * @returns 解包后的数据
 */
export function unwrapApiResponse<T>(response: ApiResponse<T>): T {
  if (response.code === 200 || response.code === 0) {
    return response.data
  }
  throw new Error(response.msg || '请求失败')
}

/**
 * 检查 API 响应是否成功
 * @param response API 响应对象
 * @returns 是否成功
 */
export function isApiSuccess(response: ApiResponse<any>): boolean {
  return response.code === 200 || response.code === 0
}

/**
 * 安全地解包 API 响应，失败时返回默认值
 * @param response API 响应对象
 * @param defaultValue 默认值
 * @returns 解包后的数据或默认值
 */
export function unwrapApiResponseSafe<T>(
  response: ApiResponse<T>,
  defaultValue: T
): T {
  try {
    return unwrapApiResponse(response)
  } catch {
    return defaultValue
  }
}

