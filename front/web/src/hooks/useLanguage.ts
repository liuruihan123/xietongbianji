import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

export interface LanguageOption {
  key: string
  label: string
  flag: string
  nativeName: string
  urlPrefix: string
}

// Language to URL prefix mapping
export const LANGUAGE_URL_MAP: Record<string, string> = {
  'zh-CN': '/cn',
  'en-US': '/en'
}

// URL prefix to language mapping
export const URL_LANGUAGE_MAP: Record<string, string> = {
  '/cn': 'zh-CN',
  '/en': 'en-US'
}

export const SUPPORTED_LANGUAGES: LanguageOption[] = [
  {
    key: 'zh-CN',
    label: 'Chinese',
    flag: '🇨🇳',
    nativeName: '中文',
    urlPrefix: '/cn'
  },
  {
    key: 'en-US',
    label: 'English',
    flag: '🇺🇸',
    nativeName: 'English',
    urlPrefix: '/en'
  }
]

export function useLanguage() {
  const { t, locale } = useI18n()

  // 当前语言
  const currentLocale = computed(() => locale.value)

  // 当前语言信息
  const currentLanguage = computed(() => {
    return SUPPORTED_LANGUAGES.find(lang => lang.key === locale.value) || SUPPORTED_LANGUAGES[0]
  })

  // 当前语言显示名称（使用翻译）
  const currentLanguageLabel = computed(() => {
    switch (locale.value) {
      case 'zh-CN':
        return t('language.chinese')
      case 'en-US':
        return t('language.english')
      default:
        return t('language.chinese')
    }
  })

  // 切换语言
  const switchLanguage = (languageKey: string) => {
    if (languageKey !== locale.value && SUPPORTED_LANGUAGES.some(lang => lang.key === languageKey)) {
      locale.value = languageKey
      setStoredLocale(languageKey)
      
      // 可选：触发自定义事件通知其他组件
      window.dispatchEvent(new CustomEvent('language-changed', { 
        detail: { 
          from: currentLocale.value, 
          to: languageKey 
        } 
      }))
    }
  }

  // 获取语言选项（用于下拉菜单）
  const getLanguageOptions = () => {
    return SUPPORTED_LANGUAGES.map(lang => ({
      key: lang.key,
      label: lang.nativeName,
      flag: lang.flag
    }))
  }

  // 检查是否为指定语言
  const isLanguage = (languageKey: string) => {
    return locale.value === languageKey
  }

  // 获取语言方向（为未来的 RTL 语言支持做准备）
  const getLanguageDirection = () => {
    // 目前只支持LTR语言
    return 'ltr'
  }

  // 从URL路径获取语言
  const getLanguageFromPath = (path: string): string | null => {
    const pathSegments = path.split('/').filter(Boolean)
    if (pathSegments.length === 0) return null

    const firstSegment = `/${pathSegments[0]}`
    return URL_LANGUAGE_MAP[firstSegment] || null
  }

  // 获取当前语言的URL前缀
  const getCurrentLanguagePrefix = (): string => {
    return LANGUAGE_URL_MAP[locale.value] || '/cn'
  }

  // 切换语言并更新URL
  const switchLanguageWithUrl = (languageKey: string, router?: any) => {
    if (languageKey !== locale.value && SUPPORTED_LANGUAGES.some(lang => lang.key === languageKey)) {
      const oldPrefix = getCurrentLanguagePrefix()
      const newPrefix = LANGUAGE_URL_MAP[languageKey]

      // 切换语言
      switchLanguage(languageKey)

      // 如果提供了router，更新URL
      if (router) {
        const currentPath = router.currentRoute.value.path
        let newPath = currentPath

        // 如果当前路径有语言前缀，替换它
        if (currentPath.startsWith(oldPrefix)) {
          newPath = currentPath.replace(oldPrefix, newPrefix)
        } else if (currentPath === '/') {
          // 如果是根路径，添加新的语言前缀
          newPath = newPrefix
        } else {
          // 否则在路径前添加新的语言前缀
          newPath = newPrefix + currentPath
        }

        router.push(newPath)
      }
    }
  }

  // 检测并设置基于URL的语言
  const detectAndSetLanguageFromUrl = (path: string) => {
    const detectedLanguage = getLanguageFromPath(path)
    if (detectedLanguage && detectedLanguage !== locale.value) {
      switchLanguage(detectedLanguage)
    }
  }

  // 获取带当前语言前缀的路径
  const getLocalizedPath = (path: string): string => {
    const currentPrefix = getCurrentLanguagePrefix()

    // 如果路径已经有语言前缀，直接返回
    const pathSegments = path.split('/').filter(Boolean)
    if (pathSegments.length > 0) {
      const firstSegment = `/${pathSegments[0]}`
      if (URL_LANGUAGE_MAP[firstSegment]) {
        return path
      }
    }

    // 如果是根路径，返回语言前缀
    if (path === '/') {
      return currentPrefix
    }

    // 为路径添加当前语言前缀
    return currentPrefix + path
  }

  return {
    // 状态
    currentLocale,
    currentLanguage,
    currentLanguageLabel,
    availableLanguages: computed(() => SUPPORTED_LANGUAGES),

    // 方法
    switchLanguage,
    setLanguage: switchLanguage,
    switchLanguageWithUrl,
    getLanguageOptions,
    isLanguage,
    getLanguageDirection,
    getLanguageFromPath,
    getCurrentLanguagePrefix,
    detectAndSetLanguageFromUrl,
    getLocalizedPath,

    // 常量
    SUPPORTED_LANGUAGES,
    LANGUAGE_URL_MAP,
    URL_LANGUAGE_MAP,

    // i18n 实例（如果需要直接访问）
    t,
    locale
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
