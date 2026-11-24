/**
 * 协同编辑相关类型定义
 */

/**
 * 在线用户状态
 */
export enum UserStatus {
  /** 在线 */
  ONLINE = 'ONLINE',
  /** 离线 */
  OFFLINE = 'OFFLINE',
  /** 空闲 */
  IDLE = 'IDLE',
  /** 忙碌 */
  BUSY = 'BUSY',
}

/**
 * 在线用户信息
 */
export interface OnlineUser {
  /** 用户ID */
  userId: number
  /** 用户名 */
  userName: string
  /** 头像 */
  avatar?: string
  /** 状态 */
  status: UserStatus
  /** 当前光标位置 */
  cursorPosition?: CursorPosition
  /** 当前选中范围 */
  selection?: SelectionRange
  /** 加入时间 */
  joinTime: string
  /** 最后活动时间 */
  lastActiveTime: string
  /** 颜色(用于光标和高亮) */
  color?: string
}

/**
 * 光标位置
 */
export interface CursorPosition {
  /** 行号 */
  line: number
  /** 列号 */
  column: number
  /** 文档ID */
  documentId?: string
}

/**
 * 选中范围
 */
export interface SelectionRange {
  /** 起始位置 */
  start: CursorPosition
  /** 结束位置 */
  end: CursorPosition
}

/**
 * 协同消息类型
 */
export enum CollaborationMessageType {
  /** 用户加入 */
  USER_JOIN = 'USER_JOIN',
  /** 用户离开 */
  USER_LEAVE = 'USER_LEAVE',
  /** 用户状态更新 */
  USER_STATUS_UPDATE = 'USER_STATUS_UPDATE',
  /** 光标位置更新 */
  CURSOR_UPDATE = 'CURSOR_UPDATE',
  /** 选中范围更新 */
  SELECTION_UPDATE = 'SELECTION_UPDATE',
  /** 文档内容更新 */
  CONTENT_UPDATE = 'CONTENT_UPDATE',
  /** 文档锁定 */
  DOCUMENT_LOCK = 'DOCUMENT_LOCK',
  /** 文档解锁 */
  DOCUMENT_UNLOCK = 'DOCUMENT_UNLOCK',
  /** 聊天消息 */
  CHAT_MESSAGE = 'CHAT_MESSAGE',
  /** 系统通知 */
  SYSTEM_NOTIFICATION = 'SYSTEM_NOTIFICATION',
}

/**
 * 协同消息
 */
export interface CollaborationMessage {
  /** 消息类型 */
  type: CollaborationMessageType
  /** 文档ID */
  documentId: number
  /** 发送者用户ID */
  userId: number
  /** 发送者用户名 */
  userName: string
  /** 消息内容 */
  payload: any
  /** 时间戳 */
  timestamp: string
}

/**
 * 文档锁定信息
 */
export interface DocumentLock {
  /** 文档ID */
  documentId: number
  /** 锁定者用户ID */
  userId: number
  /** 锁定者用户名 */
  userName: string
  /** 锁定时间 */
  lockTime: string
  /** 锁定原因 */
  reason?: string
}

/**
 * 聊天消息
 */
export interface ChatMessage {
  /** 消息ID */
  id: string
  /** 发送者用户ID */
  userId: number
  /** 发送者用户名 */
  userName: string
  /** 发送者头像 */
  avatar?: string
  /** 消息内容 */
  content: string
  /** 发送时间 */
  sendTime: string
  /** 是否为系统消息 */
  isSystem?: boolean
}

/**
 * 文档操作类型
 */
export enum DocumentOperationType {
  /** 插入 */
  INSERT = 'INSERT',
  /** 删除 */
  DELETE = 'DELETE',
  /** 替换 */
  REPLACE = 'REPLACE',
  /** 格式化 */
  FORMAT = 'FORMAT',
}

/**
 * 文档操作
 */
export interface DocumentOperation {
  /** 操作类型 */
  type: DocumentOperationType
  /** 操作位置 */
  position: CursorPosition
  /** 操作内容 */
  content?: string
  /** 操作长度 */
  length?: number
  /** 操作者用户ID */
  userId: number
  /** 时间戳 */
  timestamp: string
}

/**
 * 协同会话信息
 */
export interface CollaborationSession {
  /** 会话ID */
  sessionId: string
  /** 文档ID */
  documentId: number
  /** 在线用户列表 */
  onlineUsers: OnlineUser[]
  /** 创建时间 */
  createTime: string
  /** 最后活动时间 */
  lastActiveTime: string
}

/**
 * 用户颜色映射
 */
export const USER_COLORS = [
  '#1890ff', // 蓝色
  '#52c41a', // 绿色
  '#faad14', // 橙色
  '#f5222d', // 红色
  '#722ed1', // 紫色
  '#13c2c2', // 青色
  '#eb2f96', // 粉色
  '#fa8c16', // 橘色
]

/**
 * 获取用户颜色
 */
export function getUserColor(userId: number): string {
  return USER_COLORS[userId % USER_COLORS.length]
}

