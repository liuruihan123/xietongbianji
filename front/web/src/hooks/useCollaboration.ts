/**
 * 协同编辑Hook
 */

import { ref, onBeforeUnmount, computed } from 'vue'
import { message } from 'ant-design-vue'
import { WebSocketManager } from '@/utils/websocket'
import { getWebSocketUrl } from '@/utils/runtimeConfig'
import type {
  OnlineUser,
  CollaborationMessage,
  CollaborationMessageType,
  ChatMessage,
  DocumentLock,
  UserStatus,
} from '@/types/collaboration'
import { getUserColor } from '@/types/collaboration'

export interface UseCollaborationOptions {
  /** 文档ID (支持number和string类型) */
  documentId: number | string
  /** 当前用户ID */
  userId: number
  /** 当前用户名 */
  userName: string
  /** WebSocket服务器地址 */
  wsUrl?: string
  /** 是否自动连接 */
  autoConnect?: boolean
  /** 消息回调 */
  onMessage?: (message: CollaborationMessage) => void
  /** 用户加入回调 */
  onUserJoin?: (user: OnlineUser) => void
  /** 用户离开回调 */
  onUserLeave?: (user: OnlineUser) => void
}

export function useCollaboration(options: UseCollaborationOptions) {
  const {
    documentId,
    userId,
    userName,
    wsUrl = getWebSocketUrl(),
    autoConnect = true,
    onMessage,
    onUserJoin,
    onUserLeave,
  } = options

  const wsManager = ref<WebSocketManager | null>(null)
  const connected = ref(false)
  const connecting = ref(false)
  const onlineUsers = ref<OnlineUser[]>([])
  const chatMessages = ref<ChatMessage[]>([])
  const documentLock = ref<DocumentLock | null>(null)

  let documentSubscriptionId: string | null = null
  let userSubscriptionId: string | null = null

  /**
   * 在线用户数量
   */
  const onlineUserCount = computed(() => onlineUsers.value.length)

  /**
   * 当前用户
   */
  const currentUser = computed(() => {
    return onlineUsers.value.find(u => u.userId === userId)
  })

  /**
   * 其他用户
   */
  const otherUsers = computed(() => {
    return onlineUsers.value.filter(u => u.userId !== userId)
  })

  /**
   * 连接WebSocket
   */
  async function connect() {
    if (connected.value || connecting.value) {
      return
    }

    try {
      connecting.value = true

      // 创建WebSocket管理器
      wsManager.value = new WebSocketManager({
        url: wsUrl,
        debug: import.meta.env.DEV,
      })

      // 连接
      await wsManager.value.connect(
        () => {
          connected.value = true
          connecting.value = false
          console.log('协同编辑连接成功')
          
          // 订阅主题
          subscribeTopics()

          // 发送加入消息
          sendUserJoin()

          // 请求当前在线用户列表
          setTimeout(() => {
            requestOnlineUsers()
          }, 1000)
        },
        (error) => {
          connecting.value = false
          console.error('协同编辑连接失败:', error)
          message.error('协同编辑连接失败')
        }
      )
    } catch (error) {
      connecting.value = false
      console.error('协同编辑连接错误:', error)
      message.error('协同编辑连接错误')
    }
  }

  /**
   * 断开连接
   */
  async function disconnect() {
    if (!wsManager.value) {
      return
    }

    try {
      // 发送离开消息
      sendUserLeave()

      // 取消订阅
      if (documentSubscriptionId) {
        wsManager.value.unsubscribe(documentSubscriptionId)
        documentSubscriptionId = null
      }
      if (userSubscriptionId) {
        wsManager.value.unsubscribe(userSubscriptionId)
        userSubscriptionId = null
      }

      // 断开连接
      await wsManager.value.disconnect()
      wsManager.value = null
      connected.value = false

      console.log('协同编辑已断开')
    } catch (error) {
      console.error('断开协同编辑失败:', error)
    }
  }

  /**
   * 订阅主题
   */
  function subscribeTopics() {
    if (!wsManager.value) {
      return
    }

    // 订阅文档主题
    documentSubscriptionId = wsManager.value.subscribe(
      `/topic/document/${documentId}`,
      handleMessage
    )

    // 订阅用户主题
    userSubscriptionId = wsManager.value.subscribe(
      `/user/queue/messages`,
      handleMessage
    )
  }

  /**
   * 处理消息
   */
  function handleMessage(data: CollaborationMessage) {
    console.log('收到协同消息:', data)

    // 调用回调
    onMessage?.(data)

    // 根据消息类型处理
    switch (data.type) {
      case 'USER_JOIN':
        console.log('处理用户加入消息:', data.payload)
        handleUserJoin(data.payload)
        break
      case 'USER_LEAVE':
        console.log('处理用户离开消息:', data.payload)
        handleUserLeave(data.payload)
        break
      case 'USER_STATUS_UPDATE':
        handleUserStatusUpdate(data.payload)
        break
      case 'CURSOR_UPDATE':
        handleCursorUpdate(data.payload)
        break
      case 'CHAT_MESSAGE':
        handleChatMessage(data.payload)
        break
      case 'DOCUMENT_LOCK':
        handleDocumentLock(data.payload)
        break
      case 'DOCUMENT_UNLOCK':
        handleDocumentUnlock()
        break
      case 'ONLINE_USERS_LIST':
        console.log('收到在线用户列表:', data.payload)
        if (Array.isArray(data.payload)) {
          onlineUsers.value = data.payload.map(user => ({
            ...user,
            color: getUserColor(user.userId)
          }))
          console.log('更新在线用户列表，当前用户数:', onlineUsers.value.length)
        }
        break
      default:
        console.log('未处理的消息类型:', data.type)
    }
  }

  /**
   * 处理用户加入
   */
  function handleUserJoin(user: OnlineUser) {
    console.log('处理用户加入，当前在线用户数:', onlineUsers.value.length)
    console.log('加入的用户信息:', user)

    // 添加颜色
    user.color = getUserColor(user.userId)

    // 添加到在线用户列表
    if (!onlineUsers.value.find(u => u.userId === user.userId)) {
      onlineUsers.value.push(user)
      console.log('用户已添加到列表，新的在线用户数:', onlineUsers.value.length)
      message.success(`${user.userName} 加入了协同编辑`)
      onUserJoin?.(user)
    } else {
      console.log('用户已存在于列表中，跳过添加')
    }
  }

  /**
   * 处理用户离开
   */
  function handleUserLeave(payload: { userId: number }) {
    console.log('处理用户离开，当前在线用户数:', onlineUsers.value.length)
    console.log('离开的用户ID:', payload.userId)

    const index = onlineUsers.value.findIndex(u => u.userId === payload.userId)
    if (index !== -1) {
      const user = onlineUsers.value[index]
      onlineUsers.value.splice(index, 1)
      console.log('用户已从列表中移除，新的在线用户数:', onlineUsers.value.length)
      message.info(`${user.userName} 离开了协同编辑`)
      onUserLeave?.(user)
    } else {
      console.log('未找到要离开的用户')
    }
  }

  /**
   * 处理用户状态更新
   */
  function handleUserStatusUpdate(payload: { userId: number; status: UserStatus }) {
    const user = onlineUsers.value.find(u => u.userId === payload.userId)
    if (user) {
      user.status = payload.status
    }
  }

  /**
   * 处理光标更新
   */
  function handleCursorUpdate(payload: any) {
    const user = onlineUsers.value.find(u => u.userId === payload.userId)
    if (user) {
      user.cursorPosition = payload.cursorPosition
      user.selection = payload.selection
    }
  }

  /**
   * 处理聊天消息
   */
  function handleChatMessage(chatMessage: ChatMessage) {
    chatMessages.value.push(chatMessage)
  }

  /**
   * 处理文档锁定
   */
  function handleDocumentLock(lock: DocumentLock) {
    documentLock.value = lock
    message.warning(`文档已被 ${lock.userName} 锁定`)
  }

  /**
   * 处理文档解锁
   */
  function handleDocumentUnlock() {
    documentLock.value = null
    message.success('文档已解锁')
  }

  /**
   * 发送用户加入消息
   */
  function sendUserJoin() {
    console.log('发送用户加入消息:', { userId, userName })

    // 先将当前用户添加到本地列表
    const currentUserInfo: OnlineUser = {
      userId,
      userName,
      status: 'ONLINE' as any,
      joinTime: new Date().toISOString(),
      color: getUserColor(userId),
      isAuthenticated: true, // 假设已认证用户
    }

    // 检查当前用户是否已在列表中
    if (!onlineUsers.value.find(u => u.userId === userId)) {
      onlineUsers.value.push(currentUserInfo)
      console.log('当前用户已添加到本地列表，在线用户数:', onlineUsers.value.length)
    }

    // 发送加入消息到服务器
    sendMessage('USER_JOIN', {
      userId,
      userName,
      status: 'ONLINE',
      joinTime: new Date().toISOString(),
    })
  }

  /**
   * 发送用户离开消息
   */
  function sendUserLeave() {
    sendMessage('USER_LEAVE', { userId })
  }

  /**
   * 请求在线用户列表
   */
  function requestOnlineUsers() {
    console.log('请求在线用户列表')
    sendMessage('REQUEST_ONLINE_USERS', { documentId })
  }

  /**
   * 发送消息
   */
  function sendMessage(type: CollaborationMessageType, payload: any) {
    if (!wsManager.value || !connected.value) {
      console.warn('WebSocket未连接,无法发送消息')
      return
    }

    const message: CollaborationMessage = {
      type,
      documentId,
      userId,
      userName,
      payload,
      timestamp: new Date().toISOString(),
    }

    wsManager.value.send(`/app/document/${documentId}`, message)
  }

  /**
   * 发送聊天消息
   */
  function sendChatMessage(content: string) {
    const chatMessage: ChatMessage = {
      id: `${Date.now()}-${userId}`,
      userId,
      userName,
      content,
      sendTime: new Date().toISOString(),
    }

    sendMessage('CHAT_MESSAGE', chatMessage)
  }

  /**
   * 更新光标位置
   */
  function updateCursor(cursorPosition: any, selection?: any) {
    sendMessage('CURSOR_UPDATE', {
      userId,
      cursorPosition,
      selection,
    })
  }

  // 自动连接
  if (autoConnect) {
    connect()
  }

  // 组件卸载时断开连接
  onBeforeUnmount(() => {
    disconnect()
  })

  return {
    /** 是否已连接 */
    connected,
    /** 是否正在连接 */
    connecting,
    /** 在线用户列表 */
    onlineUsers,
    /** 在线用户数量 */
    onlineUserCount,
    /** 当前用户 */
    currentUser,
    /** 其他用户 */
    otherUsers,
    /** 聊天消息列表 */
    chatMessages,
    /** 文档锁定信息 */
    documentLock,
    /** 连接 */
    connect,
    /** 断开连接 */
    disconnect,
    /** 发送消息 */
    sendMessage,
    /** 发送聊天消息 */
    sendChatMessage,
    /** 更新光标位置 */
    updateCursor,
    /** 请求在线用户列表 */
    requestOnlineUsers,
  }
}

