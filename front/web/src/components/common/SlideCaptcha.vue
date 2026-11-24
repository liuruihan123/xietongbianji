<template>
  <div class="slide-captcha" v-show="visible">
    <div class="captcha-modal" @click="handleModalClick">
      <div class="captcha-content" @click.stop>
        <div class="captcha-header">
          <span class="captcha-title">安全验证</span>
          <button class="captcha-close" @click="close">×</button>
        </div>

        <div class="captcha-body">
          <div class="captcha-image-container" ref="imageContainer">
            <canvas
              ref="backgroundCanvas"
              class="captcha-background"
              :width="canvasWidth"
              :height="canvasHeight"
            ></canvas>
            <canvas
              ref="puzzleCanvas"
              class="captcha-puzzle"
              :width="puzzleBlockWidth"
              :height="puzzleBlockHeight"
              :style="puzzleStyle"
            ></canvas>
          </div>

          <div class="captcha-slider-container">
            <div class="slider-track" ref="sliderTrack">
              <div class="slider-progress" :style="progressStyle"></div>
              <div class="slider-text" :class="{ 'slider-text-success': isSuccess }">
                {{ sliderText }}
              </div>
              <div
                class="slider-button"
                ref="sliderButton"
                :class="{ 'slider-button-success': isSuccess }"
                :style="sliderButtonStyle"
                @mousedown="handleMouseDown"
                @touchstart="handleTouchStart"
              >
                <i class="slider-icon" :class="sliderIconClass"></i>
              </div>
            </div>
          </div>

          <div class="captcha-actions">
            <button class="refresh-btn" @click="refresh" :disabled="isLoading">
              <i class="refresh-icon">🔄</i>
              刷新
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch, toRefs } from 'vue'

interface Props {
  visible?: boolean
  width?: number
  height?: number
  puzzleSize?: number
  tolerance?: number
}

interface Emits {
  (e: 'success'): void
  (e: 'fail'): void
  (e: 'close'): void
  (e: 'update:visible', value: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  width: 350,
  height: 200,
  puzzleSize: 60,
  tolerance: 10
})

const emit = defineEmits<Emits>()

// 画布相关
const backgroundCanvas = ref<HTMLCanvasElement>()
const puzzleCanvas = ref<HTMLCanvasElement>()
const imageContainer = ref<HTMLDivElement>()
const sliderTrack = ref<HTMLDivElement>()
const sliderButton = ref<HTMLDivElement>()

// 状态管理
const isLoading = ref(false)
const isSuccess = ref(false)
const isDragging = ref(false)

// 拼图相关数据
const puzzleData = reactive({
  x: 0,
  y: 0,
  currentX: 0,
  path: null as Path2D | null
})

// 滑块相关数据
const sliderData = reactive({
  startX: 0,
  currentX: 0,
  maxX: 0
})

// 动画相关
const animationFrame = ref<number | null>(null)

// 计算属性
const canvasWidth = computed(() => props.width)
const canvasHeight = computed(() => props.height)

// 拼图块尺寸
const puzzleBlockWidth = computed(() => 60)
const puzzleBlockHeight = computed(() => 60)

const puzzleStyle = computed(() => ({
  left: `${puzzleData.currentX}px`,
  top: `${puzzleData.y}px`
}))

const sliderButtonStyle = computed(() => ({
  transform: `translateX(${sliderData.currentX}px)`
}))

// 进度条样式
const progressStyle = computed(() => ({
  width: `${(sliderData.currentX / sliderData.maxX) * 100}%`,
  backgroundColor: isSuccess.value ? '#52c41a' : '#1890ff'
}))

const sliderText = computed(() => {
  if (isSuccess.value) return '验证成功'
  if (isDragging.value) return '松开完成验证'
  return '向右滑动完成验证'
})

const sliderIconClass = computed(() => {
  if (isSuccess.value) return 'icon-success'
  return 'icon-arrow'
})

