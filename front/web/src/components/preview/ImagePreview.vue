<template>
  <div class="image-preview">
    <a-spin :spinning="loading" tip="加载中...">
      <div v-if="!loading && imageUrl" class="image-container">
        <viewer :images="[imageUrl]" :options="viewerOptions" @inited="onViewerInited">
          <img :src="imageUrl" :alt="fileName" />
        </viewer>
      </div>
      <a-empty v-else-if="!loading && !imageUrl" description="图片加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { component as Viewer } from 'v-viewer'
import 'viewerjs/dist/viewer.css'
import { getFilePreviewUrl } from '@/api/preview'
import type { ImagePreviewOptions } from '@/types/preview'

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
  options?: ImagePreviewOptions
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
const imageUrl = ref('')
let viewer: any = null

/**
 * Viewer配置
 */
const viewerOptions = computed(() => ({
  toolbar: props.options.showToolbar !== false,
  navbar: props.options.showNavbar !== false,
  title: props.options.showTitle !== false,
  tooltip: true,
  movable: true,
  zoomable: true,
  rotatable: true,
  scalable: true,
  transition: true,
  fullscreen: true,
  keyboard: true,
  url: 'data-source',
}))

/**
 * Viewer初始化回调
 */
function onViewerInited(viewerInstance: any) {
  viewer = viewerInstance
  // 自动显示查看器
  viewer.show()
}

/**
 * 加载图片
 */
async function loadImage() {
  loading.value = true
  try {
    if (props.fileUrl) {
      imageUrl.value = props.fileUrl
    } else {
      imageUrl.value = await getFilePreviewUrl(props.fileId)
    }
    emit('loaded')
  } catch (error: any) {
    message.error('图片加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadImage()
})

/**
 * 暴露方法
 */
defineExpose({
  viewer,
  reload: loadImage,
})
</script>

<style scoped lang="scss">
.image-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .image-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      max-width: 100%;
      max-height: 100%;
      cursor: pointer;
    }
  }
}

:deep(.viewer-backdrop) {
  background-color: rgba(0, 0, 0, 0.9);
}

:deep(.viewer-toolbar) {
  background-color: rgba(0, 0, 0, 0.5);
}

:deep(.viewer-title) {
  color: #fff;
  font-size: 14px;
}
</style>

