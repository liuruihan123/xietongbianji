<template>
  <div class="member-list">
    <div class="member-header">
      <h3>成员管理</h3>
      <a-button type="primary" @click="handleInvite">
        <UserAddOutlined /> 邀请成员
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="members"
      :loading="loading"
      :pagination="false"
      row-key="id"
    >
      <!-- 成员信息 -->
      <template #member="{ record }">
        <div class="member-cell">
          <a-avatar :size="40" :style="{ backgroundColor: '#1890ff' }">
            {{ record.userName?.charAt(0) || 'U' }}
          </a-avatar>
          <div class="member-info">
            <div class="member-name">{{ record.userName }}</div>
            <div class="member-email">{{ record.email || '暂无邮箱' }}</div>
          </div>
        </div>
      </template>

      <!-- 角色 -->
      <template #role="{ record }">
        <a-tag :color="getRoleColor(record.role)">
          {{ getRoleLabel(record.role) }}
        </a-tag>
      </template>

      <!-- 加入时间 -->
      <template #joinTime="{ record }">
        {{ formatDate(record.joinTime) }}
      </template>

      <!-- 操作 -->
      <template #action="{ record }">
        <a-space v-if="canManage && record.role !== 'owner'">
          <a-dropdown>
            <a-button type="link" size="small">
              更改角色 <DownOutlined />
            </a-button>
            <template #overlay>
              <a-menu @click="({ key }) => handleChangeRole(record, key)">
                <a-menu-item key="admin">管理员</a-menu-item>
                <a-menu-item key="editor">编辑者</a-menu-item>
                <a-menu-item key="viewer">查看者</a-menu-item>
                <a-menu-item key="member">成员</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>

          <a-popconfirm
            title="确定要移除该成员吗?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleRemove(record)"
          >
            <a-button type="link" size="small" danger>
              移除
            </a-button>
          </a-popconfirm>
        </a-space>
        <span v-else class="no-action">-</span>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { UserAddOutlined, DownOutlined } from '@ant-design/icons-vue'
import { useWorkspaceStore } from '@/stores/workspace'
import type { WorkspaceMember } from '@/types/workspace'
import dayjs from 'dayjs'

defineProps<{
  members: WorkspaceMember[]
  loading?: boolean
  canManage?: boolean
}>()

const emit = defineEmits<{
  invite: []
  changeRole: [member: WorkspaceMember, role: string]
  remove: [member: WorkspaceMember]
}>()

const workspaceStore = useWorkspaceStore()

const columns = [
  {
    title: '成员',
    dataIndex: 'member',
    key: 'member',
    slots: { customRender: 'member' },
    width: 300,
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
    slots: { customRender: 'role' },
    width: 120,
  },
  {
    title: '加入时间',
    dataIndex: 'joinTime',
    key: 'joinTime',
    slots: { customRender: 'joinTime' },
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 200,
  },
]

function getRoleColor(role: string): string {
  const colorMap: Record<string, string> = {
    owner: 'red',
    admin: 'orange',
    editor: 'green',
    viewer: 'orange',
    member: 'blue',
  }
  return colorMap[role] || 'default'
}

function getRoleLabel(role: string): string {
  const labelMap: Record<string, string> = {
    owner: '所有者',
    admin: '管理员',
    editor: '编辑者',
    viewer: '查看者',
    member: '成员',
  }
  return labelMap[role] || role
}

function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function handleInvite() {
  emit('invite')
}

function handleChangeRole(member: WorkspaceMember, role: string) {
  emit('changeRole', member, role)
}

function handleRemove(member: WorkspaceMember) {
  emit('remove', member)
}
</script>

<style scoped lang="scss">
.member-list {
  .member-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
    }
  }

  .member-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .member-info {
      .member-name {
        font-weight: 500;
        color: #333;
      }

      .member-email {
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }
    }
  }

  .no-action {
    color: #999;
  }
}
</style>

