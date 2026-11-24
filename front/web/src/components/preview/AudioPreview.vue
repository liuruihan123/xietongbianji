<template>
  <div class="audio-preview">
    <a-spin :spinning="loading" tip="加载音频中...">
      <div v-if="!loading && audioUrl" class="audio-container">
        <div class="audio-info">
          <FileOutlined class="file-icon" />
          <div class="file-name">{{ fileName }}</div>
        </div>
        <audio
          ref="audioRef"
          :src="audioUrl"
          controls
          controlslist="nodownload"
          @loadedmetadata="onAudioLoaded"
          @error="onAudioError"
        >
          您的浏览器不支持音频播放
        </audio>
      </div>
      <a-empty v-else-if="!loading && !audioUrl" description="音频加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import { FileOutlined } from '@ant-design/icons-vue'
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
const audioUrl = ref('')
const audioRef = ref<HTMLAudioElement>()

/**
 * 加载音频
 */
async function loadAudio() {
  loading.value = true
  try {
    if (props.fileUrl) {
      audioUrl.value = props.fileUrl
    } else {
      audioUrl.value = await getFilePreviewUrl(props.fileId)
    }
  } catch (error: any) {
    message.error('音频加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 音频加载完成
 */
function onAudioLoaded() {
  emit('loaded')
}

/**
 * 音频加载错误
 */
function onAudioError(event: Event) {
  message.error('音频播放失败')
  emit('error', new Error('音频播放失败'))
}

/**
 * 播放
 */
function play() {
  audioRef.value?.play()
}

/**
 * 暂停
 */
function pause() {
  audioRef.value?.pause()
}

/**
 * 停止
 */
function stop() {
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value.currentTime = 0
  }
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadAudio()
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
  reload: loadAudio,
  play,
  pause,
  stop,
  audioElement: audioRef,
})
</script>

<style scoped lang="scss">
.audio-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .audio-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24px;

    .audio-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;

      .file-icon {
        font-size: 64px;
        color: #1890ff;
      }

      .file-name {
        font-size: 16px;
        color: #333;
        max-width: 400px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    audio {
      width: 400px;
      outline: none;
    }
  }
}
</style>

