import { message } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'

/**
 * 国际化消息工具类
 * 提供统一的消息提示方法，自动使用国际化文本
 */
export class I18nMessage {
  private t: any

  constructor() {
    const { t } = useI18n()
    this.t = t
  }

  // 认证相关消息
  auth = {
    loginSuccess: () => message.success(this.t('auth.messages.loginSuccess')),
    loginFailed: (customMsg?: string) => message.error(customMsg || this.t('auth.messages.loginFailed')),
    registerSuccess: () => message.success(this.t('auth.messages.registerSuccess')),
    registerFailed: (customMsg?: string) => message.error(customMsg || this.t('auth.messages.registerFailed')),
    resetSuccess: () => message.success(this.t('auth.messages.resetSuccess')),
    resetFailed: (customMsg?: string) => message.error(customMsg || this.t('auth.messages.resetFailed')),
    codeSent: () => message.success(this.t('auth.messages.codeSent')),
    emailCodeSent: () => message.success(this.t('auth.messages.emailCodeSent')),
    smsCodeSent: () => message.success(this.t('auth.messages.smsCodeSent')),
    codeSendFailed: (customMsg?: string) => message.error(customMsg || this.t('auth.messages.codeSendFailed')),
    pleaseEnterEmail: () => message.error(this.t('auth.messages.pleaseEnterEmail')),
    pleaseEnterPhone: () => message.error(this.t('auth.messages.pleaseEnterPhone')),
    googleLoginDev: () => message.info(this.t('auth.messages.googleLoginDev')),
    googleLoginFailed: () => message.error(this.t('auth.messages.googleLoginFailed')),
    agreeTermsRequired: () => message.error(this.t('auth.messages.agreeTermsRequired')),
    operationSuccess: () => message.success(this.t('auth.messages.operationSuccess')),
    passwordResetEmailSent: () => message.success(this.t('auth.messages.passwordResetEmailSent')),
    logoutSuccess: () => message.success(this.t('auth.messages.logoutSuccess')),
    logoutFailed: () => message.error(this.t('auth.messages.logoutFailed')),
    usernameCopied: () => message.success(this.t('auth.messages.usernameCopied')),
    usernameEmpty: () => message.warning(this.t('auth.messages.usernameEmpty')),
    paymentSuccess: () => message.success(this.t('auth.messages.paymentSuccess')),
    deleteSuccess: () => message.success(this.t('auth.messages.deleteSuccess')),
    deleteFailed: () => message.error(this.t('auth.messages.deleteFailed')),
    purchaseSuccess: () => message.success(this.t('auth.messages.purchaseSuccess')),
    purchaseFailed: () => message.error(this.t('auth.messages.purchaseFailed')),
    refreshSuccess: () => message.success(this.t('auth.messages.refreshSuccess')),
    loginExpired: () => message.error(this.t('auth.messages.loginExpired')),
    loadDataFailed: () => message.error(this.t('auth.messages.loadDataFailed'))
  }

  // 支付相关消息
  payment = {
    fetchPackagesFailed: () => message.error(this.t('payment.messages.fetchPackagesFailed')),
    generatePaymentFailed: () => message.error(this.t('payment.messages.generatePaymentFailed')),
    orderCreationFailed: () => message.error(this.t('payment.messages.orderCreationFailed')),
    paymentSuccessful: () => message.success(this.t('payment.messages.paymentSuccessful')),
    networkError: () => message.error(this.t('payment.messages.networkError'))
  }

  // 通用消息方法
  success = (key: string, fallback?: string) => {
    message.success(this.t(key) || fallback || 'Success')
  }

  error = (key: string, fallback?: string) => {
    message.error(this.t(key) || fallback || 'Error')
  }

  warning = (key: string, fallback?: string) => {
    message.warning(this.t(key) || fallback || 'Warning')
  }

  info = (key: string, fallback?: string) => {
    message.info(this.t(key) || fallback || 'Info')
  }
}

/**
 * 创建国际化消息实例的工厂函数
 * 在组件中使用：
 * 
 * ```typescript
 * import { createI18nMessage } from '@/utils/message'
 * 
 * const i18nMsg = createI18nMessage()
 * i18nMsg.auth.loginSuccess()
 * i18nMsg.payment.networkError()
 * ```
 */
export function createI18nMessage() {
  return new I18nMessage()
}

/**
 * 直接使用 t 函数的简化版本
 * 在组件中使用：
 * 
 * ```typescript
 * import { showMessage } from '@/utils/message'
 * 
 * showMessage.success('auth.messages.loginSuccess')
 * showMessage.error('auth.messages.loginFailed', '登录失败')
 * ```
 */
export const showMessage = {
  success: (key: string, fallback?: string) => {
    const { t } = useI18n()
    message.success(t(key) || fallback || 'Success')
  },
  
  error: (key: string, fallback?: string) => {
    const { t } = useI18n()
    message.error(t(key) || fallback || 'Error')
  },
  
  warning: (key: string, fallback?: string) => {
    const { t } = useI18n()
    message.warning(t(key) || fallback || 'Warning')
  },
  
  info: (key: string, fallback?: string) => {
    const { t } = useI18n()
    message.info(t(key) || fallback || 'Info')
  }
}
