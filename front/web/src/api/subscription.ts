import { request } from '@/utils/request'

// 订阅列表项类型定义
export interface SubscriptionItem {
  id: number
  userId: number
  username: string
  phoneNumber: string
  email: string
  subscribedExchanges: string
  notificationMethod: string
  notificationEnabled: boolean
  minMarketCapFilter: number
  excludeStablecoins: boolean
  excludeTestCoins: boolean
  subscriptionStartTime: string
  subscriptionEndTime: string
  subscriptionStatus: string
  lastNotificationTime: string
  notificationCount: number
  status: number
  remark: string
}

// 订阅列表响应类型
export interface SubscriptionListResponse {
  total: number
  rows: SubscriptionItem[]
  code: number
  msg: string
}

// 订阅交易所请求类型
export interface SubscribeExchangeRequest {
  exchangeCode: string 
  notificationMethod: 'TELEGRAM' | 'EMAIL'
  notificationEnabled: boolean
}

// 订阅交易所响应类型
export interface SubscribeExchangeResponse {
  success: boolean
  message: string
  needTelegramBinding: boolean
  telegramBindLink: string | null
  subscriptionStatus: string
  exchangeCode: string
  errorMessage: string | null
}

// 邮箱验证码发送请求类型
export interface EmailSendCodeRequest {
  email: string
  exchangeCode: string
}

// 邮箱验证码发送响应类型
export interface EmailSendCodeResponse {
  // 空对象响应
}

// 邮箱验证码验证并订阅请求类型
export interface EmailVerifyAndSubscribeRequest {
  email: string
  code: string
  exchangeCode: string
}

// 通知历史查询参数
export interface NotificationHistoryQueryBo {
  userId?: number
  exchangeCode?: string
  currencySymbol?: string
  notificationType?: string
  notificationStatus?: string
}

// 分页查询参数
export interface PageQuery {
  pageSize: number
  pageNum: number
  orderByColumn?: string
  isAsc?: boolean
}

// 通知历史项类型
export interface NotificationHistoryItem {
  id: number
  exchangeCode: string
  currencySymbol: string
  tradingPair: string
  notificationType: string
  notificationStatus: string
  notificationContent: string
  notificationTime: string
}

// 通知历史响应类型
export interface NotificationHistoryResponse {
  total: number
  rows: NotificationHistoryItem[]
  code: number
  msg: string
}

// API响应包装类型
export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

// 用户订阅项（新格式）
export interface UserExchangeSubscriptionVo {
  id: number
  exchangeName: string
  exchangeCode: string | null
  notificationMethods: ('TELEGRAM' | 'EMAIL')[]
  email: string
  telegramUserId: number | null
  subscriptionStatus: number // 1=启用, 0=禁用
}

// ========== 1. 订阅列表展示 ==========

/**
 * 获取用户订阅列表（旧格式）
 * GET /bicanmou/user/subscription/list
 */
export function getUserSubscriptionList(): Promise<ApiResponse<SubscriptionListResponse>> {
  return request({
    url: '/bicanmou/user/subscription/list',
    method: 'GET'
  })
}

/**
 * 获取用户订阅列表（新格式）
 * GET /bicanmou/user/subscription/complete-list
 */
export function getUserSubscriptions(): Promise<ApiResponse<UserExchangeSubscriptionVo[]>> {
  return request({
    url: '/bicanmou/user/subscription/complete-list',
    method: 'GET'
  })
}

// ========== 2. 监控状态控制 ==========

/**
 * 开启订阅
 * PUT /bicanmou/user/subscription/{id}/enable
 */
export function enableSubscription(id: number): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: `/bicanmou/user/subscription/${id}/enable`,
    method: 'PUT'
  })
}

/**
 * 暂停订阅
 * PUT /bicanmou/user/subscription/{id}/pause
 */
export function pauseSubscription(id: number): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: `/bicanmou/user/subscription/${id}/pause`,
    method: 'PUT'
  })
}

/**
 * 通用状态更新
 * PUT /bicanmou/CryptoSubscriptionUser/{id}/status?status=1
 */
export function updateSubscriptionStatus(id: number, status: number): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: `/bicanmou/CryptoSubscriptionUser/${id}/status?status=${status}`,
    method: 'PUT'
  })
}

