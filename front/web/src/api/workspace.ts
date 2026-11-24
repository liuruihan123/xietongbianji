/**
 * 工作空间相关API接口
 */

import { get, post, put, del } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  Workspace,
  WorkspaceMember,
  CreateWorkspaceDto,
  UpdateWorkspaceDto,
  InviteMemberDto,
  UpdateMemberRoleDto,
} from '@/types/workspace'

/**
 * 获取用户的工作空间列表
 * 后端通过登录信息自动获取当前用户的工作空间
 */
export function getUserWorkspaces() {
  return get<Workspace[]>({
    url: '/coedit/workspace/my-workspaces',
  })
}

/**
 * 获取工作空间详情
 * @param id 工作空间ID
 */
export function getWorkspaceDetail(id: number) {
  return get<Workspace>({
    url: `/coedit/workspace/${id}`,
  })
}

/**
 * 创建工作空间
 * @param data 创建工作空间数据
 */
export function createWorkspace(data: CreateWorkspaceDto) {
  return post<Workspace>({
    url: '/coedit/workspace',
    data,
  })
}

/**
 * 更新工作空间
 * @param data 更新工作空间数据
 */
export function updateWorkspace(data: UpdateWorkspaceDto) {
  return put<Workspace>('/coedit/workspace', data)
}

/**
 * 删除工作空间
 * @param id 工作空间ID
 */
export function deleteWorkspace(id: number) {
  return del<void>(`/coedit/workspace/${id}`)
}

/**
 * 设置默认工作空间
 * @param id 工作空间ID
 * 后端通过登录信息自动获取当前用户
 */
export function setDefaultWorkspace(id: number) {
  return put<void>({
    url: `/coedit/workspace/set-default/${id}`,
  })
}

/**
 * 获取工作空间成员列表
 * @param workspaceId 工作空间ID
 */
export function getWorkspaceMembers(workspaceId: number) {
  return get<{ rows: WorkspaceMember[]; total: number }>({
    url: '/coedit/workspace/member/list',
    data: { workspaceId },
  })
}

/**
 * 邀请成员
 * @param data 邀请成员数据
 */
export function inviteMember(data: InviteMemberDto) {
  return post<WorkspaceMember>({
    url: `/coedit/workspace/member/invite?workspaceId=${data.workspaceId}&userId=${data.userId}&memberRole=${data.role}`,
  })
}

/**
 * 移除成员
 * @param workspaceId 工作空间ID
 * @param userId 用户ID
 */
export function removeMember(workspaceId: number, userId: number) {
  return del<void>({
    url: '/coedit/workspace/member/remove',
    params: { workspaceId, userId },
  })
}

/**
 * 更新成员角色
 * @param id 成员ID
 * @param memberRole 新角色
 */
export function updateMemberRole(id: number, memberRole: string) {
  return put<void>({
    url: '/coedit/workspace/member/role',
    params: { id, memberRole },
  })
}

/**
 * 搜索用户(用于邀请成员)
 * @param keyword 搜索关键词
 */
export function searchUsers(keyword: string) {
  return get<{ rows: any[]; total: number }>({
    url: '/system/user/search',
    data: { keyword },
  })
}

/**
 * 上传工作空间图标
 * @param file 图片文件
 */
export function uploadWorkspaceIcon(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return post<{ url: string }>({
    url: '/coedit/workspace/upload/icon',
    data: formData,
  })
}

/**
 * 获取工作空间统计数据
 * @param id 工作空间ID
 */
export function getWorkspaceStats(id: number) {
  return get<{
    fileCount: number
    folderCount: number
    memberCount: number
    usedStorage: number
    maxStorage: number
    recentFiles: any[]
    recentMembers: any[]
  }>({
    url: `/coedit/workspace/${id}/stats`,
  })
}