// 生成随机背景图案
const generateBackground = () => {
  const canvas = backgroundCanvas.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 创建渐变背景
  const gradient = ctx.createLinearGradient(0, 0, canvasWidth.value, canvasHeight.value)
  gradient.addColorStop(0, '#e3f2fd')
  gradient.addColorStop(0.5, '#bbdefb')
  gradient.addColorStop(1, '#90caf9')
  
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, canvasWidth.value, canvasHeight.value)
  
  // 添加一些装饰性图案
  ctx.fillStyle = 'rgba(255, 255, 255, 0.3)'
  for (let i = 0; i < 20; i++) {
    const x = Math.random() * canvasWidth.value
    const y = Math.random() * canvasHeight.value
    const radius = Math.random() * 3 + 1
    ctx.beginPath()
    ctx.arc(x, y, radius, 0, Math.PI * 2)
    ctx.fill()
  }
  
  // 生成拼图位置
  puzzleData.x = Math.random() * (canvasWidth.value - props.puzzleSize - 50) + 50
  puzzleData.y = Math.random() * (canvasHeight.value - props.puzzleSize - 20) + 10
  puzzleData.currentX = 0
  
  // 在背景上绘制拼图缺口
  drawPuzzleHole(ctx, puzzleData.x, puzzleData.y)
}

// 创建拼图路径
const createPuzzlePath = (x: number, y: number): Path2D => {
  const size = props.puzzleSize
  const r = size * 0.12 // 凸起半径，稍微减小以获得更好的形状
  const path = new Path2D()

  // 更精确的拼图形状
  path.moveTo(x, y)

  // 上边 - 带凸起
  path.lineTo(x + size * 0.35, y)
  path.arc(x + size * 0.5, y - r * 0.8, r, Math.PI * 0.2, Math.PI * 0.8, false)
  path.lineTo(x + size, y)

  // 右边 - 带凸起
  path.lineTo(x + size, y + size * 0.35)
  path.arc(x + size + r * 0.8, y + size * 0.5, r, Math.PI * 1.2, Math.PI * 1.8, false)
  path.lineTo(x + size, y + size)

  // 下边 - 带凹陷
  path.lineTo(x + size * 0.65, y + size)
  path.arc(x + size * 0.5, y + size + r * 0.8, r, Math.PI * 1.8, Math.PI * 1.2, true)
  path.lineTo(x, y + size)

  // 左边 - 带凹陷
  path.lineTo(x, y + size * 0.65)
  path.arc(x - r * 0.8, y + size * 0.5, r, Math.PI * 0.8, Math.PI * 0.2, true)
  path.lineTo(x, y)

  path.closePath()
  return path
}

// 绘制拼图缺口
const drawPuzzleHole = (ctx: CanvasRenderingContext2D, x: number, y: number) => {
  const path = createPuzzlePath(x, y)
  puzzleData.path = path

  ctx.save()

  // 绘制内阴影效果
  ctx.shadowColor = 'rgba(0, 0, 0, 0.3)'
  ctx.shadowBlur = 4
  ctx.shadowOffsetX = 2
  ctx.shadowOffsetY = 2

  // 创建缺口
  ctx.globalCompositeOperation = 'destination-out'
  ctx.fill(path)
  ctx.restore()

  // 绘制缺口边框
  ctx.save()
  ctx.strokeStyle = 'rgba(0, 0, 0, 0.4)'
  ctx.lineWidth = 1.5
  ctx.stroke(path)
  ctx.restore()
}

// 生成拼图块
const generatePuzzle = () => {
  const canvas = puzzleCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.clearRect(0, 0, props.puzzleSize, props.puzzleSize)

  if (!puzzleData.path) return

  // 使用相同的路径，但偏移到canvas原点
  const path = createPuzzlePath(0, 0)

  ctx.save()

  // 添加阴影效果
  ctx.shadowColor = 'rgba(0, 0, 0, 0.3)'
  ctx.shadowBlur = 6
  ctx.shadowOffsetX = 3
  ctx.shadowOffsetY = 3

  // 创建拼图块渐变
  const gradient = ctx.createLinearGradient(0, 0, props.puzzleSize, props.puzzleSize)
  gradient.addColorStop(0, '#4fc3f7')
  gradient.addColorStop(0.3, '#29b6f6')
  gradient.addColorStop(0.7, '#0288d1')
  gradient.addColorStop(1, '#01579b')

  ctx.fillStyle = gradient
  ctx.fill(path)

  ctx.restore()

  // 添加高光效果
  ctx.save()
  const highlightGradient = ctx.createLinearGradient(0, 0, props.puzzleSize * 0.6, props.puzzleSize * 0.6)
  highlightGradient.addColorStop(0, 'rgba(255, 255, 255, 0.6)')
  highlightGradient.addColorStop(1, 'rgba(255, 255, 255, 0)')

  ctx.fillStyle = highlightGradient
  ctx.fill(path)
  ctx.restore()

  // 添加边框
  ctx.save()
  ctx.strokeStyle = '#01579b'
  ctx.lineWidth = 1.5
  ctx.stroke(path)
  ctx.restore()

  // 添加中心装饰点
  ctx.save()
  ctx.fillStyle = 'rgba(255, 255, 255, 0.8)'
  ctx.beginPath()
  ctx.arc(props.puzzleSize / 2, props.puzzleSize / 2, 6, 0, Math.PI * 2)
  ctx.fill()

  ctx.fillStyle = 'rgba(1, 87, 155, 0.6)'
  ctx.beginPath()
  ctx.arc(props.puzzleSize / 2, props.puzzleSize / 2, 3, 0, Math.PI * 2)
  ctx.fill()
  ctx.restore()
}

