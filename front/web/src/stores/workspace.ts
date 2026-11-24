/**
 * 工作空间状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as workspaceApi from '@/api/workspace'
import type { Workspace, WorkspaceMember, CreateWorkspaceDto, UpdateWorkspaceDto } from '@/types/workspace'

export const useWorkspaceStore = defineStore('workspace', () => {
  // ========== 状态 ==========
  const workspaces = ref<Workspace[]>([])
  const currentWorkspace = ref<Workspace | null>(null)
  const members = ref<WorkspaceMember[]>([])
  const loading = ref(false)
  const viewMode = ref<'card' | 'list'>('card')

  // ========== 计算属性 ==========
  
  /**
   * 当前工作空间ID
   */
  const currentWorkspaceId = computed(() => currentWorkspace.value?.id)

  /**
   * 存储使用百分比
   */
  const storageUsagePercent = computed(() => {
    if (!currentWorkspace.value) return 0
    const { usedStorage, maxStorage } = currentWorkspace.value
    return Math.round((usedStorage / maxStorage) * 100)
  })

  /**
   * 是否为所有者
   */
  const isOwner = computed(() => {
    if (!currentWorkspace.value) return false
    const userInfo = JSON.parse(localStorage.getItem('USER_INFO') || '{}')
    return currentWorkspace.value.ownerId === userInfo.userId
  })

  /**
   * 是否为管理员(包括所有者)
   */
  const isAdmin = computed(() => {
    if (!currentWorkspace.value) return false
    const userInfo = JSON.parse(localStorage.getItem('USER_INFO') || '{}')
    const member = members.value.find(m => m.userId === userInfo.userId)
    return member?.role === 'owner' || member?.role === 'admin'
  })

  /**
   * 当前用户角色
   */
  const currentUserRole = computed(() => {
    if (!currentWorkspace.value) return null
    const userInfo = JSON.parse(localStorage.getItem('USER_INFO') || '{}')
    const member = members.value.find(m => m.userId === userInfo.userId)
    return member?.role || null
  })

  // ========== 操作方法 ==========

  /**
   * 获取工作空间列表
   * 后端通过登录信息自动获取当前用户的工作空间
   */
  async function fetchWorkspaces() {
    loading.value = true
    try {
      const response = await workspaceApi.getUserWorkspaces()
      workspaces.value = response.data || []
      return workspaces.value
    } catch (error) {
      console.error('获取工作空间列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取工作空间详情
   */
  async function fetchWorkspaceDetail(id: number) {
    loading.value = true
    try {
      const response = await workspaceApi.getWorkspaceDetail(id)
      currentWorkspace.value = response.data
      return currentWorkspace.value
    } catch (error) {
      console.error('获取工作空间详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建工作空间
   */
  async function createWorkspace(data: CreateWorkspaceDto) {
    try {
      const response = await workspaceApi.createWorkspace(data)
      const workspace = response.data
      workspaces.value.unshift(workspace)
      return workspace
    } catch (error) {
      console.error('创建工作空间失败:', error)
      throw error
    }
  }

  /**
   * 更新工作空间
   */
  async function updateWorkspace(data: UpdateWorkspaceDto) {
    try {
      await workspaceApi.updateWorkspace(data)
      // 更新本地数据
      const index = workspaces.value.findIndex(w => w.id === data.id)
      if (index !== -1) {
        workspaces.value[index] = { ...workspaces.value[index], ...data }
      }
      if (currentWorkspace.value?.id === data.id) {
        currentWorkspace.value = { ...currentWorkspace.value, ...data }
      }
    } catch (error) {
      console.error('更新工作空间失败:', error)
      throw error
    }
  }

  /**
   * 删除工作空间
   */
  async function deleteWorkspace(id: number) {
    try {
      await workspaceApi.deleteWorkspace(id)
      workspaces.value = workspaces.value.filter(w => w.id !== id)
      if (currentWorkspace.value?.id === id) {
        currentWorkspace.value = null
      }
    } catch (error) {
      console.error('删除工作空间失败:', error)
      throw error
    }
  }

  /**
   * 设置默认工作空间
   * 后端通过登录信息自动获取当前用户
   */
  async function setDefaultWorkspace(id: number) {
    try {
      await workspaceApi.setDefaultWorkspace(id)
      // 更新本地数据
      workspaces.value.forEach(w => {
        w.isDefault = w.id === id
      })
    } catch (error) {
      console.error('设置默认工作空间失败:', error)
      throw error
    }
  }

  /**
   * 获取成员列表
   */
  async function fetchMembers(workspaceId: number) {
    try {
      const response = await workspaceApi.getWorkspaceMembers(workspaceId)
      members.value = response.data?.rows || []
      return members.value
    } catch (error) {
      console.error('获取成员列表失败:', error)
      throw error
    }
  }

  /**
   * 邀请成员
   */
  async function inviteMember(workspaceId: number, userId: number, role: string) {
    try {
      const response = await workspaceApi.inviteMember({ workspaceId, userId, role: role as any })
      const member = response.data
      members.value.push(member)
      // 更新成员数量
      if (currentWorkspace.value) {
        currentWorkspace.value.memberCount++
      }
      return member
    } catch (error) {
      console.error('邀请成员失败:', error)
      throw error
    }
  }

  /**
   * 移除成员
   */
  async function removeMember(workspaceId: number, userId: number) {
    try {
      await workspaceApi.removeMember(workspaceId, userId)
      members.value = members.value.filter(m => m.userId !== userId)
      // 更新成员数量
      if (currentWorkspace.value) {
        currentWorkspace.value.memberCount--
      }
    } catch (error) {
      console.error('移除成员失败:', error)
      throw error
    }
  }

  /**
   * 更新成员角色
   */
  async function updateMemberRole(workspaceId: number, userId: number, role: string) {
    try {
      await workspaceApi.updateMemberRole(userId as any, role)
      // 更新本地数据
      const member = members.value.find(m => m.userId === userId)
      if (member) {
        member.role = role as any
      }
    } catch (error) {
      console.error('更新成员角色失败:', error)
      throw error
    }
  }

  /**
   * 切换视图模式
   */
  function toggleViewMode() {
    viewMode.value = viewMode.value === 'card' ? 'list' : 'card'
    localStorage.setItem('workspaceViewMode', viewMode.value)
  }

  /**
   * 重置状态
   */
  function reset() {
    workspaces.value = []
    currentWorkspace.value = null
    members.value = []
    loading.value = false
  }

  // 初始化视图模式
  const savedViewMode = localStorage.getItem('workspaceViewMode')
  if (savedViewMode === 'card' || savedViewMode === 'list') {
    viewMode.value = savedViewMode
  }

  return {
    // 状态
    workspaces,
    currentWorkspace,
    members,
    loading,
    viewMode,
    // 计算属性
    currentWorkspaceId,
    storageUsagePercent,
    isOwner,
    isAdmin,
    currentUserRole,
    // 方法
    fetchWorkspaces,
    fetchWorkspaceDetail,
    createWorkspace,
    updateWorkspace,
    deleteWorkspace,
    setDefaultWorkspace,
    fetchMembers,
    inviteMember,
    removeMember,
    updateMemberRole,
    toggleViewMode,
    reset,
  }
})

