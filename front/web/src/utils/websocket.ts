/**
 * WebSocket连接管理工具
 */

import { Client, IMessage, StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export type MessageHandler = (message: any) => void
export type ErrorHandler = (error: any) => void
export type ConnectHandler = () => void

export interface WebSocketConfig {
  /** WebSocket服务器地址 */
  url: string
  /** 心跳间隔(毫秒) */
  heartbeatIncoming?: number
  /** 心跳间隔(毫秒) */
  heartbeatOutgoing?: number
  /** 重连延迟(毫秒) */
  reconnectDelay?: number
  /** 是否开启调试 */
  debug?: boolean
}

/**
 * WebSocket管理类
 */
export class WebSocketManager {
  private client: Client | null = null
  private subscriptions: Map<string, StompSubscription> = new Map()
  private config: WebSocketConfig
  private connected = false
  private connecting = false

  constructor(config: WebSocketConfig) {
    this.config = {
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      reconnectDelay: 5000,
      debug: false,
      ...config,
    }
  }

  /**
   * 连接WebSocket
   */
  connect(
    onConnect?: ConnectHandler,
    onError?: ErrorHandler
  ): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.connected) {
        resolve()
        return
      }

      if (this.connecting) {
        console.log('WebSocket正在连接中...')
        return
      }

      this.connecting = true

      // 创建STOMP客户端
      this.client = new Client({
        webSocketFactory: () => new SockJS(this.config.url) as any,
        heartbeatIncoming: this.config.heartbeatIncoming,
        heartbeatOutgoing: this.config.heartbeatOutgoing,
        reconnectDelay: this.config.reconnectDelay,
        // 修复生产环境 "this.debug is not a function" 错误
        // 必须传入空函数而不是 undefined,因为 STOMP Client 内部会调用 this.debug()
        debug: import.meta.env.DEV && this.config.debug ? (str) => console.log(str) : () => {},

        onConnect: () => {
          console.log('WebSocket连接成功')
          this.connected = true
          this.connecting = false
          onConnect?.()
          resolve()
        },

        onStompError: (frame) => {
          console.error('WebSocket STOMP错误:', frame)
          this.connecting = false
          const error = new Error(frame.headers['message'] || 'STOMP错误')
          onError?.(error)
          reject(error)
        },

        onWebSocketError: (event) => {
          console.error('WebSocket错误:', event)
          this.connecting = false
          onError?.(event)
          reject(event)
        },

        onDisconnect: () => {
          console.log('WebSocket断开连接')
          this.connected = false
          this.connecting = false
        },
      })

      // 激活连接
      this.client.activate()
    })
  }

  /**
   * 断开连接
   */
  disconnect(): Promise<void> {
    return new Promise((resolve) => {
      if (!this.client) {
        resolve()
        return
      }

      // 取消所有订阅
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      // 断开连接
      this.client.deactivate()
      this.client = null
      this.connected = false
      this.connecting = false

      console.log('WebSocket已断开')
      resolve()
    })
  }

  /**
   * 订阅主题
   */
  subscribe(
    destination: string,
    callback: MessageHandler
  ): string {
    if (!this.client || !this.connected) {
      throw new Error('WebSocket未连接')
    }

    const subscription = this.client.subscribe(destination, (message: IMessage) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (error) {
        console.error('解析消息失败:', error)
        callback(message.body)
      }
    })

    const subscriptionId = `${destination}-${Date.now()}`
    this.subscriptions.set(subscriptionId, subscription)

    console.log(`订阅主题: ${destination}`)
    return subscriptionId
  }

  /**
   * 取消订阅
   */
  unsubscribe(subscriptionId: string): void {
    const subscription = this.subscriptions.get(subscriptionId)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(subscriptionId)
      console.log(`取消订阅: ${subscriptionId}`)
    }
  }

  /**
   * 发送消息
   */
  send(destination: string, body: any): void {
    if (!this.client || !this.connected) {
      throw new Error('WebSocket未连接')
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    })

    console.log(`发送消息到: ${destination}`, body)
  }

  /**
   * 是否已连接
   */
  isConnected(): boolean {
    return this.connected
  }

  /**
   * 是否正在连接
   */
  isConnecting(): boolean {
    return this.connecting
  }
}