// ========== 3. 新增订阅功能 ==========

/**
 * Telegram 订阅
 * POST /bicanmou/user/subscription/subscribe-exchange
 */
export function subscribeExchange(params: SubscribeExchangeRequest): Promise<ApiResponse<SubscribeExchangeResponse>> {
  return request({
    url: '/bicanmou/user/subscription/subscribe-exchange',
    method: 'POST',
    data: params
  })
}

/**
 * 邮箱订阅 - 发送验证码
 * POST /bicanmou/user/subscription/email/send-code
 */
export function sendEmailVerificationCode(params: EmailSendCodeRequest): Promise<ApiResponse<EmailSendCodeResponse>> {
  return request({
    url: '/bicanmou/user/subscription/email/send-code',
    method: 'POST',
    data: params
  })
}

/**
 * 邮箱订阅 - 验证并订阅
 * POST /bicanmou/user/subscription/email/verify-and-subscribe
 */
export function verifyEmailAndSubscribe(params: EmailVerifyAndSubscribeRequest): Promise<ApiResponse<string>> {
  return request({
    url: '/bicanmou/user/subscription/email/verify-and-subscribe',
    method: 'POST',
    data: params
  })
}

// ========== 4. 删除订阅 ==========

/**
 * 删除订阅
 * DELETE /bicanmou/user/subscription/unsubscribe-exchange/{exchangeCode}
 */
export function unsubscribeExchange(exchangeCode: string): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: `/bicanmou/user/subscription/unsubscribe-exchange/${exchangeCode}`,
    method: 'DELETE'
  })
}

// ========== 5. 通知历史记录 ==========

/**
 * 获取通知历史记录
 * GET /bicanmou/user/subscription/notification/history
 */
export function getNotificationHistory(
  queryParams: NotificationHistoryQueryBo,
  pageQuery: PageQuery
): Promise<ApiResponse<NotificationHistoryResponse>> {
  return request({
    url: '/bicanmou/user/subscription/notification/history',
    method: 'GET',
    data: {
      ...queryParams,
      ...pageQuery
    }
  })
}

// ========== 兼容性接口（保留现有功能） ==========

/**
 * 批量订阅交易所（支持多种通知方式）
 */
export function batchSubscribeExchange(params: {
  exchangeCode: string
  notificationMethods: ('TELEGRAM' | 'EMAIL')[]
  notificationEnabled: boolean
}): Promise<ApiResponse<SubscribeExchangeResponse[]>> {
  return request({
    url: '/bicanmou/user/subscription/batch-subscribe-exchange',
    method: 'POST',
    data: params
  })
}

/**
 * 更新订阅设置
 */
export function updateSubscriptionSettings(params: {
  exchangeCode: string
  notificationMethods: ('TELEGRAM' | 'EMAIL')[]
  notificationEnabled: boolean
}): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: '/bicanmou/user/subscription/update-settings',
    method: 'PUT',
    data: params
  })
}

/**
 * 直接订阅交易所（无需验证码，用于已绑定Telegram的用户）
 */
export function directSubscribeExchange(params: {
  exchangeId: string
  exchangeCode: string
  notificationMethod: 'TELEGRAM' | 'EMAIL'
  contact?: string
}): Promise<ApiResponse<{ success: boolean; message: string }>> {
  return request({
    url: '/bicanmou/user/subscription/subscribe-exchange',
    method: 'POST',
    data: params
  })
}

/**
 * 检查特定交易所和通知方式的订阅状态
 */
export function checkSubscriptionStatus(exchangeCode: string, notificationMethod: 'TELEGRAM' | 'EMAIL'): Promise<ApiResponse<boolean>> {
  return request({
    url: `/bicanmou/user/subscription/status/${exchangeCode}/${notificationMethod}`,
    method: 'GET'
  })
}

/**
 * 取消特定交易所和通知方式的订阅
 */
export function unsubscribeExchangeByMethod(exchangeCode: string, notificationMethod: 'TELEGRAM' | 'EMAIL'): Promise<ApiResponse<{ success: boolean; message: string }>> {
  console.log(exchangeCode)
  return request({
    url: `/bicanmou/user/subscription/unsubscribe-exchange/${exchangeCode}/${notificationMethod}`,
    method: 'DELETE'
  })
}
