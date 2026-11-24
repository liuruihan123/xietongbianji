/**
 * 文件夹管理相关API
 */
import { get, post, put, del } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'

export interface CreateFolderParams {
  workspaceId: number | string
  parentId?: number | string | null
  folderName: string
}

export interface ExistsParams {
  workspaceId: number | string
  parentId?: number | string | null
  folderName: string
}

export function createFolder(params: CreateFolderParams) {
  // 按后端要求使用 x-www-form-urlencoded
  const form = new URLSearchParams()
  form.append('workspaceId', String(params.workspaceId))
  if (params.parentId !== undefined && params.parentId !== null) {
    form.append('parentId', String(params.parentId))
  }
  form.append('folderName', params.folderName)

  return post<any>({
    url: '/coedit/folder/create',
    data: form,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })
}

export function renameFolder(id: number | string, newName: string) {
  // 文档：PUT /coedit/folder/{id}/rename?newName=xxx
  return put<any>(`/coedit/folder/${id}/rename?newName=${encodeURIComponent(newName)}`)
}

export function moveFolder(id: number | string, targetParentId: number | string | null) {
  const q = targetParentId == null ? '' : `?targetParentId=${encodeURIComponent(String(targetParentId))}`
  return put<any>(`/coedit/folder/${id}/move${q}`)
}

export function copyFolder(id: number | string, targetParentId?: number | string | null, newName?: string) {
  const params: Record<string, any> = {}
  if (targetParentId !== undefined && targetParentId !== null) params.targetParentId = targetParentId
  if (newName) params.newName = newName
  const usp = new URLSearchParams()
  Object.keys(params).forEach((k) => usp.append(k, String(params[k])))
  return post<any>({ url: `/coedit/folder/${id}/copy`, data: usp })
}

export function deleteFolders(ids: number | string | Array<number | string>) {
  const idStr = Array.isArray(ids) ? ids.join(',') : String(ids)
  return del<void>(`/coedit/folder/${idStr}`)
}

export function checkFolderExists(params: ExistsParams) {
  const qp: Record<string, any> = {
    workspaceId: params.workspaceId,
    folderName: params.folderName,
  }
  if (params.parentId !== undefined && params.parentId !== null) qp.parentId = params.parentId
  return get<boolean>({ url: '/coedit/folder/exists', data: qp })
}

export function hasChildren(id: number | string) {
  return get<boolean>({ url: `/coedit/folder/${id}/hasChildren` })
}

export function hasFiles(id: number | string) {
  return get<boolean>({ url: `/coedit/folder/${id}/hasFiles` })
}

