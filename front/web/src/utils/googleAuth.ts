/**
 * 谷歌登录工具类
 */

import { GOOGLE_CLIENT_ID, isGoogleConfigValid } from '@/config/google'
import type { GoogleUserInfo } from '@/types/auth'

// 谷歌登录状态
export enum GoogleLoginStatus {
  NOT_INITIALIZED = 'not_initialized',
  INITIALIZING = 'initializing',
  READY = 'ready',
  ERROR = 'error'
}

// 谷歌登录管理器
class GoogleAuthManager {
  private status: GoogleLoginStatus = GoogleLoginStatus.NOT_INITIALIZED
  private initPromise: Promise<void> | null = null

  /**
   * 获取当前状态
   */
  getStatus(): GoogleLoginStatus {
    return this.status
  }

  /**
   * 检查是否已准备就绪
   */
  isReady(): boolean {
    return this.status === GoogleLoginStatus.READY
  }

  /**
   * 初始化谷歌登录
   */
  async initialize(): Promise<void> {
    // 如果已经在初始化中，返回现有的Promise
    if (this.initPromise) {
      return this.initPromise
    }

    // 如果已经准备就绪，直接返回
    if (this.status === GoogleLoginStatus.READY) {
      return Promise.resolve()
    }

    // 检查配置是否有效
    if (!isGoogleConfigValid()) {
      this.status = GoogleLoginStatus.ERROR
      throw new Error('Google login configuration is invalid')
    }

    this.status = GoogleLoginStatus.INITIALIZING

    this.initPromise = new Promise((resolve, reject) => {
      // 检查是否已经加载了谷歌脚本
      if (window.google?.accounts?.id) {
        this.status = GoogleLoginStatus.READY
        resolve()
        return
      }

      // 动态加载谷歌登录脚本
      const script = document.createElement('script')
      script.src = 'https://accounts.google.com/gsi/client'
      script.async = true
      script.defer = true

      script.onload = () => {
        try {
          if (window.google?.accounts?.id) {
            this.status = GoogleLoginStatus.READY
            resolve()
          } else {
            this.status = GoogleLoginStatus.ERROR
            reject(new Error('Google Identity Services not available after loading'))
          }
        } catch (error) {
          this.status = GoogleLoginStatus.ERROR
          reject(error)
        }
      }

      script.onerror = () => {
        this.status = GoogleLoginStatus.ERROR
        reject(new Error('Failed to load Google Identity Services'))
      }

      document.head.appendChild(script)
    })

    return this.initPromise
  }

  /**
   * 初始化谷歌登录配置
   */
  initializeGoogleLogin(callback: (response: any) => void): void {
    if (!this.isReady()) {
      throw new Error('Google Auth Manager is not ready')
    }

    if (!window.google?.accounts?.id) {
      throw new Error('Google Identity Services not available')
    }

    try {
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: callback,
        auto_select: false,
        cancel_on_tap_outside: true,
      })
    } catch (error) {
      console.error('Failed to initialize Google login:', error)
      throw error
    }
  }

  /**
   * 触发谷歌登录弹窗
   */
  async promptGoogleLogin(): Promise<void> {
    if (!this.isReady()) {
      throw new Error('Google Auth Manager is not ready')
    }

    if (!window.google?.accounts?.id) {
      throw new Error('Google Identity Services not available')
    }

    try {
      // 添加超时处理
      const timeoutPromise = new Promise<never>((_, reject) => {
        setTimeout(() => reject(new Error('Google login timeout after 30 seconds')), 30000)
      })

      const promptPromise = new Promise<void>((resolve, reject) => {
        window.google!.accounts!.id!.prompt((notification: any) => {
          if (notification.isNotDisplayed()) {
            reject(new Error('Google login popup was blocked or not displayed. Please check popup blocker settings.'))
          } else if (notification.isSkippedMoment()) {
            reject(new Error('Google login was skipped by user or due to configuration issues.'))
          } else if (notification.isDismissedMoment()) {
            reject(new Error('Google login was dismissed by user.'))
          } else {
            resolve()
          }
        })
      })

      await Promise.race([promptPromise, timeoutPromise])
    } catch (error) {
      console.error('Failed to trigger Google login:', error)
      throw error
    }
  }

  /**
   * 渲染谷歌登录按钮
   */
  renderButton(element: HTMLElement, config: any): void {
    if (!this.isReady()) {
      throw new Error('Google Auth Manager is not ready')
    }

    if (!window.google?.accounts?.id) {
      throw new Error('Google Identity Services not available')
    }

    try {
      window.google.accounts.id.renderButton(element, {
        theme: 'outline',
        size: 'large',
        text: 'signin_with',
        shape: 'rectangular',
        logo_alignment: 'left',
        width: '100%',
        ...config
      })
    } catch (error) {
      console.error('Failed to render Google login button:', error)
      throw error
    }
  }
}

// 创建单例实例
export const googleAuthManager = new GoogleAuthManager()

/**
 * 解析谷歌JWT凭证
 */
export function parseGoogleCredential(credential: string): GoogleUserInfo | null {
  try {
    // JWT格式：header.payload.signature
    const parts = credential.split('.')
    if (parts.length !== 3) {
      throw new Error('Invalid JWT format')
    }

    // 解码payload部分
    const payload = parts[1]
    const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    const userInfo = JSON.parse(decodedPayload) as GoogleUserInfo

    return userInfo
  } catch (error) {
    console.error('Failed to parse Google credential:', error)
    return null
  }
}

/**
 * 验证谷歌用户信息
 */
export function validateGoogleUserInfo(userInfo: GoogleUserInfo): boolean {
  return !!(
    userInfo &&
    userInfo.sub &&
    userInfo.email &&
    userInfo.name &&
    userInfo.email_verified
  )
}

/**
 * 格式化谷歌用户信息用于显示
 */
export function formatGoogleUserInfo(userInfo: GoogleUserInfo) {
  return {
    id: userInfo.sub,
    name: userInfo.name,
    email: userInfo.email,
    avatar: userInfo.picture,
    firstName: userInfo.given_name,
    lastName: userInfo.family_name,
    locale: userInfo.locale,
    emailVerified: userInfo.email_verified
  }
}

// 全局类型声明
declare global {
  interface Window {
    google?: {
      accounts?: {
        id?: {
          initialize: (config: any) => void
          prompt: (callback?: (notification: any) => void) => void
          renderButton: (element: HTMLElement, config: any) => void
        }
      }
    }
  }
}
