<template>
  <div class="office-preview">
    <a-spin :spinning="loading" tip="加载文档中...">
      <div v-if="!loading && editorConfig" class="office-container">
        <OnlyOfficeEditor
          :config="editorConfig"
          :height="'100%'"
          @ready="onEditorReady"
          @error="onEditorError"
        />
      </div>
      <a-empty v-else-if="!loading && !editorConfig" description="文档加载失败" />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import OnlyOfficeEditor from '@/components/editor/OnlyOfficeEditor.vue'
import { getEditorConfig } from '@/api/editor'
import type { EditorConfig } from '@/types/editor'
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
const editorConfig = ref<EditorConfig>()

/**
 * 加载编辑器配置
 */
async function loadEditorConfig() {
  loading.value = true
  try {
    const config = await getEditorConfig(props.fileId)

    // 设置为只读模式
    editorConfig.value = {
      ...config,
      editorConfig: {
        ...config.editorConfig,
        mode: 'view', // 只读模式
        customization: {
          ...config.editorConfig.customization,
          compactHeader: true,
          compactToolbar: true,
          hideRightMenu: true,
          hideRulers: true,
          toolbarNoTabs: true,
          autosave: false,
          forcesave: false,
          comments: false,
          chat: false,
          feedback: false,
          help: false,
          plugins: false,
        },
      },
    }
  } catch (error: any) {
    message.error('文档加载失败')
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 编辑器就绪
 */
function onEditorReady() {
  emit('loaded')
}

/**
 * 编辑器错误
 */
function onEditorError(error: Error) {
  message.error('文档预览失败')
  emit('error', error)
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadEditorConfig()
})

/**
 * 暴露方法
 */
defineExpose({
  reload: loadEditorConfig,
})
</script>

<style scoped lang="scss">
.office-preview {
  width: 100%;
  height: 100%;

  .office-container {
    width: 100%;
    height: 100%;
  }
}
</style>

