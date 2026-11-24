/**
 * 文件分享相关类型定义
 */

/**
 * 分享权限类型
 */
export enum SharePermission {
  /** 仅查看 */
  VIEW = 'VIEW',
  /** 可下载 */
  DOWNLOAD = 'DOWNLOAD',
  /** 可编辑 */
  EDIT = 'EDIT',
  /** 可评论 */
  COMMENT = 'COMMENT',
}

/**
 * 分享权限类型（与后端对应）
 */
export enum SharePermissionType {
  /** 只读：只能查看和打印 */
  READ_ONLY = 0,
  /** 可编辑：可以编辑、评论、审阅 */
  EDITABLE = 1,
  /** 可下载：可以查看、打印和下载 */
  DOWNLOADABLE = 2,
  /** 完全权限：所有权限 */
  FULL_ACCESS = 3,
}

/**
 * 分享状态 - 与后端对应的数字状态码
 */
export enum ShareStatus {
  /** 0 - 正常/有效 */
  ACTIVE = 0,
  /** 1 - 已禁用 */
  DISABLED = 1,
  /** 2 - 已过期 */
  EXPIRED = 2,
  /** 已删除 */
  DELETED = 3,
}

/**
 * 分享状态字符串版本（兼容旧代码）
 */
export const ShareStatusString = {
  ACTIVE: 'ACTIVE',
  EXPIRED: 'EXPIRED',
  DISABLED: 'DISABLED',
  DELETED: 'DELETED',
}

/**
 * 有效期类型
 */
export enum ShareExpireType {
  /** 永久有效 */
  PERMANENT = 'PERMANENT',
  /** 1天 */
  ONE_DAY = '1_DAY',
  /** 7天 */
  SEVEN_DAYS = '7_DAYS',
  /** 30天 */
  THIRTY_DAYS = '30_DAYS',
  /** 自定义 */
  CUSTOM = 'CUSTOM',
}

/**
 * 分享信息
 */
export interface ShareInfo {
  /** 分享ID */
  id: number
  /** 文件ID */
  fileId: number
  /** 文件名 */
  fileName: string
  /** 文件类型 */
  fileType: string
  /** 文件大小（字节） */
  fileSize?: number
  /** 文件大小（格式化后） */
  fileSizeFormatted?: string
  /** 分享链接 */
  shareUrl: string
  /** 分享码 */
  shareCode: string
  /** 是否设置了密码 */
  hasPassword?: boolean
  /** 访问密码（加密后） */
  sharePassword?: string
  /** 明文密码（仅创建分享时返回） */
  plainPassword?: string
  /** 分享权限 */
  permissions: SharePermission[]
  /** 权限类型（与后端对应） */
  permissionType?: SharePermissionType
  /** 有效期类型 */
  expireType: ShareExpireType
  /** 过期时间 */
  expireTime?: string
  /** 分享状态 - 0:正常 1:已禁用 2:已过期 */
  status: ShareStatus | number
  /** 创建者ID */
  creatorId?: number
  /** 创建者名称 */
  creatorName?: string
  /** 创建时间 */
  createTime: string
  /** 访问次数 */
  viewCount?: number
  /** 下载次数 */
  downloadCount?: number
  /** 最后访问时间 */
  lastAccessTime?: string
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: number
  /** 是否允许打印：0-否 1-是 */
  allowPrint?: number
  /** 是否允许复制：0-否 1-是 */
  allowCopy?: number
  /** 是否允许编辑：0-否 1-是 */
  allowEdit?: number
  /** 是否允许评论：0-否 1-是 */
  allowComment?: number
}

/**
 * 创建分享请求
 */
export interface CreateShareRequest {
  /** 文件ID */
  fileId: number
  /** 分享权限 */
  permissions: SharePermission[]
  /** 有效期类型 */
  expireType: ShareExpireType
  /** 自定义过期时间 */
  expireTime?: string
  /** 访问密码 */
  sharePassword?: string
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: number
  /** 是否允许打印：0-否 1-是 */
  allowPrint?: number
  /** 是否允许复制：0-否 1-是 */
  allowCopy?: number
  /** 是否允许编辑：0-否 1-是 */
  allowEdit?: number
  /** 是否允许评论：0-否 1-是 */
  allowComment?: number
}

/**
 * 更新分享请求
 */
