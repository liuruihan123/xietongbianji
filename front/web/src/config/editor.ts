/**
 * OnlyOffice编辑器配置
 */

import {
  DocumentType,
  EditorMode,
  type EditorLanguage,
  type EditorPermissions,
  type EditorCustomization,
} from '@/types/editor'
import { getOnlyOfficeUrl } from '@/utils/runtimeConfig'

/**
 * OnlyOffice服务器地址
 * 使用运行时配置，支持 Docker 容器部署时动态修改
 */
export const ONLYOFFICE_URL = getOnlyOfficeUrl()

/**
 * 编辑器脚本URL
 */
export const ONLYOFFICE_API_URL = `${ONLYOFFICE_URL}web-apps/apps/api/documents/api.js`

/**
 * 默认编辑器语言
 */
export const DEFAULT_LANGUAGE: EditorLanguage = 'zh-CN'

/**
 * 默认编辑器模式
 */
export const DEFAULT_MODE: EditorMode = EditorMode.EDIT

/**
 * 文件扩展名到文档类型的映射
 */
export const FILE_TYPE_MAP: Record<string, DocumentType> = {
  // 文字处理
  doc: DocumentType.TEXT,
  docx: DocumentType.TEXT,
  txt: DocumentType.TEXT,
  rtf: DocumentType.TEXT,
  odt: DocumentType.TEXT,
  wps: DocumentType.TEXT, // WPS 文字
  // 电子表格
  xls: DocumentType.SPREADSHEET,
  xlsx: DocumentType.SPREADSHEET,
  csv: DocumentType.SPREADSHEET,
  ods: DocumentType.SPREADSHEET,
  et: DocumentType.SPREADSHEET, // WPS 表格
  ett: DocumentType.SPREADSHEET, // WPS 表格模板
  // 演示文稿
  ppt: DocumentType.PRESENTATION,
  pptx: DocumentType.PRESENTATION,
  odp: DocumentType.PRESENTATION,
  dps: DocumentType.PRESENTATION, // WPS 演示
  dpt: DocumentType.PRESENTATION, // WPS 演示模板
}

/**
 * 获取文档类型
 */
export function getDocumentType(fileType: string): DocumentType {
  const type = FILE_TYPE_MAP[fileType.toLowerCase()]
  if (!type) {
    throw new Error(`不支持的文件类型: ${fileType}`)
  }
  return type
}

/**
 * 默认编辑器权限
 */
export const DEFAULT_PERMISSIONS: EditorPermissions = {
  edit: true,
  download: true,
  print: true,
  review: true,
  comment: true,
  fillForms: true,
  modifyFilter: true,
  modifyContentControl: true,
}

/**
 * 只读权限
 */
export const READONLY_PERMISSIONS: EditorPermissions = {
  edit: false,
  download: true,
  print: true,
  review: false,
  comment: true,
  fillForms: false,
  modifyFilter: false,
  modifyContentControl: false,
}

/**
 * 默认自定义配置
 *
 * 界面隐藏说明：
 * - plugins: false - 隐藏插件栏（同时会隐藏 AI 助手等所有插件）
 * - feedback: false - 隐藏"提出功能建议"选项
 * - hideRightMenu: true - 隐藏右侧边栏（防止插件面板显示）
 * - help: false - 可选：隐藏帮助菜单
 */
export const DEFAULT_CUSTOMIZATION: EditorCustomization = {
  autosave: true,
  forcesave: true,
  comments: true,
  chat: true,
  help: true,
  feedback: false, // 隐藏"提出功能建议"
  plugins: false, // 隐藏插件栏和 AI 功能
  compactToolbar: false,
  hideRightMenu: true, // 隐藏右侧边栏
  macros: false, // 禁用宏
  macrosMode: 'disable', // 禁用宏模式
}

/**
 * 编辑器默认尺寸
 */
export const DEFAULT_EDITOR_SIZE = {
  width: '100%',
  height: '100%',
}

/**
 * 自动保存间隔(毫秒)
 */
export const AUTOSAVE_INTERVAL = 30000 // 30秒

/**
 * 文档状态码
 */
export enum DocumentStatus {
  /** 正在编辑 */
  EDITING = 1,
  /** 准备保存 */
  READY_FOR_SAVING = 2,
  /** 保存错误 */
  SAVE_ERROR = 3,
  /** 已关闭,无修改 */
  CLOSED_NO_CHANGES = 4,
  /** 强制保存 */
  FORCE_SAVE = 6,
  /** 保存错误(强制保存) */
  FORCE_SAVE_ERROR = 7,
}

/**
 * 错误代码
 */
export enum ErrorCode {
  /** 未知错误 */
  UNKNOWN = -1,
  /** 无错误 */
  NO_ERROR = 0,
  /** 文档密钥丢失 */
  KEY_MISSING = 1,
  /** 回调URL格式错误 */
  CALLBACK_URL_INCORRECT = 2,
  /** 内部服务器错误 */
  INTERNAL_SERVER_ERROR = 3,
  /** 下载错误 */
  DOWNLOAD_ERROR = 4,
  /** 转换错误 */
  CONVERSION_ERROR = 5,
  /** 上传错误 */
  UPLOAD_ERROR = 6,
  /** 文档密钥错误 */
  KEY_INCORRECT = 7,
  /** 数据库错误 */
  DATABASE_ERROR = 8,
}

/**
 * 获取错误信息
 */
export function getErrorMessage(code: ErrorCode): string {
  const messages: Record<ErrorCode, string> = {
    [ErrorCode.UNKNOWN]: '未知错误',
    [ErrorCode.NO_ERROR]: '无错误',
    [ErrorCode.KEY_MISSING]: '文档密钥丢失',
    [ErrorCode.CALLBACK_URL_INCORRECT]: '回调URL格式错误',
    [ErrorCode.INTERNAL_SERVER_ERROR]: '内部服务器错误',
    [ErrorCode.DOWNLOAD_ERROR]: '下载错误',
    [ErrorCode.CONVERSION_ERROR]: '转换错误',
    [ErrorCode.UPLOAD_ERROR]: '上传错误',
    [ErrorCode.KEY_INCORRECT]: '文档密钥错误',
    [ErrorCode.DATABASE_ERROR]: '数据库错误',
  }
  return messages[code] || '未知错误'
}

