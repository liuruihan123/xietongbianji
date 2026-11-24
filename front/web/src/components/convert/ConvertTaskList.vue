<template>
  <a-table
    :columns="columns"
    :data-source="tasks"
    :loading="loading"
    :pagination="false"
    row-key="id"
  >
    <!-- 源文件名 -->
    <template #sourceFileName="{ record }">
      <div class="file-name-cell">
        <FileOutlined style="font-size: 20px; color: #1890ff" />
        <span class="file-name">{{ record.sourceFileName }}</span>
      </div>
    </template>

    <!-- 目标格式 -->
    <template #targetFormat="{ record }">
      <a-tag color="blue">{{ record.targetFormat.toUpperCase() }}</a-tag>
    </template>

    <!-- 状态 -->
    <template #status="{ record }">
      <a-tag :color="getConvertStatusColor(record.status)">
        {{ getConvertStatusText(record.status) }}
      </a-tag>
    </template>

    <!-- 进度 -->
    <template #progress="{ record }">
      <a-progress
        v-if="record.status === ConvertStatus.PROCESSING"
        :percent="record.progress"
        :status="record.progress === 100 ? 'success' : 'active'"
        size="small"
      />
      <span v-else>-</span>
    </template>

    <!-- 创建时间 -->
    <template #createTime="{ record }">
      {{ formatDate(record.createTime) }}
    </template>

    <!-- 操作 -->
    <template #action="{ record }">
      <a-space>
        <!-- 下载 -->
        <a-button
          v-if="record.status === ConvertStatus.SUCCESS"
          type="link"
          size="small"
          @click="handleDownload(record)"
        >
          下载
        </a-button>

        <!-- 重试 -->
        <a-button
          v-if="record.status === ConvertStatus.FAILED"
          type="link"
          size="small"
          @click="handleRetry(record)"
        >
          重试
        </a-button>

        <!-- 取消 -->
        <a-button
          v-if="record.status === ConvertStatus.PROCESSING"
          type="link"
          size="small"
          danger
          @click="handleCancel(record)"
        >
          取消
        </a-button>

        <!-- 删除 -->
        <a-popconfirm
          title="确定要删除这个任务吗?"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleDelete(record)"
        >
          <a-button type="link" size="small" danger>删除</a-button>
        </a-popconfirm>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'
import { FileOutlined } from '@ant-design/icons-vue'
import {
  cancelConvertTask,
  deleteConvertTask,
  retryConvertTask,
  downloadConvertResult,
} from '@/api/convert'
import {
  ConvertStatus,
  getConvertStatusText,
  getConvertStatusColor,
  type ConvertTask,
} from '@/types/convert'
import dayjs from 'dayjs'

/**
 * 组件属性
 */
interface Props {
  tasks: ConvertTask[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

/**
 * 组件事件
 */
const emit = defineEmits<{
  refresh: []
  download: [task: ConvertTask]
  retry: [task: ConvertTask]
  cancel: [task: ConvertTask]
  delete: [task: ConvertTask]
}>()

// 表格列配置
const columns = [
  {
    title: '源文件',
    dataIndex: 'sourceFileName',
    key: 'sourceFileName',
    slots: { customRender: 'sourceFileName' },
  },
  {
    title: '目标格式',
    dataIndex: 'targetFormat',
    key: 'targetFormat',
    width: 100,
    slots: { customRender: 'targetFormat' },
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    slots: { customRender: 'status' },
  },
  {
    title: '进度',
    dataIndex: 'progress',
    key: 'progress',
    width: 150,
    slots: { customRender: 'progress' },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    slots: { customRender: 'createTime' },
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    slots: { customRender: 'action' },
  },
]

/**
 * 格式化日期
 */
function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

/**
 * 下载转换结果
 */
async function handleDownload(task: ConvertTask) {
  try {
    if (!task.resultFileName) {
      message.error('结果文件不存在')
      return
    }
    await downloadConvertResult(task.id, task.resultFileName)
    message.success('下载成功')
    emit('download', task)
  } catch (error) {
    message.error('下载失败')
  }
}

/**
 * 重试任务
 */
async function handleRetry(task: ConvertTask) {
  try {
    await retryConvertTask(task.id)
    message.success('任务已重新提交')
    emit('retry', task)
    emit('refresh')
  } catch (error) {
    message.error('重试失败')
  }
}

/**
 * 取消任务
 */
async function handleCancel(task: ConvertTask) {
  try {
    await cancelConvertTask(task.id)
    message.success('任务已取消')
    emit('cancel', task)
    emit('refresh')
  } catch (error) {
    message.error('取消失败')
  }
}

/**
 * 删除任务
 */
async function handleDelete(task: ConvertTask) {
  try {
    await deleteConvertTask(task.id)
    message.success('删除成功')
    emit('delete', task)
    emit('refresh')
  } catch (error) {
    message.error('删除失败')
  }
}
</script>

<style scoped lang="scss">
.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .file-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>

