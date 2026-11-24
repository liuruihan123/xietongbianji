<template>
  <MainLayout
    :show-search="false"
    :show-notification="true"
    :show-sidebar-toggle="false"
  >
    <div class="trash-page bg-gray-50 min-h-screen">
      <div class="max-w-7xl mx-auto px-6 py-8">
        <!-- 页面头部 -->
        <div class="mb-8">
          <div class="flex items-center space-x-3 mb-3">
            <button
              @click="handleBackToHome"
              class="flex items-center space-x-2 text-gray-600 hover:text-blue-600 transition-colors"
            >
              <i class="fa fa-arrow-left"></i>
              <span class="text-sm font-medium">返回主页</span>
            </button>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2 flex items-center">
                <div class="w-12 h-12 bg-red-50 rounded-xl flex items-center justify-center mr-4">
                  <i class="fa fa-trash-o text-red-600 text-2xl"></i>
                </div>
                回收站
              </h1>
              <p class="text-sm text-gray-600 ml-16">已删除的文件将在回收站保留30天，过期后将自动永久删除</p>
            </div>
            <div class="flex items-center space-x-3">
              <button
                @click="loadTrashItems"
                :disabled="loading"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
              >
                <i class="fa fa-refresh mr-2"></i>刷新
              </button>
              <button
                v-if="trashItems.length > 0"
                @click="handleClearAll"
                class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition-colors"
              >
                <i class="fa fa-trash mr-2"></i>清空回收站
              </button>
            </div>
          </div>
        </div>

        <!-- 统计信息卡片 -->
        <div v-if="trashItems.length > 0" class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
          <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">文件总数</p>
                <p class="text-3xl font-bold text-gray-900">{{ trashItems.length }}</p>
              </div>
              <div class="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center">
                <i class="fa fa-files-o text-blue-600 text-xl"></i>
              </div>
            </div>
          </div>
          <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">已选择</p>
                <p class="text-3xl font-bold text-blue-600">{{ selectedItems.length }}</p>
              </div>
              <div class="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center">
                <i class="fa fa-check-square-o text-blue-600 text-xl"></i>
              </div>
            </div>
          </div>
          <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">占用空间</p>
                <p class="text-3xl font-bold text-gray-900">{{ totalSize }}</p>
              </div>
              <div class="w-12 h-12 bg-purple-50 rounded-lg flex items-center justify-center">
                <i class="fa fa-database text-purple-600 text-xl"></i>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作栏 -->
        <div v-if="selectedItems.length > 0" class="bg-gradient-to-r from-blue-50 to-blue-100 border border-blue-200 rounded-xl p-5 mb-6 shadow-sm">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center">
                <i class="fa fa-check text-white text-lg"></i>
              </div>
              <div>
                <p class="text-sm font-semibold text-blue-900">已选择 {{ selectedItems.length }} 个文件</p>
                <p class="text-xs text-blue-700">可以批量恢复或删除选中的文件</p>
              </div>
            </div>
            <div class="flex items-center space-x-3">
              <button
                @click="handleRestore"
                class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
              >
                <i class="fa fa-undo mr-2"></i>恢复
              </button>
              <button
                @click="handlePermanentDelete"
                class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition-colors"
              >
                <i class="fa fa-trash mr-2"></i>永久删除
              </button>
              <button
                @click="handleCancelSelection"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <i class="fa fa-times mr-2"></i>取消
              </button>
            </div>
          </div>
        </div>

        <!-- 文件列表 -->
        <div v-if="trashItems.length > 0" class="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
          <table class="w-full">
            <thead class="bg-gray-50 border-b border-gray-200">
              <tr>
                <th class="px-6 py-4 text-left">
                  <input
                    type="checkbox"
                    :checked="selectedItems.length === trashItems.length && trashItems.length > 0"
                    @change="handleSelectAll"
                    class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                  />
                </th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">名称</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">原位置</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">删除时间</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">大小</th>
                <th class="px-6 py-4 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr
                v-for="file in trashItems"
                :key="file.id"
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-6 py-4">
                  <input
                    type="checkbox"
                    :checked="isSelected(file)"
                    @change="handleSelectItem(file)"
                    class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                  />
                </td>
                <td class="px-6 py-4">
                  <div class="flex items-center space-x-3">
                    <div :class="['w-10 h-10 rounded-lg flex items-center justify-center', getFileIconBg(file)]">
                      <i :class="['fa text-lg', getFileIcon(file)]"></i>
                    </div>
                    <div>
                      <div class="font-medium text-gray-900">{{ file.fileName || file.name }}</div>
                      <div v-if="file.fileExtension || file.extension" class="text-xs text-gray-500">
                        {{ file.fileExtension || file.extension }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">
                  {{ getFilePath(file) }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">
                  {{ formatDate(file.updateTime || file.deleteTime) }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">
                  {{ formatFileSize(file.fileSize || file.size) }}
                </td>
                <td class="px-6 py-4 text-right">
                  <div class="flex items-center justify-end space-x-2">
                    <button
                      @click="handleRestoreSingle(file)"
                      class="px-3 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
                    >
                      <i class="fa fa-undo mr-1"></i>恢复
                    </button>
                    <button
                      @click="handlePermanentDeleteSingle(file)"
                      class="px-3 py-1.5 text-xs font-medium text-red-600 bg-red-50 rounded-lg hover:bg-red-100 transition-colors"
                    >
                      <i class="fa fa-trash mr-1"></i>删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && trashItems.length === 0" class="bg-white border border-gray-200 rounded-xl p-20 text-center shadow-sm">
          <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <i class="fa fa-trash-o text-gray-400 text-5xl"></i>
          </div>
          <h3 class="text-2xl font-semibold text-gray-900 mb-2">回收站是空的</h3>
          <p class="text-gray-500 mb-6">删除的文件会出现在这里，并保留30天</p>
          <button
            @click="handleBackToHome"
            class="px-6 py-3 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
          >
            <i class="fa fa-arrow-left mr-2"></i>返回主页
          </button>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="flex justify-center items-center py-20">
          <div class="text-center">
            <i class="fa fa-spinner fa-spin text-4xl text-blue-600 mb-4"></i>
            <p class="text-gray-600">加载中...</p>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { getFileList, restoreFiles, permanentDeleteFiles } from '@/api/file'
import type { FileItem } from '@/types/file'
import MainLayout from '@/components/layout/MainLayout.vue'

const router = useRouter()

// 数据
const loading = ref(false)
const trashItems = ref<FileItem[]>([])
const selectedItems = ref<FileItem[]>([])

// 计算总大小
const totalSize = computed(() => {
  const total = trashItems.value.reduce((sum, item) => {
    const size = item.fileSize || item.size || 0
    return sum + size
  }, 0)
  return formatFileSize(total)
})

// 检查文件是否被选中
function isSelected(file: FileItem): boolean {
  return selectedItems.value.some(item => item.id === file.id)
}

// 选择/取消选择单个文件
function handleSelectItem(file: FileItem) {
  const index = selectedItems.value.findIndex(item => item.id === file.id)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(file)
  }
}

// 全选/取消全选
function handleSelectAll(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.checked) {
    selectedItems.value = [...trashItems.value]
  } else {
    selectedItems.value = []
  }
}

// 加载回收站文件列表
async function loadTrashItems() {
  loading.value = true
  try {
    // 调用文件列表API，传入 isDeleted=1 参数来获取回收站文件
    const response: any = await getFileList({
      isDeleted: 1,
      pageNum: 1,
      pageSize: 1000, // 获取所有已删除文件
    })

    console.log('回收站文件列表完整响应:', response)

    // 后端返回格式: { total, rows, code, msg }
    if (response && response.code === 200) {
      // 过滤出真正被删除的文件 (isDeleted === true)
      const deletedFiles = (response.rows || []).filter((file: any) => file.isDeleted === true)
      trashItems.value = deletedFiles
      console.log('回收站文件数量:', deletedFiles.length, '文件列表:', deletedFiles)
    } else {
      trashItems.value = []
      console.warn('获取回收站文件失败:', response)
    }
  } catch (error: any) {
    console.error('加载回收站文件失败:', error)
    message.error(error.message || '加载回收站文件失败')
  } finally {
    loading.value = false
  }
}

// 返回主页
function handleBackToHome() {
  router.push('/')
}

// 取消选择
function handleCancelSelection() {
  selectedItems.value = []
}

// 恢复文件
async function handleRestore() {
  if (selectedItems.value.length === 0) {
    message.warning('请先选择要恢复的文件')
    return
  }

  Modal.confirm({
    title: '确认恢复',
    content: `确定要恢复选中的 ${selectedItems.value.length} 个文件吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        const fileIds = selectedItems.value.map(item => item.id)
        await restoreFiles(fileIds)
        message.success('恢复成功')
        selectedItems.value = []
        await loadTrashItems()
      } catch (error: any) {
        console.error('恢复文件失败:', error)
        message.error(error.message || '恢复文件失败')
      }
    },
  })
}

// 恢复单个文件
async function handleRestoreSingle(record: FileItem) {
  Modal.confirm({
    title: '确认恢复',
    content: `确定要恢复文件 "${record.fileName || record.name}" 吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await restoreFiles([record.id])
        message.success('恢复成功')
        await loadTrashItems()
      } catch (error: any) {
        console.error('恢复文件失败:', error)
        message.error(error.message || '恢复文件失败')
      }
    },
  })
}

