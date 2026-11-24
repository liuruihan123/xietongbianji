<template>
  <div class="share-list">
    <a-table
      :columns="columns"
      :data-source="shares"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      @change="handleTableChange"
    >
      <!-- 文件名 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'fileName'">
          <div class="file-name">
            <FileOutlined />
            <span>{{ record.fileName }}</span>
          </div>
        </template>

        <!-- 分享链接 -->
        <template v-else-if="column.key === 'shareUrl'">
          <a-typography-paragraph
            :copyable="{ text: record.shareUrl }"
            :ellipsis="{ rows: 1 }"
            style="margin: 0"
          >
            {{ record.shareUrl }}
          </a-typography-paragraph>
        </template>

        <!-- 访问密码 -->
        <template v-else-if="column.key === 'password'">
          <a-tag v-if="record.sharePassword" color="blue">
            {{ record.sharePassword }}
          </a-tag>
          <span v-else class="text-muted">无</span>
        </template>

        <!-- 权限 -->
        <template v-else-if="column.key === 'permission'">
          <a-tag :color="getPermissionColor(record)">
            {{ getPermissionIcon(record) }} {{ getPermissionText(record) }}
          </a-tag>
        </template>

        <!-- 状态 -->
        <template v-else-if="column.key === 'status'">
          <a-tag :color="getShareStatusColor(record.status)">
            {{ getShareStatusText(record.status) }}
          </a-tag>
        </template>

        <!-- 有效期 -->
        <template v-else-if="column.key === 'expireTime'">
          <div class="expire-text" :class="getExpireTextClass(record)">
            {{ getExpireText(record) }}
          </div>
        </template>

        <!-- 访问统计 -->
        <template v-else-if="column.key === 'statistics'">
          <div class="statistics">
            <span>查看: {{ record.viewCount }}</span>
            <a-divider type="vertical" />
            <span>下载: {{ record.downloadCount }}</span>
          </div>
        </template>

        <!-- 操作 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleViewLink(record)">
              查看链接
            </a-button>
            <a-button type="link" size="small" @click="handleViewStatistics(record)">
              统计
            </a-button>
            <a-dropdown>
              <a-button type="link" size="small">
                更多 <DownOutlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleEdit(record)">
                    <EditOutlined /> 编辑
                  </a-menu-item>
                  <a-menu-item
                    v-if="record.status === ShareStatus.ACTIVE || record.status === 0"
                    @click="handleDisable(record)"
                  >
                    <StopOutlined /> 禁用
                  </a-menu-item>
                  <a-menu-item
                    v-else-if="record.status === ShareStatus.DISABLED || record.status === 1"
                    @click="handleEnable(record)"
                  >
                    <CheckCircleOutlined /> 启用
                  </a-menu-item>
                  <a-menu-item danger @click="handleDelete(record)">
                    <DeleteOutlined /> 删除
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  FileOutlined,
  DownOutlined,
  EditOutlined,
  StopOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import type { ShareInfo } from '@/types/share'
import { ShareStatus, ShareExpireType, getShareStatusText, getShareStatusColor } from '@/types/share'
import { deleteShare, disableShare, enableShare } from '@/api/share'
import dayjs from 'dayjs'

const props = defineProps<{
  shares: ShareInfo[]
  loading?: boolean
  total?: number
}>()

const emit = defineEmits<{
  refresh: []
  viewLink: [share: ShareInfo]
  viewStatistics: [share: ShareInfo]
  edit: [share: ShareInfo]
}>()

const pagination = computed(() => ({
  total: props.total || 0,
  pageSize: 10,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
}))

const columns = [
  { title: '文件名', key: 'fileName', width: 200 },
  { title: '分享链接', key: 'shareUrl', width: 250, ellipsis: true },
  { title: '访问密码', key: 'password', width: 100 },
  { title: '状态', key: 'status', width: 80 },
  { title: '有效期', key: 'expireTime', width: 150 },
  { title: '访问统计', key: 'statistics', width: 150 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

function getExpireText(share: ShareInfo): string {
  // 优先显示 expire_time 的具体时间
  if (share.expireTime && share.expireType !== ShareExpireType.PERMANENT) {
    const expireDate = dayjs(share.expireTime)
    const now = dayjs()

    // 计算剩余天数
    const daysLeft = expireDate.diff(now, 'day')
    const formattedDate = expireDate.format('YYYY-MM-DD HH:mm')

    if (daysLeft < 0) {
      return `已过期 (${formattedDate})`
    } else if (daysLeft === 0) {
      return `今天过期 (${formattedDate})`
    } else if (daysLeft === 1) {
      return `明天过期 (${formattedDate})`
    } else {
      return `${daysLeft}天后过期 (${formattedDate})`
    }
  }

  // 根据 expireType 显示
  switch (share.expireType) {
    case ShareExpireType.PERMANENT:
      return '永久有效'
    case ShareExpireType.ONE_DAY:
      return '1天'
    case ShareExpireType.SEVEN_DAYS:
      return '7天'
    case ShareExpireType.THIRTY_DAYS:
      return '30天'
    case ShareExpireType.CUSTOM:
      return share.expireTime ? dayjs(share.expireTime).format('YYYY-MM-DD HH:mm') : '-'
    default:
      return '-'
  }
}

function getExpireTextClass(share: ShareInfo): string {
  if (share.expireTime) {
    const expireDate = dayjs(share.expireTime)
    const daysLeft = expireDate.diff(dayjs(), 'day')

    if (daysLeft < 0) {
      return 'expire-danger'
    } else if (daysLeft <= 3) {
      return 'expire-warning'
    } else {
      return 'expire-normal'
    }
  }
  return 'expire-normal'
}

function handleTableChange(pagination: any) {
  console.log('分页变化:', pagination)
}

function handleViewLink(share: ShareInfo) {
  emit('viewLink', share)
}

function handleViewStatistics(share: ShareInfo) {
  emit('viewStatistics', share)
}

function handleEdit(share: ShareInfo) {
  emit('edit', share)
}

async function handleDisable(share: ShareInfo) {
  try {
    await disableShare(share.id)
    message.success('分享已禁用')
    emit('refresh')
  } catch (error) {
    message.error('禁用失败')
  }
}

async function handleEnable(share: ShareInfo) {
  try {
    await enableShare(share.id)
    message.success('分享已启用')
    emit('refresh')
  } catch (error) {
    message.error('启用失败')
  }
}

function handleDelete(share: ShareInfo) {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个分享吗?删除后将无法恢复。',
    onOk: async () => {
      try {
        await deleteShare(share.id)
        message.success('分享已删除')
        emit('refresh')
      } catch (error) {
        message.error('删除失败')
      }
    },
  })
}
</script>

<style scoped lang="scss">
.share-list {
  .file-name {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .statistics {
    font-size: 12px;
    color: #666;
  }

  .text-muted {
    color: #999;
  }

  /* 有效期显示样式 */
  .expire-text {
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 4px;
    white-space: nowrap;

    &.expire-normal {
      color: #52c41a;
      background-color: #f6ffed;
    }

    &.expire-warning {
      color: #faad14;
      background-color: #fffbe6;
    }

    &.expire-danger {
      color: #ff4d4f;
      background-color: #fff1f0;
    }
  }
}
</style>