// 优化的拼图块位置更新
const updatePuzzlePosition = (sliderX: number) => {
  // 直接映射滑块位置到拼图位置，避免复杂计算
  const maxSliderX = sliderData.maxX
  const maxPuzzleX = canvasWidth.value - props.puzzleSize

  if (maxSliderX > 0) {
    puzzleData.currentX = (sliderX / maxSliderX) * maxPuzzleX
  } else {
    puzzleData.currentX = 0
  }
}

// 使用requestAnimationFrame优化动画
const smoothUpdatePosition = (targetX: number) => {
  if (animationFrame.value) {
    cancelAnimationFrame(animationFrame.value)
  }

  const animate = () => {
    const currentX = sliderData.currentX
    const diff = targetX - currentX

    if (Math.abs(diff) > 0.5) {
      sliderData.currentX += diff * 0.3
      updatePuzzlePosition(sliderData.currentX)
      animationFrame.value = requestAnimationFrame(animate)
    } else {
      sliderData.currentX = targetX
      updatePuzzlePosition(sliderData.currentX)
      animationFrame.value = null
    }
  }

  animate()
}

// 初始化滑块
const initSlider = () => {
  nextTick(() => {
    if (sliderTrack.value && sliderButton.value) {
      sliderData.maxX = sliderTrack.value.offsetWidth - sliderButton.value.offsetWidth
      sliderData.currentX = 0
    }
  })
}

// 鼠标事件处理
const handleMouseDown = (e: MouseEvent) => {
  if (isSuccess.value || isLoading.value) return
  
  isDragging.value = true
  sliderData.startX = e.clientX - sliderData.currentX
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

const handleMouseMove = (e: MouseEvent) => {
  if (!isDragging.value) return

  const newX = e.clientX - sliderData.startX
  const clampedX = Math.max(0, Math.min(newX, sliderData.maxX))

  // 直接更新位置，避免延迟
  sliderData.currentX = clampedX
  updatePuzzlePosition(clampedX)
}

const handleMouseUp = () => {
  if (!isDragging.value) return
  
  isDragging.value = false
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  
  verify()
}

// 触摸事件处理
const handleTouchStart = (e: TouchEvent) => {
  if (isSuccess.value || isLoading.value) return
  
  e.preventDefault()
  isDragging.value = true
  sliderData.startX = e.touches[0].clientX - sliderData.currentX
  
  document.addEventListener('touchmove', handleTouchMove, { passive: false })
  document.addEventListener('touchend', handleTouchEnd)
}

const handleTouchMove = (e: TouchEvent) => {
  if (!isDragging.value) return

  e.preventDefault()
  const newX = e.touches[0].clientX - sliderData.startX
  const clampedX = Math.max(0, Math.min(newX, sliderData.maxX))

  // 直接更新位置，避免延迟
  sliderData.currentX = clampedX
  updatePuzzlePosition(clampedX)
}

const handleTouchEnd = () => {
  if (!isDragging.value) return
  
  isDragging.value = false
  document.removeEventListener('touchmove', handleTouchMove)
  document.removeEventListener('touchend', handleTouchEnd)
  
  verify()
}

// 验证逻辑
const verify = () => {
  const tolerance = props.tolerance
  const targetX = puzzleData.x
  const currentX = puzzleData.currentX
  
  if (Math.abs(currentX - targetX) <= tolerance) {
    // 验证成功
    isSuccess.value = true
    setTimeout(() => {
      emit('success')
      close()
    }, 1000)
  } else {
    // 验证失败
    emit('fail')
    reset()
  }
}

// 重置状态
const reset = () => {
  isSuccess.value = false
  isDragging.value = false

  // 清理动画
  if (animationFrame.value) {
    cancelAnimationFrame(animationFrame.value)
    animationFrame.value = null
  }

  // 平滑重置到初始位置
  smoothUpdatePosition(0)

  setTimeout(() => {
    refresh()
  }, 500)
}

// 刷新验证码
const refresh = () => {
  if (isLoading.value) return
  
  isLoading.value = true
  isSuccess.value = false
  isDragging.value = false
  
  setTimeout(() => {
    generateBackground()
    generatePuzzle()
    initSlider()
    isLoading.value = false
  }, 300)
}

// 关闭验证码
const close = () => {
  emit('update:visible', false)
  emit('close')
  reset()
}

// 模态框点击处理
const handleModalClick = () => {
  close()
}

// 初始化
onMounted(() => {
  if (props.visible) {
    refresh()
  }
})

// 监听visible变化
const { visible } = toRefs(props)
watch(visible, (newVal) => {
  if (newVal) {
    nextTick(() => {
      refresh()
    })
  }
})

// 清理事件监听
onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  document.removeEventListener('touchmove', handleTouchMove)
  document.removeEventListener('touchend', handleTouchEnd)

  // 清理动画
  if (animationFrame.value) {
    cancelAnimationFrame(animationFrame.value)
  }
})
</script>

