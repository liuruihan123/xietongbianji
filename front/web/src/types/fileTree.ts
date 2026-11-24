/**
 * 文件树相关类型定义
 */

/**
 * 节点类型
 */
export type NodeType = 'folder' | 'file'

/**
 * 工作空间类型
 */
export interface Workspace {
  /** 工作空间ID */
  id: number
  /** 工作空间名称 */
  workspaceName: string
  /** 工作空间类型: 1-个人, 2-团队, 3-企业 */
  workspaceType: number
  /** 工作空间图标 */
  workspaceIcon?: string
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 工作空间描述 */
  workspaceDesc?: string
  /** 最大存储空间(字节) */
  maxStorage: number
  /** 已使用存储空间(字节) */
  usedStorage: number
  /** 当前成员数量 */
  memberCount?: number
  /** 工作空间内容数量(文件/文档数) */
  contentCount?: number
  /** 是否默认工作空间: 0-否, 1-是 */
  isDefault: number
  /** 状态: 0-正常, 1-已归档, 2-已删除 */
  status: number
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 工作空间信息(用于文件树节点中的简化信息)
 */
export interface WorkspaceInfo {
  /** 工作空间ID */
  id: number
  /** 工作空间名称 */
  workspaceName: string
  /** 工作空间类型 */
  workspaceType: string
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 文件夹信息
 */
export interface FolderInfo {
  /** 文件夹ID */
  id: number
  /** 文件夹名称 */
  folderName: string
  /** 文件夹路径 */
  folderPath: string
  /** 所属工作空间ID */
  workspaceId: number
  /** 父文件夹ID */
  parentId: number | null
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 文件信息
 */
export interface FileInfo {
  /** 文件ID */
  id: number
  /** 文件名 */
  fileName: string
  /** 文件类型 */
  fileType: string
  /** 文件扩展名(如 "docx") */
  fileExtension: string
  /** 文件大小(字节) */
  fileSize: number
  /** 文件路径 */
  filePath: string
  /** 所属工作空间ID */
  workspaceId: number
  /** 所属文件夹ID */
  folderId: number | null
  /** MIME类型 */
  mimeType: string
  /** 所有者ID */
  ownerId: number
  /** 所有者名称 */
  ownerName: string
  /** 所有者昵称 */
  ownerNickName?: string
  /** 文件位置路径(如:"工作空间名称/文件夹1/文件夹2") */
  locationPath: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 文件树节点
 */
export interface FileTreeNode {
  /** 节点类型: folder-文件夹, file-文件 */
  nodeType: NodeType
  /** 节点ID */
  id: number
  /** 节点名称 */
  name: string
  /** 父节点ID(null表示根级节点) */
  parentId: number | null
  /** 节点路径 */
  path: string
  /** 所属工作空间ID */
  workspaceId: number
  /** 所属工作空间名称 */
  workspaceName: string
  /** 是否已收藏 */
  isFavorited: boolean
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string

  // 文件夹节点特有属性
  /** 文件夹信息 */
  folderInfo?: FolderInfo
  /** 是否有子节点(用于判断是否显示展开图标) */
  hasChildren?: boolean
  /** 直接子文件数 */
  fileCount?: number
  /** 直接子文件夹数 */
  folderCount?: number
  /** 子节点(懒加载模式下为空数组) */
  children?: FileTreeNode[]

  // 文件节点特有属性
  /** 文件信息 */
  fileInfo?: FileInfo
  /** 最后访问时间(如果用户从未访问过则为null) */
  lastAccessTime?: string | null
}

/**
 * 获取文件树参数
 */
export interface GetFileTreeParams {
  /** 工作空间ID(为null时返回所有可访问的工作空间及其一级文件树) */
  workspaceId?: number | null
  /** 父文件夹ID(为null时返回根级文件和文件夹,仅在workspaceId有值时生效) */
  parentId?: number | null
  /** 工作空间类型筛选: 1-个人空间, 2-部门空间, 3-团队空间 (仅在workspaceId为null时生效) */
  workspaceType?: number | null
  /** 所有权类型筛选: 1-我创建的, 2-我加入的 (仅在workspaceId为null时生效) */
  ownershipType?: number | null
  /** 页码(仅在workspaceId为null时生效) */
  pageNum?: number
  /** 每页大小(仅在workspaceId为null时生效) */
  pageSize?: number
}

/**
 * 文件树响应数据
 */
export interface FileTreeResponse {
  /** 工作空间列表(workspaceId为null时有数据,有值时为空数组) */
  workspaces: Workspace[]
  /** 文件树节点列表 */
  fileTree: FileTreeNode[]
  /** 总记录数(仅全局模式有值) */
  total?: number
  /** 当前页码(仅全局模式有值) */
  pageNum?: number
  /** 每页大小(仅全局模式有值) */
  pageSize?: number
  /** 总页数(仅全局模式有值) */
  pages?: number
}

