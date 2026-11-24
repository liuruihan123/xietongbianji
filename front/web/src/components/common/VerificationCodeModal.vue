<template>
  <div v-if="visible" class="verification-modal-overlay" @click.self="handleClose">
    <div class="verification-modal">
      <!-- 关闭按钮 -->
      <button class="modal-close-btn" @click="handleClose">
        <i class="ti ti-x"></i>
      </button>

      <!-- 标题 -->
      <div class="modal-header">
        <i class="ti ti-shield-check modal-icon"></i>
        <h3 class="modal-title">安全验证</h3>
        <p class="modal-subtitle">
          {{ verificationType === 'phone' ? '短信验证码' : '邮箱验证码' }}已发送至
          <span class="target-display">{{ maskedTarget }}</span>
        </p>
      </div>

      <!-- 验证码输入 -->
      <div class="modal-body">
        <div class="code-input-group">
          <input
            v-for="(digit, index) in codeDigits"
            :key="index"
            :ref="el => setInputRef(el, index)"
            v-model="codeDigits[index]"
            type="text"
            maxlength="1"
            class="code-input"
            :class="{ 'has-error': hasError }"
            @input="handleInput(index, $event)"
            @keydown="handleKeydown(index, $event)"
            @paste="handlePaste"
          />
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          <i class="ti ti-alert-circle"></i>
          {{ errorMessage }}
        </div>

        <!-- 倒计时和重发 -->
        <div class="resend-section">
          <template v-if="countdown > 0">
            <span class="countdown-text">{{ countdown }}秒后可重新发送</span>
          </template>
          <template v-else>
            <button class="resend-btn" @click="handleResend" :disabled="isResending">
              <i class="ti ti-refresh"></i>
              {{ isResending ? '发送中...' : '重新发送验证码' }}
            </button>
          </template>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="handleClose" :disabled="isVerifying">
          取消
        </button>
        <button 
          class="btn btn-primary" 
          @click="handleVerify" 
          :disabled="!isCodeComplete || isVerifying"
        >
          <span v-if="isVerifying">
            <i class="ti ti-loader spinning"></i>
            验证中...
          </span>
          <span v-else>确认</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

interface Props {
  visible: boolean
  verificationType: 'phone' | 'email'
  target: string
  codeLength?: number
  countdownSeconds?: number
  verifying?: boolean
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'verify-success', code: string): void
  (e: 'verify-error', error: string): void
  (e: 'resend'): void
}

const props = withDefaults(defineProps<Props>(), {
  codeLength: 6,
  countdownSeconds: 60,
  verifying: false
})

const emit = defineEmits<Emits>()

// 验证码输入
const codeDigits = ref<string[]>(Array(props.codeLength).fill(''))
const inputRefs = ref<(HTMLInputElement | null)[]>([])
const hasError = ref(false)
const errorMessage = ref('')
const isResending = ref(false)

// 验证状态由父组件控制
const isVerifying = computed(() => props.verifying)

// 倒计时
const countdown = ref(props.countdownSeconds)
let countdownTimer: number | null = null

// 设置输入框引用
const setInputRef = (el: any, index: number) => {
  if (el) {
    inputRefs.value[index] = el as HTMLInputElement
  }
}

// 脱敏显示目标
const maskedTarget = computed(() => {
  if (props.verificationType === 'phone') {
    // 手机号: 138****5678
    return props.target.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  } else {
    // 邮箱: abc***@example.com
    const [username, domain] = props.target.split('@')
    const maskedUsername = username.length > 3
      ? username.substring(0, 3) + '***'
      : username + '***'
    return `${maskedUsername}@${domain}`
  }
})

// 验证码是否完整
const isCodeComplete = computed(() => {
  return codeDigits.value.every(digit => digit !== '')
})

// 获取完整验证码
const getFullCode = () => {
  return codeDigits.value.join('')
}

// 处理输入
const handleInput = (index: number, event: Event) => {
  const input = event.target as HTMLInputElement
  const value = input.value

  // 只允许数字
  if (value && !/^\d$/.test(value)) {
    codeDigits.value[index] = ''
    return
  }

  // 清除错误状态
  hasError.value = false
  errorMessage.value = ''

  // 自动跳转到下一个输入框
  if (value && index < props.codeLength - 1) {
    inputRefs.value[index + 1]?.focus()
  }

  // 如果输入完成，自动验证
  if (isCodeComplete.value) {
    setTimeout(() => {
      handleVerify()
    }, 300)
  }
}

// 处理键盘事件
const handleKeydown = (index: number, event: KeyboardEvent) => {
  // 退格键
  if (event.key === 'Backspace') {
    if (!codeDigits.value[index] && index > 0) {
      // 如果当前为空，删除前一个
      codeDigits.value[index - 1] = ''
      inputRefs.value[index - 1]?.focus()
    }
  }
  // 左箭头
  else if (event.key === 'ArrowLeft' && index > 0) {
    inputRefs.value[index - 1]?.focus()
  }
  // 右箭头
  else if (event.key === 'ArrowRight' && index < props.codeLength - 1) {
    inputRefs.value[index + 1]?.focus()
  }
}

