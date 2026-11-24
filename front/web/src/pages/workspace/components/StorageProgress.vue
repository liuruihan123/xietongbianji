<template>
  <div class="storage-progress">
    <div class="storage-header">
      <span class="storage-title">存储空间</span>
      <span class="storage-text">
        {{ formatSize(usedStorage) }} / {{ formatSize(maxStorage) }}
      </span>
    </div>
    
    <a-progress
      :percent="percent"
      :status="progressStatus"
      :stroke-color="strokeColor"
    />
    
    <div class="storage-footer">
      <span class="storage-available">
        剩余 {{ formatSize(maxStorage - usedStorage) }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  usedStorage: number
  maxStorage: number
}>()

/**
 * 存储使用百分比
 */
const percent = computed(() => {
  if (props.maxStorage === 0) return 0
  return Math.round((props.usedStorage / props.maxStorage) * 100)
})

/**
 * 进度条状态
 */
const progressStatus = computed(() => {
  if (percent.value >= 90) return 'exception'
  if (percent.value >= 70) return 'normal'
  return 'success'
})

/**
 * 进度条颜色
 */
const strokeColor = computed(() => {
  if (percent.value >= 90) return '#ff4d4f'
  if (percent.value >= 70) return '#faad14'
  return '#52c41a'
})

/**
 * 格式化文件大小
 */
function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}
</script>

<style scoped lang="scss">
.storage-progress {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;

  .storage-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .storage-title {
      font-weight: 500;
      color: #333;
    }

    .storage-text {
      font-size: 14px;
      color: #666;
    }
  }

  .storage-footer {
    margin-top: 8px;
    text-align: right;

    .storage-available {
      font-size: 12px;
      color: #999;
    }
  }
}
</style>

