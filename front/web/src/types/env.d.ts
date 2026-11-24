/// <reference types="vite/client" />

// 环境变量类型定义
interface ImportMetaEnv {
  // 应用基础配置
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_ENV: 'development' | 'production' | 'staging' | 'testing'
  readonly VITE_APP_VERSION: string

  // API 配置
  readonly VITE_GLOB_API_URL: string
  readonly VITE_APP_API_BASE_URL: string

  // 功能开关
  readonly VITE_GLOB_APP_PWA: string
  readonly VITE_USE_LAZY_LOADING: string

  // 上传配置
  readonly VITE_UPLOAD_URL: string

  // 第三方服务配置
  readonly VITE_GOOGLE_CLIENT_ID: string

  // 开发环境配置
  readonly VITE_DEV_SERVER_PORT?: string
  readonly VITE_DEV_SERVER_HOST?: string
  readonly VITE_DEV_MOCK?: string
  readonly VITE_DEV_PROXY?: string

  // 生产环境配置
  readonly VITE_BUILD_COMPRESS?: string
  readonly VITE_BUILD_DROP_CONSOLE?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 全局环境变量
declare const __APP_INFO__: {
  pkg: {
    name: string
    version: string
    dependencies: Record<string, string>
    devDependencies: Record<string, string>
  }
  lastBuildTime: string
}

// 扩展 Window 接口
declare global {
  interface Window {
    __themeCleanup?: () => void
  }
}

// 扩展 Axios 请求头接口
declare module 'axios' {
  export interface AxiosRequestHeaders {
    isToken?: boolean
    repeatSubmit?: boolean
  }
}