// 处理粘贴
const handlePaste = (event: ClipboardEvent) => {
  event.preventDefault()
  const pastedData = event.clipboardData?.getData('text') || ''
  const digits = pastedData.replace(/\D/g, '').split('').slice(0, props.codeLength)
  
  digits.forEach((digit, index) => {
    if (index < props.codeLength) {
      codeDigits.value[index] = digit
    }
  })

  // 聚焦到最后一个输入框
  const lastFilledIndex = Math.min(digits.length - 1, props.codeLength - 1)
  inputRefs.value[lastFilledIndex]?.focus()

  // 如果粘贴完成，自动验证
  if (isCodeComplete.value) {
    setTimeout(() => {
      handleVerify()
    }, 300)
  }
}

// 验证验证码
const handleVerify = async () => {
  if (!isCodeComplete.value || isVerifying.value) return

  hasError.value = false
  errorMessage.value = ''

  try {
    const code = getFullCode()
    // 触发验证事件，由父组件处理实际验证逻辑
    emit('verify-success', code)
  } catch (error: any) {
    hasError.value = true
    errorMessage.value = error.message || '验证失败，请重试'
    emit('verify-error', errorMessage.value)
  }
}

// 重新发送验证码
const handleResend = async () => {
  if (countdown.value > 0 || isResending.value) return

  isResending.value = true
  try {
    emit('resend')
    // 重置倒计时
    startCountdown()
  } finally {
    isResending.value = false
  }
}

// 关闭模态框
const handleClose = () => {
  if (isVerifying.value) return
  emit('update:visible', false)
}

// 开始倒计时
const startCountdown = () => {
  countdown.value = props.countdownSeconds
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdownTimer = window.setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer!)
      countdownTimer = null
    }
  }, 1000)
}

// 重置验证码输入
const resetCode = () => {
  codeDigits.value = Array(props.codeLength).fill('')
  hasError.value = false
  errorMessage.value = ''
  inputRefs.value[0]?.focus()
}

// 监听 visible 变化
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetCode()
    startCountdown()
    // 延迟聚焦，确保 DOM 已渲染
    setTimeout(() => {
      inputRefs.value[0]?.focus()
    }, 100)
  } else {
    if (countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})

// 暴露方法给父组件
defineExpose({
  resetCode,
  setError: (message: string) => {
    hasError.value = true
    errorMessage.value = message
  }
})
</script>

<style scoped>
/* 模态框遮罩 */
.verification-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 模态框主体 */
.verification-modal {
  background: var(--block-bg, #1a1d29);
  border-radius: 12px;
  padding: 30px;
  max-width: 500px;
  width: 90%;
  position: relative;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 关闭按钮 */
.modal-close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: var(--white, #fff);
}

.modal-close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

/* 模态框头部 */
.modal-header {
  text-align: center;
  margin-bottom: 30px;
}

.modal-icon {
  font-size: 3rem;
  color: var(--primary, #00d4ff);
  margin-bottom: 15px;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--white, #fff);
  margin: 0 0 10px 0;
}

.modal-subtitle {
  font-size: 0.9rem;
  color: #999;
  margin: 0;
}

.target-display {
  color: var(--primary, #00d4ff);
  font-weight: 600;
}

/* 模态框主体 */
.modal-body {
  margin-bottom: 25px;
}

/* 验证码输入组 */
.code-input-group {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 20px;
}

.code-input {
  width: 50px;
  height: 60px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 600;
  border: 2px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.05);
  color: var(--white, #fff);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.code-input:focus {
  outline: none;
  border-color: var(--primary, #00d4ff);
  background: rgba(0, 212, 255, 0.1);
  box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.2);
}

.code-input.has-error {
  border-color: #ff5252;
  background: rgba(255, 82, 82, 0.1);
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ff5252;
  font-size: 0.9rem;
  padding: 10px;
  background: rgba(255, 82, 82, 0.1);
  border-radius: 6px;
  margin-bottom: 15px;
}

.error-message i {
  font-size: 1.1rem;
}

/* 重发区域 */
.resend-section {
  text-align: center;
}

.countdown-text {
  color: #999;
  font-size: 0.9rem;
}

.resend-btn {
  background: none;
  border: none;
  color: var(--primary, #00d4ff);
  font-size: 0.9rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 4px;
}

.resend-btn:hover:not(:disabled) {
  background: rgba(0, 212, 255, 0.1);
}

.resend-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 模态框底部 */
.modal-footer {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn {
  padding: 12px 30px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: var(--white, #fff);
}

.btn-secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary, #00d4ff), #0b7dda);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.4);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 旋转动画 */
.spinning {
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

/* 响应式设计 */
@media (max-width: 576px) {
  .verification-modal {
    padding: 20px;
  }

  .code-input {
    width: 40px;
    height: 50px;
    font-size: 1.2rem;
  }

  .code-input-group {
    gap: 6px;
  }
}
</style>
