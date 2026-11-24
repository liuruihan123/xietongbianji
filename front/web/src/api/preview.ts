/**
 * 文件预览API
 */

import { get } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type { FilePreviewInfo } from '@/types/preview'

/**
 * 获取文件预览信息
 * @param fileId 文件ID
 * @returns 文件预览信息
 */
export async function getFilePreviewInfo(fileId: number): Promise<FilePreviewInfo> {
  const response = await get<FilePreviewInfo>({
    url: `/coedit/file/preview/${fileId}`,
  })
  return response.data
}

/**
 * 获取文件预览URL
 * @param fileId 文件ID
 * @returns 预览URL
 */
export async function getFilePreviewUrl(fileId: number): Promise<string> {
  const response = await get<string>({
    url: `/coedit/file/preview/${fileId}/url`,
  })
  return response.data
}

/**
 * 获取文件内容(用于文本和代码文件)
 * @param fileId 文件ID
 * @returns 文件内容
 */
export async function getFileContent(fileId: number): Promise<string> {
  const response = await get<string>({
    url: `/coedit/file/content/${fileId}`,
  })
  return response.data
}

/**
 * 获取文件下载URL
 * @param fileId 文件ID
 * @returns 下载URL
 */
export async function getFileDownloadUrl(fileId: number): Promise<string> {
  console.log(15555)
  const response = await get<string>({
    url: `/coedit/file/download/${fileId}/url`,
  })
  return response.data
}

/**
 * 下载文件
 * @param fileId 文件ID
 * @param fileName 文件名
 */
export async function downloadFile(fileId: number, fileName: string): Promise<void> {
  try {
    const url = await getFileDownloadUrl(fileId)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.click()
  } catch (error) {
    console.error('下载文件失败:', error)
    throw error
  }
}

/**
 * 获取图片列表预览URL
 * @param fileIds 文件ID列表
 * @returns 预览URL列表
 */
export async function getImagePreviewUrls(fileIds: number[]): Promise<string[]> {
  const response = await get<string[]>({
    url: '/coedit/file/preview/images',
    data: { fileIds },
  })
  return response.data
}

