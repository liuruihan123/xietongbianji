/**
 * 收藏相关类型定义
 */

/**
 * 收藏目标类型枚举
 */
export enum FavoriteTargetType {
  /** 工作空间 */
  WORKSPACE = 1,
  /** 文件夹 */
  FOLDER = 2,
  /** 文件 */
  FILE = 3,
}

/**
 * 收藏记录
 */
export interface Favorite {
  /** 收藏ID */
  id: number | string
  /** 目标类型: 1-工作空间, 2-文件夹, 3-文件 */
  targetType: FavoriteTargetType
  /** 目标ID (支持大整数,使用 string 避免精度丢失) */
  targetId: number | string
  /** 收藏时的名称(快照) */
  favoriteName: string
  /** 收藏时的路径(快照) */
  favoritePath: string
  /** 是否有效: 0-已失效, 1-有效 */
  isValid: 0 | 1
  /** 失效原因 */
  invalidReason?: string
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 目标信息(有效时有值,失效时为null) */
  targetInfo: any | null
}

/**
 * 添加收藏参数
 */
export interface AddFavoriteParams {
  /** 目标类型: 1-工作空间, 2-文件夹, 3-文件 */
  targetType: FavoriteTargetType
  /** 目标ID (支持大整数,使用 string 避免精度丢失) */
  targetId: number | string
  /** 工作空间ID (支持大整数,使用 string 避免精度丢失) */
  workspaceId: number | string
  /** 备注 */
  remark?: string
}

/**
 * 获取收藏列表参数
 */
export interface GetFavoriteListParams {
  /** 页码 */
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
}

/**
 * 检查是否已收藏参数
 */
export interface CheckFavoriteParams {
  /** 目标类型: 1-工作空间, 2-文件夹, 3-文件 */
  targetType: FavoriteTargetType
  /** 目标ID (支持大整数,使用 string 避免精度丢失) */
  targetId: number | string
}

/**
 * 收藏列表响应
 */
export interface FavoriteListResponse {
  /** 收藏列表 */
  rows: Favorite[]
  /** 总数 */
  total: number
}

/**
 * 检查收藏状态响应
 */
export interface CheckFavoriteResponse {
  /** 是否已收藏 */
  isFavorited: boolean
  /** 收藏ID (如果已收藏) */
  favoriteId?: number | string
}

