/**
 * 系统设置模块API
 */

import { get, post, put, del } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  UserProfile,
  UpdateProfileParams,
  UpdatePasswordParams,
  UpdateAvatarParams,
  SystemConfig,
  UpdateSystemConfigParams,
  OperationLog,
  OperationLogListParams,
} from '@/types/settings'

/**
 * 获取用户个人信息
 */
export async function getUserProfile(): Promise<UserProfile> {
  const response = await get<UserProfile>({
    url: '/coedit/user/profile',
  })
  return response.data
}

/**
 * 更新用户个人信息
 */
export async function updateUserProfile(params: UpdateProfileParams): Promise<void> {
  await put<void>({
    url: '/coedit/user/profile',
    data: params,
  })
}

/**
 * 修改密码
 */
export async function updatePassword(params: UpdatePasswordParams): Promise<void> {
  await put<void>({
    url: '/coedit/user/password',
    data: params,
  })
}

/**
 * 上传头像
 */
export async function uploadAvatar(file: File): Promise<{ url: string }> {
  const formData = new FormData()
  formData.append('file', file)

  const response = await post<{ url: string }>({
    url: '/coedit/user/avatar/upload',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

/**
 * 更新头像
 */
export async function updateAvatar(params: UpdateAvatarParams): Promise<void> {
  await put<void>({
    url: '/coedit/user/avatar',
    data: params,
  })
}

/**
 * 获取系统配置
 */
export async function getSystemConfig(): Promise<SystemConfig> {
  const response = await get<SystemConfig>({
    url: '/coedit/system/config',
  })
  return response.data
}

/**
 * 更新系统配置
 */
export async function updateSystemConfig(params: UpdateSystemConfigParams): Promise<void> {
  await put<void>({
    url: '/coedit/system/config',
    data: params,
  })
}

/**
 * 获取操作日志列表
 */
export async function getOperationLogList(
  params: OperationLogListParams
): Promise<{ rows: OperationLog[]; total: number }> {
  const response = await get<{ rows: OperationLog[]; total: number }>({
    url: '/coedit/system/log/operation/list',
    data: params,
  })
  return response.data
}

/**
 * 删除操作日志
 */
export async function deleteOperationLog(id: number): Promise<void> {
  await del<void>({
    url: `/coedit/system/log/operation/${id}`,
  })
}

/**
 * 批量删除操作日志
 */
export async function batchDeleteOperationLogs(ids: number[]): Promise<void> {
  await del<void>({
    url: '/coedit/system/log/operation/batch',
    data: { ids },
  })
}

/**
 * 清空操作日志
 */
export async function clearOperationLogs(): Promise<void> {
  await del<void>({
    url: '/coedit/system/log/operation/clear',
  })
}


