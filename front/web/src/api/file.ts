/**
 * 文件管理相关API接口
 */

import { get, post, put, del, upload, download } from '@/utils/request'
import type { ApiResponse } from '@/types/auth'
import type {
  FileItem,
  FolderTreeNode,
  CreateFileDto,
  CreateFolderDto,
  RenameFileDto,
  MoveFileDto,
  CopyFileDto,
  DeleteFileDto,
  FileSearchParams,
} from '@/types/file'

/**
 * 获取文件列表
 * @param params 查询参数
 */
export function getFileList(params: FileSearchParams) {
  return get<{ rows: FileItem[]; total: number }>({
    url: '/coedit/file/list',
    data: params,
  })
}

/**
 * 获取文件详情
 * @param id 文件ID
 */
export function getFileDetail(id: number | string) {
  return get<FileItem>({
    url: `/coedit/file/${id}`,
  })
}

/**
 * 获取文件夹树
 * @param workspaceId 工作空间ID
 */
export function getFolderTree(workspaceId: number | string) {
  return get<FolderTreeNode[]>({
    url: '/coedit/file/tree',
    data: { workspaceId },
  })
}

/**
 * 创建在线文档
 * @param data 创建数据
 */
export function createFile(data: CreateFileDto) {
  return post<FileItem>({
    url: '/coedit/file/create',
    data,
  })
}

/**
 * 创建文件夹
 * @param data 创建数据
 */
export function createFolder(data: CreateFolderDto) {
  return post<FileItem>({
    url: '/coedit/file/folder',
    data,
  })
}

/**
 * 重命名文件
 * @param data 重命名数据 { id: 文件ID, name: 新文件名 }
 */
export function renameFile(data: RenameFileDto) {
  // 后端接口: PUT /coedit/file/{id}/rename?newFileName=xxx
  const { id, name } = data
  const encodedFileName = encodeURIComponent(name)
  return put<void>(`/coedit/file/${id}/rename?newFileName=${encodedFileName}`)
}

/**
 * 移动文件
 * @param data 移动数据
 */
export function moveFiles(data: MoveFileDto) {
  return post<void>({
    url: '/coedit/file/move',
    data,
  })
}

/**
 * 复制文件
 * @param data 复制数据
 */
export function copyFiles(data: CopyFileDto) {
  return post<void>({
    url: '/coedit/file/copy',
    data,
  })
}

/**
 * 删除文件(移至回收站)
 * @param data 删除数据
 */
export function deleteFiles(data: DeleteFileDto) {
  // 后端接口: DELETE /coedit/file/{ids}
  // 将文件ID数组转换为逗号分隔的字符串作为路径参数
  const ids = data.fileIds.join(',')
  return del<void>(`/coedit/file/${ids}`)
}

/**
 * 恢复文件(从回收站)
 * @param fileId 文件ID
 */
export function restoreFile(fileId: number | string) {
  return put<void>(`/coedit/recycleBin/${fileId}/restore`)
}

/**
 * 批量恢复文件(从回收站)
 * @param fileIds 文件ID列表
 */
export function restoreFiles(fileIds: (number | string)[]) {
  // 批量调用单个恢复接口
  return Promise.all(fileIds.map(id => restoreFile(id)))
}

/**
 * 永久删除单个文件(从回收站)
 * @param fileId 文件ID
 */
export function permanentDeleteFile(fileId: number | string) {
  return del<void>(`/coedit/recycleBin/${fileId}/permanent`)
}

/**
 * 批量永久删除文件(从回收站)
 * @param fileIds 文件ID列表
 */
export function permanentDeleteFiles(fileIds: (number | string)[]) {
  // 批量调用单个永久删除接口
  return Promise.all(fileIds.map(id => permanentDeleteFile(id)))
}


/**
 * 清空回收站
 * @param workspaceId 工作空间ID
 */
export function emptyRecycleBin(workspaceId: number | string) {
  return post<void>({
    url: '/coedit/file/empty-recycle-bin',
    data: { workspaceId },
  })
}

/**
 * 上传文件
 * @param file 文件对象
 * @param workspaceId 工作空间ID (可选,后端会自动创建默认工作空间)
 * @param parentId 父文件夹ID
 * @param onProgress 进度回调
 */
export function uploadFile(
  file: File,
  workspaceId?: number | string,
  parentId?: number | string | null,
  onProgress?: (percent: number) => void
) {
  const formData = new FormData()
  formData.append('file', file)
  if (workspaceId !== undefined) {
    formData.append('workspaceId', String(workspaceId))
  }
  if (parentId !== null && parentId !== undefined) {
    formData.append('parentId', String(parentId))
  }

  return upload<FileItem>('/coedit/file/upload', formData)
}

/**
 * 下载文件
 * @param id 文件ID
 * @param authToken 认证Token（可选）
 */
export async function downloadFile(id: number | string, authToken?: string) {
  const params: any = {}
  console.log('开始下载文件, ID:', id)
  if (authToken) {
    params.authToken = authToken
  }

  try {
    // 调用 download 函数获取 blob 数据
    const response = await download(`/coedit/file/download/${id}`, params)

    // 从响应头中获取文件名
    let fileName = `file_${id}`
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

    console.log('文件下载成功:', fileName)
    return response
  } catch (error) {
    console.error('文件下载失败:', error)
    throw error
  }
}

/**
 * 批量下载文件(打包为zip)
 * @param fileIds 文件ID列表
 */
export function downloadFiles(fileIds: (number | string)[]) {
  console.log(13334442)
  return download('/coedit/file/download/batch', { fileIds })
}

/**
 * 收藏文件
 * @param id 文件ID
 */
export function favoriteFile(id: number | string) {
  return post<void>({
    url: `/coedit/file/${id}/favorite`,
    data: {},
  })
}

/**
 * 取消收藏文件
 * @param id 文件ID
 */
export function unfavoriteFile(id: number | string) {
  return post<void>({
    url: `/coedit/file/${id}/unfavorite`,
    data: {},
  })
}

/**
 * 锁定文件
 * @param id 文件ID
 */
export function lockFile(id: number | string) {
  return post<void>({
    url: `/coedit/file/${id}/lock`,
    data: {},
  })
}

/**
 * 解锁文件
 * @param id 文件ID
 */
export function unlockFile(id: number | string) {
  return post<void>({
    url: `/coedit/file/${id}/unlock`,
    data: {},
  })
}

/**
 * 获取回收站文件列表
 * @param workspaceId 工作空间ID
 */
export function getRecycleBinFiles(workspaceId: number | string) {
  return get<{ rows: FileItem[]; total: number }>({
    url: '/coedit/file/recycle-bin',
    data: { workspaceId },
  })
}

/**
 * 搜索文件
 * @param params 搜索参数
 */
export function searchFiles(params: FileSearchParams) {
  return get<{ rows: FileItem[]; total: number }>({
    url: '/coedit/file/search',
    data: params,
  })
}

/**
 * 获取最近访问的文件
 * @param workspaceId 工作空间ID
 * @param limit 数量限制
 */
export function getRecentFiles(workspaceId: number | string, limit: number = 10) {
  return get<FileItem[]>({
    url: '/coedit/file/recent',
    data: { workspaceId, limit },
  })
}

/**
 * 获取收藏的文件
 * @param workspaceId 工作空间ID
 */
export function getFavoriteFiles(workspaceId: number | string) {
  return get<{ rows: FileItem[]; total: number }>({
    url: '/coedit/file/favorites',
    data: { workspaceId },
  })
}

