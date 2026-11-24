<template>
  <div class="cursor-sync">
    <!-- 其他用户的光标 -->
    <div
      v-for="user in otherUsers"
      :key="user.userId"
      class="remote-cursor"
      :style="getCursorStyle(user)"
    >
      <div class="cursor-flag" :style="{ backgroundColor: user.color }">
        <span class="cursor-name">{{ user.userName }}</span>
      </div>
      <div class="cursor-line" :style="{ backgroundColor: user.color }"></div>
    </div>

    <!-- 其他用户的选中范围 -->
    <div
      v-for="user in usersWithSelection"
      :key="`selection-${user.userId}`"
      class="remote-selection"
      :style="getSelectionStyle(user)"
    >
      <div
        class="selection-highlight"
        :style="{ backgroundColor: user.color, opacity: 0.2 }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OnlineUser } from '@/types/collaboration'

const props = defineProps<{
  users: OnlineUser[]
  currentUserId: number
}>()

/**
 * 其他用户(排除当前用户)
 */
const otherUsers = computed(() => {
  return props.users.filter(u => u.userId !== props.currentUserId && u.cursorPosition)
})

/**
 * 有选中范围的用户
 */
const usersWithSelection = computed(() => {
  return props.users.filter(u => u.userId !== props.currentUserId && u.selection)
})

/**
 * 获取光标样式
 */
function getCursorStyle(user: OnlineUser) {
  if (!user.cursorPosition) {
    return { display: 'none' }
  }

  // 这里需要根据实际的编辑器坐标系统来计算位置
  // 以下是示例代码,实际使用时需要根据OnlyOffice的API来获取准确位置
  const { line, column } = user.cursorPosition
  
  return {
    position: 'absolute',
    top: `${line * 20}px`, // 假设每行高度20px
    left: `${column * 8}px`, // 假设每个字符宽度8px
    zIndex: 1000,
  }
}

/**
 * 获取选中范围样式
 */
function getSelectionStyle(user: OnlineUser) {
  if (!user.selection) {
    return { display: 'none' }
  }

  const { start, end } = user.selection
  
  // 计算选中范围的位置和大小
  const top = Math.min(start.line, end.line) * 20
  const left = Math.min(start.column, end.column) * 8
  const height = Math.abs(end.line - start.line + 1) * 20
  const width = Math.abs(end.column - start.column) * 8

  return {
    position: 'absolute',
    top: `${top}px`,
    left: `${left}px`,
    width: `${width}px`,
    height: `${height}px`,
    zIndex: 999,
  }
}
</script>

<style scoped lang="scss">
.cursor-sync {
  position: relative;
  pointer-events: none;

  .remote-cursor {
    position: absolute;
    pointer-events: none;

    .cursor-flag {
      position: absolute;
      top: -20px;
      left: 0;
      padding: 2px 6px;
      border-radius: 3px;
      white-space: nowrap;
      font-size: 12px;
      color: white;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);

      .cursor-name {
        font-weight: 500;
      }
    }

    .cursor-line {
      position: absolute;
      top: 0;
      left: 0;
      width: 2px;
      height: 20px;
      animation: blink 1s infinite;
    }
  }

  .remote-selection {
    position: absolute;
    pointer-events: none;

    .selection-highlight {
      width: 100%;
      height: 100%;
      border-radius: 2px;
    }
  }
}

@keyframes blink {
  0%, 49% {
    opacity: 1;
  }
  50%, 100% {
    opacity: 0;
  }
}
</style>

