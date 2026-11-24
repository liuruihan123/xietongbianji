/**
 * 版本管理相关API接口
 */

import { get, post, del, download } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'

/**
 * 版本信息
 */
export interface VersionInfo {
  id: number | string
  fileId: number | string
  versionNumber: number
  fileName: string
  fileSize: number
  filePath: string
  fileHash: string
  documentKey?: string
  changeType?: number // 1-创建 2-编辑 3-上传 4-替换
  changeDescription?: string
  changeSummary?: string
  isCurrent: number // 0-否 1-是
  isMajor?: number // 0-否 1-是
  createdBy?: number
  createdName?: string
  editorId?: number
  editorName?: string
  editDuration?: number
  createTime: string
  updateTime?: string
}

/**
 * 创建版本请求参数
 */
export interface CreateVersionParams {
  fileId: number | string
  versionDesc?: string
  changeType?: number
  isMajor?: number
}

/**
 * 版本对比结果
 */
export interface VersionCompareResult {
  version1: VersionInfo
  version2: VersionInfo
  sizeDiff: number
  isSameContent: boolean
  timeDiffSeconds: number
  versionDiff: number
}

/**
 * 版本统计信息
 */
export interface VersionStatistics {
  totalVersions: number
  currentVersion: number
  totalSize: number
  oldestVersion: string
  latestVersion: string
}

/**
 * 创建文件版本
 * @param params 创建版本参数
 */
export function createVersion(params: CreateVersionParams) {
  return post<VersionInfo>({
    url: '/coedit/version/create',
    data: params,
  })
}

/**
 * 查询文件的版本列表（分页）
 * @param fileId 文件ID
 * @param pageNum 页码
 * @param pageSize 每页数量
 */
export async function getVersionList(fileId: number | string, pageNum = 1, pageSize = 10) {
  // 响应拦截器已经返回了 data 对象，不需要再访问 .data
  return get<{ rows: VersionInfo[]; total: number }>({
    url: `/coedit/version/list/${fileId}`,
    data: { pageNum, pageSize },
  })
}

/**
 * 获取版本详情
 * @param versionId 版本ID
 */
export function getVersionDetail(versionId: number | string) {
  return get<VersionInfo>({
    url: `/coedit/version/${versionId}`,
  })
}

/**
 * 版本对比
 * @param versionId1 版本1的ID
 * @param versionId2 版本2的ID
 */
export function compareVersions(versionId1: number | string, versionId2: number | string) {
  return post<VersionCompareResult>({
    url: '/coedit/version/compare',
    data: { versionId1, versionId2 },
  })
}

/**
 * 版本回滚
 * @param versionId 版本ID
 */
export function rollbackVersion(versionId: number | string) {
  return post<void>({
    url: `/coedit/version/rollback/${versionId}`,
    data: {},
  })
}

/**
 * 删除版本
 * @param versionId 版本ID
 */
export function deleteVersion(versionId: number | string) {
  return del<void>(`/coedit/version/${versionId}`)
}

/**
 * 清理文件的旧版本
 * @param fileId 文件ID
 * @param keepCount 保留的版本数量，默认10
 */
export function cleanOldVersions(fileId: number | string, keepCount = 10) {
  return post<number>({
    url: `/coedit/version/clean/${fileId}`,
    data: { keepCount },
  })
}

/**
 * 获取文件的当前版本
 * @param fileId 文件ID
 */
export function getCurrentVersion(fileId: number | string) {
  return get<VersionInfo>({
    url: `/coedit/version/current/${fileId}`,
  })
}

/**
 * 获取文件的版本统计信息
 * @param fileId 文件ID
 */
export function getVersionStatistics(fileId: number | string) {
  return get<VersionStatistics>({
    url: `/coedit/version/statistics/${fileId}`,
  })
}

/**
 * 下载版本文件（带认证）
 * @param versionId 版本ID
 */
export async function downloadVersionFile(versionId: number | string) {
  console.log('开始下载版本文件, versionId:', versionId)

  try {
    // 调用 download 函数获取 blob 数据（自动携带 Authorization header）
    const response = await download(`/coedit/version/download/${versionId}`)

    // 从响应头中获取文件名
    let fileName = `version_${versionId}`
    const contentDisposition = response.headers?.['content-disposition']
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = fileNameMatch[1].replace(/['"]/g, '')
        // 解码 URL 编码的文件名
        try {
          fileName = decodeURIComponent(fileName)
        } catch (e) {
          console.warn('文件名解码失败:', e)
        }
      }
    }

    // 创建 Blob URL
    const blob = new Blob([response.data], {
      type: response.headers?.['content-type'] || 'application/octet-stream'
    })
    const url = window.URL.createObjectURL(blob)

    // 创建临时链接并触发下载
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()

    // 清理
    setTimeout(() => {
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }, 100)

    console.log('版本文件下载成功:', fileName)
    return response
  } catch (error) {
    console.error('版本文件下载失败:', error)
    throw error
  }
}

