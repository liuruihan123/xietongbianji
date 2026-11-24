/**
 * 文件管理状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as fileApi from '@/api/file'
import type {
  FileItem,
  FolderTreeNode,
  FileViewMode,
  FileSortField,
  FileSortOrder,
  UploadFileItem,
} from '@/types/file'

export const useFileStore = defineStore('file', () => {
  // ========== 状态 ==========
  const files = ref<FileItem[]>([])
  const currentFolder = ref<FileItem | null>(null)
  const folderTree = ref<FolderTreeNode[]>([])
  const selectedFiles = ref<number[]>([])
  const loading = ref(false)
  const uploading = ref(false)
  const uploadQueue = ref<UploadFileItem[]>([])
  
  // 视图设置
  const viewMode = ref<FileViewMode>('list')
  const sortField = ref<FileSortField>('name')
  const sortOrder = ref<FileSortOrder>('asc')
  
  // 回收站
  const recycleBinFiles = ref<FileItem[]>([])
  
  // 收藏
  const favoriteFiles = ref<FileItem[]>([])
  
  // 最近文件
  const recentFiles = ref<FileItem[]>([])

  // ========== 计算属性 ==========
  
  /**
   * 当前文件夹ID
   */
  const currentFolderId = computed(() => currentFolder.value?.id || null)

  /**
   * 当前文件夹路径
   */
  const currentPath = computed(() => {
    if (!currentFolder.value) return '/'
    return currentFolder.value.path || '/'
  })

  /**
   * 是否有选中的文件
   */
  const hasSelectedFiles = computed(() => selectedFiles.value.length > 0)

  /**
   * 选中的文件数量
   */
  const selectedFilesCount = computed(() => selectedFiles.value.length)

  /**
   * 排序后的文件列表
   */
  const sortedFiles = computed(() => {
    const filesCopy = [...files.value]
    
    filesCopy.sort((a, b) => {
      // 文件夹优先
      if (a.isFolder && !b.isFolder) return -1
      if (!a.isFolder && b.isFolder) return 1
      
      // 按指定字段排序
      let compareResult = 0
      
      switch (sortField.value) {
        case 'name':
          compareResult = a.name.localeCompare(b.name)
          break
        case 'size':
          compareResult = a.size - b.size
          break
        case 'createTime':
          compareResult = new Date(a.createTime).getTime() - new Date(b.createTime).getTime()
          break
        case 'updateTime':
          compareResult = new Date(a.updateTime).getTime() - new Date(b.updateTime).getTime()
          break
      }
      
      return sortOrder.value === 'asc' ? compareResult : -compareResult
    })
    
    return filesCopy
  })

  /**
   * 是否正在上传
   */
  const isUploading = computed(() => uploading.value || uploadQueue.value.length > 0)

  // ========== 操作方法 ==========

  /**
   * 获取文件列表
   */
  async function fetchFiles(workspaceId: number, parentId: number | null = null) {
    loading.value = true
    try {
      const response = await fileApi.getFileList({
        workspaceId,
        parentId,
      })
      files.value = response.data?.rows || []
      return files.value
    } catch (error) {
      console.error('获取文件列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取文件夹树
   */
  async function fetchFolderTree(workspaceId: number) {
    try {
      const response = await fileApi.getFolderTree(workspaceId)
      folderTree.value = response.data || []
      return folderTree.value
    } catch (error) {
      console.error('获取文件夹树失败:', error)
      throw error
    }
  }

  /**
   * 创建文件夹
   */
  async function createFolder(name: string, workspaceId: number, parentId: number | null = null) {
    try {
      const response = await fileApi.createFolder({
        name,
        workspaceId,
        parentId,
      })
      const folder = response.data
      files.value.unshift(folder)
      return folder
    } catch (error) {
      console.error('创建文件夹失败:', error)
      throw error
    }
  }

  /**
   * 重命名文件
   */
  async function renameFile(id: number, name: string) {
    try {
      await fileApi.renameFile({ id, name })
      const file = files.value.find(f => f.id === id)
      if (file) {
        file.name = name
      }
    } catch (error) {
      console.error('重命名文件失败:', error)
      throw error
    }
  }

  /**
   * 移动文件
   */
  async function moveFiles(fileIds: number[], targetFolderId: number | null) {
    try {
      await fileApi.moveFiles({ fileIds, targetFolderId })
      files.value = files.value.filter(f => !fileIds.includes(f.id))
      selectedFiles.value = []
    } catch (error) {
      console.error('移动文件失败:', error)
      throw error
    }
  }

  /**
   * 复制文件
   */
  async function copyFiles(fileIds: number[], targetFolderId: number | null) {
    try {
      await fileApi.copyFiles({ fileIds, targetFolderId })
      selectedFiles.value = []
    } catch (error) {
      console.error('复制文件失败:', error)
      throw error
    }
  }

  /**
   * 删除文件
   */
  async function deleteFiles(fileIds: number[], permanent: boolean = false) {
    try {
      await fileApi.deleteFiles({ fileIds, permanent })
      files.value = files.value.filter(f => !fileIds.includes(f.id))
      selectedFiles.value = []
    } catch (error) {
      console.error('删除文件失败:', error)
      throw error
    }
  }

  /**
   * 选择文件
   */
  function selectFile(id: number) {
    if (!selectedFiles.value.includes(id)) {
      selectedFiles.value.push(id)
    }
  }

  /**
   * 取消选择文件
   */
  function unselectFile(id: number) {
    selectedFiles.value = selectedFiles.value.filter(fileId => fileId !== id)
  }

  /**
   * 切换文件选择状态
   */
  function toggleFileSelection(id: number) {
    if (selectedFiles.value.includes(id)) {
      unselectFile(id)
    } else {
      selectFile(id)
    }
  }

  /**
   * 全选/取消全选
   */
  function toggleSelectAll() {
    if (selectedFiles.value.length === files.value.length) {
      selectedFiles.value = []
    } else {
      selectedFiles.value = files.value.map(f => f.id)
    }
  }

  /**
   * 清空选择
   */
  function clearSelection() {
    selectedFiles.value = []
  }

  /**
   * 切换视图模式
   */
  function toggleViewMode() {
    viewMode.value = viewMode.value === 'list' ? 'grid' : 'list'
    localStorage.setItem('fileViewMode', viewMode.value)
  }

  /**
   * 设置排序
   */
  function setSort(field: FileSortField, order: FileSortOrder) {
    sortField.value = field
    sortOrder.value = order
  }

  /**
   * 设置当前文件夹
   */
  function setCurrentFolder(folder: FileItem | null) {
    currentFolder.value = folder
  }

  /**
   * 重置状态
   */
  function reset() {
    files.value = []
    currentFolder.value = null
    folderTree.value = []
    selectedFiles.value = []
    loading.value = false
    uploading.value = false
    uploadQueue.value = []
  }

  // 初始化视图模式
  const savedViewMode = localStorage.getItem('fileViewMode')
  if (savedViewMode === 'list' || savedViewMode === 'grid') {
    viewMode.value = savedViewMode
  }

  return {
    // 状态
    files,
    currentFolder,
    folderTree,
    selectedFiles,
    loading,
    uploading,
    uploadQueue,
    viewMode,
    sortField,
    sortOrder,
    recycleBinFiles,
    favoriteFiles,
    recentFiles,
    // 计算属性
    currentFolderId,
    currentPath,
    hasSelectedFiles,
    selectedFilesCount,
    sortedFiles,
    isUploading,
    // 方法
    fetchFiles,
    fetchFolderTree,
    createFolder,
    renameFile,
    moveFiles,
    copyFiles,
    deleteFiles,
    selectFile,
    unselectFile,
    toggleFileSelection,
    toggleSelectAll,
    clearSelection,
    toggleViewMode,
    setSort,
    setCurrentFolder,
    reset,
  }
})

