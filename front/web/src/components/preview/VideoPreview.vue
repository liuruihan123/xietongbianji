<template>
  <div class="video-preview">
    <a-spin :spinning="loading" tip="加载视频中...">
      <div v-if="!loading && videoUrl" class="video-container">
        <video
          ref="videoRef"
          :src="videoUrl"
          controls
          controlslist="nodownload"
          @loadedmetadata="onVideoLoaded"
          @error="onVideoError"
        >
          您的浏览器不支持视频播放
        </video>
      </div>
      <a-empty v-else-if="!loading && !videoUrl" description="视频加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import { getFilePreviewUrl } from '@/api/preview'
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
  error: [error: Error]
  loaded: []
}>()

// 状态
const loading = ref(false)
const videoUrl = ref('')
const videoRef = ref<HTMLVideoElement>()

/**
 * 加载视频
 */
async function loadVideo() {
  loading.value = true
  try {
    if (props.fileUrl) {
      videoUrl.value = props.fileUrl
    } else {
      videoUrl.value = await getFilePreviewUrl(props.fileId)
    }
  } catch (error: any) {
    message.error('视频加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 视频加载完成
 */
function onVideoLoaded() {
  emit('loaded')
}

/**
 * 视频加载错误
 */
function onVideoError(event: Event) {
  message.error('视频播放失败')
  emit('error', new Error('视频播放失败'))
}

/**
 * 播放
 */
function play() {
  videoRef.value?.play()
}

/**
 * 暂停
 */
function pause() {
  videoRef.value?.pause()
}

/**
 * 停止
 */
function stop() {
  if (videoRef.value) {
    videoRef.value.pause()
    videoRef.value.currentTime = 0
  }
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadVideo()
})

/**
 * 组件卸载
 */
onBeforeUnmount(() => {
  stop()
})

/**
 * 暴露方法
 */
defineExpose({
  reload: loadVideo,
  play,
  pause,
  stop,
  videoElement: videoRef,
})
</script>

<style scoped lang="scss">
.video-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #000;

  .video-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    video {
      max-width: 100%;
      max-height: 100%;
      outline: none;
    }
  }
}
</style>

