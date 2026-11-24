import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import SockJS from 'sockjs-client'

export interface UserPresence {
  userId?: number
  userName: string
  sessionId: string
  isAuthenticated: boolean
  avatarUrl?: string
  userColor?: string
  joinTime: number
  lastActiveTime: number
  cursorPosition?: {
    line: number
    column: number
    timestamp: number
  }
  selectionRange?: {
    startLine: number
    startColumn: number
    endLine: number
    endColumn: number
    timestamp: number
  }
  status: 'online' | 'away' | 'busy'
}

export interface CollaborationMessage {
  type: 'cursor' | 'selection' | 'content' | 'user_joined' | 'user_left' | 'online_users' | 'heartbeat'
  userId?: number
  userName?: string
  data?: any
  timestamp?: number
  documentId?: string
  sessionId?: string
}

/**
 * 实时协作组合式函数
 * 提供WebSocket连接、用户状态管理、实时同步等功能
 */
export function useCollaboration(documentId: string, shareCode?: string) {
  const socket = ref<WebSocket | null>(null)
  const isConnected = ref(false)
  const onlineUsers = ref<UserPresence[]>([])
  const currentUser = ref<UserPresence | null>(null)
  
  // 连接状态
  const connectionStatus = ref<'connecting' | 'connected' | 'disconnected' | 'error'>('disconnected')
  const reconnectAttempts = ref(0)
  const maxReconnectAttempts = 5
  
  // 事件回调
  const eventCallbacks = reactive<{
    onUserJoined?: (user: UserPresence) => void
    onUserLeft?: (user: UserPresence) => void
    onCursorUpdate?: (userId: number | string, position: any) => void
    onSelectionUpdate?: (userId: number | string, selection: any) => void
    onContentUpdate?: (data: any) => void
    onConnectionChange?: (status: string) => void
  }>({})

  /**
   * 连接到WebSocket服务器
   */
  const connect = () => {
    if (socket.value?.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接，跳过重复连接')
      return
    }

    connectionStatus.value = 'connecting'
    console.log('正在连接协作WebSocket...', { documentId, shareCode })

    try {
      // 构建WebSocket URL
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = window.location.host
      const params = new URLSearchParams({
        documentId,
        ...(shareCode && { shareCode })
      })
      
      // 使用SockJS连接，提供更好的兼容性
      const sockjsUrl = `${window.location.protocol}//${host}/ws/collaboration?${params.toString()}`
      console.log('SockJS连接URL:', sockjsUrl)
      
      const sockjs = new SockJS(sockjsUrl)
      socket.value = sockjs as any

      sockjs.onopen = () => {
        console.log('协作WebSocket连接成功')
        isConnected.value = true
        connectionStatus.value = 'connected'
        reconnectAttempts.value = 0
        
        // 发送心跳
        startHeartbeat()
        
        // 触发连接状态回调
        eventCallbacks.onConnectionChange?.('connected')
        
        message.success('实时协作已启用')
      }

      sockjs.onmessage = (event) => {
        try {
          const message: CollaborationMessage = JSON.parse(event.data)
          handleMessage(message)
        } catch (error) {
          console.error('解析WebSocket消息失败:', error)
        }
      }

      sockjs.onclose = (event) => {
        console.log('协作WebSocket连接关闭:', event)
        isConnected.value = false
        connectionStatus.value = 'disconnected'
        
        // 清空在线用户列表
        onlineUsers.value = []
        
        // 触发连接状态回调
        eventCallbacks.onConnectionChange?.('disconnected')
        
        // 尝试重连
        if (reconnectAttempts.value < maxReconnectAttempts) {
          setTimeout(() => {
            reconnectAttempts.value++
            console.log(`尝试重连 (${reconnectAttempts.value}/${maxReconnectAttempts})`)
            connect()
          }, 3000 * reconnectAttempts.value) // 递增延迟
        } else {
          message.error('实时协作连接已断开，请刷新页面重试')
        }
      }

      sockjs.onerror = (error) => {
        console.error('协作WebSocket错误:', error)
        connectionStatus.value = 'error'
        eventCallbacks.onConnectionChange?.('error')
      }

    } catch (error) {
      console.error('创建WebSocket连接失败:', error)
      connectionStatus.value = 'error'
      message.error('无法建立实时协作连接')
    }
  }

  /**
   * 断开连接
   */
  const disconnect = () => {
    if (socket.value) {
      socket.value.close()
      socket.value = null
    }
    isConnected.value = false
    connectionStatus.value = 'disconnected'
    stopHeartbeat()
  }

  /**
   * 发送消息
   */
  const sendMessage = (message: CollaborationMessage) => {
    if (!isConnected.value || !socket.value) {
      console.warn('WebSocket未连接，无法发送消息')
      return false
    }

    try {
      message.documentId = documentId
      message.timestamp = Date.now()
      socket.value.send(JSON.stringify(message))
      return true
    } catch (error) {
      console.error('发送WebSocket消息失败:', error)
      return false
    }
  }

  /**
   * 处理接收到的消息
   */
  const handleMessage = (message: CollaborationMessage) => {
    console.log('收到协作消息:', message)

    switch (message.type) {
      case 'online_users':
        onlineUsers.value = message.data || []
        console.log('在线用户列表更新:', onlineUsers.value)
        break

      case 'user_joined':
        const joinedUser = message.data as UserPresence
        if (joinedUser && !onlineUsers.value.find(u => u.sessionId === joinedUser.sessionId)) {
          onlineUsers.value.push(joinedUser)
          console.log('用户加入:', joinedUser.userName)
          message.info(`${joinedUser.userName} 加入了协作`)
          eventCallbacks.onUserJoined?.(joinedUser)
        }
        break

      case 'user_left':
        const leftUser = message.data as UserPresence
        if (leftUser) {
          onlineUsers.value = onlineUsers.value.filter(u => u.sessionId !== leftUser.sessionId)
          console.log('用户离开:', leftUser.userName)
          eventCallbacks.onUserLeft?.(leftUser)
        }
        break

      case 'cursor':
        if (message.userId) {
          eventCallbacks.onCursorUpdate?.(message.userId, message.data)
        }
        break

      case 'selection':
        if (message.userId) {
          eventCallbacks.onSelectionUpdate?.(message.userId, message.data)
        }
        break

      case 'content':
        eventCallbacks.onContentUpdate?.(message.data)
        break
    }
  }

  /**
   * 发送光标位置更新
   */
  const updateCursor = (line: number, column: number) => {
    sendMessage({
      type: 'cursor',
      data: { line, column }
    })
  }

  /**
   * 发送选择区域更新
   */
  const updateSelection = (startLine: number, startColumn: number, endLine: number, endColumn: number) => {
    sendMessage({
      type: 'selection',
      data: { startLine, startColumn, endLine, endColumn }
    })
  }

  /**
   * 发送内容更新
   */
  const updateContent = (data: any) => {
    sendMessage({
      type: 'content',
      data
    })
  }

  // 心跳相关
  let heartbeatInterval: number | null = null

  const startHeartbeat = () => {
    if (heartbeatInterval) return
    
    heartbeatInterval = window.setInterval(() => {
      sendMessage({ type: 'heartbeat' })
    }, 30000) // 30秒心跳
  }

  const stopHeartbeat = () => {
    if (heartbeatInterval) {
      clearInterval(heartbeatInterval)
      heartbeatInterval = null
    }
  }

  /**
   * 设置事件回调
   */
  const setEventCallbacks = (callbacks: Partial<typeof eventCallbacks>) => {
    Object.assign(eventCallbacks, callbacks)
  }

  /**
   * 获取用户颜色
   */
  const getUserColor = (userId: number | string): string => {
    const colors = [
      '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
      '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
    ]
    
    const hash = typeof userId === 'string' ? 
      userId.split('').reduce((a, b) => { a = ((a << 5) - a) + b.charCodeAt(0); return a & a }, 0) :
      userId
    
    return colors[Math.abs(hash) % colors.length]
  }

  // 生命周期管理
  onMounted(() => {
    connect()
  })

  onUnmounted(() => {
    disconnect()
  })

  return {
    // 状态
    isConnected,
    connectionStatus,
    onlineUsers,
    currentUser,
    
    // 方法
    connect,
    disconnect,
    sendMessage,
    updateCursor,
    updateSelection,
    updateContent,
    setEventCallbacks,
    getUserColor
  }
}
