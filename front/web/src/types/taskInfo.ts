// 检测历史相关类型定义

// 分页查询参数
export interface PageQuery {
  /** 分页大小 */
  pageSize?: number
  /** 当前页数 */
  pageNum?: number
  /** 排序列 */
  orderByColumn?: string
  /** 排序的方向desc或者asc */
  isAsc?: string
}

// 逻辑任务查询参数
export interface LogicTaskInfoBo {
  /** 主键ID */
  id?: number
  /** 逻辑任务ID */
  lid?: string
  /** 用户ID */
  uid?: number
  /** 逻辑任务名称 */
  lname?: string
  /** 总字数 */
  totalWords?: number
  /** 片段字数 */
  segmentSize?: number
  /** 描述 */
  description?: string
  /** 任务类型id */
  typeId?: number
  /** 菜单状态 */
  status?: string
  /** 状态说明 */
  taskInfo?: string
  /** 是否登录(0登录 1未登录) */
  isLogin?: string
  /** 是否是免费任务 */
  isFree?: number
  /** 片段对比数量 */
  comparisonNum?: number
  /** 总计花费点数 */
  usePoints?: number
  /** 总计花费token */
  useTokens?: number
  /** 总计输入token */
  inputToken?: number
  /** 总计输出token */
  outputToken?: number
  /** 模型名称 */
  modelName?: string
  /** 创建者 */
  createBy?: number
  /** 创建时间 */
  createTime?: Date | string
  /** 请求参数 */
  params?: Record<string, any>
}

// 逻辑任务响应数据
export interface LogicTaskInfoVo {
  /** 主键ID */
  id: number
  /** 逻辑任务ID */
  lid: string
  /** 用户ID */
  uid: number
  /** 逻辑任务名称 */
  lname: string
  /** 总字数 */
  totalWords: number
  /** 片段字数 */
  segmentSize: number
  /** 描述 */
  description: string
  /** 任务类型id */
  typeId: number
  /** 菜单状态 */
  status: string
  /** 状态说明 */
  taskInfo: string
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
  /** 消耗点数 */
  usePoints?: number
}

// 表格数据信息（分页响应）
export interface TableDataInfo<T = any> {
  /** 总记录数 */
  total: number
  /** 列表数据 */
  rows: T[]
  /** 状态码 */
  code?: number
  /** 消息 */
  msg?: string
}

// 任务历史响应（直接包含分页信息）
export interface TaskHistoryResponse {
  /** 状态码 */
  code: number
  /** 消息 */
  msg: string
  /** 总记录数 */
  total: number
  /** 列表数据 */
  rows: LogicTaskInfoVo[]
}

// 任务状态枚举
export enum TaskStatus {
  /** 未开始 */
  NOT_STARTED = '0',
  /** 进行中 */
  IN_PROGRESS = '1',
  /** 中断 */
  INTERRUPTED = '2',
  /** 异常停止 */
  ERROR_STOPPED = '3',
  /** 完成 */
  COMPLETED = '4'
}

// 任务状态文本映射
export const TaskStatusText = {
  [TaskStatus.NOT_STARTED]: '未开始',
  [TaskStatus.IN_PROGRESS]: '进行中',
  [TaskStatus.INTERRUPTED]: '中断',
  [TaskStatus.ERROR_STOPPED]: '异常停止',
  [TaskStatus.COMPLETED]: '完成'
} as const

// 任务状态颜色映射
export const TaskStatusColor = {
  [TaskStatus.NOT_STARTED]: 'default',
  [TaskStatus.IN_PROGRESS]: 'processing',
  [TaskStatus.INTERRUPTED]: 'warning',
  [TaskStatus.ERROR_STOPPED]: 'error',
  [TaskStatus.COMPLETED]: 'success'
} as const

// 获取任务状态文本
export function getTaskStatusText(status: string): string {
  return TaskStatusText[status as TaskStatus] || '未知状态'
}

// 获取任务状态颜色
export function getTaskStatusColor(status: string): string {
  return TaskStatusColor[status as TaskStatus] || 'default'
}

// 判断任务是否为终结状态
export function isTaskFinished(status: string): boolean {
  return status === TaskStatus.INTERRUPTED ||
         status === TaskStatus.ERROR_STOPPED ||
         status === TaskStatus.COMPLETED
}

// 检测历史列表查询参数
export interface TaskHistoryQuery extends PageQuery {
  /** 任务名称（模糊查询） */
  lname?: string
  /** 任务状态 */
  status?: string
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

// 删除任务请求参数
export interface DeleteTaskRequest {
  /** 要删除的任务ID数组 */
  ids: number[]
}

// 逻辑分析结果响应数据
export interface LogicAnalysisResultsVo {
  /** 编号 */
  id: number
  /** 逻辑任务ID */
  lid: string
  /** 文档id */
  docId: string
  /** 错误级别(1.严重 2.一般 3.轻微 4.可忽略) */
  lvTag: string
  /** 最终分析内容 */
  resultContent: string
  /** 备注 */
  remark: string
}

// 错误级别枚举
export enum ErrorLevel {
  /** 严重 */
  SEVERE = '1',
  /** 一般 */
  NORMAL = '2',
  /** 轻微 */
  MINOR = '3',
  /** 可忽略 */
  IGNORABLE = '4'
}

// 错误级别文本映射
export const ErrorLevelText = {
  [ErrorLevel.SEVERE]: '严重',
  [ErrorLevel.NORMAL]: '一般',
  [ErrorLevel.MINOR]: '轻微',
  [ErrorLevel.IGNORABLE]: '可忽略'
} as const

// 错误级别颜色映射
export const ErrorLevelColor = {
  [ErrorLevel.SEVERE]: 'red',
  [ErrorLevel.NORMAL]: 'orange',
  [ErrorLevel.MINOR]: 'blue',
  [ErrorLevel.IGNORABLE]: 'green'
} as const

// 获取错误级别文本
export function getErrorLevelText(level: string): string {
  return ErrorLevelText[level as ErrorLevel] || '未知级别'
}

// 获取错误级别颜色
export function getErrorLevelColor(level: string): string {
  return ErrorLevelColor[level as ErrorLevel] || 'default'
}
