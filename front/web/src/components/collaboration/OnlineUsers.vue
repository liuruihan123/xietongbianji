<template>
  <div class="online-users">
    <div class="header">
      <h4>在线用户 ({{ users.length }})</h4>
      <a-badge :count="users.length" :number-style="{ backgroundColor: '#52c41a' }" />
    </div>

    <a-list
      :data-source="users"
      :locale="{ emptyText: '暂无在线用户' }"
      size="small"
    >
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #avatar>
              <a-badge :color="item.color" dot>
                <a-avatar
                  :style="{ backgroundColor: item.color || '#1890ff' }"
                  :size="32"
                >
                  {{ item.userName?.charAt(0) || 'U' }}
                </a-avatar>
              </a-badge>
            </template>

            <template #title>
              <div class="user-name">
                {{ item.userName }}
                <a-tag v-if="item.userId === currentUserId" color="blue" size="small">
                  我
                </a-tag>
              </div>
            </template>

            <template #description>
              <div class="user-status">
                <a-tag :color="getStatusColor(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </a-tag>
                <span class="join-time">
                  {{ formatJoinTime(item.joinTime) }}
                </span>
              </div>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OnlineUser, UserStatus } from '@/types/collaboration'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const props = defineProps<{
  users: OnlineUser[]
  currentUserId?: number
}>()

function getStatusColor(status: UserStatus): string {
  const colorMap: Record<UserStatus, string> = {
    ONLINE: 'green',
    OFFLINE: 'default',
    IDLE: 'orange',
    BUSY: 'red',
  }
  return colorMap[status] || 'default'
}

function getStatusText(status: UserStatus): string {
  const textMap: Record<UserStatus, string> = {
    ONLINE: '在线',
    OFFLINE: '离线',
    IDLE: '空闲',
    BUSY: '忙碌',
  }
  return textMap[status] || '未知'
}

function formatJoinTime(time: string): string {
  return dayjs(time).fromNow() + '加入'
}
</script>

<style scoped lang="scss">
.online-users {
  .header {
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

  .user-name {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
  }

  .user-status {
    display: flex;
    align-items: center;
    gap: 8px;

    .join-time {
      font-size: 12px;
      color: #999;
    }
  }
}
</style>

