<template>
  <a-table
    :columns="columns"
    :data-source="files"
    :loading="loading"
    :row-selection="rowSelection"
    :pagination="false"
    row-key="id"
    @row-click="handleRowClick"
  >
    <!-- 文件名 -->
    <template #name="{ record }">
      <div class="file-name-cell">
        <component
          :is="getFileIcon(record)"
          :style="{ fontSize: '24px', color: getFileColor(record) }"
        />
        <span class="file-name">{{ record.name }}</span>
        <a-tag v-if="record.isLocked" color="orange" size="small">
          <LockOutlined /> 锁定
        </a-tag>
      </div>
    </template>

    <!-- 文件大小 -->
    <template #size="{ record }">
      {{ record.isFolder ? '-' : formatSize(record.size) }}
    </template>

    <!-- 创建者 -->
    <template #creator="{ record }">
      <div class="creator-cell">
        <a-avatar size="small" :style="{ backgroundColor: '#1890ff' }">
          {{ record.creatorName?.charAt(0) || 'U' }}
        </a-avatar>
        <span>{{ record.creatorName }}</span>
      </div>
    </template>

    <!-- 创建时间 -->
    <template #createTime="{ record }">
      {{ formatDate(record.createTime) }}
    </template>

    <!-- 操作 -->
    <template #action="{ record }">
      <a-space>
        <a-button
          v-if="!record.isFolder && isSupportedPreview(record.name)"
          type="link"
          size="small"
          @click.stop="handlePreview(record)"
        >
          预览
        </a-button>
        <a-button
          v-if="!record.isFolder"
          type="link"
          size="small"
          @click.stop="handleOpen(record)"
        >
          打开
        </a-button>
        <a-button type="link" size="small" @click.stop="handleDownload(record)">
          下载
        </a-button>
        <a-dropdown>
          <a-button type="link" size="small" @click.stop>
            更多 <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="({ key }) => handleMenuClick(key, record)">
              <a-menu-item key="rename">重命名</a-menu-item>
              <a-menu-item key="move">移动</a-menu-item>
              <a-menu-item key="copy">复制</a-menu-item>
              <a-menu-item v-if="!record.isFolder" key="share">分享</a-menu-item>
              <a-menu-item v-if="!record.isFolder" key="convert">转换</a-menu-item>
              <a-menu-divider />
              <a-menu-item key="delete" danger>删除</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </a-space>
    </template>
  </a-table>

  <!-- 文件预览模态框 -->
  <FilePreview
    v-if="previewFile"
    ref="filePreviewRef"
    :file-id="previewFile.id"
    :file-name="previewFile.name"
    @close="handlePreviewClose"
  />
</template>

<script setup lang="ts">
import { computed, ref, nextTick } from 'vue'
import {
  FolderOutlined,
  FileWordOutlined,
  FileExcelOutlined,
  FilePptOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  FileZipOutlined,
  FileOutlined,
  LockOutlined,
  DownOutlined,
} from '@ant-design/icons-vue'
import FilePreview from '@/components/preview/FilePreview.vue'
import { isSupportedPreview } from '@/types/preview'
import type { FileItem, FileType } from '@/types/file'
import { FILE_TYPE_COLORS } from '@/types/file'
import dayjs from 'dayjs'

const props = defineProps<{
  files: FileItem[]
  loading?: boolean
  selectedFileIds?: number[]
}>()

const emit = defineEmits<{
  'update:selectedFileIds': [ids: number[]]
  open: [file: FileItem]
  download: [file: FileItem]
  rename: [file: FileItem]
  move: [file: FileItem]
  copy: [file: FileItem]
  share: [file: FileItem]
  convert: [file: FileItem]
  delete: [file: FileItem]
  rowClick: [file: FileItem]
}>()

const columns = [
  {
    title: '文件名',
    dataIndex: 'name',
    key: 'name',
    slots: { customRender: 'name' },
  },
  {
    title: '大小',
    dataIndex: 'size',
    key: 'size',
    width: 120,
    slots: { customRender: 'size' },
  },
  {
    title: '创建者',
    dataIndex: 'creatorName',
    key: 'creatorName',
    width: 150,
    slots: { customRender: 'creator' },
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

const rowSelection = computed(() => ({
  selectedRowKeys: props.selectedFileIds || [],
  onChange: (selectedRowKeys: number[]) => {
    emit('update:selectedFileIds', selectedRowKeys)
  },
}))

function getFileIcon(file: FileItem) {
  if (file.isFolder) return FolderOutlined
  
  const iconMap: Record<string, any> = {
    DOCUMENT: FileWordOutlined,
    SPREADSHEET: FileExcelOutlined,
    PRESENTATION: FilePptOutlined,
    PDF: FilePdfOutlined,
    IMAGE: FileImageOutlined,
    ARCHIVE: FileZipOutlined,
  }
  
  return iconMap[file.type] || FileOutlined
}

function getFileColor(file: FileItem): string {
  return FILE_TYPE_COLORS[file.type as FileType] || '#8c8c8c'
}

function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function handleRowClick(record: FileItem) {
  emit('rowClick', record)
}

// 预览相关
const previewFile = ref<FileItem>()
const filePreviewRef = ref<InstanceType<typeof FilePreview>>()

/**
 * 预览文件
 */
async function handlePreview(file: FileItem) {
  previewFile.value = file
  await nextTick()
  filePreviewRef.value?.open()
}

/**
 * 关闭预览
 */
function handlePreviewClose() {
  previewFile.value = undefined
}

function handleOpen(file: FileItem) {
  emit('open', file)
}

function handleDownload(file: FileItem) {
  emit('download', file)
}

function handleMenuClick(key: string, file: FileItem) {
  switch (key) {
    case 'rename':
      emit('rename', file)
      break
    case 'move':
      emit('move', file)
      break
    case 'copy':
      emit('copy', file)
      break
    case 'share':
      emit('share', file)
      break
    case 'convert':
      emit('convert', file)
      break
    case 'delete':
      emit('delete', file)
      break
  }
}
</script>

<style scoped lang="scss">
.file-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .file-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.creator-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

