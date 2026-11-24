/**
 * 文件树相关API接口
 */

import { get } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type { FileTreeResponse, GetFileTreeParams } from '@/types/fileTree'

/**
 * 获取文件树
 *
 * 使用场景:
 * 1. 不传workspaceId: 返回所有可访问的工作空间及其一级文件树
 * 2. 传workspaceId但不传parentId: 返回指定工作空间的根级文件树
 * 3. 同时传workspaceId和parentId: 懒加载指定文件夹下的子文件和子文件夹
 *
 * @param params 查询参数
 * @returns 文件树响应数据(包含工作空间列表和文件树节点列表)
 */
export function getFileTree(params?: GetFileTreeParams) {
  const queryParams: Record<string, any> = {}

  // 只有当参数有值时才添加到查询参数中
  if (params?.workspaceId !== undefined && params.workspaceId !== null) {
    queryParams.workspaceId = params.workspaceId
  }

  if (params?.parentId !== undefined && params.parentId !== null) {
    queryParams.parentId = params.parentId
  }

  // 筛选参数(仅在workspaceId为null时生效)
  if (params?.workspaceType !== undefined && params.workspaceType !== null) {
    queryParams.workspaceType = params.workspaceType
  }

  if (params?.ownershipType !== undefined && params.ownershipType !== null) {
    queryParams.ownershipType = params.ownershipType
  }

  // 分页参数(仅在workspaceId为null时生效)
  if (params?.pageNum !== undefined && params.pageNum !== null) {
    queryParams.pageNum = params.pageNum
  }

  if (params?.pageSize !== undefined && params.pageSize !== null) {
    queryParams.pageSize = params.pageSize
  }

  return get<FileTreeResponse>({
    url: '/coedit/file-tree/workspace',
    data: queryParams,
  })
}

