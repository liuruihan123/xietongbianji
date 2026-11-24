/**
 * 文档转换API
 */

import { get, post, put, del } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  ConvertTask,
  CreateConvertTaskParams,
  ConvertTaskListParams,
  ConvertStatistics,
} from '@/types/convert'

/**
 * 创建转换任务
 */
export async function createConvertTask(params: CreateConvertTaskParams): Promise<ConvertTask> {
  const response = await post<ConvertTask>({
    url: '/coedit/convert/task',
    data: params,
  })
  return response.data
}

/**
 * 获取转换任务详情
 */
export async function getConvertTask(taskId: number): Promise<ConvertTask> {
  const response = await get<ConvertTask>({
    url: `/coedit/convert/task/${taskId}`,
  })
  return response.data
}

/**
 * 获取转换任务列表
 */
export async function getConvertTaskList(
  params: ConvertTaskListParams
): Promise<{ rows: ConvertTask[]; total: number }> {
  const response = await get<{ rows: ConvertTask[]; total: number }>({
    url: '/coedit/convert/task/list',
    data: params,
  })
  return response.data
}

/**
 * 取消转换任务
 */
export async function cancelConvertTask(taskId: number): Promise<void> {
  await put<void>({
    url: `/coedit/convert/task/${taskId}/cancel`,
  })
}

/**
 * 删除转换任务
 */
export async function deleteConvertTask(taskId: number): Promise<void> {
  await del<void>({
    url: `/coedit/convert/task/${taskId}`,
  })
}

/**
 * 重试转换任务
 */
export async function retryConvertTask(taskId: number): Promise<ConvertTask> {
  const response = await post<ConvertTask>({
    url: `/coedit/convert/task/${taskId}/retry`,
  })
  return response.data
}

/**
 * 批量删除转换任务
 */
export async function batchDeleteConvertTasks(taskIds: number[]): Promise<void> {
  await del<void>({
    url: '/coedit/convert/task/batch',
    data: { taskIds },
  })
}

/**
 * 获取转换结果下载URL
 */
export async function getConvertResultUrl(taskId: number): Promise<string> {
  const response = await get<string>({
    url: `/coedit/convert/task/${taskId}/result/url`,
  })
  return response.data
}

/**
 * 下载转换结果
 */
export async function downloadConvertResult(taskId: number, fileName: string): Promise<void> {
  try {
    const url = await getConvertResultUrl(taskId)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.click()
  } catch (error) {
    console.error('下载转换结果失败:', error)
    throw error
  }
}

/**
 * 获取转换统计信息
 */
export async function getConvertStatistics(): Promise<ConvertStatistics> {
  const response = await get<ConvertStatistics>({
    url: '/coedit/convert/statistics',
  })
  return response.data
}

/**
 * 获取支持的转换格式
 */
export async function getSupportedFormats(): Promise<string[]> {
  const response = await get<string[]>({
    url: '/coedit/convert/formats',
  })
  return response.data
}


