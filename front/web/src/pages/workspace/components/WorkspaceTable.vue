<template>
  <a-table
    :columns="columns"
    :data-source="workspaces"
    :loading="loading"
    :pagination="pagination"
    row-key="id"
    @change="handleTableChange"
  >
    <!-- 工作空间名称 -->
    <template #name="{ record }">
      <div class="workspace-name-cell">
        <a-avatar
          v-if="record.icon"
          :src="record.icon"
          :size="40"
          shape="square"
        />
        <a-avatar
          v-else
          :size="40"
          shape="square"
          :style="{ backgroundColor: '#1890ff' }"
        >
          <template #icon>
            <FolderOpenOutlined />
          </template>
        </a-avatar>
        <div class="name-info">
          <div class="name-text">
            {{ record.name }}
            <a-tag v-if="record.isDefault" color="blue" size="small">默认</a-tag>
          </div>
          <div class="description-text">{{ record.description || '暂无描述' }}</div>
        </div>
      </div>
    </template>

    <!-- 存储空间 -->
    <template #storage="{ record }">
      <div class="storage-cell">
        <a-progress
          :percent="getStoragePercent(record)"
          :stroke-color="getStorageColor(record)"
          :show-info="false"
          size="small"
        />
        <div class="storage-text">
          {{ formatSize(record.usedStorage) }} / {{ formatSize(record.maxStorage) }}
        </div>
      </div>
    </template>

    <!-- 成员数 -->
    <template #memberCount="{ record }">
      <a-badge :count="record.memberCount" :number-style="{ backgroundColor: '#52c41a' }" />
    </template>

    <!-- 文件数 -->
    <template #fileCount="{ record }">
      <a-badge :count="record.fileCount" :number-style="{ backgroundColor: '#1890ff' }" />
    </template>

    <!-- 所有者 -->
    <template #owner="{ record }">
      <div class="owner-cell">
        <a-avatar size="small" :style="{ backgroundColor: '#1890ff' }">
          {{ record.ownerNickName?.charAt(0) || 'U' }}
        </a-avatar>
        <span>{{ record.owownerNickName || record.ownerName }}</span>
      </div>
    </template>

    <!-- 创建时间 -->
    <template #createTime="{ record }">
      {{ formatDate(record.createTime) }}
    </template>

    <!-- 操作 -->
    <template #action="{ record }">
      <a-space>
        <a-button type="link" size="small" @click="handleView(record)">
          查看
        </a-button>
        <a-button type="link" size="small" @click="handleEdit(record)">
          编辑
        </a-button>
        <a-button
          v-if="!record.isDefault"
          type="link"
          size="small"
          @click="handleSetDefault(record)"
        >
          设为默认
        </a-button>
        <a-popconfirm
          title="确定要删除这个工作空间吗?"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleDelete(record)"
        >
          <a-button type="link" size="small" danger>
            删除
          </a-button>
        </a-popconfirm>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { FolderOpenOutlined } from '@ant-design/icons-vue'
import type { Workspace } from '@/types/workspace'
import dayjs from 'dayjs'

defineProps<{
  workspaces: Workspace[]
  loading?: boolean
}>()

const emit = defineEmits<{
  view: [workspace: Workspace]
  edit: [workspace: Workspace]
  delete: [workspace: Workspace]
  setDefault: [workspace: Workspace]
}>()

const columns = [
  {
    title: '工作空间名称',
    dataIndex: 'name',
    key: 'name',
    slots: { customRender: 'name' },
    width: 300,
  },
  {
    title: '存储空间',
    dataIndex: 'storage',
    key: 'storage',
    slots: { customRender: 'storage' },
    width: 200,
  },
  {
    title: '成员',
    dataIndex: 'memberCount',
    key: 'memberCount',
    slots: { customRender: 'memberCount' },
    width: 100,
    align: 'center',
  },
  {
    title: '文件',
    dataIndex: 'fileCount',
    key: 'fileCount',
    slots: { customRender: 'fileCount' },
    width: 100,
    align: 'center',
  },
  {
    title: '所有者',
    dataIndex: 'ownerNickName',
    key: 'ownerNickName',
    slots: { customRender: 'owner' },
    width: 150,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    slots: { customRender: 'createTime' },
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 280,
    fixed: 'right',
  },
]

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

function getStoragePercent(workspace: Workspace): number {
  if (workspace.maxStorage === 0) return 0
  return Math.round((workspace.usedStorage / workspace.maxStorage) * 100)
}

function getStorageColor(workspace: Workspace): string {
  const percent = getStoragePercent(workspace)
  if (percent >= 90) return '#ff4d4f'
  if (percent >= 70) return '#faad14'
  return '#52c41a'
}

function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function handleTableChange(pag: any) {
  pagination.value = pag
}

function handleView(workspace: Workspace) {
  emit('view', workspace)
}

function handleEdit(workspace: Workspace) {
  emit('edit', workspace)
}

function handleDelete(workspace: Workspace) {
  emit('delete', workspace)
}

function handleSetDefault(workspace: Workspace) {
  emit('setDefault', workspace)
}
</script>

<style scoped lang="scss">
.workspace-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .name-info {
    flex: 1;
    overflow: hidden;

    .name-text {
      font-weight: 500;
      color: #333;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .description-text {
      font-size: 12px;
      color: #999;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-top: 4px;
    }
  }
}

.storage-cell {
  .storage-text {
    margin-top: 4px;
    font-size: 12px;
    color: #666;
  }
}

.owner-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