// 永久删除
async function handlePermanentDelete() {
  if (selectedItems.value.length === 0) {
    message.warning('请先选择要删除的文件')
    return
  }

  Modal.confirm({
    title: '确认永久删除',
    content: `确定要永久删除选中的 ${selectedItems.value.length} 个文件吗？此操作不可恢复！`,
    okText: '确定删除',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        const fileIds = selectedItems.value.map(item => item.id)
        await permanentDeleteFiles(fileIds)
        message.success('删除成功')
        selectedItems.value = []
        await loadTrashItems()
      } catch (error: any) {
        console.error('永久删除文件失败:', error)
        message.error(error.message || '永久删除文件失败')
      }
    },
  })
}

// 永久删除单个文件
async function handlePermanentDeleteSingle(record: FileItem) {
  Modal.confirm({
    title: '确认永久删除',
    content: `确定要永久删除文件 "${record.fileName || record.name}" 吗？此操作不可恢复！`,
    okText: '确定删除',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        await permanentDeleteFiles([record.id])
        message.success('删除成功')
        await loadTrashItems()
      } catch (error: any) {
        console.error('永久删除文件失败:', error)
        message.error(error.message || '永久删除文件失败')
      }
    },
  })
}

// 清空回收站
async function handleClearAll() {
  Modal.confirm({
    title: '确认清空回收站',
    content: `确定要清空回收站吗？这将永久删除所有 ${trashItems.value.length} 个文件，此操作不可恢复！`,
    okText: '确定清空',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        const fileIds = trashItems.value.map(item => item.id)
        await permanentDeleteFiles(fileIds)
        message.success('清空成功')
        await loadTrashItems()
      } catch (error: any) {
        console.error('清空回收站失败:', error)
        message.error(error.message || '清空回收站失败')
      }
    },
  })
}

