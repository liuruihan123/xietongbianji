import { ref, reactive } from 'vue'

export interface CaptchaState {
  visible: boolean
  verified: boolean
  loading: boolean
  attempts: number
  maxAttempts: number
}

export function useCaptcha(maxAttempts: number = 3) {
  const state = reactive<CaptchaState>({
    visible: false,
    verified: false,
    loading: false,
    attempts: 0,
    maxAttempts
  })

  // 显示验证码
  const showCaptcha = () => {
    state.visible = true
    state.verified = false
  }

  // 隐藏验证码
  const hideCaptcha = () => {
    state.visible = false
  }

  // 验证成功
  const onCaptchaSuccess = () => {
    state.verified = true
    state.visible = false
    state.attempts = 0
    console.log('验证码验证成功')
  }

  // 验证失败
  const onCaptchaFail = () => {
    state.attempts++
    state.verified = false
    
    if (state.attempts >= state.maxAttempts) {
      console.warn(`验证码验证失败次数过多，已达到最大尝试次数: ${state.maxAttempts}`)
      // 可以在这里添加额外的安全措施，比如临时锁定
      setTimeout(() => {
        state.attempts = 0
      }, 60000) // 1分钟后重置尝试次数
    } else {
      console.log(`验证码验证失败，剩余尝试次数: ${state.maxAttempts - state.attempts}`)
    }
  }

  // 关闭验证码
  const onCaptchaClose = () => {
    state.visible = false
  }

  // 重置验证码状态
  const resetCaptcha = () => {
    state.verified = false
    state.attempts = 0
    state.visible = false
    state.loading = false
  }

  // 检查是否需要验证码
  const needsCaptcha = () => {
    // 可以根据业务需求添加更复杂的逻辑
    // 比如：失败次数、时间间隔、用户行为等
    return !state.verified
  }

  // 设置加载状态
  const setLoading = (loading: boolean) => {
    state.loading = loading
  }

  return {
    state,
    showCaptcha,
    hideCaptcha,
    onCaptchaSuccess,
    onCaptchaFail,
    onCaptchaClose,
    resetCaptcha,
    needsCaptcha,
    setLoading
  }
}

// 全局验证码管理器
class CaptchaManager {
  private instances = new Map<string, ReturnType<typeof useCaptcha>>()

  // 获取或创建验证码实例
  getInstance(key: string, maxAttempts: number = 3) {
    if (!this.instances.has(key)) {
      this.instances.set(key, useCaptcha(maxAttempts))
    }
    return this.instances.get(key)!
  }

  // 重置所有实例
  resetAll() {
    this.instances.forEach(instance => {
      instance.resetCaptcha()
    })
  }

  // 删除实例
  removeInstance(key: string) {
    this.instances.delete(key)
  }
}

export const captchaManager = new CaptchaManager()

// 预定义的验证码类型
export const CAPTCHA_TYPES = {
  LOGIN: 'login',
  REGISTER: 'register',
  RESET_PASSWORD: 'reset_password',
  SEND_CODE: 'send_code'
} as const

export type CaptchaType = typeof CAPTCHA_TYPES[keyof typeof CAPTCHA_TYPES]
