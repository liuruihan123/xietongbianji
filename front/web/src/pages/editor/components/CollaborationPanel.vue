<template>
  <div class="collaboration-panel">
    <!-- 协作状态指示器 -->
    <div class="collaboration-status">
      <div class="status-header">
        <TeamOutlined class="icon" />
        <span class="title">实时协作</span>
        <a-badge :count="onlineUsers.length" :offset="[8, 0]" />
      </div>
      
      <div class="connection-indicator">
        <div :class="['status-dot', connectionStatus]"></div>
        <span class="status-text">{{ getConnectionStatusText() }}</span>
      </div>
    </div>

    <!-- 在线用户列表 -->
    <div class="online-users">
      <div class="users-header">
        <span>在线用户 ({{ onlineUsers.length }})</span>
        <a-button 
          type="text" 
          size="small" 
          @click="toggleUsersList"
          :icon="showUsersList ? h(UpOutlined) : h(DownOutlined)"
        />
      </div>

      <div v-show="showUsersList" class="users-list">
        <div
          v-for="user in onlineUsers"
          :key="user.sessionId"
          class="user-item"
          :class="{ 'current-user': isCurrentUser(user) }"
        >
          <div class="user-avatar" :style="{ backgroundColor: getUserColor(user) }">
            <UserOutlined v-if="!user.avatarUrl" />
            <img v-else :src="user.avatarUrl" :alt="user.userName" />
          </div>
          
          <div class="user-info">
            <div class="user-name">
              {{ user.userName }}
              <a-tag v-if="user.isAuthenticated" size="small" color="blue">认证</a-tag>
              <a-tag v-else size="small" color="default">访客</a-tag>
            </div>
            <div class="user-status">
              <span class="status-dot" :class="user.status"></span>
              {{ getStatusText(user.status) }}
            </div>
          </div>

          <!-- 光标指示器 -->
          <div v-if="user.cursorPosition" class="cursor-indicator">
            <EnvironmentOutlined :style="{ color: getUserColor(user) }" />
          </div>
        </div>

        <div v-if="onlineUsers.length === 0" class="empty-state">
          <UserOutlined class="empty-icon" />
          <p>暂无其他协作者</p>
        </div>
      </div>
    </div>

    <!-- 协作工具 -->
    <div class="collaboration-tools">
      <a-space>
        <a-tooltip title="重新连接">
          <a-button 
            type="text" 
            size="small" 
            @click="reconnect"
            :loading="connectionStatus === 'connecting'"
            :disabled="connectionStatus === 'connected'"
          >
            <ReloadOutlined />
          </a-button>
        </a-tooltip>
        
        <a-tooltip title="断开协作">
          <a-button 
            type="text" 
            size="small" 
            @click="disconnect"
            :disabled="connectionStatus === 'disconnected'"
          >
            <DisconnectOutlined />
          </a-button>
        </a-tooltip>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  TeamOutlined,
  UserOutlined,
  UpOutlined,
  DownOutlined,
  EnvironmentOutlined,
  ReloadOutlined,
  DisconnectOutlined,
} from '@ant-design/icons-vue'
import type { UserPresence } from '@/composables/useCollaboration'

interface Props {
  onlineUsers: UserPresence[]
  connectionStatus: 'connecting' | 'connected' | 'disconnected' | 'error'
  currentUserId?: number | string
}

interface Emits {
  (e: 'reconnect'): void
  (e: 'disconnect'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const showUsersList = ref(true)

const isCurrentUser = (user: UserPresence) => {
  if (props.currentUserId && user.userId) {
    return props.currentUserId === user.userId
  }
  return false
}

const getUserColor = (user: UserPresence) => {
  if (user.userColor) return user.userColor
  
  const colors = [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
    '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
  ]
  
  const id = user.userId || user.sessionId
  const hash = id.toString().split('').reduce((a, b) => {
    a = ((a << 5) - a) + b.charCodeAt(0)
    return a & a
  }, 0)
  
  return colors[Math.abs(hash) % colors.length]
}

const getStatusText = (status: string) => {
  const statusMap = {
    online: '在线',
    away: '离开',
    busy: '忙碌'
  }
  return statusMap[status as keyof typeof statusMap] || '未知'
}

const getConnectionStatusText = () => {
  const statusMap = {
    connecting: '连接中...',
    connected: '已连接',
    disconnected: '已断开',
    error: '连接错误'
  }
  return statusMap[props.connectionStatus]
}

const toggleUsersList = () => {
  showUsersList.value = !showUsersList.value
}

const reconnect = () => {
  emit('reconnect')
  message.info('正在重新连接协作服务...')
}

const disconnect = () => {
  emit('disconnect')
  message.info('已断开协作连接')
}
</script>

<style scoped lang="scss">
.collaboration-panel {
  width: 280px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  .collaboration-status {
    .status-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .icon {
        color: #1890ff;
        font-size: 16px;
      }

      .title {
        font-weight: 500;
        font-size: 14px;
      }
    }

    .connection-indicator {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #666;

      .status-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;

        &.connecting {
          background: #faad14;
          animation: pulse 1.5s infinite;
        }

        &.connected {
          background: #52c41a;
        }

        &.disconnected,
        &.error {
          background: #ff4d4f;
        }
      }
    }
  }

  .online-users {
    .users-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      font-size: 13px;
      font-weight: 500;
      color: #333;
    }

    .users-list {
      max-height: 200px;
      overflow-y: auto;

      .user-item {
        display: flex;
        align-items: center;
        padding: 8px 0;
        gap: 10px;
        border-bottom: 1px solid #f5f5f5;

        &:last-child {
          border-bottom: none;
        }

        &.current-user {
          background: #e6f7ff;
          border-radius: 4px;
          padding: 8px;
          margin: 0 -8px;
        }

        .user-avatar {
          width: 28px;
          height: 28px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 12px;
          font-weight: 500;

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
          }
        }

        .user-info {
          flex: 1;
          min-width: 0;

          .user-name {
            font-size: 13px;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 4px;
            margin-bottom: 2px;
          }

          .user-status {
            font-size: 11px;
            color: #666;
            display: flex;
            align-items: center;
            gap: 4px;

            .status-dot {
              width: 6px;
              height: 6px;
              border-radius: 50%;

              &.online {
                background: #52c41a;
              }

              &.away {
                background: #faad14;
              }

              &.busy {
                background: #ff4d4f;
              }
            }
          }
        }

        .cursor-indicator {
          font-size: 12px;
          opacity: 0.7;
        }
      }

      .empty-state {
        text-align: center;
        padding: 20px 0;
        color: #999;

        .empty-icon {
          font-size: 24px;
          margin-bottom: 8px;
          opacity: 0.5;
        }

        p {
          margin: 0;
          font-size: 12px;
        }
      }
    }
  }

  .collaboration-tools {
    border-top: 1px solid #f0f0f0;
    padding-top: 12px;
    display: flex;
    justify-content: center;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
