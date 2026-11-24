<template>
  <div class="pdf-preview">
    <a-spin :spinning="loading" tip="加载PDF中...">
      <div v-if="!loading && pdfUrl" class="pdf-container">
        <!-- 工具栏 -->
        <div v-if="showToolbar" class="pdf-toolbar">
          <a-space>
            <a-button size="small" @click="previousPage" :disabled="currentPage <= 1">
              <template #icon><LeftOutlined /></template>
              上一页
            </a-button>
            <span class="page-info">
              {{ currentPage }} / {{ totalPages }}
            </span>
            <a-button size="small" @click="nextPage" :disabled="currentPage >= totalPages">
              下一页
              <template #icon><RightOutlined /></template>
            </a-button>
            <a-divider type="vertical" />
            <a-button size="small" @click="zoomOut" :disabled="scale <= 0.5">
              <template #icon><ZoomOutOutlined /></template>
            </a-button>
            <span class="scale-info">{{ Math.round(scale * 100) }}%</span>
            <a-button size="small" @click="zoomIn" :disabled="scale >= 3">
              <template #icon><ZoomInOutlined /></template>
            </a-button>
            <a-divider type="vertical" />
            <a-button size="small" @click="handlePrint">
              <template #icon><PrinterOutlined /></template>
              打印
            </a-button>
            <a-button size="small" @click="handleDownload">
              <template #icon><DownloadOutlined /></template>
              下载
            </a-button>
          </a-space>
        </div>

        <!-- PDF内容 -->
        <div class="pdf-content" ref="pdfContentRef">
          <VuePDF
            v-if="pdf"
            :pdf="pdf"
            :page="currentPage"
            :scale="scale"
            @loaded="onPdfLoaded"
          />
        </div>
      </div>
      <a-empty v-else-if="!loading && !pdfUrl" description="PDF加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  LeftOutlined,
  RightOutlined,
  ZoomInOutlined,
  ZoomOutOutlined,
  PrinterOutlined,
  DownloadOutlined,
} from '@ant-design/icons-vue'
import { VuePDF, usePDF } from '@tato30/vue-pdf'
import { getFilePreviewUrl, downloadFile } from '@/api/preview'
import type { PdfPreviewOptions } from '@/types/preview'

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
  options?: PdfPreviewOptions
}

const props = withDefaults(defineProps<Props>(), {
  fileUrl: '',
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
const pdfUrl = ref('')
const currentPage = ref(1)
const totalPages = ref(0)
const scale = ref(1)
const pdfContentRef = ref<HTMLElement>()

// 是否显示工具栏
const showToolbar = computed(() => props.options.showToolbar !== false)

// PDF对象
let pdf: any = null
let pdfInstance: any = null

/**
 * 加载PDF
 */
async function loadPdf() {
  loading.value = true
  try {
    if (props.fileUrl) {
      pdfUrl.value = props.fileUrl
    } else {
      pdfUrl.value = await getFilePreviewUrl(props.fileId)
    }

    // 使用usePDF加载PDF
    const { pdf: pdfObj, pages } = usePDF(pdfUrl.value)
    pdf = pdfObj
    pdfInstance = pdfObj

    // 等待PDF加载完成
    await pdf.value.promise
    totalPages.value = pages.value
    emit('loaded')
  } catch (error: any) {
    message.error('PDF加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * PDF加载完成回调
 */
function onPdfLoaded() {
  console.log('PDF加载完成')
}

/**
 * 上一页
 */
function previousPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

/**
 * 下一页
 */
function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

/**
 * 放大
 */
function zoomIn() {
  if (scale.value < 3) {
    scale.value += 0.25
  }
}

/**
 * 缩小
 */
function zoomOut() {
  if (scale.value > 0.5) {
    scale.value -= 0.25
  }
}

/**
 * 打印
 */
async function handlePrint() {
  try {
    if (pdfInstance) {
      const doc = await pdfInstance.value.promise
      // 使用pdf.js的打印功能
      window.print()
    }
  } catch (error) {
    message.error('打印失败')
  }
}

/**
 * 下载
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
 * 组件挂载
 */
onMounted(() => {
  loadPdf()
})

/**
 * 暴露方法
 */
defineExpose({
  reload: loadPdf,
  currentPage,
  totalPages,
  scale,
  previousPage,
  nextPage,
  zoomIn,
  zoomOut,
})
</script>

<style scoped lang="scss">
.pdf-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .pdf-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;

    .pdf-toolbar {
      padding: 12px;
      background-color: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;
      display: flex;
      align-items: center;
      justify-content: center;

      .page-info,
      .scale-info {
        font-size: 14px;
        color: #666;
        min-width: 80px;
        text-align: center;
      }
    }

    .pdf-content {
      flex: 1;
      overflow: auto;
      background-color: #525659;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;

      :deep(canvas) {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
    }
  }
}
</style>

