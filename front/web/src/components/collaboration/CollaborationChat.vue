<template>
  <div class="collaboration-chat">
    <div class="chat-header">
      <h4>协同聊天</h4>
      <a-badge :count="unreadCount" />
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <a-empty v-if="messages.length === 0" description="暂无消息" />

      <div
        v-for="msg in messages"
        :key="msg.id"
        :class="['message-item', { 'is-mine': msg.userId === currentUserId, 'is-system': msg.isSystem }]"
      >
        <!-- 系统消息 -->
        <div v-if="msg.isSystem" class="system-message">
          <InfoCircleOutlined />
          <span>{{ msg.content }}</span>
        </div>

        <!-- 普通消息 -->
        <div v-else class="user-message">
          <a-avatar
            v-if="msg.userId !== currentUserId"
            :size="32"
            :style="{ backgroundColor: getUserColor(msg.userId) }"
          >
            {{ msg.userName?.charAt(0) || 'U' }}
          </a-avatar>

          <div class="message-content">
            <div v-if="msg.userId !== currentUserId" class="message-sender">
              {{ msg.userName }}
            </div>
            <div class="message-bubble">
              {{ msg.content }}
            </div>
            <div class="message-time">
              {{ formatTime(msg.sendTime) }}
            </div>
          </div>

          <a-avatar
            v-if="msg.userId === currentUserId"
            :size="32"
            :style="{ backgroundColor: getUserColor(msg.userId) }"
          >
            {{ msg.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <a-input
        v-model:value="inputMessage"
        placeholder="输入消息..."
        @press-enter="handleSend"
      >
        <template #suffix>
          <a-button
            type="primary"
            size="small"
            :disabled="!inputMessage.trim()"
            @click="handleSend"
          >
            <SendOutlined />
          </a-button>
        </template>
      </a-input>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { InfoCircleOutlined, SendOutlined } from '@ant-design/icons-vue'
import type { ChatMessage } from '@/types/collaboration'
import { getUserColor } from '@/types/collaboration'
import dayjs from 'dayjs'

const props = defineProps<{
  messages: ChatMessage[]
  currentUserId: number
}>()

const emit = defineEmits<{
  send: [message: string]
}>()

const inputMessage = ref('')
const messagesContainer = ref<HTMLElement>()
const unreadCount = ref(0)

function handleSend() {
  const message = inputMessage.value.trim()
  if (!message) {
    return
  }

  emit('send', message)
  inputMessage.value = ''
}

function formatTime(time: string): string {
  return dayjs(time).format('HH:mm')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 监听消息变化,自动滚动到底部
watch(() => props.messages.length, () => {
  scrollToBottom()
})
</script>

<style scoped lang="scss">
.collaboration-chat {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: white;

  .chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;

    h4 {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
    }
  }

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .message-item {
      margin-bottom: 16px;

      &.is-system {
        .system-message {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          padding: 8px;
          background: #f0f0f0;
          border-radius: 4px;
          font-size: 12px;
          color: #666;
        }
      }

      &.is-mine {
        .user-message {
          flex-direction: row-reverse;

          .message-content {
            align-items: flex-end;

            .message-bubble {
              background: #1890ff;
              color: white;
            }
          }
        }
      }

      .user-message {
        display: flex;
        gap: 8px;

        .message-content {
          display: flex;
          flex-direction: column;
          gap: 4px;
          max-width: 70%;

          .message-sender {
            font-size: 12px;
            color: #999;
          }

          .message-bubble {
            padding: 8px 12px;
            background: #f0f0f0;
            border-radius: 8px;
            word-break: break-word;
          }

          .message-time {
            font-size: 11px;
            color: #999;
          }
        }
      }
    }
  }

  .chat-input {
    padding: 12px 16px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>

