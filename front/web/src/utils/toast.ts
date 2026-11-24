import { toast } from 'vue-sonner'

/**
 * Toast 通知工具类
 * 提供统一的通知接口，替代原生的 alert
 */
export class Toast {
  /**
   * 成功通知
   * @param message 消息内容
   * @param options 可选配置
   */
  static success(message: string, options?: any) {
    return toast.success(message, {
      duration: 3000,
      ...options
    })
  }

  /**
   * 错误通知
   * @param message 消息内容
   * @param options 可选配置
   */
  static error(message: string, options?: any) {
    return toast.error(message, {
      duration: 4000,
      ...options
    })
  }

  /**
   * 警告通知
   * @param message 消息内容
   * @param options 可选配置
   */
  static warning(message: string, options?: any) {
    return toast.warning(message, {
      duration: 3500,
      ...options
    })
  }

  /**
   * 信息通知
   * @param message 消息内容
   * @param options 可选配置
   */
  static info(message: string, options?: any) {
    return toast.info(message, {
      duration: 3000,
      ...options
    })
  }

  /**
   * 普通通知
   * @param message 消息内容
   * @param options 可选配置
   */
  static message(message: string, options?: any) {
    return toast(message, {
      duration: 3000,
      ...options
    })
  }

  /**
   * Promise 通知
   * 用于异步操作的状态提示
   * @param promise Promise对象
   * @param messages 不同状态的消息
   */
  static promise<T>(
    promise: Promise<T>,
    messages: {
      loading: string
      success: string | ((data: T) => string)
      error: string | ((error: any) => string)
    }
  ) {
    return toast.promise(promise, messages)
  }

  /**
   * 确认对话框
   * 使用toast实现的确认对话框，比原生confirm更美观
   * @param message 确认消息
   * @param onConfirm 确认回调
   * @param onCancel 取消回调
   */
  static confirm(
    message: string,
    onConfirm: () => void,
    onCancel?: () => void
  ) {
    return toast(message, {
      duration: Infinity,
      action: {
        label: '确认',
        onClick: () => {
          onConfirm()
          toast.dismiss()
        }
      },
      cancel: {
        label: '取消',
        onClick: () => {
          onCancel?.()
          toast.dismiss()
        }
      }
    })
  }

  /**
   * 加载通知
   * @param message 加载消息
   * @param options 可选配置
   */
  static loading(message: string, options?: any) {
    return toast.loading(message, {
      duration: Infinity,
      ...options
    })
  }

  /**
   * 关闭所有通知
   */
  static dismissAll() {
    toast.dismiss()
  }

  /**
   * 关闭指定通知
   * @param toastId 通知ID
   */
  static dismiss(toastId?: string | number) {
    toast.dismiss(toastId)
  }
}

// 导出简化的函数接口
export const showToast = Toast.message
export const showSuccess = Toast.success
export const showError = Toast.error
export const showWarning = Toast.warning
export const showInfo = Toast.info
export const showLoading = Toast.loading
export const showConfirm = Toast.confirm
export const showPromise = Toast.promise
