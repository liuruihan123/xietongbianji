<template>
  <div
    class="local-avatar"
    :style="{
      width: size + 'px',
      height: size + 'px',
      backgroundColor: backgroundColor,
      fontSize: fontSize + 'px',
      borderRadius: rounded ? '50%' : borderRadius + 'px'
    }"
    :title="name"
  >
    <img
      v-if="src && !imageError"
      :src="src"
      :alt="name"
      class="avatar-image"
      @error="handleImageError"
    />
    <span v-else class="avatar-text">{{ initials }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  /** 头像图片地址 */
  src?: string
  /** 用户名称 */
  name: string
  /** 头像大小（像素） */
  size?: number
  /** 是否圆形 */
  rounded?: boolean
  /** 圆角大小 */
  borderRadius?: number
  /** 自定义背景色 */
  customColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  src: '',
  name: 'User',
  size: 32,
  rounded: true,
  borderRadius: 4,
  customColor: ''
})

const imageError = ref(false)

// 处理图片加载失败
const handleImageError = () => {
  imageError.value = true
}

// 获取用户名首字母（支持中英文）
const initials = computed(() => {
  if (!props.name) return 'U'
  
  const name = props.name.trim()
  
  // 如果是中文名，取前两个字
  if (/[\u4e00-\u9fa5]/.test(name)) {
    return name.substring(0, Math.min(2, name.length))
  }
  
  // 如果是英文名，取首字母（最多2个）
  const words = name.split(/\s+/).filter(w => w.length > 0)
  if (words.length === 0) return 'U'
  
  if (words.length === 1) {
    return words[0].charAt(0).toUpperCase()
  }
  
  return (words[0].charAt(0) + words[words.length - 1].charAt(0)).toUpperCase()
})

// 根据名称生成一致的背景色
const backgroundColor = computed(() => {
  if (props.customColor) return props.customColor
  if (props.src && !imageError.value) return 'transparent'
  
  // 预定义的柔和配色方案
  const colors = [
    '#FF6B6B', // 红色
    '#4ECDC4', // 青色
    '#45B7D1', // 蓝色
    '#96CEB4', // 绿色
    '#FFEAA7', // 黄色
    '#DDA0DD', // 紫色
    '#98D8C8', // 薄荷绿
    '#F7DC6F', // 金黄色
    '#BB8FCE', // 淡紫色
    '#85C1E9', // 天蓝色
    '#F8B88B', // 橙色
    '#FAD7A0', // 浅橙色
    '#AED6F1', // 浅蓝色
    '#A9DFBF', // 浅绿色
    '#F5B7B1', // 粉红色
  ]
  
  // 使用名称生成哈希值
  let hash = 0
  for (let i = 0; i < props.name.length; i++) {
    hash = props.name.charCodeAt(i) + ((hash << 5) - hash)
    hash = hash & hash // Convert to 32bit integer
  }
  
  return colors[Math.abs(hash) % colors.length]
})

// 根据头像大小计算字体大小
const fontSize = computed(() => {
  return Math.floor(props.size * 0.4)
})
</script>

<style scoped>
.local-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  text-align: center;
  user-select: none;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  line-height: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
</style>

