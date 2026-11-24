<template>
  <div class="app-page min-h-screen flex flex-col">
    <AppHeader
      :show-search="showSearch"
      :show-notification="showNotification"
      :show-sidebar-toggle="showSidebarToggle"
      :title="title"
      @search="handleSearch"
      @notification-click="handleNotificationClick"
      @sidebar-toggle="handleSidebarToggle"
    />

    <main class="main-content flex-1 pt-16">
      <slot />
    </main>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

// Props 定义
interface Props {
  showSearch?: boolean
  showNotification?: boolean
  showSidebarToggle?: boolean
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  showSearch: false,
  showNotification: false,
  showSidebarToggle: false,
  title: ''
})

// Emits 定义
const emit = defineEmits<{
  'search': [keyword: string]
  'notification-click': []
  'sidebar-toggle': []
}>()

// 事件处理
function handleSearch(keyword: string) {
  emit('search', keyword)
}

function handleNotificationClick() {
  emit('notification-click')
}

function handleSidebarToggle() {
  emit('sidebar-toggle')
}
</script>

<style scoped>
/* Tailwind CSS 已经提供了大部分样式 */
</style>
