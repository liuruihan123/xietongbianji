/**
 * 收藏相关API接口
 */

import { get, post, del } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  Favorite,
  AddFavoriteParams,
  GetFavoriteListParams,
  CheckFavoriteParams,
  FavoriteListResponse,
  CheckFavoriteResponse,
} from '@/types/favorite'

/**
 * 获取收藏列表
 * @param params 查询参数
 * @returns 收藏列表
 */
export function getFavoriteList(params?: GetFavoriteListParams) {
  const finalParams = params || { pageNum: 1, pageSize: 50 }
  const ts = new Date().toISOString()
  console.log('[API] getFavoriteList call', { ts, url: '/coedit/favorite/list', params: finalParams, stack: (new Error().stack || '').split('\n').slice(2, 6) })
  return get<FavoriteListResponse>({
    url: '/coedit/favorite/list',
    data: finalParams,
  })
}

/**
 * 添加收藏
 * @param params 收藏参数
 */
export function addFavorite(params: AddFavoriteParams) {
  return post<void>({
    url: '/coedit/favorite',
    data: params,
  })
}

/**
 * 取消收藏
 * @param ids 收藏ID,多个用逗号分隔
 */
export function removeFavorite(ids: string | number) {
  return del<void>(`/coedit/favorite/${ids}`)
}



/**
 * 检查是否已收藏
 * @param params 检查参数
 * @returns 收藏状态信息
 */
export function checkFavorite(params: CheckFavoriteParams) {
  return get<CheckFavoriteResponse>({
    url: '/coedit/favorite/check',
    data: params,
  })
}

/**
 * 根据文件ID查询收藏状态
 * @param fileId 文件ID
 * @returns 收藏状态信息
 */
export function getFavoriteByFileId(fileId: number | string) {
  return get<Favorite | null>({
    url: `/coedit/favorite/file/${fileId}`,
  })
}

