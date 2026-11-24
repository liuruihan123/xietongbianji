import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { routeUtils } from '@/router'

/**
 * 导航状态类型定义
 */
export type NavType = 'recent' | 'all' | 'favorite' | 'shared'
export type CategoryType = 'personal' | 'team' | 'department'
export type WorkspaceTabType = 'all' | 'created' | 'joined'

/**
 * 导航状态管理 Composable
 * 
 * 功能:
 * 1. 管理左侧导航菜单的选择状态
 * 2. 自动同步状态到 URL 查询参数和 localStorage
 * 3. 页面刷新后自动恢复状态
 * 4. 支持通过 URL 直接访问特定菜单状态
 */
export function useNavigationState() {
  const router = useRouter()
  const route = useRoute()

  // localStorage 键名
  const STORAGE_KEYS = {
    nav: 'home-nav-state',
    category: 'home-category-state',
    workspaceTab: 'home-workspace-tab',
  }

  // 默认值
  const DEFAULT_VALUES = {
    nav: 'recent' as NavType,
    category: 'personal' as CategoryType,
    workspaceTab: 'all' as WorkspaceTabType,
  }

  /**
   * 从 URL 查询参数或 localStorage 获取初始值
   */
  function getInitialValue<T>(
    queryKey: string,
    storageKey: string,
    defaultValue: T
  ): T {
    // 优先从 URL 查询参数读取
    const queryValue = route.query[queryKey] as T
    if (queryValue) {
      // 同步到 localStorage
      localStorage.setItem(storageKey, String(queryValue))
      return queryValue
    }

    // 其次从 localStorage 读取
    const storageValue = localStorage.getItem(storageKey) as T
    if (storageValue) {
      return storageValue
    }

    // 最后使用默认值
    return defaultValue
  }

  // 创建响应式状态
  const activeNav = ref<NavType>(
    getInitialValue('nav', STORAGE_KEYS.nav, DEFAULT_VALUES.nav)
  )
  const activeCategory = ref<CategoryType>(
    getInitialValue('category', STORAGE_KEYS.category, DEFAULT_VALUES.category)
  )
  const activeWorkspaceTab = ref<WorkspaceTabType>(
    getInitialValue('tab', STORAGE_KEYS.workspaceTab, DEFAULT_VALUES.workspaceTab)
  )

  // 标志位,防止循环更新
  let isUpdatingFromRoute = false

  /**
   * 更新 URL 查询参数和 localStorage
   */
  function syncStateToUrlAndStorage() {
    if (isUpdatingFromRoute) return

    // 更新 localStorage
    localStorage.setItem(STORAGE_KEYS.nav, activeNav.value)
    localStorage.setItem(STORAGE_KEYS.category, activeCategory.value)
    localStorage.setItem(STORAGE_KEYS.workspaceTab, activeWorkspaceTab.value)

    // 更新 URL 查询参数(使用 replace 避免产生历史记录)
    const currentPath = routeUtils.getPathWithoutLanguage()
    const languagePrefix = routeUtils.getCurrentLanguagePrefix()
    
    router.replace({
      path: languagePrefix + currentPath,
      query: {
        ...route.query,
        nav: activeNav.value,
        category: activeCategory.value,
        tab: activeWorkspaceTab.value,
      },
    })
  }

  /**
   * 监听路由变化,同步状态(支持浏览器前进/后退)
   */
  watch(
    () => route.query,
    (newQuery) => {
      isUpdatingFromRoute = true

      if (newQuery.nav && newQuery.nav !== activeNav.value) {
        activeNav.value = newQuery.nav as NavType
        localStorage.setItem(STORAGE_KEYS.nav, String(newQuery.nav))
      }

      if (newQuery.category && newQuery.category !== activeCategory.value) {
        activeCategory.value = newQuery.category as CategoryType
        localStorage.setItem(STORAGE_KEYS.category, String(newQuery.category))
      }

      if (newQuery.tab && newQuery.tab !== activeWorkspaceTab.value) {
        activeWorkspaceTab.value = newQuery.tab as WorkspaceTabType
        localStorage.setItem(STORAGE_KEYS.workspaceTab, String(newQuery.tab))
      }

      // 重置标志位
      setTimeout(() => {
        isUpdatingFromRoute = false
      }, 100)
    },
    { deep: true }
  )

  /**
   * 监听状态变化,同步到 URL 和 localStorage
   */
  watch([activeNav, activeCategory, activeWorkspaceTab], () => {
    syncStateToUrlAndStorage()
  })

  return {
    activeNav,
    activeCategory,
    activeWorkspaceTab,
  }
}

