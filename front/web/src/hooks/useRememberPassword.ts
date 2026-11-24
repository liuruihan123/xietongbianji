import { ref, onMounted } from 'vue'

interface RememberedCredentials {
  userName: string
  password: string
  remember: boolean
}

const STORAGE_KEY = 'alpha_radar_remembered_credentials'

/**
 * 记住密码功能的 Composable
 * 提供保存、读取、清除登录凭据的功能
 */
export function useRememberPassword() {
  const rememberPassword = ref(false)

  /**
   * 简单的Base64编码（基本安全措施）
   */
  const encode = (str: string): string => {
    try {
      return btoa(encodeURIComponent(str))
    } catch {
      return str
    }
  }

  /**
   * 简单的Base64解码
   */
  const decode = (str: string): string => {
    try {
      return decodeURIComponent(atob(str))
    } catch {
      return str
    }
  }

  /**
   * 保存登录凭据到localStorage
   */
  const saveCredentials = (userName: string, password: string, remember: boolean) => {
    if (!remember) {
      clearCredentials()
      return
    }

    const credentials: RememberedCredentials = {
      userName,
      password: encode(password), // 对密码进行Base64编码
      remember
    }

    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(credentials))
      rememberPassword.value = true
    } catch (error) {
      console.warn('无法保存登录凭据:', error)
    }
  }

  /**
   * 从localStorage读取登录凭据
   */
  const loadCredentials = (): RememberedCredentials | null => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (!stored) return null

      const credentials: RememberedCredentials = JSON.parse(stored)
      
      // 解码密码
      credentials.password = decode(credentials.password)
      
      return credentials
    } catch (error) {
      console.warn('无法读取登录凭据:', error)
      clearCredentials() // 清除损坏的数据
      return null
    }
  }

  /**
   * 清除保存的登录凭据
   */
  const clearCredentials = () => {
    try {
      localStorage.removeItem(STORAGE_KEY)
      rememberPassword.value = false
    } catch (error) {
      console.warn('无法清除登录凭据:', error)
    }
  }

  /**
   * 自动填充表单数据
   */
  const autoFillForm = (loginForm: { userName: string; password: string }) => {
    const credentials = loadCredentials()
    if (credentials && credentials.remember) {
      loginForm.userName = credentials.userName
      loginForm.password = credentials.password
      rememberPassword.value = true
      return true
    }
    return false
  }

  /**
   * 检查是否有保存的凭据
   */
  const hasRememberedCredentials = (): boolean => {
    const credentials = loadCredentials()
    return !!(credentials && credentials.remember)
  }

  return {
    rememberPassword,
    saveCredentials,
    loadCredentials,
    clearCredentials,
    autoFillForm,
    hasRememberedCredentials
  }
}