<style scoped>
.slide-captcha {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
}

.captcha-modal {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}

.captcha-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  max-width: 90vw;
  max-height: 90vh;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.captcha-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.captcha-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.captcha-close {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.captcha-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

.captcha-body {
  padding: 24px;
}

.captcha-image-container {
  position: relative;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.captcha-background {
  display: block;
  border-radius: 8px;
}

.captcha-puzzle {
  position: absolute;
  top: 0;
  left: 0;
  cursor: pointer;
  transition: none; /* 移除transition以避免延迟 */
  will-change: transform; /* 优化GPU加速 */
}

.captcha-slider-container {
  margin-bottom: 20px;
}

.slider-track {
  position: relative;
  width: 100%;
  height: 50px;
  background: linear-gradient(to right, #f5f5f5, #e8e8e8);
  border-radius: 25px;
  border: 2px solid #ddd;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.slider-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #666;
  font-size: 14px;
  font-weight: 500;
  pointer-events: none;
  transition: color 0.3s ease;
  z-index: 1;
}

.slider-text-success {
  color: #52c41a;
}

.slider-button {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 46px;
  height: 46px;
  background: linear-gradient(135deg, #4fc3f7, #29b6f6);
  border-radius: 50%;
  cursor: grab;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: box-shadow 0.2s ease, transform 0.1s ease; /* 只保留必要的transition */
  will-change: transform; /* 优化GPU加速 */
  z-index: 2;
}

.slider-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.slider-button:active {
  cursor: grabbing;
  transform: scale(0.98);
}

.slider-button-success {
  background: linear-gradient(135deg, #52c41a, #389e0d);
}

.slider-icon {
  font-size: 16px;
  color: white;
  font-weight: bold;
}

.icon-arrow::before {
  content: '→';
}

.icon-success::before {
  content: '✓';
}

.captcha-actions {
  display: flex;
  justify-content: center;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  color: #495057;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: #e9ecef;
  border-color: #adb5bd;
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.refresh-icon {
  font-size: 14px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.refresh-btn:not(:disabled) .refresh-icon {
  animation: none;
}

.refresh-btn:disabled .refresh-icon {
  animation: spin 1s linear infinite;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .captcha-content {
    margin: 20px;
    width: calc(100% - 40px);
  }

  .captcha-header {
    padding: 16px 20px;
  }

  .captcha-title {
    font-size: 16px;
  }

  .captcha-body {
    padding: 20px;
  }

  .slider-track {
    height: 45px;
  }

  .slider-button {
    width: 41px;
    height: 41px;
  }

  .slider-text {
    font-size: 13px;
  }
}
</style>
