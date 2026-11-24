import { ref, computed, watch } from 'vue'
/// <reference types="../types/env.d.ts" />

export type ThemeMode = 'light' | 'dark' | 'auto'

export interface ThemeConfig {
  mode: ThemeMode
  primaryColor: string
  borderRadius: number
  fontSize: number
}

const THEME_STORAGE_KEY = 'app-theme'
const THEME_MODE_STORAGE_KEY = 'app-theme-mode'

// 默认主题配置
const defaultThemeConfig: ThemeConfig = {
  mode: 'light',
  primaryColor: '#1890ff',
  borderRadius: 6,
  fontSize: 14
}

// 从localStorage获取保存的主题设置
const getStoredThemeMode = (): ThemeMode => {
  try {
    const stored = localStorage.getItem(THEME_MODE_STORAGE_KEY)
    if (stored && ['light', 'dark', 'auto'].includes(stored)) {
      return stored as ThemeMode
    }
  } catch {
    // localStorage不可用时使用默认值
  }
  return 'light'
}

const getStoredThemeConfig = (): Partial<ThemeConfig> => {
  try {
    const stored = localStorage.getItem(THEME_STORAGE_KEY)
    if (stored) {
      return JSON.parse(stored)
    }
  } catch {
    // localStorage不可用或JSON解析失败时使用默认值
  }
  return {}
}

// 保存主题设置到localStorage
const saveThemeMode = (mode: ThemeMode): void => {
  try {
    localStorage.setItem(THEME_MODE_STORAGE_KEY, mode)
  } catch {
    // localStorage不可用时静默失败
  }
}

const saveThemeConfig = (config: ThemeConfig): void => {
  try {
    localStorage.setItem(THEME_STORAGE_KEY, JSON.stringify(config))
  } catch {
    // localStorage不可用时静默失败
  }
}

// 检测系统主题偏好
const getSystemThemePreference = (): 'light' | 'dark' => {
  if (typeof window !== 'undefined' && window.matchMedia) {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  }
  return 'light'
}

export function useTheme() {
  // 响应式状态
  const themeConfig = ref<ThemeConfig>({
    ...defaultThemeConfig,
    ...getStoredThemeConfig(),
    mode: getStoredThemeMode()
  })

  const systemTheme = ref<'light' | 'dark'>(getSystemThemePreference())

  // 计算当前实际主题
  const currentTheme = computed(() => {
    if (themeConfig.value.mode === 'auto') {
      return systemTheme.value
    }
    return themeConfig.value.mode
  })

  // 是否为暗黑模式
  const isDark = computed(() => currentTheme.value === 'dark')

  // 监听系统主题变化
  if (typeof window !== 'undefined' && window.matchMedia) {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleSystemThemeChange = (e: MediaQueryListEvent) => {
      systemTheme.value = e.matches ? 'dark' : 'light'
    }
    
    mediaQuery.addEventListener('change', handleSystemThemeChange)
    
    // 清理函数（在组件卸载时调用）
    const cleanup = () => {
      mediaQuery.removeEventListener('change', handleSystemThemeChange)
    }
    
    // 返回清理函数供外部调用
    (window as any).__themeCleanup = cleanup
  }

  // 监听主题配置变化并保存
  watch(
    () => themeConfig.value,
    (newConfig) => {
      saveThemeConfig(newConfig)
      saveThemeMode(newConfig.mode)
      applyTheme(newConfig)
    },
    { deep: true, immediate: true }
  )

  // 监听当前主题变化并应用到DOM
  watch(
    currentTheme,
    (theme) => {
      applyThemeToDOM(theme)
    },
    { immediate: true }
  )

  // 应用主题到DOM
  const applyThemeToDOM = (theme: 'light' | 'dark') => {
    if (typeof document !== 'undefined') {
      const root = document.documentElement
      
      // 移除之前的主题类
      root.classList.remove('theme-light', 'theme-dark')
      
      // 添加当前主题类
      root.classList.add(`theme-${theme}`)
      
      // 设置data属性供CSS使用
      root.setAttribute('data-theme', theme)
    }
  }

  // 应用完整主题配置
  const applyTheme = (config: ThemeConfig) => {
    if (typeof document !== 'undefined') {
      const root = document.documentElement
      
      // 设置CSS变量
      root.style.setProperty('--primary-color', config.primaryColor)
      root.style.setProperty('--border-radius', `${config.borderRadius}px`)
      root.style.setProperty('--font-size', `${config.fontSize}px`)
    }
  }

  // 切换主题模式
  const setThemeMode = (mode: ThemeMode) => {
    themeConfig.value.mode = mode
  }

  // 切换暗黑模式
  const toggleDarkMode = () => {
    const newMode = currentTheme.value === 'dark' ? 'light' : 'dark'
    setThemeMode(newMode)
  }

  // 设置主色调
  const setPrimaryColor = (color: string) => {
    themeConfig.value.primaryColor = color
  }

  // 设置圆角大小
  const setBorderRadius = (radius: number) => {
    themeConfig.value.borderRadius = Math.max(0, Math.min(20, radius))
  }

  // 设置字体大小
  const setFontSize = (size: number) => {
    themeConfig.value.fontSize = Math.max(12, Math.min(20, size))
  }

  // 重置主题为默认值
  const resetTheme = () => {
    themeConfig.value = { ...defaultThemeConfig }
  }

  // 获取主题选项
  const getThemeOptions = () => {
    return [
      { key: 'light', label: 'Light', icon: '☀️' },
      { key: 'dark', label: 'Dark', icon: '🌙' },
      { key: 'auto', label: 'Auto', icon: '🔄' }
    ]
  }

  // 获取预设颜色
  const getPresetColors = () => {
    return [
      '#1890ff', // 蓝色（默认）
      '#52c41a', // 绿色
      '#faad14', // 黄色
      '#f5222d', // 红色
      '#722ed1', // 紫色
      '#fa541c', // 橙色
      '#13c2c2', // 青色
      '#eb2f96'  // 粉色
    ]
  }

  return {
    // 状态
    themeConfig,
    themeMode: computed(() => themeConfig.value.mode),
    currentTheme,
    isDark,
    systemTheme,

    // 方法
    setThemeMode,
    toggleDarkMode,
    setPrimaryColor,
    setBorderRadius,
    setFontSize,
    resetTheme,
    getThemeOptions,
    getPresetColors,

    // 工具方法
    applyTheme,
    applyThemeToDOM
  }
}
