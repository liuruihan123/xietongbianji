<template>
  <div class="editor-container">
    <a-spin :spinning="loading" tip="加载编辑器中..." class="editor-spin">
      <OnlyOfficeEditor
        v-if="editorConfig"
        ref="editorRef"
        :config="editorConfig"
        @app-ready="$emit('app-ready')"
        @ready="$emit('ready')"
        @save="$emit('save')"
        @state-change="$emit('state-change', $event)"
        @error="$emit('error', $event)"
      />
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import OnlyOfficeEditor from '@/components/editor/OnlyOfficeEditor.vue'
import type { OnlyOfficeConfig } from '@/types/editor'

// Props 定义
interface Props {
  loading: boolean
  editorConfig: OnlyOfficeConfig | null
}

defineProps<Props>()

// 事件定义
defineEmits<{
  'app-ready': []
  ready: []
  save: []
  'state-change': [hasChanges: boolean]
  error: [error: any]
}>()

// 编辑器引用
const editorRef = ref()

// 暴露刷新方法给父组件
defineExpose({
  refresh: () => {
    editorRef.value?.refresh()
  }
})
</script>

<style scoped lang="scss">
.editor-container {
  flex: 1;
  min-height: 0; // 防止 flex 子项溢出
  overflow: hidden;
  background: white;
  display: flex;
  flex-direction: column;

  :deep(.editor-spin) {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  :deep(.ant-spin-container) {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  :deep(.onlyoffice-editor) {
    flex: 1;
    min-height: 0;
  }
}
</style>

