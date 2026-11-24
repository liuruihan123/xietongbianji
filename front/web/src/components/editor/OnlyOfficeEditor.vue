<template>
  <div class="onlyoffice-editor">
    <!-- 移除内部 a-spin，由父组件统一管理 loading 状态 -->
    <div :id="editorId" class="editor-container"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { message } from 'ant-design-vue'
import { ONLYOFFICE_API_URL } from '@/config/editor'
import type { OnlyOfficeConfig } from '@/types/editor'

const props = defineProps<{
  /** 编辑器配置 */
  config: OnlyOfficeConfig
  /** 编辑器ID */
  editorId?: string
}>()

const emit = defineEmits<{
  /** 编辑器应用准备就绪 */
  appReady: []
  /** 文档准备就绪 */
  ready: []
  /** 文档保存 */
  save: []
  /** 文档状态改变 */
  stateChange: [hasChanges: boolean]
  /** 错误 */
  error: [error: any]
}>()

const editorId = ref(props.editorId || `onlyoffice-editor-${Date.now()}`)
let editorInstance: any = null
let scriptLoaded = false

/**
 * 加载OnlyOffice脚本
 */
function loadScript(): Promise<void> {
  return new Promise((resolve, reject) => {
    // 检查脚本是否已加载
    if (scriptLoaded || (window as any).DocsAPI) {
      scriptLoaded = true
      resolve()
      return
    }

    // 检查是否已有script标签
    const existingScript = document.querySelector(`script[src="${ONLYOFFICE_API_URL}"]`)
    if (existingScript) {
      existingScript.addEventListener('load', () => {
        scriptLoaded = true
        resolve()
      })
      existingScript.addEventListener('error', reject)
      return
    }

    // 创建script标签
    const script = document.createElement('script')
    script.src = ONLYOFFICE_API_URL
    script.async = true
    script.onload = () => {
      scriptLoaded = true
      resolve()
    }
    script.onerror = () => {
      reject(new Error('加载OnlyOffice脚本失败'))
    }
    document.head.appendChild(script)
  })
}

/**
 * 初始化编辑器
 * 【重要修复】强制销毁旧实例和清空DOM，确保完全重建编辑器
 */
async function initEditor() {
  try {
    console.log('开始初始化编辑器...')
    console.log('文档Key:', props.config.document?.key)

    // 【关键】强制销毁旧实例 - 避免缓存问题
    if (editorInstance) {
      console.log('销毁旧编辑器实例...')
      try {
        editorInstance.destroyEditor()
      } catch (e) {
        console.warn('销毁编辑器时出错:', e)
      }
      editorInstance = null
    }

    // 【关键】清空DOM容器 - 完全清理旧实例
    const container = document.getElementById(editorId.value)
    if (container) {
      console.log('清空编辑器容器...')
      container.innerHTML = ''
    }

    // 【关键】等待DOM刷新 - 确保旧内容完全清除
    await new Promise(resolve => setTimeout(resolve, 100))

    // 加载脚本
    await loadScript()

    // 检查DocsAPI是否可用
    if (!(window as any).DocsAPI) {
      throw new Error('OnlyOffice API未加载')
    }

    // 【关键修复】为 document.url 添加时间戳，绕过 OnlyOffice 服务端缓存
    const originalUrl = props.config.document?.url || ''
    const cacheBuster = `_t=${Date.now()}`
    const urlWithCacheBuster = originalUrl.includes('?')
      ? `${originalUrl}&${cacheBuster}`
      : `${originalUrl}?${cacheBuster}`

    console.log('原始文档URL:', originalUrl)
    console.log('添加缓存绕过后URL:', urlWithCacheBuster)
    console.log('🔑 传入的 Token:', props.config.token)

    // 创建编辑器配置，使用自适应高度
    const config = {
      ...props.config,
      document: {
        ...props.config.document,
        url: urlWithCacheBuster,  // 使用带时间戳的 URL
      },
      token: props.config.token,  // 传递 JWT token
      height: '100%',
      width: '100%',
      events: {
        onAppReady: () => {
          console.log('✅ 编辑器应用已加载')
          emit('appReady')
        },
        onDocumentReady: () => {
          console.log('✅ 文档已加载，当前Key:', config.document?.key)
          emit('ready')
        },
        onDocumentStateChange: (event: any) => {
          emit('stateChange', event.data)
        },
        onRequestSave: () => {
          emit('save')
        },
        onError: (event: any) => {
          console.error('OnlyOffice错误:', event)
          const errorMsg = event.data?.errorDescription
            ? `${event.data.errorDescription} (代码: ${event.data.errorCode || '未知'})`
            : (typeof event.data === 'string' ? event.data : '编辑器发生错误')
          message.error(`编辑器错误: ${errorMsg}`)
          emit('error', event.data)
        },
        onWarning: (event: any) => {
          console.warn('OnlyOffice警告:', event)
          if (event.data?.warningDescription) {
            console.warn(`编辑器警告: ${event.data.warningDescription} (代码: ${event.data.warningCode || '未知'})`)
          }
        },
        onInfo: (event: any) => {
          console.info('OnlyOffice信息:', event)
        },
      },
    }

    console.log('创建新的编辑器实例，documentKey=', config.document?.key)
    editorInstance = new (window as any).DocsAPI.DocEditor(editorId.value, config)
    console.log('✅ 编辑器实例创建成功')
  } catch (error) {
    console.error('初始化编辑器失败:', error)
    message.error('初始化编辑器失败')
    emit('error', error)
  }
}

/**
 * 销毁编辑器
 */
function destroyEditor() {
  if (editorInstance) {
    try {
      editorInstance.destroyEditor()
      editorInstance = null
    } catch (error) {
      console.error('销毁编辑器失败:', error)
    }
  }
}

// 监听配置变化
watch(() => props.config, () => {
  initEditor()
}, { deep: true })

onMounted(() => {
  initEditor()
})

onBeforeUnmount(() => {
  destroyEditor()
})

// 暴露方法给父组件
defineExpose({
  /** 刷新编辑器 */
  refresh: initEditor,
  /** 销毁编辑器 */
  destroy: destroyEditor,
})
</script>

<style scoped lang="scss">
.onlyoffice-editor {
  width: 100%;
  height: 100%;
  position: relative;

  .editor-container {
    width: 100%;
    // 使用 100% 高度，自动适应父容器
    height: 100%;
  }
}
</style>

