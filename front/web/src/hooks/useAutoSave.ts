/**
 * 文档自动保存Hook
 */

import { ref, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import { AUTOSAVE_INTERVAL } from '@/config/editor'
import { forceSaveDocument } from '@/api/editor'

export interface UseAutoSaveOptions {
  /** 文件ID (支持number和string类型) */
  fileId: number | string
  /** 自动保存间隔(毫秒) */
  interval?: number
  /** 是否启用自动保存 */
  enabled?: boolean
  /** 保存成功回调 */
  onSaveSuccess?: () => void
  /** 保存失败回调 */
  onSaveError?: (error: any) => void
}

export function useAutoSave(options: UseAutoSaveOptions) {
  const {
    fileId,
    interval = AUTOSAVE_INTERVAL,
    enabled = true,
    onSaveSuccess,
    onSaveError,
  } = options

  const isSaving = ref(false)
  const lastSaveTime = ref<Date | null>(null)
  const hasUnsavedChanges = ref(false)
  let timer: NodeJS.Timeout | null = null

  /**
   * 执行保存
   */
  async function save() {
    if (isSaving.value) {
      console.log('正在保存中,跳过本次保存')
      return
    }

    if (!hasUnsavedChanges.value) {
      console.log('没有未保存的更改,跳过保存')
      return
    }

    try {
      isSaving.value = true
      await forceSaveDocument(fileId)
      lastSaveTime.value = new Date()
      hasUnsavedChanges.value = false
      console.log('自动保存成功:', lastSaveTime.value)
      onSaveSuccess?.()
    } catch (error) {
      console.error('自动保存失败:', error)
      message.error('自动保存失败')
      onSaveError?.(error)
    } finally {
      isSaving.value = false
    }
  }

  /**
   * 启动自动保存
   */
  function start() {
    if (!enabled) {
      console.log('自动保存未启用')
      return
    }

    stop() // 先停止之前的定时器

    timer = setInterval(() => {
      save()
    }, interval)

    console.log(`自动保存已启动,间隔: ${interval}ms`)
  }

  /**
   * 停止自动保存
   */
  function stop() {
    if (timer) {
      clearInterval(timer)
      timer = null
      console.log('自动保存已停止')
    }
  }

  /**
   * 标记有未保存的更改
   */
  function markAsChanged() {
    hasUnsavedChanges.value = true
  }

  /**
   * 标记已保存
   */
  function markAsSaved() {
    hasUnsavedChanges.value = false
  }

  /**
   * 手动保存
   */
  async function manualSave() {
    await save()
    message.success('保存成功')
  }

  // 组件卸载时停止自动保存
  onBeforeUnmount(() => {
    stop()
  })

  // 如果启用,立即启动
  if (enabled) {
    start()
  }

  return {
    /** 是否正在保存 */
    isSaving,
    /** 最后保存时间 */
    lastSaveTime,
    /** 是否有未保存的更改 */
    hasUnsavedChanges,
    /** 启动自动保存 */
    start,
    /** 停止自动保存 */
    stop,
    /** 标记有未保存的更改 */
    markAsChanged,
    /** 标记已保存 */
    markAsSaved,
    /** 手动保存 */
    manualSave,
  }
}

