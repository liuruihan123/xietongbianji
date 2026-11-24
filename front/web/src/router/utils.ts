import { router } from './index'
import { URL_LANGUAGE_MAP, LANGUAGE_URL_MAP } from '@/hooks/useLanguage'

// 路由工具函数
export const routerUtils = {
  // 获取当前语言前缀
  getCurrentLanguagePrefix(): string {
    const route = router.currentRoute.value
    const pathSegments = route.path.split('/').filter(Boolean)
    if (pathSegments.length > 0) {
      const firstSegment = `/${pathSegments[0]}`
      if (URL_LANGUAGE_MAP[firstSegment]) {
        return firstSegment
      }
    }
    return '/cn' // 默认中文
  },

  // 获取不带语言前缀的路径
  getPathWithoutLanguage(): string {
    const route = router.currentRoute.value
    const languagePrefix = this.getCurrentLanguagePrefix()
    return route.path.replace(languagePrefix, '') || '/'
  },

  // 切换到指定语言
  switchToLanguage(languageKey: string) {
    const targetPrefix = LANGUAGE_URL_MAP[languageKey]
    if (!targetPrefix) return

    const currentPath = this.getPathWithoutLanguage()
    const newPath = targetPrefix + (currentPath === '/' ? '' : currentPath)

    return router.push(newPath)
  },

  // 获取带当前语言前缀的路径
  getLocalizedPath(path: string): string {
    const languagePrefix = this.getCurrentLanguagePrefix()
    
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
      return languagePrefix
    }

    // 为路径添加当前语言前缀
    return languagePrefix + path
  },

  // 导航到指定页面（自动添加语言前缀）
  navigateTo(path: string, query?: Record<string, any>) {
    const localizedPath = this.getLocalizedPath(path)
    return router.push({ path: localizedPath, query })
  },

  // 替换当前路由（自动添加语言前缀）
  replaceTo(path: string, query?: Record<string, any>) {
    const localizedPath = this.getLocalizedPath(path)
    return router.replace({ path: localizedPath, query })
  }
}