export interface UpdateShareRequest {
  /** 分享ID */
  id: number
  /** 分享权限 */
  permissions?: SharePermission[]
  /** 有效期类型 */
  expireType?: ShareExpireType
  /** 自定义过期时间 */
  expireTime?: string
  /** 访问密码 */
  sharePassword?: string
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: number
  /** 是否允许打印：0-否 1-是 */
  allowPrint?: number
  /** 是否允许复制：0-否 1-是 */
  allowCopy?: number
  /** 是否允许编辑：0-否 1-是 */
  allowEdit?: number
  /** 是否允许评论：0-否 1-是 */
  allowComment?: number
}

/**
 * 分享访问记录
 */
export interface ShareAccessLog {
  /** 记录ID */
  id: number
  /** 分享ID */
  shareId: number
  /** 访问者IP */
  visitorIp: string
  /** 访问者地区 */
  visitorLocation?: string
  /** 访问时间 */
  accessTime: string
  /** 访问类型(查看/下载) */
  accessType: 'VIEW' | 'DOWNLOAD'
  /** 用户代理 */
  userAgent?: string
}

/**
 * 分享统计信息
 */
export interface ShareStatistics {
  /** 分享ID */
  shareId: number
  /** 总访问次数 */
  totalViews: number
  /** 总下载次数 */
  totalDownloads: number
  /** 今日访问次数 */
  todayViews: number
  /** 今日下载次数 */
  todayDownloads: number
  /** 访问趋势(最近7天) */
  viewTrend: { date: string; count: number }[]
  /** 下载趋势(最近7天) */
  downloadTrend: { date: string; count: number }[]
  /** 访问地区分布 */
  locationDistribution: { location: string; count: number }[]
}

/**
 * 有效期选项
 */
export const EXPIRE_TYPE_OPTIONS = [
  { label: '永久有效', value: ShareExpireType.PERMANENT },
  { label: '1天', value: ShareExpireType.ONE_DAY },
  { label: '7天', value: ShareExpireType.SEVEN_DAYS },
  { label: '30天', value: ShareExpireType.THIRTY_DAYS },
  { label: '自定义', value: ShareExpireType.CUSTOM },
]

/**
 * 权限选项
 */
export const PERMISSION_OPTIONS = [
  { label: '仅查看', value: SharePermission.VIEW },
  { label: '可下载', value: SharePermission.DOWNLOAD },
  { label: '可编辑', value: SharePermission.EDIT },
  { label: '可评论', value: SharePermission.COMMENT },
]

/**
 * 获取分享状态文本
 */
export function getShareStatusText(status: ShareStatus | number): string {
  const textMap: Record<number, string> = {
    [ShareStatus.ACTIVE]: '正常',
    [ShareStatus.DISABLED]: '已禁用',
    [ShareStatus.EXPIRED]: '已过期',
    [ShareStatus.DELETED]: '已删除',
  }
  return textMap[status as number] || '未知'
}

/**
 * 获取分享状态颜色
 */
export function getShareStatusColor(status: ShareStatus | number): string {
  const colorMap: Record<number, string> = {
    [ShareStatus.ACTIVE]: 'success',
    [ShareStatus.DISABLED]: 'error',
    [ShareStatus.EXPIRED]: 'warning',
    [ShareStatus.DELETED]: 'error',
  }
  return colorMap[status as number] || 'default'
}

/**
 * 权限类型选项
 */
export const PERMISSION_TYPE_OPTIONS = [
  { label: '只读（仅查看和打印）', value: SharePermissionType.READ_ONLY },
  { label: '可编辑（编辑、评论、审阅）', value: SharePermissionType.EDITABLE },
  { label: '可下载（查看、打印、下载）', value: SharePermissionType.DOWNLOADABLE },
  { label: '完全权限（所有权限）', value: SharePermissionType.FULL_ACCESS },
]

/**
 * 获取权限类型文本
 */
export function getPermissionTypeText(type: SharePermissionType): string {
  const textMap: Record<SharePermissionType, string> = {
    [SharePermissionType.READ_ONLY]: '只读',
    [SharePermissionType.EDITABLE]: '可编辑',
    [SharePermissionType.DOWNLOADABLE]: '可下载',
    [SharePermissionType.FULL_ACCESS]: '完全权限',
  }
  return textMap[type] || '未知'
}

/**
 * 权限类型转换为权限数组
 */
