/**
 * 文件预览模块类型定义
 */

/**
 * 预览类型枚举
 */
export enum PreviewType {
  /** 图片 */
  IMAGE = 'IMAGE',
  /** PDF */
  PDF = 'PDF',
  /** Office文档 */
  OFFICE = 'OFFICE',
  /** 视频 */
  VIDEO = 'VIDEO',
  /** 音频 */
  AUDIO = 'AUDIO',
  /** 文本 */
  TEXT = 'TEXT',
  /** 代码 */
  CODE = 'CODE',
  /** 不支持 */
  UNSUPPORTED = 'UNSUPPORTED',
}

/**
 * 文件预览信息接口
 */
export interface FilePreviewInfo {
  /** 文件ID */
  id: number
  /** 文件名 */
  name: string
  /** 文件类型 */
  type: string
  /** 文件大小(字节) */
  size: number
  /** 预览URL */
  previewUrl?: string
  /** 下载URL */
  downloadUrl?: string
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
  /** 创建者 */
  creatorName?: string
}

/**
 * 预览配置接口
 */
export interface PreviewOptions {
  /** 是否显示工具栏 */
  showToolbar?: boolean
  /** 是否允许下载 */
  allowDownload?: boolean
  /** 是否允许打印 */
  allowPrint?: boolean
  /** 是否全屏显示 */
  fullscreen?: boolean
  /** 初始缩放比例 */
  initialScale?: number
  /** 最大缩放比例 */
  maxScale?: number
  /** 最小缩放比例 */
  minScale?: number
}

/**
 * 图片预览配置接口
 */
export interface ImagePreviewOptions extends PreviewOptions {
  /** 是否显示缩略图 */
  showThumbnail?: boolean
  /** 是否循环播放 */
  loop?: boolean
  /** 是否显示导航按钮 */
  showNavbar?: boolean
  /** 是否显示标题 */
  showTitle?: boolean
}

/**
 * PDF预览配置接口
 */
export interface PdfPreviewOptions extends PreviewOptions {
  /** 当前页码 */
  currentPage?: number
  /** 总页数 */
  totalPages?: number
  /** 是否显示页码 */
  showPageNumber?: boolean
  /** 是否显示缩略图 */
  showThumbnail?: boolean
}

/**
 * 代码预览配置接口
 */
export interface CodePreviewOptions extends PreviewOptions {
  /** 编程语言 */
  language?: string
  /** 主题 */
  theme?: string
  /** 是否显示行号 */
  showLineNumbers?: boolean
  /** 是否自动换行 */
  wordWrap?: boolean
}

/**
 * 文件类型映射
 */
export const FILE_TYPE_MAP: Record<string, PreviewType> = {
  // 图片
  jpg: PreviewType.IMAGE,
  jpeg: PreviewType.IMAGE,
  png: PreviewType.IMAGE,
  gif: PreviewType.IMAGE,
  bmp: PreviewType.IMAGE,
  webp: PreviewType.IMAGE,
  svg: PreviewType.IMAGE,
  ico: PreviewType.IMAGE,

  // PDF
  pdf: PreviewType.PDF,

  // Office文档
  doc: PreviewType.OFFICE,
  docx: PreviewType.OFFICE,
  xls: PreviewType.OFFICE,
  xlsx: PreviewType.OFFICE,
  ppt: PreviewType.OFFICE,
  pptx: PreviewType.OFFICE,

  // 视频
  mp4: PreviewType.VIDEO,
  webm: PreviewType.VIDEO,
  ogg: PreviewType.VIDEO,
  avi: PreviewType.VIDEO,
  mov: PreviewType.VIDEO,
  mkv: PreviewType.VIDEO,

  // 音频
  mp3: PreviewType.AUDIO,
  wav: PreviewType.AUDIO,
  m4a: PreviewType.AUDIO,
  flac: PreviewType.AUDIO,
  aac: PreviewType.AUDIO,

  // 文本
  txt: PreviewType.TEXT,
  md: PreviewType.TEXT,
  csv: PreviewType.TEXT,
  log: PreviewType.TEXT,

  // 代码
  js: PreviewType.CODE,
  ts: PreviewType.CODE,
  jsx: PreviewType.CODE,
  tsx: PreviewType.CODE,
  vue: PreviewType.CODE,
  html: PreviewType.CODE,
  css: PreviewType.CODE,
  scss: PreviewType.CODE,
  less: PreviewType.CODE,
  json: PreviewType.CODE,
  xml: PreviewType.CODE,
  java: PreviewType.CODE,
  py: PreviewType.CODE,
  go: PreviewType.CODE,
  php: PreviewType.CODE,
  c: PreviewType.CODE,
  cpp: PreviewType.CODE,
  h: PreviewType.CODE,
  cs: PreviewType.CODE,
  rb: PreviewType.CODE,
  swift: PreviewType.CODE,
  kt: PreviewType.CODE,
  rs: PreviewType.CODE,
  sql: PreviewType.CODE,
  sh: PreviewType.CODE,
  yaml: PreviewType.CODE,
  yml: PreviewType.CODE,
}

/**
 * 代码语言映射
 */
export const CODE_LANGUAGE_MAP: Record<string, string> = {
  js: 'javascript',
  ts: 'typescript',
  jsx: 'javascript',
  tsx: 'typescript',
  vue: 'vue',
  html: 'html',
  css: 'css',
  scss: 'scss',
  less: 'less',
  json: 'json',
  xml: 'xml',
  java: 'java',
  py: 'python',
  go: 'go',
  php: 'php',
  c: 'c',
  cpp: 'cpp',
  h: 'c',
  cs: 'csharp',
  rb: 'ruby',
  swift: 'swift',
  kt: 'kotlin',
  rs: 'rust',
  sql: 'sql',
  sh: 'bash',
  yaml: 'yaml',
  yml: 'yaml',
  md: 'markdown',
}

/**
 * 根据文件扩展名获取预览类型
 * @param fileName 文件名
 * @returns 预览类型
 */
export function getPreviewType(fileName: string): PreviewType {
  const ext = fileName.split('.').pop()?.toLowerCase()
  if (!ext) return PreviewType.UNSUPPORTED
  return FILE_TYPE_MAP[ext] || PreviewType.UNSUPPORTED
}

/**
 * 根据文件扩展名获取代码语言
 * @param fileName 文件名
 * @returns 代码语言
 */
export function getCodeLanguage(fileName: string): string {
  const ext = fileName.split('.').pop()?.toLowerCase()
  if (!ext) return 'plaintext'
  return CODE_LANGUAGE_MAP[ext] || 'plaintext'
}

/**
 * 判断是否支持预览
 * @param fileName 文件名
 * @returns 是否支持预览
 */
export function isSupportedPreview(fileName: string): boolean {
  return getPreviewType(fileName) !== PreviewType.UNSUPPORTED
}

