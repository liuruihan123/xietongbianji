<template>
  <a-modal
    v-model:visible="visible"
    :title="title"
    :width="modalWidth"
    :footer="null"
    :destroyOnClose="true"
    wrapClassName="file-preview-modal"
    @cancel="handleClose"
  >
    <div class="file-preview-wrapper" :style="{ height: modalHeight }">
      <!-- 图片预览 -->
      <ImagePreview
        v-if="previewType === PreviewType.IMAGE"
        :file-id="fileId"
        :file-name="fileName"
        :file-url="fileUrl"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- PDF预览 -->
      <PdfPreview
        v-else-if="previewType === PreviewType.PDF"
        :file-id="fileId"
        :file-name="fileName"
        :file-url="fileUrl"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- Office文档预览 -->
      <OfficePreview
        v-else-if="previewType === PreviewType.OFFICE"
        :file-id="fileId"
        :file-name="fileName"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- 视频预览 -->
      <VideoPreview
        v-else-if="previewType === PreviewType.VIDEO"
        :file-id="fileId"
        :file-name="fileName"
        :file-url="fileUrl"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- 音频预览 -->
      <AudioPreview
        v-else-if="previewType === PreviewType.AUDIO"
        :file-id="fileId"
        :file-name="fileName"
        :file-url="fileUrl"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- 文本预览 -->
      <TextPreview
        v-else-if="previewType === PreviewType.TEXT"
        :file-id="fileId"
        :file-name="fileName"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- 代码预览 -->
      <CodePreview
        v-else-if="previewType === PreviewType.CODE"
        :file-id="fileId"
        :file-name="fileName"
        :options="options"
        @loaded="onLoaded"
        @error="onError"
      />

      <!-- 不支持预览 -->
      <a-empty v-else description="该文件类型不支持预览">
        <template #extra>
          <a-button type="primary" @click="handleDownload">下载文件</a-button>
        </template>
      </a-empty>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import ImagePreview from './ImagePreview.vue'
import PdfPreview from './PdfPreview.vue'
import OfficePreview from './OfficePreview.vue'
import VideoPreview from './VideoPreview.vue'
import AudioPreview from './AudioPreview.vue'
import TextPreview from './TextPreview.vue'
import CodePreview from './CodePreview.vue'
import { downloadFile } from '@/api/preview'
import { PreviewType, getPreviewType } from '@/types/preview'
import type { PreviewOptions } from '@/types/preview'

/**
 * 组件属性
 */
interface Props {
  /** 文件ID */
  fileId: number
  /** 文件名 */
  fileName: string
  /** 文件URL(可选) */
  fileUrl?: string
  /** 预览配置 */
  options?: PreviewOptions
}

const props = withDefaults(defineProps<Props>(), {
  fileUrl: '',
  options: () => ({}),
})

/**
 * 组件事件
 */
const emit = defineEmits<{
  close: []
  loaded: []
  error: [error: Error]
}>()

// 状态
const visible = ref(false)

// 预览类型
const previewType = computed(() => getPreviewType(props.fileName))

// 标题
const title = computed(() => `预览 - ${props.fileName}`)

// 模态框宽度
const modalWidth = computed(() => {
  if (previewType.value === PreviewType.OFFICE) return '90%'
  if (previewType.value === PreviewType.PDF) return '80%'
  return '70%'
})

// 模态框高度
const modalHeight = computed(() => {
  if (previewType.value === PreviewType.OFFICE) return '80vh'
  if (previewType.value === PreviewType.PDF) return '75vh'
  if (previewType.value === PreviewType.VIDEO) return '60vh'
  return '70vh'
})

/**
 * 打开预览
 */
function open() {
  visible.value = true
}

/**
 * 关闭预览
 */
function handleClose() {
  visible.value = false
  emit('close')
}

/**
 * 加载完成
 */
function onLoaded() {
  emit('loaded')
}

/**
 * 加载错误
 */
function onError(error: Error) {
  emit('error', error)
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
 * 暴露方法
 */
defineExpose({
  open,
  close: handleClose,
})
</script>

<style lang="scss">
.file-preview-modal {
  .ant-modal-body {
    padding: 0;
  }
}
</style>

<style scoped lang="scss">
.file-preview-wrapper {
  width: 100%;
  overflow: hidden;
}
</style>

