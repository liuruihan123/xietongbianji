<template>
  <div
    v-if="visible"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="handleCancel"
  >
    <div
      class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4"
      @click.stop
    >
      <!-- Header -->
      <div class="px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-900">
          {{ title }}
        </h3>
      </div>

      <!-- Content -->
      <div class="px-6 py-4">
        <p class="text-gray-600">
          {{ message }}
        </p>
        
        <!-- File info for context -->
        <div v-if="fileInfo" class="mt-3 p-3 bg-gray-50 rounded-lg">
          <div class="flex items-center space-x-2">
            <i :class="['fa', getFileIcon(fileInfo)]" class="text-blue-500"></i>
            <span class="font-medium text-gray-900">{{ fileInfo.name }}</span>
          </div>
          <div v-if="fileInfo.size" class="text-xs text-gray-500 mt-1">
            大小: {{ formatFileSize(fileInfo.size) }}
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="px-6 py-4 border-t border-gray-200 flex justify-end space-x-3">
        <button
          @click="handleCancel"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          取消
        </button>
        <button
          @click="handleConfirm"
          :disabled="loading"
          :class="[
            'px-4 py-2 text-sm font-medium text-white rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2',
            type === 'danger' 
              ? 'bg-red-600 hover:bg-red-700 focus:ring-red-500' 
              : 'bg-blue-600 hover:bg-blue-700 focus:ring-blue-500',
            loading && 'opacity-50 cursor-not-allowed'
          ]"
        >
          <span v-if="loading" class="flex items-center">
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            处理中...
          </span>
          <span v-else>{{ confirmText }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface FileInfo {
  name: string
  size?: number
  type?: string
  extension?: string
}

interface Props {
  visible: boolean
  title: string
  message: string
  confirmText?: string
  type?: 'default' | 'danger'
  loading?: boolean
  fileInfo?: FileInfo
}

interface Emits {
  (e: 'confirm'): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  confirmText: '确认',
  type: 'default',
  loading: false
})

const emit = defineEmits<Emits>()

function handleConfirm() {
  if (!props.loading) {
    emit('confirm')
  }
}

function handleCancel() {
  if (!props.loading) {
    emit('cancel')
  }
}

function getFileIcon(file: FileInfo) {
  const ext = file.extension?.toLowerCase() || file.type?.toLowerCase() || ''
  
  if (['doc', 'docx'].includes(ext)) return 'fa-file-word-o'
  if (['xls', 'xlsx'].includes(ext)) return 'fa-file-excel-o'
  if (['ppt', 'pptx'].includes(ext)) return 'fa-file-powerpoint-o'
  if (['pdf'].includes(ext)) return 'fa-file-pdf-o'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) return 'fa-file-image-o'
  if (['mp4', 'avi', 'mov', 'wmv'].includes(ext)) return 'fa-file-video-o'
  if (['mp3', 'wav', 'flac'].includes(ext)) return 'fa-file-audio-o'
  if (['zip', 'rar', '7z'].includes(ext)) return 'fa-file-archive-o'
  if (['txt', 'md'].includes(ext)) return 'fa-file-text-o'
  
  return 'fa-file-o'
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>
