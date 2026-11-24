/**
 * 工作空间相关类型定义
 */

/**
 * 工作空间类型枚举
 */
export enum WorkspaceType {
  /** 个人空间 */
  PERSONAL = 1,
  /** 部门空间 */
  DEPARTMENT = 2,
  /** 团队空间 */
  TEAM = 3,
}

/**
 * 工作空间类型选项
 */
export const WORKSPACE_TYPE_OPTIONS = [
  { label: '个人空间', value: WorkspaceType.PERSONAL },
  { label: '部门空间', value: WorkspaceType.DEPARTMENT },
  { label: '团队空间', value: WorkspaceType.TEAM },
] as const

/**
 * 工作空间状态枚举
 */
export enum WorkspaceStatus {
  /** 禁用 */
  DISABLED = 0,
  /** 正常 */
  NORMAL = 1,
  /** 归档 */
  ARCHIVED = 2,
}

/**
 * 工作空间接口
 */
export interface Workspace {
  /** 工作空间ID */
  id: number
  /** 工作空间名称 */
  workspaceName: string
  /** 工作空间类型：1-个人空间 2-部门空间 3-团队空间 */
  workspaceType: WorkspaceType
  /** 工作空间描述 */
  workspaceDesc?: string
  /** 工作空间图标 */
  workspaceIcon?: string
  /** 工作空间颜色标识 */
  workspaceColor?: string
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName?: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 部门ID（部门空间时使用） */
  deptId?: number
  /** 部门名称 */
  deptName?: string
  /** 最大存储空间(字节) */
  maxStorage: number
  /** 已用存储空间(字节) */
  usedStorage: number
  /** 最大成员数量 */
  maxMembers?: number
  /** 当前成员数量 */
  memberCount: number
  /** 工作空间内容数量(文件/文档数) */
  contentCount?: number
  /** 默认权限：view-查看 edit-编辑 admin-管理 */
  defaultPermission?: string
  /** 是否允许成员邀请：0-否 1-是 */
  allowInvite?: boolean
  /** 是否允许外部分享：0-否 1-是 */
  allowShare?: boolean
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: boolean
  /** 状态：0-禁用 1-正常 2-归档 */
  status: WorkspaceStatus
  /** 是否为默认工作空间 */
  isDefault?: boolean
  /** 排序顺序 */
  sortOrder?: number
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 工作空间成员接口
 */
export interface WorkspaceMember {
  /** 成员ID */
  id: number
  /** 工作空间ID */
  workspaceId: number
  /** 用户ID */
  userId: number
  /** 用户名 */
  userName: string
  /** 用户昵称 */
  nickName?: string
  /** 用户邮箱 */
  email?: string
  /** 用户头像 */
  avatar?: string
  /** 角色: owner-所有者 admin-管理员 editor-编辑 viewer-查看 member-成员 */
  role: 'owner' | 'admin' | 'editor' | 'viewer' | 'member'
  /** 加入时间 */
  joinTime: string
  /** 最后活跃时间 */
  lastActiveTime?: string
}

/**
 * 创建工作空间DTO
 */
export interface CreateWorkspaceDto {
  /** 工作空间名称（必填） */
  workspaceName: string
  /** 工作空间类型：1-个人空间 2-部门空间 3-团队空间（必填） */
  workspaceType: WorkspaceType
  /** 工作空间描述 */
  workspaceDesc?: string
  /** 工作空间图标 */
  workspaceIcon?: string
  /** 工作空间颜色标识 */
  workspaceColor?: string
  /** 部门ID（部门空间时使用） */
  deptId?: number
  /** 最大存储空间（字节） */
  maxStorage?: number
  /** 最大成员数量 */
  maxMembers?: number
  /** 默认权限：view-查看 edit-编辑 admin-管理 */
  defaultPermission?: string
  /** 是否允许成员邀请：0-否 1-是 */
  allowInvite?: boolean
  /** 是否允许外部分享：0-否 1-是 */
  allowShare?: boolean
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: boolean
  /** 排序顺序 */
  sortOrder?: number
}

/**
 * 更新工作空间DTO
 */
export interface UpdateWorkspaceDto {
  /** 工作空间ID（必填） */
  id: number
  /** 工作空间名称 */
  workspaceName?: string
  /** 工作空间类型：1-个人空间 2-部门空间 3-团队空间 */
  workspaceType?: WorkspaceType
  /** 工作空间描述 */
  workspaceDesc?: string
  /** 工作空间图标 */
  workspaceIcon?: string
  /** 工作空间颜色标识 */
  workspaceColor?: string
  /** 最大存储空间（字节） */
  maxStorage?: number
  /** 最大成员数量 */
  maxMembers?: number
  /** 默认权限：view-查看 edit-编辑 admin-管理 */
  defaultPermission?: string
  /** 是否允许成员邀请：0-否 1-是 */
  allowInvite?: boolean
  /** 是否允许外部分享：0-否 1-是 */
  allowShare?: boolean
  /** 是否允许下载：0-否 1-是 */
  allowDownload?: boolean
  /** 排序顺序 */
  sortOrder?: number
}

/**
 * 邀请成员DTO
 */
export interface InviteMemberDto {
  /** 工作空间ID */
  workspaceId: number
  /** 用户ID */
  userId: number
  /** 角色 */
  role: 'admin' | 'editor' | 'viewer' | 'member'
}

/**
 * 更新成员角色DTO
 */
export interface UpdateMemberRoleDto {
  /** 工作空间ID */
  workspaceId: number
  /** 用户ID */
  userId: number
  /** 新角色 */
  role: 'owner' | 'admin' | 'editor' | 'viewer' | 'member'
}

/**
 * 存储配额选项
 */
export const STORAGE_QUOTA_OPTIONS = [
  { label: '1GB', value: 1073741824 },      // 1GB
  { label: '5GB', value: 5368709120 },      // 5GB
  { label: '10GB', value: 10737418240 },    // 10GB
  { label: '50GB', value: 53687091200 },    // 50GB
  { label: '100GB', value: 107374182400 },  // 100GB
] as const

/**
 * 角色选项
 */
export const ROLE_OPTIONS = [
  { label: '管理员', value: 'admin', color: 'red' },
  { label: '编辑者', value: 'editor', color: 'green' },
  { label: '查看者', value: 'viewer', color: 'orange' },
  { label: '成员', value: 'member', color: 'blue' },
] as const

/**
 * 角色权限映射
 */
export const ROLE_PERMISSIONS = {
  owner: {
    canDelete: true,
    canInvite: true,
    canRemove: true,
    canEdit: true,
    canManageMembers: true,
  },
  admin: {
    canDelete: false,
    canInvite: true,
    canRemove: true,
    canEdit: true,
    canManageMembers: true,
  },
  editor: {
    canDelete: false,
    canInvite: false,
    canRemove: false,
    canEdit: true,
    canManageMembers: false,
  },
  viewer: {
    canDelete: false,
    canInvite: false,
    canRemove: false,
    canEdit: false,
    canManageMembers: false,
  },
  member: {
    canDelete: false,
    canInvite: false,
    canRemove: false,
    canEdit: true,
    canManageMembers: false,
  },
} as const

