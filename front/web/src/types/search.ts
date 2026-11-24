/**
 * 搜索模块类型定义
 */

/**
 * 搜索类型枚举
 */
export enum SearchType {
  /** 全部 */
  ALL = 'ALL',
  /** 文件 */
  FILE = 'FILE',
  /** 文件夹 */
  FOLDER = 'FOLDER',
}

/**
 * 搜索范围枚举
 */
export enum SearchScope {
  /** 全部 */
  ALL = 'ALL',
  /** 当前工作空间 */
  CURRENT_WORKSPACE = 'CURRENT_WORKSPACE',
  /** 当前文件夹 */
  CURRENT_FOLDER = 'CURRENT_FOLDER',
}

/**
 * 排序方式枚举
 */
export enum SortBy {
  /** 相关性 */
  RELEVANCE = 'RELEVANCE',
  /** 创建时间 */
  CREATE_TIME = 'CREATE_TIME',
  /** 更新时间 */
  UPDATE_TIME = 'UPDATE_TIME',
  /** 文件大小 */
  SIZE = 'SIZE',
  /** 文件名 */
  NAME = 'NAME',
}

/**
 * 排序顺序枚举
 */
export enum SortOrder {
  /** 升序 */
  ASC = 'ASC',
  /** 降序 */
  DESC = 'DESC',
}

/**
 * 搜索结果项接口(匹配后端返回字段)
 */
export interface SearchResultItem {
  /** 文件ID */
  id: number
  /** 工作空间ID */
  workspaceId: number
  /** 文件夹ID */
  folderId: number
  /** 文件名 */
  fileName: string
  /** 文件类型 */
  fileType: string
  /** 文件大小(字节) */
  fileSize: number
  /** 文件路径 */
  filePath: string
  /** 文件扩展名 */
  fileExtension: string
  /** MIME类型 */
  mimeType: string
  /** 文档密钥 */
  documentKey: string
  /** 当前版本 */
  currentVersion: number
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 下载次数 */
  downloadCount: number
  /** 查看次数 */
  viewCount: number
  /** 编辑次数 */
  editCount: number
  /** 分享次数 */
  shareCount: number
  /** 最后编辑时间 */
  lastEditTime: string
  /** 最后编辑者ID */
  lastEditBy: number
  /** 最后编辑者名称 */
  lastEditByName: string
  /** 状态(0-已删除 1-正常 2-归档) */
  status: number
  /** 是否已删除 */
  isDeleted: boolean
  /** 文件URL */
  fileUrl: string
  /** 位置路径 */
  locationPath: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 备注 */
  remark?: string
  /** 匹配的关键词高亮(前端添加) */
  highlightName?: string
  /** 相关性得分(前端添加) */
  score?: number

  // 兼容旧字段(前端使用)
  /** @deprecated 使用 fileName */
  name?: string
  /** @deprecated 使用 fileType */
  type?: string
  /** @deprecated 使用 fileSize */
  size?: number
  /** @deprecated 使用 filePath */
  path?: string
  /** @deprecated 使用 folderId */
  parentId?: number
  /** @deprecated 使用 ownerId */
  creatorId?: number
  /** @deprecated 使用 ownerName */
  creatorName?: string
  /** 是否为文件夹(前端判断) */
  isFolder?: boolean
  /** 工作空间名称(前端添加) */
  workspaceName?: string
}

/**
 * 文件搜索请求参数(CoFileSearchBo)
 */
export interface CoFileSearchBo {
  /** 搜索关键词(必填,最大200字符) */
  keyword: string
  /** 工作空间ID */
  workspaceId?: number
  /** 文件夹ID */
  folderId?: number
  /** 文件类型(如 docx, xlsx, pdf) */
  fileType?: string
  /** 最小文件大小(字节) */
  minSize?: number
  /** 最大文件大小(字节) */
  maxSize?: number
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
  /** 创建者ID */
  creatorId?: number
  /** 是否包含子文件夹,默认true */
  includeSubFolders?: boolean
  /** 搜索类型(1-文件名 2-内容 3-高级搜索),默认1 */
  searchType?: 1 | 2 | 3
  /** 排序字段(name/size/createTime/updateTime),默认updateTime */
  sortField?: 'name' | 'size' | 'createTime' | 'updateTime'
  /** 排序方向(asc/desc),默认desc */
  sortOrder?: 'asc' | 'desc'
  /** 页码 */
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
}

/**
 * 快速搜索请求参数
 */
export interface QuickSearchParams {
  /** 关键词(必填) */
  keyword: string
  /** 工作空间ID */
  workspaceId?: number
}

/**
 * 搜索请求参数接口(保留向后兼容)
 * @deprecated 请使用 CoFileSearchBo
 */
