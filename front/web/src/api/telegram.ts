/**
 * Telegram 集成 API
 * 用于处理 Telegram 账号绑定和通知功能
 */

import { get, post } from '@/utils/request'

/**
 * Telegram 绑定状态接口
 */
export interface TelegramBindStatus {
  /** 是否已绑定 */
  isBound: boolean
  /** Telegram 用户ID */
  telegramUserId?: string
  /** Telegram 用户名 */
  telegramUsername?: string
  /** 绑定时间 */
  bindTime?: string
  /** 是否启用通知 */
  notificationEnabled?: boolean
}

/**
 * Telegram 绑定链接响应接口
 */
export interface TelegramLinkResponse {
  /** 绑定链接 */
  link: string
  /** 绑定码 */
  bindCode?: string
  /** 过期时间 */
  expireTime?: string
}

/**
 * API 响应接口
 */
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 生成 Telegram 绑定链接
 * @returns Promise<TelegramLinkResponse> Telegram 绑定链接信息
 */
export async function generateTelegramLink(): Promise<TelegramLinkResponse> {
  const res = await post<TelegramLinkResponse>({
    url: '/telegram/generateBindLink',
  })
  if (res.code === 200) {
    return res.data
  }
  throw new Error(res.msg || '生成 Telegram 绑定链接失败')
}

/**
 * 获取 Telegram 绑定状态
 * @returns Promise<TelegramBindStatus> Telegram 绑定状态信息
 */
export async function getTelegramBindStatus(): Promise<TelegramBindStatus> {
  const res = await get<TelegramBindStatus>({
    url: '/telegram/bindStatus',
  })
  if (res.code === 200) {
    return res.data
  }
  throw new Error(res.msg || '获取 Telegram 绑定状态失败')
}

/**
 * 解绑 Telegram 账号
 * @returns Promise<void>
 */
export function unbindTelegram(): Promise<void> {
  return post<ApiResponse<void>>({
    url: '/telegram/unbind',
  }).then((res) => {
    if (res.code !== 200) {
      throw new Error(res.msg || '解绑 Telegram 失败')
    }
  })
}

/**
 * 更新 Telegram 通知设置
 * @param enabled 是否启用通知
 * @returns Promise<void>
 */
export function updateTelegramNotification(enabled: boolean): Promise<void> {
  return post<ApiResponse<void>>({
    url: '/telegram/updateNotification',
    data: { enabled },
  }).then((res) => {
    if (res.code !== 200) {
      throw new Error(res.msg || '更新 Telegram 通知设置失败')
    }
  })
}

/**
 * 发送 Telegram 测试消息
 * @param message 测试消息内容
 * @returns Promise<void>
 */
export function sendTelegramTestMessage(message: string): Promise<void> {
  return post<ApiResponse<void>>({
    url: '/telegram/sendTestMessage',
    data: { message },
  }).then((res) => {
    if (res.code !== 200) {
      throw new Error(res.msg || '发送测试消息失败')
    }
  })
}

