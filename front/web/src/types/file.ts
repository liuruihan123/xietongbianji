/**
 * 文件管理相关类型定义
 */

/**
 * 文件类型枚举
 */
export enum FileType {
  /** 文件夹 */
  FOLDER = 'FOLDER',
  /** 文档 */
  DOCUMENT = 'DOCUMENT',
  /** 电子表格 */
  SPREADSHEET = 'SPREADSHEET',
  /** 演示文稿 */
  PRESENTATION = 'PRESENTATION',
  /** PDF */
  PDF = 'PDF',
  /** 图片 */
  IMAGE = 'IMAGE',
  /** 压缩包 */
  ARCHIVE = 'ARCHIVE',
  /** 其他 */
  OTHER = 'OTHER',
}

/**
 * 文件状态枚举
 */
export enum FileStatus {
  /** 正常 */
  NORMAL = 0,
  /** 已删除(回收站) */
  DELETED = 1,
  /** 锁定 */
  LOCKED = 2,
}

/**
 * 文件接口
 */
export interface FileItem {
  /** 文件ID (支持number和string类型,兼容后端返回的长整型ID) */
  id: number | string
  /** 文件名 */
  name: string
  /** 文件名（后端返回的字段名，与 name 相同） */
  fileName?: string
  /** 文件类型 */
  type: FileType
  /** 文件大小(字节) */
  size: number
  /** 文件大小（后端返回的字段名） */
  fileSize?: number
  /** 文件路径 */
  path: string
  /** 文件路径（后端返回的字段名） */
  filePath?: string
  /** 父文件夹ID */
  parentId: number | string | null
  /** 父文件夹ID（后端返回的字段名，与 parentId 相同） */
  folderId?: number | string | null
  /** 工作空间ID */
  workspaceId: number | string
  /** 创建者ID */
  creatorId: number | string
  /** 创建者名称 */
  creatorName: string
  /** 所有者ID（后端返回的字段名） */
  ownerId?: number | string
  /** 所有者名称（后端返回的字段名） */
  ownerName?: string
  /** 所有者昵称（后端返回的字段名） */
  ownerNickName?: string
  /** 文件状态 */
  status: FileStatus
  /** 是否为文件夹 */
  isFolder: boolean
  /** 是否已删除（后端返回的字段名，布尔类型） */
  isDeleted?: boolean
  /** 文件扩展名 */
  extension?: string
  /** 文件扩展名（后端返回的字段名） */
  fileExtension?: string
  /** 文件类型（后端返回的字段名） */
  fileType?: string
  /** MIME类型（后端返回的字段名） */
  mimeType?: string
  /** 文档密钥（后端返回的字段名） */
  documentKey?: string
  /** 当前版本（后端返回的字段名） */
  currentVersion?: number
  /** 文件URL */
  url?: string
  /** 文件URL（后端返回的字段名） */
  fileUrl?: string
  /** 缩略图URL */
  thumbnail?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 最后访问时间 */
  lastAccessTime?: string
  /** 最后编辑时间（后端返回的字段名） */
  lastEditTime?: string
  /** 最后编辑者（后端返回的字段名） */
  lastEditBy?: string
  /** 最后编辑者名称（后端返回的字段名） */
  lastEditByName?: string
  /** 是否被收藏 */
  isFavorite?: boolean
  /** 是否被锁定 */
  isLocked?: boolean | number
  /** 锁定者ID */
  lockerId?: number | string
  /** 锁定者名称 */
  lockerName?: string
  /** 锁定者（后端返回的字段名） */
  lockedBy?: string
  /** 锁定者名称（后端返回的字段名） */
  lockedByName?: string
  /** 锁定时间（后端返回的字段名） */
  lockedTime?: string
  /** 锁定原因（后端返回的字段名） */
  lockReason?: string
  /** 是否私有（后端返回的字段名） */
  isPrivate?: number
  /** 编辑状态（后端返回的字段名） */
  editStatus?: number
  /** 当前编辑者ID（后端返回的字段名） */
  currentEditorId?: string
  /** 当前编辑者名称（后端返回的字段名） */
  currentEditorName?: string
  /** 编辑开始时间（后端返回的字段名） */
  editStartTime?: string
  /** 编辑者数量（后端返回的字段名） */
  editorCount?: number
  /** 下载次数（后端返回的字段名） */
  downloadCount?: number
  /** 查看次数（后端返回的字段名） */
  viewCount?: number
  /** 编辑次数（后端返回的字段名） */
  editCount?: number
  /** 分享次数（后端返回的字段名） */
  shareCount?: number
  /** 排序顺序（后端返回的字段名） */
  sortOrder?: number
  /** 备注（后端返回的字段名） */
  remark?: string
  /** 位置路径（后端返回的字段名） */
  locationPath?: string
  /** 子文件数量(仅文件夹) */
  childCount?: number
}

/**
 * 文件夹树节点
 */
export interface FolderTreeNode {
  /** 文件夹ID */
  id: number | string
  /** 文件夹名称 */
  name: string
  /** 父文件夹ID */
  parentId: number | string | null
  /** 子文件夹 */
  children?: FolderTreeNode[]
  /** 文件数量 */
  fileCount?: number
  /** 是否展开 */
  expanded?: boolean
}

/**
 * 文件上传项
 */
