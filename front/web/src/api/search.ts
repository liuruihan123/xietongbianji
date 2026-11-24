/**
 * 搜索模块API
 */

import { get, post, del } from '@/utils/request'
import type {
  CoFileSearchBo,
  QuickSearchParams,
  SearchRequest,
  SearchResponse,
  SearchFilesResponse,
  SearchSuggestion,
  SearchHistory,
  HotKeyword,
  SearchStatistics,
  SearchResultItem,
} from '@/types/search'

/**
 * 搜索文件(基础文件搜索功能)
 * @param params 搜索参数
 * @returns 分页的文件列表 {code, msg, rows, total}
 */
export function searchFiles(params: CoFileSearchBo): Promise<SearchFilesResponse> {
  return post({
    url: '/coedit/search/files',
    data: params,
  }) as unknown as Promise<SearchFilesResponse>
}

/**
 * 快速搜索文件
 * @param params 快速搜索参数
 * @returns 文件列表(非分页) {code, msg, data: [...]}
 */
export function quickSearch(params: QuickSearchParams) {
  return get<SearchResultItem[]>({
    url: '/coedit/search/quick',
    data: params,
  })
}

/**
 * 高级搜索
 * @param params 搜索参数
 * @returns 分页的文件列表 {code, msg, rows, total}
 */
export function advancedSearch(params: CoFileSearchBo): Promise<SearchFilesResponse> {
  return post({
    url: '/coedit/search/advanced',
    data: params,
  }) as unknown as Promise<SearchFilesResponse>
}

/**
 * 执行搜索(保留向后兼容)
 * @deprecated 请使用 searchFiles
 * @param params 搜索参数
 * @returns 搜索结果
 */
export function search(params: SearchRequest) {
  // 转换为新的参数格式
  const newParams: CoFileSearchBo = {
    keyword: params.keyword,
    workspaceId: params.workspaceId,
    folderId: params.folderId,
    fileType: params.fileTypes?.join(','),
    minSize: params.sizeMin,
    maxSize: params.sizeMax,
    startTime: params.createTimeStart,
    endTime: params.createTimeEnd,
    creatorId: params.creatorId,
    pageNum: params.pageNum,
    pageSize: params.pageSize,
  }
  return searchFiles(newParams)
}

/**
 * 获取搜索建议
 * @param keyword 关键词前缀
 * @param workspaceId 工作空间ID
 * @param limit 返回数量限制,默认5
 * @returns 搜索建议列表
 */
export function getSearchSuggestions(
  keyword: string,
  workspaceId?: number,
  limit = 5
) {
  return get<SearchSuggestion[]>({
    url: '/coedit/search/suggestions',
    data: { keyword, workspaceId, limit },
  })
}

/**
 * 获取搜索历史
 * @param limit 数量限制,默认10
 * @returns 搜索历史列表
 */
export function getSearchHistory(limit = 10) {
  return get<SearchHistory[]>({
    url: '/coedit/search/history',
    data: { limit },
  })
}

/**
 * 删除单条搜索历史
 * @param historyId 历史记录ID
 * @returns 操作结果
 */
export function deleteSearchHistory(historyId: string | number) {
  return del({
    url: `/coedit/search/history/${historyId}`,
  })
}

/**
 * 清空搜索历史
 * @returns 操作结果
 */
export function clearSearchHistory() {
  return del({
    url: '/coedit/search/history/clear',
  })
}

/**
 * 获取热门搜索关键词
 * @param workspaceId 工作空间ID
 * @param limit 数量限制,默认10
 * @returns 热门关键词列表
 */
export function getHotSearches(workspaceId?: number, limit = 10) {
  return get<HotKeyword[]>({
    url: '/coedit/search/hot',
    data: { workspaceId, limit },
  })
}

/**
 * 获取搜索统计
 * @returns 搜索统计信息
 */
export function getSearchStatistics() {
  return get<SearchStatistics>({
    url: '/coedit/search/statistics',
  })
}

