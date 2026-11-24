<template>
  <div v-if="visible" class="editor-footer">
    <!-- 预留底部区域,可用于显示状态栏、快捷操作等 -->
    <slot>
      <!-- 默认内容为空 -->
    </slot>
  </div>
</template>

<script setup lang="ts">
// Props 定义
interface Props {
  visible?: boolean
}

withDefaults(defineProps<Props>(), {
  visible: true
})
</script>

<style scoped lang="scss">
.editor-footer {
  padding: 8px 24px;
  background: white;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 0px;
}
</style>