export interface UploadFileItem {
  /** 唯一ID */
  uid: string
  /** 文件名 */
  name: string
  /** 文件对象 */
  file: File
  /** 文件大小 */
  size: number
  /** 上传进度(0-100) */
  progress: number
  /** 上传状态 */
  status: 'waiting' | 'uploading' | 'success' | 'error'
  /** 错误信息 */
  error?: string
  /** 上传响应 */
  response?: any
}

/**
 * 创建文件DTO
 */
export interface CreateFileDto {
  /** 文件名 */
  fileName: string
  /** 文件类型 (docx/xlsx/pptx) */
  fileType: string
  /** 工作空间ID (可选,后端会自动创建默认工作空间) */
  workspaceId?: number | string
  /** 父文件夹ID */
  folderId?: number | string
}

/**
 * 创建文件夹DTO
 */
export interface CreateFolderDto {
  /** 文件夹名称 */
  name: string
  /** 父文件夹ID */
  parentId: number | string | null
  /** 工作空间ID */
  workspaceId: number | string
}

/**
 * 重命名文件DTO
 */
export interface RenameFileDto {
  /** 文件ID */
  id: number | string
  /** 新名称 */
  name: string
}

/**
 * 移动文件DTO
 */
export interface MoveFileDto {
  /** 文件ID列表 */
  fileIds: (number | string)[]
  /** 目标文件夹ID */
  targetFolderId: number | string | null
}

/**
 * 复制文件DTO
 */
export interface CopyFileDto {
  /** 文件ID列表 */
  fileIds: (number | string)[]
  /** 目标文件夹ID */
  targetFolderId: number | string | null
}

/**
 * 删除文件DTO
 */
export interface DeleteFileDto {
  /** 文件ID列表 */
  fileIds: (number | string)[]
  /** 是否永久删除 */
  permanent?: boolean
}

/**
 * 文件搜索参数
 */
export interface FileSearchParams {
  /** 搜索关键词 */
  keyword?: string
  /** 文件类型 */
  type?: FileType
  /** 工作空间ID */
  workspaceId?: number | string
  /** 父文件夹ID */
  parentId?: number | string | null
  /** 页码 */
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
  /** 是否已删除 (0-正常, 1-已删除) */
  isDeleted?: number
}

/**
 * 文件视图模式
 */
export type FileViewMode = 'list' | 'grid'

/**
 * 文件排序字段
 */
export type FileSortField = 'name' | 'size' | 'createTime' | 'updateTime'

/**
 * 文件排序方向
 */
export type FileSortOrder = 'asc' | 'desc'

/**
 * 文件类型图标映射
 */
export const FILE_TYPE_ICONS: Record<FileType, string> = {
  [FileType.FOLDER]: 'folder',
  [FileType.DOCUMENT]: 'file-word',
  [FileType.SPREADSHEET]: 'file-excel',
  [FileType.PRESENTATION]: 'file-ppt',
  [FileType.PDF]: 'file-pdf',
  [FileType.IMAGE]: 'file-image',
  [FileType.ARCHIVE]: 'file-zip',
  [FileType.OTHER]: 'file',
}

/**
 * 文件类型颜色映射
 */
export const FILE_TYPE_COLORS: Record<FileType, string> = {
  [FileType.FOLDER]: '#1890ff',
  [FileType.DOCUMENT]: '#2b579a',
  [FileType.SPREADSHEET]: '#217346',
  [FileType.PRESENTATION]: '#d24726',
  [FileType.PDF]: '#ff4d4f',
  [FileType.IMAGE]: '#52c41a',
  [FileType.ARCHIVE]: '#faad14',
  [FileType.OTHER]: '#8c8c8c',
}

/**
 * 文件扩展名到类型的映射
 */
export const FILE_EXTENSION_TYPE_MAP: Record<string, FileType> = {
  // 文档
  doc: FileType.DOCUMENT,
  docx: FileType.DOCUMENT,
  txt: FileType.DOCUMENT,
  wps: FileType.DOCUMENT, // WPS 文字
  // 电子表格
  xls: FileType.SPREADSHEET,
  xlsx: FileType.SPREADSHEET,
  csv: FileType.SPREADSHEET,
  et: FileType.SPREADSHEET, // WPS 表格
  ett: FileType.SPREADSHEET, // WPS 表格模板
  // 演示文稿
  ppt: FileType.PRESENTATION,
  pptx: FileType.PRESENTATION,
  dps: FileType.PRESENTATION, // WPS 演示
  dpt: FileType.PRESENTATION, // WPS 演示模板
  // PDF
  pdf: FileType.PDF,
  // 图片
  jpg: FileType.IMAGE,
  jpeg: FileType.IMAGE,
  png: FileType.IMAGE,
  gif: FileType.IMAGE,
  bmp: FileType.IMAGE,
  svg: FileType.IMAGE,
  // 压缩包
  zip: FileType.ARCHIVE,
  rar: FileType.ARCHIVE,
  '7z': FileType.ARCHIVE,
  tar: FileType.ARCHIVE,
  gz: FileType.ARCHIVE,
}

/**
 * 获取文件类型
 */
export function getFileType(extension: string): FileType {
  return FILE_EXTENSION_TYPE_MAP[extension.toLowerCase()] || FileType.OTHER
}

