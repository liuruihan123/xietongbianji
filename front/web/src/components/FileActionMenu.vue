<template>
  <div class="relative" ref="menuRef">
    <!-- Three-dot menu button -->
    <button
      ref="triggerRef"
      @click.stop="toggleMenu"
      class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
      :class="{ 'bg-gray-100 text-gray-600': isMenuOpen }"
    >
      <i class="fa fa-ellipsis-h"></i>
    </button>

    <!-- Dropdown menu - 使用 Teleport 传送到 body,避免被表格裁剪 -->
    <Teleport to="body">
      <div
        v-if="isMenuOpen"
        ref="dropdownRef"
        class="fixed w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-[9999]"
        :style="dropdownStyle"
        @click.stop
      >
        <div class="py-1">
          <!-- Download option -->
          <button
            @click="handleDownload"
            class="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-50 flex items-center space-x-2"
          >
            <i class="fa fa-download text-green-600"></i>
            <span>下载</span>
          </button>

          <!-- Delete option -->
          <button
            @click="handleDelete"
            class="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center space-x-2"
          >
            <i class="fa fa-trash"></i>
            <span>删除</span>
          </button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'

interface FileItem {
  id: number
  name: string
  type?: string
  extension?: string
  size?: number
  path?: string
  [key: string]: any
}

interface Props {
  file: FileItem
}

interface Emits {
  (e: 'download', file: FileItem): void
  (e: 'delete', file: FileItem): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const menuRef = ref<HTMLElement>()
const triggerRef = ref<HTMLElement>()
const dropdownRef = ref<HTMLElement>()
const isMenuOpen = ref(false)

// 计算下拉菜单的位置
const dropdownStyle = computed(() => {
  if (!triggerRef.value) {
    return { top: '0px', left: '0px' }
  }

  const rect = triggerRef.value.getBoundingClientRect()
  const dropdownHeight = 100 // 预估菜单高度
  const viewportHeight = window.innerHeight

  // 判断是否有足够空间在下方显示
  const spaceBelow = viewportHeight - rect.bottom
  const shouldShowAbove = spaceBelow < dropdownHeight && rect.top > dropdownHeight

  let top: number
  if (shouldShowAbove) {
    // 在上方显示
    top = rect.top - dropdownHeight
  } else {
    // 在下方显示
    top = rect.bottom + 4
  }

  // 右对齐
  const left = rect.right - 192 // 192px = w-48

  return {
    top: `${Math.max(4, top)}px`,
    left: `${Math.max(4, left)}px`,
  }
})

// 监听菜单打开状态,更新位置
watch(isMenuOpen, async (newVal) => {
  if (newVal) {
    await nextTick()
    // 菜单打开后可以进行额外的位置调整
  }
})

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
}

function handleDownload() {
  isMenuOpen.value = false
  emit('download', props.file)
}

function handleDelete() {
  isMenuOpen.value = false
  emit('delete', props.file)
}

function handleClickOutside(event: MouseEvent) {
  const target = event.target as Node

  // 检查点击是否在菜单按钮或下拉菜单内
  const isClickInside =
    (menuRef.value && menuRef.value.contains(target)) ||
    (dropdownRef.value && dropdownRef.value.contains(target))

  if (!isClickInside) {
    isMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // 监听滚动事件,关闭菜单
  window.addEventListener('scroll', () => {
    isMenuOpen.value = false
  }, true)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('scroll', () => {
    isMenuOpen.value = false
  }, true)
})
</script>
