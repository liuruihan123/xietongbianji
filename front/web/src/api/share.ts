/**
 * 文件分享API
 */

import { get, post, put, del } from '@/utils/request'
import type {
  ShareInfo,
  CreateShareRequest,
  UpdateShareRequest,
  ShareAccessLog,
  ShareStatistics,
} from '@/types/share'
import {
  permissionsToPermissionType,
  expireTypeToValidDays,
  ShareExpireType,
} from '@/types/share'

/**
 * 创建分享
 */
export function createShare(data: CreateShareRequest) {
  // 转换前端数据为后端格式
  const backendData: any = {
    fileId: data.fileId,
    // 共享类型：根据是否有密码判断（0-公开 1-密码保护）
    shareType: data.sharePassword && data.sharePassword.trim() ? 1 : 0,
    // 访问密码
    sharePassword: data.sharePassword,
    // 权限转换: permissions数组 -> permissionType整数（保留用于兼容）
    permissionType: permissionsToPermissionType(data.permissions),
  }

  // 有效期转换: expireType -> validDays 或 expireTime
  if (data.expireType === ShareExpireType.CUSTOM) {
    // 自定义有效期,使用具体的过期时间
    backendData.expireTime = data.expireTime
  } else {
    // 预设有效期,转换为天数
    const validDays = expireTypeToValidDays(data.expireType)
    if (validDays !== undefined) {
      backendData.validDays = validDays
    }
  }

  // 添加细粒度权限控制字段（确保始终传递，使用默认值1）
  backendData.allowDownload = data.allowDownload ?? 1
  backendData.allowPrint = data.allowPrint ?? 1
  backendData.allowCopy = data.allowCopy ?? 1
  backendData.allowEdit = data.allowEdit ?? 1
  backendData.allowComment = data.allowComment ?? 1

  console.log('========== createShare 数据转换 ==========')
  console.log('前端数据:', data)
  console.log('后端数据:', backendData)

  return post<ShareInfo>({
    url: '/coedit/share/create',
    data: backendData,
  })
}

/**
 * 获取分享信息
 */
export function getShareInfo(shareId: number) {
  return get<ShareInfo>({
    url: `/coedit/share/${shareId}`,
  })
}

/**
 * 通过分享码获取分享信息
 */
export function getShareByCode(shareCode: string, sharePassword?: string) {
  return post<ShareInfo>({
    url: '/coedit/share/access',
    data: { shareCode, sharePassword },
  })
}

/**
 * 更新分享
 */
export function updateShare(data: UpdateShareRequest) {
  // 转换前端数据为后端格式
  const backendData: any = {
    id: data.id,
  }

  // 权限转换
  if (data.permissions) {
    backendData.permissionType = permissionsToPermissionType(data.permissions)
  }

  // 密码字段名转换
  if (data.sharePassword !== undefined) {
    backendData.sharePassword = data.sharePassword
  }

  // 有效期转换
  if (data.expireType) {
    if (data.expireType === ShareExpireType.CUSTOM) {
      backendData.expireTime = data.expireTime
    } else {
      const validDays = expireTypeToValidDays(data.expireType)
      if (validDays !== undefined) {
        backendData.validDays = validDays
      }
    }
  }

  // 添加共享类型（如果有密码字段）
  if (data.sharePassword !== undefined) {
    backendData.shareType = data.sharePassword && data.sharePassword.trim() ? 1 : 0
  }

  // 添加细粒度权限控制字段
  if (data.allowDownload !== undefined) {
    backendData.allowDownload = data.allowDownload
  }
  if (data.allowPrint !== undefined) {
    backendData.allowPrint = data.allowPrint
  }
  if (data.allowCopy !== undefined) {
    backendData.allowCopy = data.allowCopy
  }
  if (data.allowEdit !== undefined) {
    backendData.allowEdit = data.allowEdit
  }
  if (data.allowComment !== undefined) {
    backendData.allowComment = data.allowComment
  }

  console.log('========== updateShare 数据转换 ==========')
  console.log('前端数据:', data)
  console.log('后端数据:', backendData)

  return put<ShareInfo>({
    url: `/coedit/share/${data.id}`,
    data: backendData,
  })
}

/**
 * 删除分享
 */
export function deleteShare(shareId: number) {
  return del({
    url: `/coedit/share/${shareId}`,
  })
}

/**
 * 禁用分享
 */
export function disableShare(shareId: number) {
  return post({
    url: `/coedit/share/${shareId}/disable`,
  })
}

/**
 * 启用分享
 */
export function enableShare(shareId: number) {
  return post({
    url: `/coedit/share/${shareId}/enable`,
  })
}

/**
 * 获取我的分享列表
 */
export function getMyShares(params?: {
  pageNum?: number
  pageSize?: number
  status?: string
  keyword?: string
}) {
  return get<{ rows: ShareInfo[]; total: number }>({
    url: '/coedit/share/my',
    data: params,
  })
}

/**
 * 获取文件的分享列表
 */
export function getFileShares(fileId: number) {
  return get<ShareInfo[]>({
    url: `/coedit/share/file/${fileId}`,
  })
}

/**
 * 获取分享访问记录
 */
export function getShareAccessLogs(shareId: number, params?: {
  pageNum?: number
  pageSize?: number
}) {
  return get<{ rows: ShareAccessLog[]; total: number }>({
    url: `/coedit/share/${shareId}/logs`,
    data: params,
  })
}

/**
 * 获取分享统计信息
 */
export function getShareStatistics(shareId: number) {
  return get<ShareStatistics>({
    url: `/coedit/share/${shareId}/statistics`,
  })
}

/**
 * 复制分享链接
 */
export async function copyShareLink(shareInfo: ShareInfo): Promise<void> {
  // 导入复制工具函数
  const { copyToClipboard } = await import('@/utils/copy')

  // 构造分享文本,如果有密码则包含密码
  let text = `📎 分享链接: ${shareInfo.shareUrl}`

  // 优先使用明文密码,如果没有则使用加密密码
  const password = shareInfo.plainPassword || shareInfo.sharePassword
  if (password) {
    text += `\n🔐 访问密码: ${password}`
  }

  text += `\n\n💡 提示: 点击链接即可访问「${shareInfo.fileName}」`

  // 使用统一的复制工具函数(包含降级方案)
  return copyToClipboard(text)
}

/**
 * 生成分享二维码
 */
export async function generateShareQRCode(shareUrl: string): Promise<string> {
  const response = await get<string>({
    url: '/coedit/share/qrcode',
    data: { url: shareUrl },
  })
  return response.data
}

/**
 * 验证分享密码
 */
export function verifySharePassword(shareCode: string, sharePassword: string) {
  return post<boolean>({
    url: '/coedit/share/verify-password',
    data: { shareCode, sharePassword },
  })
}

