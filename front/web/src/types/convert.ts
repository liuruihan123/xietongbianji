/**
 * 文档转换模块类型定义
 */

/**
 * 转换任务状态
 */
export enum ConvertStatus {
  /** 待处理 */
  PENDING = 'PENDING',
  /** 转换中 */
  PROCESSING = 'PROCESSING',
  /** 成功 */
  SUCCESS = 'SUCCESS',
  /** 失败 */
  FAILED = 'FAILED',
  /** 已取消 */
  CANCELLED = 'CANCELLED',
}

/**
 * 转换类型
 */
export enum ConvertType {
  /** Office → PDF */
  OFFICE_TO_PDF = 'OFFICE_TO_PDF',
  /** PDF → 图片 */
  PDF_TO_IMAGE = 'PDF_TO_IMAGE',
  /** 图片 → PDF */
  IMAGE_TO_PDF = 'IMAGE_TO_PDF',
  /** Office格式互转 */
  OFFICE_CONVERT = 'OFFICE_CONVERT',
}

/**
 * 目标格式
 */
export enum TargetFormat {
  PDF = 'pdf',
  JPG = 'jpg',
  PNG = 'png',
  DOCX = 'docx',
  XLSX = 'xlsx',
  PPTX = 'pptx',
}

/**
 * 转换任务信息
 */
export interface ConvertTask {
  /** 任务ID */
  id: number
  /** 源文件ID */
  sourceFileId: number
  /** 源文件名 */
  sourceFileName: string
  /** 目标格式 */
  targetFormat: TargetFormat
  /** 转换类型 */
  convertType: ConvertType
  /** 任务状态 */
  status: ConvertStatus
  /** 转换进度(0-100) */
  progress: number
  /** 结果文件ID */
  resultFileId?: number
  /** 结果文件名 */
  resultFileName?: string
  /** 错误信息 */
  errorMessage?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
  /** 完成时间 */
  completeTime?: string
  /** 创建者 */
  creatorName?: string
}

/**
 * 创建转换任务参数
 */
export interface CreateConvertTaskParams {
  /** 源文件ID */
  sourceFileId: number
  /** 目标格式 */
  targetFormat: TargetFormat
  /** 转换配置 */
  config?: ConvertConfig
}

/**
 * 转换配置
 */
export interface ConvertConfig {
  /** 图片质量(1-100) */
  imageQuality?: number
  /** PDF页面范围(例如: "1-5,8,10-12") */
  pageRange?: string
  /** 图片DPI */
  imageDpi?: number
  /** 是否保留书签 */
  keepBookmarks?: boolean
  /** 是否保留超链接 */
  keepHyperlinks?: boolean
}

/**
 * 转换任务列表查询参数
 */
export interface ConvertTaskListParams {
  /** 页码 */
  page?: number
  /** 每页数量 */
  pageSize?: number
  /** 任务状态 */
  status?: ConvertStatus
  /** 源文件名(模糊搜索) */
  sourceFileName?: string
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

/**
 * 转换统计信息
 */
export interface ConvertStatistics {
  /** 总任务数 */
  totalTasks: number
  /** 成功任务数 */
  successTasks: number
  /** 失败任务数 */
  failedTasks: number
  /** 进行中任务数 */
  processingTasks: number
  /** 今日转换数 */
  todayTasks: number
  /** 转换成功率 */
  successRate: number
}

/**
 * 转换格式选项
 */
export interface ConvertFormatOption {
  /** 显示标签 */
  label: string
  /** 选项值 */
  value: string
  /** 源文件扩展名 */
  source: string[]
  /** 目标格式 */
  target: TargetFormat
  /** 转换类型 */
  convertType: ConvertType
}

/**
 * 支持的转换格式选项
 */
export const CONVERT_FORMAT_OPTIONS: ConvertFormatOption[] = [
  // Office → PDF
  {
    label: 'Word → PDF',
    value: 'WORD_TO_PDF',
    source: ['doc', 'docx'],
    target: TargetFormat.PDF,
    convertType: ConvertType.OFFICE_TO_PDF,
  },
  {
    label: 'Excel → PDF',
    value: 'EXCEL_TO_PDF',
    source: ['xls', 'xlsx'],
    target: TargetFormat.PDF,
    convertType: ConvertType.OFFICE_TO_PDF,
  },
  {
    label: 'PowerPoint → PDF',
    value: 'PPT_TO_PDF',
    source: ['ppt', 'pptx'],
    target: TargetFormat.PDF,
    convertType: ConvertType.OFFICE_TO_PDF,
  },

  // PDF → 图片
  {
    label: 'PDF → JPG',
    value: 'PDF_TO_JPG',
    source: ['pdf'],
    target: TargetFormat.JPG,
    convertType: ConvertType.PDF_TO_IMAGE,
  },
  {
    label: 'PDF → PNG',
    value: 'PDF_TO_PNG',
    source: ['pdf'],
    target: TargetFormat.PNG,
    convertType: ConvertType.PDF_TO_IMAGE,
  },

  // 图片 → PDF
  {
    label: '图片 → PDF',
    value: 'IMAGE_TO_PDF',
    source: ['jpg', 'jpeg', 'png', 'bmp'],
    target: TargetFormat.PDF,
    convertType: ConvertType.IMAGE_TO_PDF,
  },

  // Office格式互转
  {
    label: 'Word → Excel',
    value: 'WORD_TO_EXCEL',
    source: ['doc', 'docx'],
    target: TargetFormat.XLSX,
    convertType: ConvertType.OFFICE_CONVERT,
  },
  {
    label: 'Excel → Word',
    value: 'EXCEL_TO_WORD',
    source: ['xls', 'xlsx'],
    target: TargetFormat.DOCX,
    convertType: ConvertType.OFFICE_CONVERT,
  },
]

/**
 * 任务状态选项
 */
export const CONVERT_STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '待处理', value: ConvertStatus.PENDING },
  { label: '转换中', value: ConvertStatus.PROCESSING },
  { label: '成功', value: ConvertStatus.SUCCESS },
  { label: '失败', value: ConvertStatus.FAILED },
  { label: '已取消', value: ConvertStatus.CANCELLED },
]

/**
 * 根据文件名获取可用的转换选项
 */
export function getAvailableConvertOptions(fileName: string): ConvertFormatOption[] {
  const ext = fileName.split('.').pop()?.toLowerCase()
  if (!ext) return []

  return CONVERT_FORMAT_OPTIONS.filter((option) => option.source.includes(ext))
}

/**
 * 获取任务状态文本
 */
export function getConvertStatusText(status: ConvertStatus): string {
  const statusMap: Record<ConvertStatus, string> = {
    [ConvertStatus.PENDING]: '待处理',
    [ConvertStatus.PROCESSING]: '转换中',
    [ConvertStatus.SUCCESS]: '成功',
    [ConvertStatus.FAILED]: '失败',
    [ConvertStatus.CANCELLED]: '已取消',
  }
  return statusMap[status] || '未知'
}

/**
 * 获取任务状态颜色
 */
export function getConvertStatusColor(status: ConvertStatus): string {
  const colorMap: Record<ConvertStatus, string> = {
    [ConvertStatus.PENDING]: 'default',
    [ConvertStatus.PROCESSING]: 'processing',
    [ConvertStatus.SUCCESS]: 'success',
    [ConvertStatus.FAILED]: 'error',
    [ConvertStatus.CANCELLED]: 'warning',
  }
  return colorMap[status] || 'default'
}

