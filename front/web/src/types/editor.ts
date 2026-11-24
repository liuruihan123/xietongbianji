/**
 * OnlyOffice编辑器相关类型定义
 */

/**
 * 文档类型
 */
export enum DocumentType {
  /** 文字处理 */
  TEXT = 'text',
  /** 电子表格 */
  SPREADSHEET = 'spreadsheet',
  /** 演示文稿 */
  PRESENTATION = 'presentation',
}

/**
 * 编辑器模式
 */
export enum EditorMode {
  /** 编辑模式 */
  EDIT = 'edit',
  /** 查看模式 */
  VIEW = 'view',
  /** 审阅模式 */
  REVIEW = 'review',
  /** 评论模式 */
  COMMENT = 'comment',
}

/**
 * 编辑器语言
 */
export type EditorLanguage = 'zh-CN' | 'en-US' | 'ja-JP' | 'ko-KR'

/**
 * 用户信息
 */
export interface EditorUser {
  /** 用户ID */
  id: string
  /** 用户名 */
  name: string
  /** 头像URL */
  avatar?: string
}

/**
 * 文档信息
 */
export interface DocumentInfo {
  /** 文档ID */
  fileId: number
  /** 文档标题 */
  title: string
  /** 文档URL */
  url: string
  /** 文档类型 */
  fileType: string
  /** 文档密钥(用于标识文档版本) */
  key: string
}

/**
 * 编辑器权限
 */
export interface EditorPermissions {
  /** 是否可编辑 */
  edit?: boolean
  /** 是否可下载 */
  download?: boolean
  /** 是否可打印 */
  print?: boolean
  /** 是否可审阅 */
  review?: boolean
  /** 是否可评论 */
  comment?: boolean
  /** 是否可填写表单 */
  fillForms?: boolean
  /** 是否可修改过滤器 */
  modifyFilter?: boolean
  /** 是否可修改内容控件 */
  modifyContentControl?: boolean
}

/**
 * 编辑器自定义配置
 * @see https://api.onlyoffice.com/editors/config/editor/customization
 */
export interface EditorCustomization {
  /** 是否自动保存 */
  autosave?: boolean
  /** 是否强制保存 */
  forcesave?: boolean
  /** 是否显示评论功能 */
  comments?: boolean
  /** 是否显示聊天功能 */
  chat?: boolean
  /** 是否显示帮助菜单 */
  help?: boolean
  /**
   * 是否显示反馈/功能建议选项
   * - false: 完全隐藏
   * - { visible: false }: 对象形式隐藏
   */
  feedback?: boolean | { visible?: boolean }
  /** 是否使用紧凑工具栏 */
  compactToolbar?: boolean
  /** 是否使用紧凑头部 */
  compactHeader?: boolean
  /** 是否隐藏右侧菜单（插件面板等） */
  hideRightMenu?: boolean
  /** 是否隐藏标尺 */
  hideRulers?: boolean
  /** 是否隐藏工具栏标签页 */
  toolbarNoTabs?: boolean
  /**
   * 宏模式
   * - 'warn': 警告用户（默认）
   * - 'disable': 禁用宏
   * - 'enable': 启用宏
   */
  macrosMode?: 'warn' | 'disable' | 'enable'
  /**
   * 是否启用插件功能
   * 设置为 false 将隐藏插件栏和所有插件（包括 AI 助手）
   */
  plugins?: boolean
  /** 是否启用宏功能 */
  macros?: boolean
  /** 是否显示关于信息 */
  about?: boolean
  /** 自定义 Logo 配置 */
  logo?: {
    /** Logo 图片 URL */
    image?: string
    /** Logo 图片（深色主题） */
    imageDark?: string
    /** Logo 链接 URL */
    url?: string
  }
  /** 自定义 CSS 文件 URL */
  css?: string
  /** 是否启用评审模式 */
  review?: boolean
  /** 强制使用的语言（覆盖用户设置） */
  forceLang?: string
  /** 是否隐藏笔记面板 */
  hideNotes?: boolean
  /** 是否显示评审变更 */
  showReviewChanges?: boolean
  /** 是否启用拼写检查 */
  spellcheck?: boolean
  /** 是否启用单位切换 */
  unit?: 'cm' | 'pt' | 'inch'
  /** 是否启用缩放 */
  zoom?: number
}

/**
 * 文档信息配置
 */
export interface DocumentInfo {
  /** 文档编码（用于 TXT 文件） */
  encoding?: string
  /** 其他文档信息 */
  [key: string]: any
}

/**
 * OnlyOffice配置
 */
export interface OnlyOfficeConfig {
  /** 文档配置 */
  document: {
    /** 文档类型 */
    fileType: string
    /** 文档密钥 */
    key: string
    /** 文档标题 */
    title: string
    /** 文档URL */
    url: string
    /** 文档权限 */
    permissions?: EditorPermissions
    /** 文档信息（如编码等） */
    info?: DocumentInfo
  }
  /** 文档类型 */
  documentType: DocumentType
  /** 编辑器类型 */
  editorConfig: {
    /** 回调URL */
    callbackUrl?: string
    /** 语言 */
    lang?: EditorLanguage
    /** 模式 */
    mode?: EditorMode
    /** 用户信息 */
    user?: EditorUser
    /** 自定义配置 */
    customization?: EditorCustomization
  }
  /** 编辑器高度 */
  height?: string
  /** 编辑器宽度 */
  width?: string
  /** Token(JWT) */
  token?: string
}

/**
 * 编辑器事件类型
 */
export enum EditorEventType {
  /** 文档准备就绪 */
  DOCUMENT_READY = 'onDocumentReady',
  /** 文档状态改变 */
  DOCUMENT_STATE_CHANGE = 'onDocumentStateChange',
  /** 请求保存 */
  REQUEST_SAVE = 'onRequestSave',
  /** 错误 */
  ERROR = 'onError',
  /** 警告 */
  WARNING = 'onWarning',
  /** 信息 */
  INFO = 'onInfo',
}

/**
 * 文档版本
 */
export interface DocumentVersion {
  /** 版本ID */
  id: number
  /** 文档ID */
  fileId: number
  /** 版本号 */
  version: number
  /** 文档密钥 */
  key: string
  /** 文档URL */
  url: string
  /** 创建者ID */
  creatorId: number
  /** 创建者名称 */
  creatorName: string
  /** 创建时间 */
  createTime: string
  /** 备注 */
  remark?: string
  /** 文件大小 */
  size: number
}

/**
 * 保存回调数据
 */
export interface SaveCallbackData {
  /** 文档密钥 */
  key: string
  /** 状态码 */
  status: number
  /** 下载URL */
  url?: string
  /** 用户列表 */
  users?: string[]
  /** 错误信息 */
  error?: string
}