export interface SearchRequest {
  /** 搜索关键词 */
  keyword: string
  /** 搜索类型 */
  type?: SearchType
  /** 搜索范围 */
  scope?: SearchScope
  /** 工作空间ID(当scope为CURRENT_WORKSPACE时) */
  workspaceId?: number
  /** 文件夹ID(当scope为CURRENT_FOLDER时) */
  folderId?: number
  /** 文件类型过滤 */
  fileTypes?: string[]
  /** 创建时间范围-开始 */
  createTimeStart?: string
  /** 创建时间范围-结束 */
  createTimeEnd?: string
  /** 更新时间范围-开始 */
  updateTimeStart?: string
  /** 更新时间范围-结束 */
  updateTimeEnd?: string
  /** 文件大小范围-最小(字节) */
  sizeMin?: number
  /** 文件大小范围-最大(字节) */
  sizeMax?: number
  /** 创建者ID */
  creatorId?: number
  /** 排序方式 */
  sortBy?: SortBy
  /** 排序顺序 */
  sortOrder?: SortOrder
  /** 页码 */
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
}

/**
 * 搜索响应接口
 */
export interface SearchResponse {
  /** 搜索结果列表 */
  rows: SearchResultItem[]
  /** 总数 */
  total: number
  /** 搜索耗时(毫秒) */
  duration?: number
}

/**
 * 搜索文件API响应接口(特殊格式,不包装在data中)
 * 后端返回格式: {code, msg, rows, total}
 */
export interface SearchFilesResponse {
  /** 响应码 */
  code: number
  /** 响应消息 */
  msg: string
  /** 搜索结果列表 */
  rows: SearchResultItem[]
  /** 总数 */
  total: number
}

/**
 * 搜索建议项接口
 */
export interface SearchSuggestion {
  /** 文件ID */
  id: number
  /** 文件名 */
  name: string
  /** 文件类型 */
  type: string
  /** 是否为文件夹 */
  isFolder: boolean
  /** 文件路径 */
  path: string
}

/**
 * 搜索历史记录接口
 */
export interface SearchHistory {
  /** 历史记录ID */
  id: string | number
  /** 搜索关键词 */
  keyword: string
  /** 搜索时间 */
  searchTime: string
  /** 搜索结果数量 */
  resultCount?: number
  /** 工作空间ID */
  workspaceId?: number
  /** 用户ID */
  userId?: number
}

/**
 * 热门关键词接口
 */
export interface HotKeyword {
  /** 关键词 */
  keyword: string
  /** 搜索次数 */
  count?: number
}

/**
 * 搜索统计接口
 */
export interface SearchStatistics {
  /** 总搜索次数 */
  totalSearches: number
  /** 总结果数 */
  totalResults: number
  /** 平均搜索时间(毫秒) */
  avgSearchTime?: number
  /** 热门关键词 */
  topKeywords?: Array<{ keyword: string; count: number }>
}

/**
 * 高级搜索参数接口
 */
export interface AdvancedSearchParams {
  /** 文件类型 */
  fileTypes: string[]
  /** 创建时间范围 */
  createTimeRange?: [string, string]
  /** 更新时间范围 */
  updateTimeRange?: [string, string]
  /** 文件大小范围(MB) */
  sizeRange?: [number, number]
  /** 创建者ID */
  creatorId?: number
  /** 工作空间ID */
  workspaceId?: number
  /** 文件夹ID */
  folderId?: number
}

/**
 * 搜索类型选项
 */
export const SEARCH_TYPE_OPTIONS = [
  { label: '全部', value: SearchType.ALL },
  { label: '文件', value: SearchType.FILE },
  { label: '文件夹', value: SearchType.FOLDER },
]

/**
 * 搜索范围选项
 */
export const SEARCH_SCOPE_OPTIONS = [
  { label: '全部', value: SearchScope.ALL },
  { label: '当前工作空间', value: SearchScope.CURRENT_WORKSPACE },
  { label: '当前文件夹', value: SearchScope.CURRENT_FOLDER },
]

/**
 * 排序方式选项
 */
export const SORT_BY_OPTIONS = [
  { label: '相关性', value: SortBy.RELEVANCE },
  { label: '创建时间', value: SortBy.CREATE_TIME },
  { label: '更新时间', value: SortBy.UPDATE_TIME },
  { label: '文件大小', value: SortBy.SIZE },
  { label: '文件名', value: SortBy.NAME },
]

/**
 * 排序顺序选项
 */
export const SORT_ORDER_OPTIONS = [
  { label: '升序', value: SortOrder.ASC },
  { label: '降序', value: SortOrder.DESC },
]

/**
 * 文件类型选项
 */
export const FILE_TYPE_OPTIONS = [
  { label: 'Word文档', value: 'docx,doc' },
  { label: 'Excel表格', value: 'xlsx,xls' },
  { label: 'PPT演示', value: 'pptx,ppt' },
  { label: 'PDF文档', value: 'pdf' },
  { label: '图片', value: 'jpg,jpeg,png,gif,bmp' },
  { label: '压缩包', value: 'zip,rar,7z' },
  { label: '文本文件', value: 'txt,md' },
]

/**
 * 高亮搜索关键词
 * @param text 原始文本
 * @param keyword 关键词
 * @returns 高亮后的HTML字符串
 */
export function highlightKeyword(text: string, keyword: string): string {
  if (!keyword) return text

  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}

