<template>
  <div class="text-preview">
    <a-spin :spinning="loading" tip="加载文本中...">
      <div v-if="!loading && content" class="text-container">
        <!-- 工具栏 -->
        <div class="text-toolbar">
          <a-space>
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

        <!-- 文本内容 -->
        <div class="text-content">
          <pre>{{ content }}</pre>
        </div>
      </div>
      <a-empty v-else-if="!loading && !content" description="文本加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { CopyOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import { getFileContent, downloadFile } from '@/api/preview'
import { copyToClipboard } from '@/utils/copy'
import type { PreviewOptions } from '@/types/preview'

/**
 * 组件属性
 */
interface Props {
  /** 文件ID */
  fileId: number
  /** 文件名 */
  fileName: string
  /** 预览配置 */
  options?: PreviewOptions
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

/**
 * 加载文本内容
 */
async function loadContent() {
  loading.value = true
  try {
    content.value = await getFileContent(props.fileId)
    emit('loaded')
  } catch (error: any) {
    message.error('文本加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 复制内容
 */
async function handleCopy() {
  try {
    await copyToClipboard(content.value)
    message.success('复制成功')
  } catch (error: any) {
    console.error('复制文本失败:', error)
    message.error(error.message || '复制失败,请手动复制')
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
})
</script>

<style scoped lang="scss">
.text-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .text-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;

    .text-toolbar {
      padding: 12px;
      background-color: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;

      .file-info {
        font-size: 14px;
        color: #666;
      }
    }

    .text-content {
      flex: 1;
      overflow: auto;
      padding: 16px;
      background-color: #fff;

      pre {
        margin: 0;
        font-family: 'Courier New', Courier, monospace;
        font-size: 14px;
        line-height: 1.6;
        white-space: pre-wrap;
        word-wrap: break-word;
      }
    }
  }
}
</style>

