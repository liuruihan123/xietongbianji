<template>
  <div class="file-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <HomeOutlined /> 工作空间
        </a-breadcrumb-item>
        <a-breadcrumb-item>文件管理</a-breadcrumb-item>
      </a-breadcrumb>

      <a-space>
        <a-button @click="handleRefresh">
          <ReloadOutlined /> 刷新
        </a-button>
        <a-button type="primary" @click="showUploadModal = true">
          <UploadOutlined /> 上传文件
        </a-button>
        <a-button @click="showCreateFolderModal = true">
          <FolderAddOutlined /> 新建文件夹
        </a-button>
      </a-space>
    </div>

    <!-- 主内容区 -->
    <div class="page-content">
      <a-row :gutter="16">
        <!-- 左侧文件夹树 -->
        <a-col :span="6">
          <FolderTree
            :folders="fileStore.folderTree"
            :selected-folder-id="fileStore.currentFolderId"
            @select="handleFolderSelect"
          />
        </a-col>

        <!-- 右侧文件列表 -->
        <a-col :span="18">
          <div class="file-list-container">
            <!-- 工具栏 -->
            <div class="toolbar">
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索文件..."
                style="width: 300px"
                @search="handleSearch"
              />

              <a-space>
                <a-button
                  v-if="fileStore.hasSelectedFiles"
                  @click="handleBatchDelete"
                  danger
                >
                  <DeleteOutlined /> 删除选中 ({{ fileStore.selectedFilesCount }})
                </a-button>

                <a-radio-group v-model:value="fileStore.viewMode" button-style="solid">
                  <a-radio-button value="list">
                    <UnorderedListOutlined /> 列表
                  </a-radio-button>
                  <a-radio-button value="grid">
                    <AppstoreOutlined /> 网格
                  </a-radio-button>
                </a-radio-group>
              </a-space>
            </div>

            <!-- 文件列表 -->
            <a-spin :spinning="fileStore.loading" tip="加载中...">
              <a-empty
                v-if="!fileStore.loading && fileStore.sortedFiles.length === 0"
                description="暂无文件"
              >
                <a-button type="primary" @click="showUploadModal = true">
                  上传第一个文件
                </a-button>
              </a-empty>

              <FileList
                v-else
                :files="fileStore.sortedFiles"
                :loading="fileStore.loading"
                v-model:selected-file-ids="fileStore.selectedFiles"
                @row-click="handleFileClick"
                @open="handleOpenFile"
                @download="handleDownloadFile"
                @rename="handleRenameFile"
                @move="handleMoveFile"
                @copy="handleCopyFile"
                @share="handleShareFile"
                @delete="handleDeleteFile"
              />
            </a-spin>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 上传文件对话框(预留) -->
    <a-modal
      v-model:visible="showUploadModal"
      title="上传文件"
      :footer="null"
    >
      <a-empty description="上传功能开发中..." />
    </a-modal>

    <!-- 创建文件夹对话框 -->
    <a-modal
      v-model:visible="showCreateFolderModal"
      title="新建文件夹"
      @ok="handleCreateFolder"
    >
      <a-input
        v-model:value="newFolderName"
        placeholder="请输入文件夹名称"
        @press-enter="handleCreateFolder"
      />
    </a-modal>

    <!-- 分享文件对话框 -->
    <CreateShareModal
      ref="createShareModalRef"
      :file-id="currentFile?.id || 0"
      :file-name="currentFile?.name || ''"
      @success="handleShareSuccess"
    />

    <!-- 分享链接对话框 -->
    <ShareLinkModal ref="shareLinkModalRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  HomeOutlined,
  ReloadOutlined,
  UploadOutlined,
  FolderAddOutlined,
  DeleteOutlined,
  UnorderedListOutlined,
  AppstoreOutlined,
} from '@ant-design/icons-vue'
import { useFileStore } from '@/stores/file'
import FolderTree from './components/FolderTree.vue'
import FileList from './components/FileList.vue'
import CreateShareModal from '@/components/share/CreateShareModal.vue'
import ShareLinkModal from '@/components/share/ShareLinkModal.vue'
import type { FileItem } from '@/types/file'
import type { ShareInfo } from '@/types/share'

const route = useRoute()
const fileStore = useFileStore()

const searchKeyword = ref('')
const showUploadModal = ref(false)
const showCreateFolderModal = ref(false)
const newFolderName = ref('')
const currentFile = ref<FileItem>()
const createShareModalRef = ref()
const shareLinkModalRef = ref()

const workspaceId = Number(route.params.workspaceId)

async function handleRefresh() {
  try {
    await Promise.all([
      fileStore.fetchFiles(workspaceId, fileStore.currentFolderId),
      fileStore.fetchFolderTree(workspaceId),
    ])
    message.success('刷新成功')
  } catch (error) {
    message.error('刷新失败')
  }
}

function handleFolderSelect(folderId: number | null) {
  fileStore.fetchFiles(workspaceId, folderId)
}

function handleFileClick(file: FileItem) {
  if (file.isFolder) {
    fileStore.setCurrentFolder(file)
    fileStore.fetchFiles(workspaceId, file.id)
  }
}

function handleOpenFile(file: FileItem) {
  message.info('打开文件功能开发中...')
}

function handleDownloadFile(file: FileItem) {
  message.info('下载文件功能开发中...')
}

function handleRenameFile(file: FileItem) {
  message.info('重命名功能开发中...')
}

function handleMoveFile(file: FileItem) {
  message.info('移动功能开发中...')
}

function handleCopyFile(file: FileItem) {
  message.info('复制功能开发中...')
}

function handleShareFile(file: FileItem) {
  currentFile.value = file
  createShareModalRef.value?.open()
}

function handleShareSuccess(shareInfo: ShareInfo) {
  shareLinkModalRef.value?.open(shareInfo)
}

async function handleDeleteFile(file: FileItem) {
  try {
    await fileStore.deleteFiles([file.id])
    message.success('删除成功')
    await handleRefresh()
  } catch (error) {
    message.error('删除失败')
  }
}

async function handleBatchDelete() {
  try {
    await fileStore.deleteFiles(fileStore.selectedFiles)
    message.success('删除成功')
    await handleRefresh()
  } catch (error) {
    message.error('删除失败')
  }
}

async function handleCreateFolder() {
  if (!newFolderName.value.trim()) {
    message.error('请输入文件夹名称')
    return
  }

  try {
    await fileStore.createFolder(newFolderName.value, workspaceId, fileStore.currentFolderId)
    message.success('创建成功')
    showCreateFolderModal.value = false
    newFolderName.value = ''
    await handleRefresh()
  } catch (error) {
    message.error('创建失败')
  }
}

function handleSearch() {
  message.info('搜索功能开发中...')
}

onMounted(async () => {
  await handleRefresh()
})
</script>

<style scoped lang="scss">
.file-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
    background: white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .page-content {
    padding: 24px;

    .file-list-container {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
      }
    }
  }
}
</style>

