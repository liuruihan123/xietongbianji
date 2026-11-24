/**
 * 全局错误处理工具
 */

import { App } from 'vue'
import { Router } from 'vue-router'
import { message } from 'ant-design-vue'
import { isDev } from '@/utils/env'

// 错误类型定义
export interface ErrorInfo {
  message: string
  stack?: string
  componentStack?: string
  url?: string
  line?: number
  column?: number
  timestamp: string
  userAgent: string
}

// 错误处理器类
export class ErrorHandler {
  private router: Router
  private errorQueue: ErrorInfo[] = []
  private maxErrorCount = 10

  constructor(router: Router) {
    this.router = router
  }

  /**
   * 处理 JavaScript 错误
   */
  handleJSError = (error: Error, url?: string, line?: number, column?: number) => {
    const errorInfo: ErrorInfo = {
      message: error.message,
      stack: error.stack,
      url,
      line,
      column,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
    }

    this.logError(errorInfo)
    this.showErrorMessage(error.message)
    
    // 严重错误跳转到 500 页面
    if (this.isCriticalError(error)) {
      this.redirectToErrorPage('500', errorInfo)
    }
  }

  /**
   * 处理 Promise 拒绝错误
   */
  handlePromiseRejection = (event: PromiseRejectionEvent) => {
    const error = event.reason
    const errorInfo: ErrorInfo = {
      message: error?.message || 'Promise rejection',
      stack: error?.stack,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
    }

    this.logError(errorInfo)
    
    // 网络错误或 API 错误
    if (this.isNetworkError(error) || this.isAPIError(error)) {
      this.showErrorMessage('网络连接失败，请检查网络后重试')
    } else {
      this.showErrorMessage('系统出现异常，请稍后重试')
      
      // 严重错误跳转到 500 页面
      if (this.isCriticalError(error)) {
        this.redirectToErrorPage('500', errorInfo)
      }
    }
  }

  /**
   * 处理 Vue 错误
   */
  handleVueError = (error: Error, instance: any, info: string) => {
    const errorInfo: ErrorInfo = {
      message: error.message,
      stack: error.stack,
      componentStack: info,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
    }

    this.logError(errorInfo)
    this.showErrorMessage('页面组件出现错误')
    
    // 组件错误通常不需要跳转到错误页面，除非是关键组件
    if (this.isCriticalComponentError(info)) {
      this.redirectToErrorPage('500', errorInfo)
    }
  }

  /**
   * 处理路由错误
   */
  handleRouterError = (error: Error) => {
    const errorInfo: ErrorInfo = {
      message: error.message,
      stack: error.stack,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
    }

    this.logError(errorInfo)
    
    // 路由不存在，跳转到 404
    if (error.message.includes('404') || error.message.includes('not found')) {
      this.redirectToErrorPage('404')
    } else {
      this.showErrorMessage('页面跳转失败')
    }
  }

  /**
   * 记录错误
   */
  private logError(errorInfo: ErrorInfo) {
    // 添加到错误队列
    this.errorQueue.unshift(errorInfo)
    
    // 限制队列长度
    if (this.errorQueue.length > this.maxErrorCount) {
      this.errorQueue = this.errorQueue.slice(0, this.maxErrorCount)
    }

    // 开发环境打印详细错误
    if (isDev()) {
      console.group('🚨 Error Caught')
      console.error('Message:', errorInfo.message)
      console.error('Stack:', errorInfo.stack)
      console.error('Component Stack:', errorInfo.componentStack)
      console.error('Full Info:', errorInfo)
      console.groupEnd()
    }

    // 生产环境发送错误报告（可选）
    if (!isDev()) {
      this.reportError(errorInfo)
    }
  }

  /**
   * 显示错误消息
   */
  private showErrorMessage(msg: string) {
    message.error(msg, 3)
  }

  /**
   * 跳转到错误页面
   */
  private redirectToErrorPage(type: '404' | '500', errorInfo?: ErrorInfo) {
    const query: Record<string, string> = {}
    
    if (errorInfo && isDev()) {
      query.error = encodeURIComponent(JSON.stringify(errorInfo, null, 2))
    }

    this.router.push({
      path: `/${type}`,
      query,
    }).catch(() => {
      // 如果路由跳转失败，直接刷新页面
      window.location.href = `/${type}`
    })
  }

  /**
   * 判断是否为严重错误
   */
  private isCriticalError(error: any): boolean {
    if (!error) return false
    
    const criticalKeywords = [
      'ChunkLoadError',
      'Loading chunk',
      'Loading CSS chunk',
      'Script error',
      'Network Error',
      'Failed to fetch',
    ]
    
    return criticalKeywords.some(keyword => 
      error.message?.includes(keyword) || error.toString().includes(keyword)
    )
  }

  /**
   * 判断是否为网络错误
   */
  private isNetworkError(error: any): boolean {
    if (!error) return false
    
    const networkKeywords = [
      'Network Error',
      'Failed to fetch',
      'ERR_NETWORK',
      'ERR_INTERNET_DISCONNECTED',
      'timeout',
    ]
    
    return networkKeywords.some(keyword => 
      error.message?.includes(keyword) || error.toString().includes(keyword)
    )
  }

  /**
   * 判断是否为 API 错误
   */
  private isAPIError(error: any): boolean {
    return error?.response?.status >= 400
  }

  /**
   * 判断是否为关键组件错误
   */
  private isCriticalComponentError(info: string): boolean {
    const criticalComponents = ['App', 'Router', 'Layout']
    return criticalComponents.some(component => info.includes(component))
  }

  /**
   * 发送错误报告到服务器
   */
  private async reportError(errorInfo: ErrorInfo) {
    try {
      // 这里可以集成错误监控服务，如 Sentry、Bugsnag 等
      // await fetch('/api/error-report', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(errorInfo)
      // })
      
      console.log('Error reported:', errorInfo.message)
    } catch (error) {
      console.error('Failed to report error:', error)
    }
  }

  /**
   * 获取错误历史
   */
  getErrorHistory(): ErrorInfo[] {
    return [...this.errorQueue]
  }

  /**
   * 清除错误历史
   */
  clearErrorHistory() {
    this.errorQueue = []
  }
}

// 全局错误处理器实例
let errorHandler: ErrorHandler

/**
 * 设置全局错误处理
 */
export function setupErrorHandler(app: App, router: Router) {
  errorHandler = new ErrorHandler(router)

  // Vue 错误处理
  app.config.errorHandler = errorHandler.handleVueError

  // JavaScript 错误处理
  window.onerror = (message, source, lineno, colno, error) => {
    errorHandler.handleJSError(error || new Error(String(message)), source, lineno, colno)
    return true
  }

  // Promise 拒绝处理
  window.addEventListener('unhandledrejection', errorHandler.handlePromiseRejection)

  // 路由错误处理
  router.onError(errorHandler.handleRouterError)

  return errorHandler
}

/**
 * 获取错误处理器实例
 */
export function getErrorHandler(): ErrorHandler | undefined {
  return errorHandler
}
