/**
 * 访问记录相关类型定义
 */

/**
 * 目标类型枚举
 */
export enum TargetType {
  /** 工作空间 */
  WORKSPACE = 1,
  /** 文件夹 */
  FOLDER = 2,
  /** 文件 */
  FILE = 3,
}

/**
 * 访问类型枚举
 */
export enum AccessType {
  /** 查看 */
  VIEW = 1,
  /** 编辑 */
  EDIT = 2,
  /** 下载 */
  DOWNLOAD = 3,
}

/**
 * 工作空间信息
 */
export interface WorkspaceInfo {
  /** 工作空间ID */
  id: number
  /** 工作空间名称 */
  workspaceName: string
  /** 工作空间描述 */
  description?: string
  /** 创建时间 */
  createTime?: string
  /** 所有者名称 */
  ownerName?: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 位置路径 */
  locationPath?: string
}

/**
 * 文件夹信息
 */
export interface FolderInfo {
  /** 文件夹ID */
  id: number
  /** 文件夹名称 */
  folderName: string
  /** 文件夹路径 */
  folderPath: string
  /** 所属工作空间ID */
  workspaceId: number
  /** 创建时间 */
  createTime?: string
  /** 所有者名称 */
  ownerName?: string
  /** 所有者昵称 */
    ownerNickName?: string
  /** 位置路径 */
  locationPath?: string
  /** 文件夹大小(字节) */
  folderSize?: number
}

/**
 * 文件信息
 */
export interface FileInfo {
  /** 文件ID */
  id: number
  /** 文件名 */
  fileName: string
  /** 文件类型/扩展名 */
  fileType: string
  /** 文件扩展名(如 .xlsx, .docx) */
  fileExtension?: string
  /** 文件大小(字节) */
  fileSize: number
  /** 文件路径 */
  filePath: string
  /** 所属工作空间ID */
  workspaceId: number
  /** 创建时间 */
  createTime?: string
  /** 所有者名称 */
  ownerName?: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 位置路径 */
  locationPath?: string
}

/**
 * 访问记录
 */
export interface AccessRecord {
  /** 记录ID */
  id: number | string
  /** 目标类型: 1-工作空间, 2-文件夹, 3-文件 */
  targetType: TargetType
  /** 目标ID (支持大整数,使用 string 避免精度丢失) */
  targetId: number | string
  /** 访问时间 */
  accessTime: string
  /** 访问类型: 1-查看, 2-编辑, 3-下载 */
  accessType: AccessType
  /** 目标信息(根据targetType不同,返回不同的对象) */
  targetInfo: WorkspaceInfo | FolderInfo | FileInfo | null
}

/**
 * 记录工作空间访问参数
 */
export interface RecordWorkspaceAccessParams {
  /** 工作空间ID */
  workspaceId: number
}

/**
 * 记录文件夹访问参数
 */
export interface RecordFolderAccessParams {
  /** 文件夹ID */
  folderId: number
  /** 工作空间ID */
  workspaceId: number
}

/**
 * 记录文件访问参数
 */
export interface RecordFileAccessParams {
  /** 文件ID */
  fileId: number
  /** 工作空间ID */
  workspaceId: number
  /** 访问类型: 1-查看, 2-编辑, 3-下载 */
  accessType: AccessType
}

/**
 * 获取最近访问列表参数
 */
export interface GetRecentAccessListParams {
  /** 返回数量限制,默认50 */
  limit?: number
}

