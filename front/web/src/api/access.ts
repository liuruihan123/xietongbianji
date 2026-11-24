/**
 * 访问记录相关API接口
 */

import { get, post } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  AccessRecord,
  RecordWorkspaceAccessParams,
  RecordFolderAccessParams,
  RecordFileAccessParams,
  GetRecentAccessListParams,
} from '@/types/access'

/**
 * 获取最近访问列表
 * @param params 查询参数
 * @returns 最近访问记录列表
 */
export function getRecentAccessList(params?: GetRecentAccessListParams) {
  const finalParams = params || { limit: 50 }
  const ts = new Date().toISOString()
  console.log('[API] getRecentAccessList call', { ts, url: '/coedit/access-record/recent/list', params: finalParams, stack: (new Error().stack || '').split('\n').slice(2, 6) })
  return get<AccessRecord[]>({
    url: '/coedit/access-record/recent/list',
    data: finalParams,
  })
}

/**
 * 记录工作空间访问
 * @param params 工作空间访问参数
 */
export function recordWorkspaceAccess(params: RecordWorkspaceAccessParams) {
  return post<void>({
    url: '/coedit/access-record/workspace',
    data: params,
  })
}

/**
 * 记录文件夹访问
 * @param params 文件夹访问参数
 */
export function recordFolderAccess(params: RecordFolderAccessParams) {
  return post<void>({
    url: '/coedit/access-record/folder',
    data: params,
  })
}

/**
 * 记录文件访问
 * @param params 文件访问参数
 */
export function recordFileAccess(params: RecordFileAccessParams) {
  return post<void>({
    url: '/coedit/access-record/file',
    data: params,
  })
}

