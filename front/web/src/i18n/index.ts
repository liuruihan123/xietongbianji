import { createI18n } from 'vue-i18n'
import type { I18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'

type MessageSchema = typeof zhCN

const messages: Record<string, MessageSchema> = {
  'zh-CN': zhCN,
  'en-US': enUS,
}

// 从 localStorage 获取保存的语言设置
const getStoredLocale = (): string | null => {
  try {
    return localStorage.getItem('app-locale')
  } catch {
    return null
  }
}

// 保存语言设置到 localStorage
export const setStoredLocale = (locale: string): void => {
  try {
    localStorage.setItem('app-locale', locale)
  } catch {
    // localStorage 不可用时静默失败
  }
}

const getBrowserLocale = (): string => {
  // 优先使用保存的语言设置
  const storedLocale = getStoredLocale()
  if (storedLocale && messages[storedLocale]) {
    return storedLocale
  }

  // 其次使用浏览器语言
  const lang = navigator.language || 'zh-CN'
  if (messages[lang]) return lang

  // 语言映射
  if (lang.startsWith('zh')) return 'zh-CN'
  return 'en-US'
}

export const i18n: I18n<MessageSchema, {}, {}, string, false> = createI18n({
  legacy: false,
  locale: getBrowserLocale(),
  fallbackLocale: 'en-US',
  messages,
})
