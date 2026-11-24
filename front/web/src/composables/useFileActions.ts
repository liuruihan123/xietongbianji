/**
 * File actions composable for handling download and delete operations
 */

import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { downloadFile, deleteFiles } from '@/api/file'

interface FileItem {
  id: number | string
  name: string
  type?: string
  extension?: string
  size?: number
  path?: string
  [key: string]: any
}

export function useFileActions() {
  const isDownloading = ref(false)
  const isDeleting = ref(false)

  /**
   * Download a file
   */
  async function downloadFileAction(file: FileItem, authToken?: string): Promise<boolean> {
    if (isDownloading.value) {
      return false
    }

    isDownloading.value = true
    try {
      console.log('开始下载文件:', file)

      // Call the download API with optional authToken
      await downloadFile(file.id, authToken)

      message.success(`${file.name} 下载成功`)
      return true
    } catch (error: any) {
      console.error('下载文件失败:', error)
      message.error(error.message || `${file.name} 下载失败`)
      return false
    } finally {
      isDownloading.value = false
    }
  }

  /**
   * Delete a file (move to recycle bin)
   */
  async function deleteFileAction(file: FileItem): Promise<boolean> {
    if (isDeleting.value) {
      return false
    }

    isDeleting.value = true
    try {
      console.log('开始删除文件:', file)
      
      // Call the delete API (moves to recycle bin)
      await deleteFiles({
        fileIds: [file.id],
        workspaceId: file.workspaceId || undefined
      })
      
      message.success(`${file.name} 已移至回收站`)
      return true
    } catch (error: any) {
      console.error('删除文件失败:', error)
      message.error(error.message || `${file.name} 删除失败`)
      return false
    } finally {
      isDeleting.value = false
    }
  }

  /**
   * Handle file download with confirmation
   */
  async function handleFileDownload(file: FileItem, showConfirmation: boolean = true, authToken?: string): Promise<boolean> {
    if (!showConfirmation) {
      return await downloadFileAction(file, authToken)
    }

    // For download, we might want to show confirmation for large files or in certain contexts
    // For now, we'll proceed directly but this can be customized
    return await downloadFileAction(file, authToken)
  }

  /**
   * Handle file deletion with confirmation
   */
  async function handleFileDelete(file: FileItem): Promise<boolean> {
    // Delete always requires confirmation, handled by the parent component
    return await deleteFileAction(file)
  }

  return {
    isDownloading,
    isDeleting,
    downloadFileAction,
    deleteFileAction,
    handleFileDownload,
    handleFileDelete
  }
}