export function permissionTypeToPermissions(type: SharePermissionType): SharePermission[] {
  switch (type) {
    case SharePermissionType.READ_ONLY:
      return [SharePermission.VIEW]
    case SharePermissionType.EDITABLE:
      return [SharePermission.VIEW, SharePermission.EDIT, SharePermission.COMMENT]
    case SharePermissionType.DOWNLOADABLE:
      return [SharePermission.VIEW, SharePermission.DOWNLOAD]
    case SharePermissionType.FULL_ACCESS:
      return [SharePermission.VIEW, SharePermission.EDIT, SharePermission.DOWNLOAD, SharePermission.COMMENT]
    default:
      return [SharePermission.VIEW]
  }
}

/**
 * 权限数组转换为权限类型
 */
export function permissionsToPermissionType(permissions: SharePermission[]): SharePermissionType {
  const hasView = permissions.includes(SharePermission.VIEW)
  const hasEdit = permissions.includes(SharePermission.EDIT)
  const hasDownload = permissions.includes(SharePermission.DOWNLOAD)
  const hasComment = permissions.includes(SharePermission.COMMENT)

  if (hasView && hasEdit && hasDownload && hasComment) {
    return SharePermissionType.FULL_ACCESS
  } else if (hasView && hasEdit && hasComment) {
    return SharePermissionType.EDITABLE
  } else if (hasView && hasDownload) {
    return SharePermissionType.DOWNLOADABLE
  } else {
    return SharePermissionType.READ_ONLY
  }
}

/**
 * 有效期类型转换为有效天数
 */
export function expireTypeToValidDays(expireType: ShareExpireType): number | undefined {
  switch (expireType) {
    case ShareExpireType.PERMANENT:
      return undefined // 永久有效,不设置过期时间
    case ShareExpireType.ONE_DAY:
      return 1
    case ShareExpireType.SEVEN_DAYS:
      return 7
    case ShareExpireType.THIRTY_DAYS:
      return 30
    default:
      return undefined
  }
}

/**
 * 权限模板类型
 */
export enum PermissionTemplate {
  /** 只读：所有权限关闭 */
  READONLY = 'READONLY',
  /** 预览：允许下载、打印、复制，禁止编辑 */
  PREVIEW = 'PREVIEW',
  /** 编辑：允许编辑和评论，禁止下载 */
  EDIT = 'EDIT',
  /** 完全权限：所有权限开启 */
  FULL = 'FULL',
}

/**
 * 权限配置接口
 */
export interface PermissionConfig {
  allowDownload: number
  allowPrint: number
  allowCopy: number
  allowEdit: number
  allowComment: number
}

/**
 * 获取权限模板配置
 */
export function getPermissionTemplateConfig(template: PermissionTemplate): PermissionConfig {
  switch (template) {
    case PermissionTemplate.READONLY:
      return {
        allowDownload: 0,
        allowPrint: 0,
        allowCopy: 0,
        allowEdit: 0,
        allowComment: 0,
      }
    case PermissionTemplate.PREVIEW:
      return {
        allowDownload: 1,
        allowPrint: 1,
        allowCopy: 1,
        allowEdit: 0,
        allowComment: 0,
      }
    case PermissionTemplate.EDIT:
      return {
        allowDownload: 0,
        allowPrint: 0,
        allowCopy: 1,
        allowEdit: 1,
        allowComment: 1,
      }
    case PermissionTemplate.FULL:
      return {
        allowDownload: 1,
        allowPrint: 1,
        allowCopy: 1,
        allowEdit: 1,
        allowComment: 1,
      }
  }
}

/**
 * 判断权限级别
 */
export function getPermissionLevel(shareInfo: ShareInfo): string {
  const {
    allowDownload = 1,
    allowPrint = 1,
    allowCopy = 1,
    allowEdit = 1,
    allowComment = 1,
  } = shareInfo

  // 只读（严格）
  if (!allowDownload && !allowPrint && !allowCopy && !allowEdit && !allowComment) {
    return '只读'
  }

  // 预览
  if (allowDownload && allowPrint && allowCopy && !allowEdit && !allowComment) {
    return '预览'
  }

  // 编辑
  if (!allowDownload && !allowPrint && allowCopy && allowEdit && allowComment) {
    return '编辑'
  }

  // 完全权限
  if (allowDownload && allowPrint && allowCopy && allowEdit && allowComment) {
    return '完全权限'
  }

  // 自定义
  return '自定义'
}

/**
 * 获取权限级别图标
 */
export function getPermissionLevelIcon(level: string): string {
  const iconMap: Record<string, string> = {
    '只读': '🔒',
    '预览': '👁️',
    '编辑': '✏️',
    '完全权限': '⭐',
    '自定义': '🔧',
  }
  return iconMap[level] || '📄'
}