// 获取文件图标
function getFileIcon(file: FileItem): string {
  // 如果是文件夹
  if (file.isFolder) {
    return 'fa-folder text-blue-500'
  }

  // 根据文件扩展名或类型判断（优先使用后端返回的字段）
  const extension = (file.fileExtension || file.extension || '').toLowerCase()
  const fileType = (file.fileType || '').toLowerCase()

  // 文档类型
  if (extension === 'docx' || extension === 'doc' || fileType === 'docx' || fileType === 'doc') {
    return 'fa-file-word-o text-blue-600'
  }
  // 表格类型
  if (extension === 'xlsx' || extension === 'xls' || fileType === 'xlsx' || fileType === 'xls') {
    return 'fa-file-excel-o text-green-600'
  }
  // 演示文稿类型
  if (extension === 'pptx' || extension === 'ppt' || fileType === 'pptx' || fileType === 'ppt') {
    return 'fa-file-powerpoint-o text-orange-600'
  }
  // PDF类型
  if (extension === 'pdf' || fileType === 'pdf') {
    return 'fa-file-pdf-o text-red-600'
  }
  // 图片类型
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg'].includes(extension)) {
    return 'fa-file-image-o text-purple-600'
  }
  // 压缩包类型
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) {
    return 'fa-file-archive-o text-yellow-600'
  }

  // 默认文件图标
  return 'fa-file-o text-gray-500'
}

// 获取文件图标背景色
function getFileIconBg(file: FileItem): string {
  // 如果是文件夹
  if (file.isFolder) {
    return 'bg-blue-50'
  }

  // 根据文件扩展名或类型判断
  const extension = (file.fileExtension || file.extension || '').toLowerCase()
  const fileType = (file.fileType || '').toLowerCase()

  // 文档类型
  if (extension === 'docx' || extension === 'doc' || fileType === 'docx' || fileType === 'doc') {
    return 'bg-blue-50'
  }
  // 表格类型
  if (extension === 'xlsx' || extension === 'xls' || fileType === 'xlsx' || fileType === 'xls') {
    return 'bg-green-50'
  }
  // 演示文稿类型
  if (extension === 'pptx' || extension === 'ppt' || fileType === 'pptx' || fileType === 'ppt') {
    return 'bg-orange-50'
  }
  // PDF类型
  if (extension === 'pdf' || fileType === 'pdf') {
    return 'bg-red-50'
  }
  // 图片类型
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg'].includes(extension)) {
    return 'bg-purple-50'
  }
  // 压缩包类型
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) {
    return 'bg-yellow-50'
  }

  // 默认背景色
  return 'bg-gray-50'
}

// 获取文件路径
function getFilePath(file: FileItem): string {
  const path = file.locationPath || file.filePath || file.path
  if (!path) return '-'

  // 简化路径显示
  const parts = path.split(/[/\\]/)
  if (parts.length > 3) {
    return `.../${parts.slice(-2).join('/')}`
  }
  return path
}

// 格式化日期
function formatDate(date: string): string {
  if (!date) return '-'
  try {
    const d = new Date(date)
    return d.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch (error) {
    return '-'
  }
}

// 格式化文件大小
function formatFileSize(bytes: number): string {
  if (!bytes || bytes === 0) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + sizes[i]
}

// 组件挂载时加载数据
onMounted(() => {
  loadTrashItems()
})
</script>

