<template>
  <div class="code-preview">
    <a-spin :spinning="loading" tip="加载代码中...">
      <div v-if="!loading && content" class="code-container">
        <!-- 工具栏 -->
        <div class="code-toolbar">
          <a-space>
            <a-tag color="blue">{{ language }}</a-tag>
            <a-button size="small" @click="handleCopy">
              <template #icon><CopyOutlined /></template>
              复制
            </a-button>
            <a-button size="small" @click="handleDownload">
              <template #icon><DownloadOutlined /></template>
              下载
            </a-button>
            <a-divider type="vertical" />
            <span class="file-info">{{ fileName }} ({{ formatFileSize(content.length) }})</span>
          </a-space>
        </div>

        <!-- 代码内容 -->
        <div class="code-content">
          <pre><code ref="codeRef" :class="`language-${language}`">{{ content }}</code></pre>
        </div>
      </div>
      <a-empty v-else-if="!loading && !content" description="代码加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { CopyOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { getFileContent, downloadFile } from '@/api/preview'
import { getCodeLanguage } from '@/types/preview'
import type { CodePreviewOptions } from '@/types/preview'
import { copyToClipboard } from '@/utils/copy'

/**
 * 组件属性
 */
interface Props {
  /** 文件ID */
  fileId: number
  /** 文件名 */
  fileName: string
  /** 预览配置 */
  options?: CodePreviewOptions
}

const props = withDefaults(defineProps<Props>(), {
  options: () => ({}),
})

/**
 * 组件事件
 */
const emit = defineEmits<{
  error: [error: Error]
  loaded: []
}>()

// 状态
const loading = ref(false)
const content = ref('')
const codeRef = ref<HTMLElement>()

// 编程语言
const language = ref(props.options.language || getCodeLanguage(props.fileName))

/**
 * 加载代码内容
 */
async function loadContent() {
  loading.value = true
  try {
    content.value = await getFileContent(props.fileId)
    emit('loaded')

    // 等待DOM更新后高亮代码
    await nextTick()
    highlightCode()
  } catch (error: any) {
    message.error('代码加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 高亮代码
 */
function highlightCode() {
  if (codeRef.value) {
    hljs.highlightElement(codeRef.value)
  }
}

/**
 * 复制代码
 */
async function handleCopy() {
  try {
    await navigator.clipboard.writeText(content.value)
    message.success('复制成功')
  } catch (error) {
    message.error('复制失败')
  }
}

/**
 * 下载文件
 */
async function handleDownload() {
  try {
    // 获取当前用户的authToken（如果已认证）
    const token = localStorage.getItem('ACCESS_TOKEN')
    await downloadFile(props.fileId, props.fileName)
    message.success('下载成功')
  } catch (error) {
    message.error('下载失败')
  }
}

/**
 * 格式化文件大小
 */
function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadContent()
})

/**
 * 暴露方法
 */
defineExpose({
  reload: loadContent,
  content,
  language,
})
</script>

<style scoped lang="scss">
.code-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .code-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;

    .code-toolbar {
      padding: 12px;
      background-color: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;

      .file-info {
        font-size: 14px;
        color: #666;
      }
    }

    .code-content {
      flex: 1;
      overflow: auto;
      background-color: #f6f8fa;

      pre {
        margin: 0;
        padding: 16px;
        height: 100%;

        code {
          font-family: 'Courier New', Courier, monospace;
          font-size: 14px;
          line-height: 1.6;
        }
      }
    }
  }
}
</style>

