<template>
  <div class="debug-page">
    <a-card title="协作调试信息" style="margin: 20px;">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="连接状态">
          <a-tag :color="connected ? 'green' : 'red'">
            {{ connected ? '已连接' : '未连接' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="正在连接">
          <a-tag :color="connecting ? 'orange' : 'default'">
            {{ connecting ? '是' : '否' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="在线用户数量">
          <a-tag color="blue">{{ onlineUserCount }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="用户列表长度">
          <a-tag color="purple">{{ onlineUsers.length }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-space>
        <a-button type="primary" @click="handleConnect" :loading="connecting">
          连接协作
        </a-button>
        <a-button @click="handleDisconnect" :disabled="!connected">
          断开连接
        </a-button>
        <a-button @click="handleRequestUsers" :disabled="!connected">
          请求用户列表
        </a-button>
        <a-button @click="handleClearLogs">
          清空日志
        </a-button>
      </a-space>

      <a-divider />

      <h4>在线用户列表</h4>
      <a-list :data-source="onlineUsers" size="small">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #avatar>
                <a-avatar :style="{ backgroundColor: item.color }">
                  {{ item.userName?.charAt(0) }}
                </a-avatar>
              </template>
              <template #title>{{ item.userName }}</template>
              <template #description>
                ID: {{ item.userId }} | 状态: {{ item.status }}
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>

      <a-divider />

      <h4>控制台日志</h4>
      <a-textarea
        v-model:value="logs"
        :rows="10"
        readonly
        style="font-family: monospace; font-size: 12px;"
      />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCollaboration } from '@/hooks/useCollaboration'

const logs = ref('')

// 重写console.log来捕获日志
const originalLog = console.log
const originalError = console.error
const originalWarn = console.warn

console.log = (...args) => {
  logs.value += `[LOG] ${new Date().toLocaleTimeString()}: ${args.join(' ')}\n`
  originalLog(...args)
}

console.error = (...args) => {
  logs.value += `[ERROR] ${new Date().toLocaleTimeString()}: ${args.join(' ')}\n`
  originalError(...args)
}

console.warn = (...args) => {
  logs.value += `[WARN] ${new Date().toLocaleTimeString()}: ${args.join(' ')}\n`
  originalWarn(...args)
}

// 协作功能
const collaboration = useCollaboration({
  documentId: 'debug-test',
  userId: 999,
  userName: '调试用户',
  autoConnect: false,
  onUserJoin: (user) => {
    logs.value += `[EVENT] 用户加入: ${user.userName}\n`
  },
  onUserLeave: (user) => {
    logs.value += `[EVENT] 用户离开: ${user.userName}\n`
  },
})

const {
  connected,
  connecting,
  onlineUsers,
  onlineUserCount,
  connect,
  disconnect,
  requestOnlineUsers,
} = collaboration

const handleConnect = () => {
  logs.value += `[ACTION] 开始连接协作服务\n`
  connect()
}

const handleDisconnect = () => {
  logs.value += `[ACTION] 断开协作连接\n`
  disconnect()
}

const handleRequestUsers = () => {
  logs.value += `[ACTION] 请求在线用户列表\n`
  requestOnlineUsers()
}

const handleClearLogs = () => {
  logs.value = ''
}

onMounted(() => {
  logs.value = `[INIT] 调试页面已加载\n`
})
</script>

<style scoped>
.debug-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>
