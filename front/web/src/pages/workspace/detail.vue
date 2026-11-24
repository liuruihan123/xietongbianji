<template>
  <div class="workspace-detail-page">
    <!-- 左右分栏布局 -->
    <div class="workspace-layout">
      <!-- 左侧面板 -->
      <div class="left-panel" :class="{ collapsed: leftPanelCollapsed }">
      
        <!-- 顶部操作栏 -->
        <div class="top-bar">
          <a-button type="text" @click="handleBack" class="back-btn">
            <template #icon><ArrowLeftOutlined /></template>
            返回
          </a-button>
          <div class="top-actions">
            <span class="member-count">
              <TeamOutlined /> {{ workspace?.memberCount || 0 }}人
            </span>
            <a-button v-if="workspace?.workspaceType === 3" type="text" @click="handleShare" class="share-btn">
              <ShareAltOutlined /> 邀请成员
            </a-button>
              <!-- 折叠按钮 -->
        <!-- <a-button
          class="collapse-btn"
          type="text"
          @click="toggleLeftPanel"
          :title="leftPanelCollapsed ? '展开侧边栏' : '折叠侧边栏'"
        >
          <MenuFoldOutlined v-if="!leftPanelCollapsed" />
          <MenuUnfoldOutlined v-else />
        </a-button> -->
          </div>
        </div>

        <!-- 工作空间信息区域 -->
        <div class="workspace-info-section">
          <!-- 封面图 -->
          <div class="workspace-cover">
            <img
              v-if="workspace?.workspaceIcon"
              :src="getWorkspaceIconUrl(workspace.workspaceIcon)"
              alt="工作空间封面"
              class="cover-image"
            />
            <div v-else class="cover-placeholder">
              <FolderOpenOutlined :style="{ fontSize: '48px', color: '#fff' }" />
            </div>
          </div>

          <!-- 工作空间名称 -->
          <h2 class="workspace-name">
            {{ workspace?.workspaceName || '加载中...' }}
          </h2>

          <!-- 元信息行 -->
          <div class="workspace-meta">
            <span class="meta-item">
              <UserOutlined />
              {{ workspace?.ownerNickName || workspace?.ownerName || '未知' }}
            </span>
            <span class="meta-item">
              <FileTextOutlined />
              {{ workspace?.contentCount || 0 }} 项
            </span>
          </div>

          <!-- 工作空间描述 -->
          <div class="workspace-description">
            <p v-if="workspace?.workspaceDesc" class="desc-text">
              {{ workspace.workspaceDesc }}
            </p>
            <p v-else class="desc-placeholder">还没有添加描述</p>
          </div>
        </div>

        <!-- 搜索框 -->
        <div class="search-section">
          <div class="search-bar-wrapper">
            <a-input-search
              v-model:value="searchKeyword"
              placeholder="搜索文档..."
              @search="handleSearch"
              allow-clear
            >
              <template #prefix>
                <SearchOutlined />
              </template>
            </a-input-search>
            <!-- 新增按钮下拉框 -->
            <a-dropdown :trigger="['click']" placement="bottomRight" @click.stop>
              <template #default>
                <a-button type="primary" class="create-btn">
                  <template #icon><PlusOutlined /></template>
                  新增
                  <DownOutlined />
                </a-button>
              </template>
              <template #overlay>
                <a-menu @click="handleCreateMenuClick">
                  <!-- <a-menu-item key="file">
                    <FileTextOutlined /> 新建文件
                  </a-menu-item> -->
                  <a-menu-item key="document">
                    <FileTextOutlined /> 创建文档
                  </a-menu-item>
                  <a-menu-item key="spreadsheet">
                    <TableOutlined /> 创建表格
                  </a-menu-item>
                  <a-menu-item key="presentation">
                    <FileOutlined /> 创建演示文稿
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="folder">
                    <FolderOutlined /> 新建文件夹
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>

        <!-- 面包屑导航 -->
        <div class="breadcrumb-section" v-if="currentPath.length > 0">
          <a-breadcrumb>
            <a-breadcrumb-item v-for="(item, index) in currentPath" :key="item.id ?? 'root'">
              <a
                v-if="index < currentPath.length - 1"
                @click="navigateToFolder(item.id, index)"
                class="breadcrumb-link"
              >
                <HomeOutlined v-if="index === 0" />
                <span>{{ item.name }}</span>
              </a>
              <span v-else class="breadcrumb-current">
                <HomeOutlined v-if="index === 0" />
                {{ item.name }}
              </span>
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <!-- 文件树 -->
        <div class="file-tree-section">
          <a-spin :spinning="treeLoading">
            <a-tree
              v-if="treeData.length > 0"
              :tree-data="treeData"
              :load-data="onLoadData"
              :selected-keys="selectedKeys"
              @select="onSelectNode"
              @dblclick="onDoubleClickNode"
              :show-icon="true"
            >
              <template #icon="{ nodeType }">
                <FolderOutlined v-if="nodeType === 'folder'" style="color: #faad14" />
                <FileTextOutlined v-else style="color: #1890ff" />
              </template>
              <template #title="{ title, nodeType, dataRef }">
                <span
                  class="tree-node-title"
                  @dblclick.stop="() => nodeType === 'folder' && enterFolder(dataRef)"
                >
                  {{ title }}
                </span>
              </template>
            </a-tree>
            <a-empty v-else description="暂无文件" :image="Empty.PRESENTED_IMAGE_SIMPLE" />
          </a-spin>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 编辑器视图 - 当选中文件时显示 -->
        <div v-if="selectedFile && selectedFile.nodeType === 'file'" class="editor-view">
          <div class="editor-header">
            <a-typography-title
              :level="4"
              :content="fileNameWithoutExtension"
              :editable="{
                tooltip: '点击编辑文件名',
                triggerType: ['icon', 'text'],
                onChange: handleFileNameChangeAndRename,
              }"
              class="editor-file-name"
            />
            <span v-if="fileExtension" class="file-extension">{{ fileExtension }}</span>
          </div>

          <div class="editor-container-wrapper">
            <EditorContent
              :loading="editorLoading"
              :editor-config="editorConfig"
            />
          </div>
        </div>

        <!-- 默认详情视图 - 未选中文件时显示 -->
        <div v-else class="workspace-overview">
          <!-- 标题区域 -->
          <div class="overview-header">
            <h1 class="overview-title">{{ workspace?.workspaceName }}</h1>
          </div>

          <!-- 元信息区域 -->
          <div class="overview-meta">
            <a-descriptions :column="2" size="small">
              <a-descriptions-item label="创建人">
                {{ workspace?.ownerNickName || workspace?.ownerName || '未知' }}
              </a-descriptions-item>
              <a-descriptions-item label="创建时间">
                {{ formatDate(workspace?.createTime) }}
              </a-descriptions-item>
              <a-descriptions-item label="内容数量" :span="2">
                {{ workspace?.contentCount || 0 }} 项
              </a-descriptions-item>
            </a-descriptions>
          </div>

          <!-- 描述区域 -->
          <div class="overview-description">
            <h3>描述</h3>
            <p v-if="workspace?.workspaceDesc" class="desc-content">
              {{ workspace.workspaceDesc }}
            </p>
            <p v-else class="desc-empty">还没有添加描述</p>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-cards">
            <a-row :gutter="16">
              <a-col :span="8">
                <a-card class="stat-card">
                  <a-statistic
                    title="文件数"
                    :value="stats?.fileCount || 0"
                    :prefix="h(FileTextOutlined)"
                  />
                </a-card>
              </a-col>
              <a-col :span="8">
                <a-card class="stat-card">
                  <a-statistic
                    title="文件夹数"
                    :value="stats?.folderCount || 0"
                    :prefix="h(FolderOutlined)"
                  />
                </a-card>
              </a-col>
              <a-col :span="8">
                <a-card class="stat-card">
                  <a-statistic
                    title="成员数"
                    :value="stats?.memberCount || 0"
                    :prefix="h(TeamOutlined)"
                  />
                </a-card>
              </a-col>
            </a-row>
          </div>

          <!-- 存储使用情况 -->
          <div class="storage-section">
            <h3>存储使用情况</h3>
            <a-progress
              :percent="storagePercent"
              :status="storagePercent > 90 ? 'exception' : 'normal'"
            />
            <p class="storage-text">
              已用 {{ formatStorage(workspace?.usedStorage || 0) }} /
              总共 {{ formatStorage(workspace?.maxStorage || 0) }}
            </p>
          </div>

          <!-- 成员管理 -->
          <div class="member-section">
            <MemberList
              :members="members"
              :loading="membersLoading"
              :can-manage="workspace?.ownerId === currentUserId"
              @invite="handleShare"
              @change-role="handleChangeRole"
              @remove="handleRemoveMember"
            />
          </div>

          <!-- 最近活动 -->
          <div class="recent-activity">
            <h3>最近活动</h3>
            <a-list
              v-if="stats?.recentFiles && stats.recentFiles.length > 0"
              :data-source="stats.recentFiles"
              size="small"
            >
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #avatar>
                      <FileTextOutlined style="font-size: 20px; color: #1890ff" />
                    </template>
                    <template #title>
                      <span>{{ item.fileName || item.name }}</span>
                    </template>
                    <template #description>
                      {{ formatDate(item.updateTime) }}
                    </template>
                  </a-list-item-meta>
                  <template #actions>
                    <FileActionMenu
                      :file="convertRecentFileToFileItem(item)"
                      @download="handleFileDownloadWithConfirm"
                      @delete="handleFileDeleteWithConfirm"
                    />
                  </template>
                </a-list-item>
              </template>
            </a-list>
            <a-empty v-else description="暂无最近活动" :image="Empty.PRESENTED_IMAGE_SIMPLE" />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 确认对话框 -->
  <ConfirmDialog
    :visible="confirmDialogVisible"
    :title="confirmDialogConfig.title"
    :message="confirmDialogConfig.message"
    :confirm-text="confirmDialogConfig.confirmText"
    :type="confirmDialogConfig.type"
    :loading="isDownloading || isDeleting"
    :file-info="confirmDialogConfig.fileInfo"
    @confirm="confirmDialogConfig.onConfirm"
    @cancel="confirmDialogVisible = false"
  />

  <!-- 创建文件夹对话框 -->
  <a-modal
    :visible="createFolderModalVisible"
    title="新建文件夹"
    :confirm-loading="createFolderLoading"
    @ok="handleCreateFolderOk"
    @cancel="handleCreateFolderCancel"
    okText="确定"
    cancelText="取消"
  >
    <a-form
      ref="createFolderFormRef"
      :model="createFolderForm"
      :rules="createFolderRules"
      layout="vertical"
    >
      <a-form-item label="文件夹名称" name="folderName">
        <a-input
          v-model:value="createFolderForm.folderName"
          placeholder="请输入文件夹名称"
          :maxlength="50"
          show-count
          @pressEnter="handleCreateFolderOk"
        />
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- 邀请成员弹窗 -->
  <InviteMemberModal
    v-model:visible="inviteModalVisible"
    :workspace-id="Number(route.params.id)"
    @success="handleInviteSuccess"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Empty } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  FolderOpenOutlined,
  FolderOutlined,
  FileTextOutlined,
  TeamOutlined,
  ShareAltOutlined,
  UserOutlined,
  SearchOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  PlusOutlined,
  DownOutlined,
  TableOutlined,
  FileOutlined,
  HomeOutlined,
} from '@ant-design/icons-vue'
import { getFileTree } from '@/api/fileTree'
import { getWorkspaceDetail, getWorkspaceStats, getWorkspaceMembers, inviteMember, removeMember, updateMemberRole } from '@/api/workspace'
import { renameFile, createFile } from '@/api/file'
import { createFolder as apiCreateFolder } from '@/api/folder'
import FileActionMenu from '@/components/FileActionMenu.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import InviteMemberModal from './components/InviteMemberModal.vue'
import MemberList from './components/MemberList.vue'
import { useFileActions } from '@/composables/useFileActions'
import { useWorkspaceStore } from '@/stores/workspace'
import { useUserStore } from '@/stores/useUserStore'
import type { Workspace, WorkspaceMember } from '@/types/workspace'
import type { FileTreeNode } from '@/types/fileTree'
import type { OnlyOfficeConfig } from '@/types/editor'
import dayjs from 'dayjs'
// 导入编辑器组件
import EditorContent from '@/pages/editor/components/EditorContent.vue'
import { getEditorConfig } from '@/api/editor'

const route = useRoute()
const router = useRouter()
const workspaceStore = useWorkspaceStore()
const userStore = useUserStore()

// 获取当前用户ID
const currentUserId = computed(() => userStore.userInfo?.userId)

// 状态
const workspace = ref<Workspace | null>(null)
const stats = ref<any>(null)
const treeData = ref<any[]>([])
const selectedKeys = ref<string[]>([])
const selectedFile = ref<FileTreeNode | null>(null)
const searchKeyword = ref('')
const treeLoading = ref(false)
const loading = ref(false)

// 成员相关状态
const members = ref<WorkspaceMember[]>([])
const membersLoading = ref(false)
const inviteModalVisible = ref(false)

// 编辑器相关状态
const editorLoading = ref(false)
const editorConfig = ref<OnlyOfficeConfig | null>(null)
const leftPanelCollapsed = ref(false) // 左侧面板折叠状态
const isRenaming = ref(false) // 防止重复提交

// 文件操作相关
const { handleFileDownload, handleFileDelete, isDownloading, isDeleting } = useFileActions()

// 确认对话框状态
const confirmDialogVisible = ref(false)
const confirmDialogConfig = ref({
  title: '',
  message: '',
  confirmText: '确认',
  type: 'default' as 'default' | 'danger',
  fileInfo: null as any,
  onConfirm: () => {}
})

// 创建文件夹 Modal 状态
const createFolderModalVisible = ref(false)
const createFolderLoading = ref(false)
const createFolderFormRef = ref<any>(null)
const createFolderForm = ref({
  folderName: ''
})
const createFolderRules = {
  folderName: [
    { required: true, message: '请输入文件夹名称', trigger: 'blur' },
    { min: 1, max: 50, message: '文件夹名称长度为 1-50 个字符', trigger: 'blur' },
    {
      pattern: /^[^\\/:*?"<>|]+$/,
      message: '文件夹名称不能包含特殊字符: \\ / : * ? " < > |',
      trigger: 'blur'
    }
  ]
}

// 文件夹导航状态
interface PathItem {
  id: number | null
  name: string
}
const currentFolderId = ref<number | null>(null)
const currentPath = ref<PathItem[]>([])

/**
 * 计算属性: 获取文件名(不包括扩展名)
 */
const fileNameWithoutExtension = computed(() => {
  if (!selectedFile.value || !selectedFile.value.name) {
    return ''
  }
  const fileName = selectedFile.value.name
  const lastDotIndex = fileName.lastIndexOf('.')
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    // 没有扩展名或文件名以点开头(隐藏文件)
    return fileName
  }
  return fileName.substring(0, lastDotIndex)
})

/**
 * 计算属性: 获取文件扩展名(包括点号)
 */
const fileExtension = computed(() => {
  if (!selectedFile.value || !selectedFile.value.name) {
    return ''
  }
  const fileName = selectedFile.value.name
  const lastDotIndex = fileName.lastIndexOf('.')
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    // 没有扩展名或文件名以点开头(隐藏文件)
    return ''
  }
  return fileName.substring(lastDotIndex)
})

/**
 * 计算存储使用百分比
 */
const storagePercent = computed(() => {
  if (!workspace.value) return 0
  const { usedStorage = 0, maxStorage = 1 } = workspace.value
  return Math.round((usedStorage / maxStorage) * 100)
})

/**
 * 格式化日期
 */
function formatDate(date?: string): string {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

/**
 * 格式化存储大小
 */
function formatStorage(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

/**
 * 获取工作空间图标URL
 */
function getWorkspaceIconUrl(icon: string): string {
  // 如果是完整URL,直接返回
  if (icon.startsWith('http://') || icon.startsWith('https://')) {
    return icon
  }
  // 如果是相对路径,拼接基础URL
  if (icon.startsWith('/')) {
    return new URL(icon, import.meta.url).href
  }
  // 如果是资源路径,使用 import.meta.url
  return new URL(`../../assets/work_icon/${icon}`, import.meta.url).href
}

/**
 * 返回上一页
 */
function handleBack() {
  router.back()
}

/**
 * 邀请成员
 */
function handleShare() {
  console.log('点击邀请成员按钮, 当前值:', inviteModalVisible.value)
  inviteModalVisible.value = true
  console.log('设置后的值:', inviteModalVisible.value)
}

/**
 * 加载成员列表
 */
async function loadMembers() {
  if (!workspace.value?.id) return
  try {
    membersLoading.value = true
    const response = await getWorkspaceMembers(workspace.value.id)
    members.value = response.data?.rows || response.data || []
  } catch (error) {
    console.error('加载成员列表失败:', error)
  } finally {
    membersLoading.value = false
  }
}

/**
 * 邀请成功回调
 */
async function handleInviteSuccess() {
  // 重新加载成员列表
  await loadMembers()
  // 更新工作空间成员数量
  if (workspace.value) {
    workspace.value.memberCount++
  }
}

/**
 * 更改成员角色
 */
async function handleChangeRole(member: any, role: string) {
  try {
    await updateMemberRole(member.id, role)
    message.success('角色更新成功')
    // 重新加载成员列表
    await loadMembers()
  } catch (error: any) {
    message.error(error.message || '角色更新失败')
  }
}

/**
 * 移除成员
 */
async function handleRemoveMember(member: any) {
  if (!workspace.value?.id) return
  try {
    await removeMember(workspace.value.id, member.userId)
    message.success('移除成员成功')
    // 重新加载成员列表
    await loadMembers()
    // 更新工作空间成员数量
    if (workspace.value) {
      workspace.value.memberCount--
    }
  } catch (error: any) {
    message.error(error.message || '移除成员失败')
  }
}

/**
 * 搜索文档
 */
function handleSearch() {
  if (!searchKeyword.value.trim()) {
    message.warning('请输入搜索关键词')
    return
  }
  message.info(`搜索功能开发中: ${searchKeyword.value}`)
}

/**
 * 处理新增菜单点击
 */
async function handleCreateMenuClick(e: any) {
  const workspaceId = Number(route.params.id)
  if (!workspaceId) {
    message.error('工作空间ID无效')
    return
  }

  const menuKey = e.key

  switch (menuKey) {
    case 'document':
      await createNewFile('文档', 'docx', workspaceId)
      break
    case 'spreadsheet':
      await createNewFile('表格', 'xlsx', workspaceId)
      break
    case 'presentation':
      await createNewFile('演示文稿', 'pptx', workspaceId)
      break
    case 'folder':
      await createNewFolder(workspaceId)
      break
    default:
      message.warning('未知的操作')
  }
}

/**
 * 创建新文件
 */
async function createNewFile(fileTypeName: string, extension: string, workspaceId: number) {
  try {
    const fileName = `新建${fileTypeName}.${extension}`
    const loadingMsg = message.loading(`正在创建${fileTypeName}...`, 0)

    const response = await createFile({
      fileName,
      fileType: extension,
      workspaceId,
    })

    loadingMsg()

    if (response?.data?.id) {
      message.success(`${fileTypeName}创建成功`)
      // 重新加载文件树
      await loadFileTree()
      // 可选：自动打开新创建的文件
      // await router.push(routeUtils.getLocalizedPath(`/editor/${response.data.id}`))
    }
  } catch (error: any) {
    message.error(error.message || '创建文件失败')
  }
}

/**
 * 打开创建文件夹对话框
 */
function createNewFolder(_workspaceId: number) {
  console.log('打开创建文件夹对话框, workspaceId:', _workspaceId)
  // 重置表单
  createFolderForm.value.folderName = ''
  createFolderModalVisible.value = true
}

/**
 * 处理创建文件夹确认
 */
async function handleCreateFolderOk() {
  try {
    // 表单验证
    if (createFolderFormRef.value) {
      await createFolderFormRef.value.validate()
    }

    const folderName = createFolderForm.value.folderName?.trim()
    if (!folderName) {
      message.error('请输入文件夹名称')
      return
    }

    const workspaceId = Number(route.params.id)
    if (!workspaceId) {
      message.error('工作空间ID无效')
      return
    }

    createFolderLoading.value = true

    console.log('创建文件夹参数:', {
      workspaceId,
      parentId: currentFolderId.value,
      folderName,
    })

    const response = await apiCreateFolder({
      workspaceId,
      parentId: currentFolderId.value,
      folderName,
    })

    console.log('创建文件夹响应:', response)

    // 检查响应是否成功（兼容不同的响应格式）
    if (response?.code === 200 || response?.data?.id || response?.data) {
      message.success('文件夹创建成功')
      createFolderModalVisible.value = false
      createFolderForm.value.folderName = ''
      // 重新加载当前目录的文件树
      await loadFileTree()
    } else {
      message.error(response?.msg || '创建文件夹失败')
    }
  } catch (error: any) {
    console.error('创建文件夹错误:', error)
    if (error?.errorFields) {
      // 表单验证错误，不处理（Ant Design Vue 会自动显示）
      return
    }
    message.error(error?.response?.data?.msg || error?.message || '创建文件夹失败')
  } finally {
    createFolderLoading.value = false
  }
}

/**
 * 处理创建文件夹取消
 */
function handleCreateFolderCancel() {
  createFolderModalVisible.value = false
  createFolderForm.value.folderName = ''
}

/**
 * 转换文件树节点为 Tree 组件数据
 */
function convertToTreeData(nodes: FileTreeNode[]): any[] {
  return nodes.map(node => ({
    key: `${node.nodeType}-${node.id}`,
    title: node.name,
    name: node.name,
    nodeType: node.nodeType,
    isLeaf: node.nodeType === 'file',
    children: node.nodeType === 'folder' && node.hasChildren ? [] : undefined,
    // 保存原始节点数据
    ...node
  }))
}

/**
 * 加载文件树（支持指定文件夹）
 */
async function loadFileTree() {
  try {
    treeLoading.value = true
    const workspaceId = Number(route.params.id)

    const response = await getFileTree({
      workspaceId,
      parentId: currentFolderId.value
    })

    treeData.value = convertToTreeData(response.data.fileTree || [])
  } catch (error) {
    console.error('加载文件树失败:', error)
    message.error('加载文件树失败')
  } finally {
    treeLoading.value = false
  }
}

/**
 * 懒加载子节点
 */
async function onLoadData(treeNode: any): Promise<void> {
  // 只有文件夹才需要加载子节点
  if (treeNode.dataRef.nodeType !== 'folder') {
    return Promise.resolve()
  }

  try {
    const workspaceId = Number(route.params.id)
    const response = await getFileTree({
      workspaceId,
      parentId: treeNode.dataRef.id
    })

    const children = convertToTreeData(response.data.fileTree || [])
    treeNode.dataRef.children = children
    treeData.value = [...treeData.value]
  } catch (error) {
    console.error('加载子节点失败:', error)
    message.error('加载子节点失败')
  }
}

/**
 * 选择节点
 */
function onSelectNode(_selectedKeysParam: string[], info: any) {
  const node = info.node.dataRef

  // 如果是文件,加载编辑器配置并在右侧显示
  if (node.nodeType === 'file') {
    selectedFile.value = node
    loadEditorForFile(node)
  } else {
    // 如果是文件夹,清空选中的文件
    selectedFile.value = null
    editorConfig.value = null
  }
}

/**
 * 双击节点处理
 */
function onDoubleClickNode(_e: MouseEvent) {
  // 双击事件由 tree-node-title 的 @dblclick 处理
}

/**
 * 进入文件夹
 */
async function enterFolder(folderNode: any) {
  if (!folderNode || folderNode.nodeType !== 'folder') return

  // 更新当前文件夹ID
  currentFolderId.value = folderNode.id

  // 更新路径面包屑
  currentPath.value.push({
    id: folderNode.id,
    name: folderNode.name
  })

  // 清空选中状态
  selectedKeys.value = []
  selectedFile.value = null
  editorConfig.value = null

  // 重新加载文件树
  await loadFileTree()
}

/**
 * 导航到指定文件夹（通过面包屑点击）
 */
async function navigateToFolder(folderId: number | null, pathIndex: number) {
  // 更新当前文件夹ID
  currentFolderId.value = folderId

  // 截断路径到点击位置
  currentPath.value = currentPath.value.slice(0, pathIndex + 1)

  // 清空选中状态
  selectedKeys.value = []
  selectedFile.value = null
  editorConfig.value = null

  // 重新加载文件树
  await loadFileTree()
}

/**
 * 为选中的文件加载编辑器
 */
async function loadEditorForFile(node: FileTreeNode) {
  const fileId = node.fileInfo?.id
  if (!fileId) {
    message.error('无法打开文件:文件ID不存在')
    return
  }

  editorLoading.value = true
  try {
    console.log('加载文件编辑器配置:', fileId)
    const response = await getEditorConfig(fileId, 'edit')
    editorConfig.value = response.data
    console.log('编辑器配置加载成功:', editorConfig.value)
  } catch (error: any) {
    console.error('加载编辑器配置失败:', error)
    message.error(error.message || '加载编辑器失败')
    editorConfig.value = null
  } finally {
    editorLoading.value = false
  }
}

/**
 * 切换左侧面板折叠状态
 */
function toggleLeftPanel() {
  leftPanelCollapsed.value = !leftPanelCollapsed.value
}

/**
 * 转换最近文件为文件项
 */
function convertRecentFileToFileItem(item: any): any {
  return {
    id: item.id,
    name: item.fileName || item.name,
    type: item.fileType || item.extension || '',
    extension: item.extension || '',
    size: item.fileSize || item.size || 0,
    path: item.filePath || '',
    workspaceId: item.workspaceId,
    creatorName: item.ownerNickName || '未知',
    updateTime: item.updateTime
  }
}

/**
 * 文件下载确认处理
 */
async function handleFileDownloadWithConfirm(file: any) {
  confirmDialogConfig.value = {
    title: '确认下载',
    message: '确定要下载这个文件吗？',
    confirmText: '下载',
    type: 'default',
    fileInfo: file,
    onConfirm: async () => {
      const success = await handleFileDownload(file, false)
      confirmDialogVisible.value = false
      if (success) {
        // 下载成功，不需要更新UI
      }
    }
  }
  confirmDialogVisible.value = true
}

/**
 * 文件删除确认处理
 */
async function handleFileDeleteWithConfirm(file: any) {
  confirmDialogConfig.value = {
    title: '确认删除',
    message: '确定要删除这个文件吗？此操作会将文件移至回收站，可以在回收站中恢复。',
    confirmText: '删除',
    type: 'danger',
    fileInfo: file,
    onConfirm: async () => {
      const success = await handleFileDelete(file)
      confirmDialogVisible.value = false
      if (success) {
        // 删除成功，重新加载文件树和统计信息
        await loadFileTree()
        await loadWorkspaceStats()
      }
    }
  }
  confirmDialogVisible.value = true
}

/**
 * 文件名改变并重命名（onChange 在确认时触发：回车或失焦）
 */
async function handleFileNameChangeAndRename(newNameWithoutExt: string) {
  console.log('handleFileNameChangeAndRename 被调用:', newNameWithoutExt)

  // 防止重复提交
  if (isRenaming.value) {
    console.log('正在重命名中，跳过本次请求')
    return
  }

  if (!selectedFile.value || !selectedFile.value.fileInfo?.id) {
    message.error('无法重命名:文件信息不存在')
    return
  }

  // 验证文件名
  if (!newNameWithoutExt || !newNameWithoutExt.trim()) {
    message.error('文件名不能为空')
    return
  }

  const trimmedName = newNameWithoutExt.trim()

  // 检查文件名是否包含非法字符
  const invalidChars = /[\\/:*?"<>|]/
  if (invalidChars.test(trimmedName)) {
    message.error('文件名不能包含以下字符: \\ / : * ? " < > |')
    return
  }

  // 拼接扩展名,得到完整的新文件名
  const fullNewName = trimmedName + fileExtension.value

  // 如果文件名没有改变,不需要调用API
  if (fullNewName === selectedFile.value.name) {
    return
  }

  try {
    isRenaming.value = true
    const fileId = selectedFile.value.fileInfo.id

    console.log('重命名文件:', {
      fileId,
      oldName: selectedFile.value.name,
      newName: fullNewName,
    })

    // 调用重命名API
    await renameFile({
      id: fileId,
      name: fullNewName,
    })

    // 更新当前选中文件的名称
    selectedFile.value.name = fullNewName

    // 更新左侧文件树中对应节点的名称
    const updateTreeNodeName = (nodes: any[]): boolean => {
      for (const node of nodes) {
        if (node.nodeType === 'file' && node.fileInfo?.id === fileId) {
          node.name = fullNewName
          return true
        }
        if (node.children && updateTreeNodeName(node.children)) {
          return true
        }
      }
      return false
    }

    updateTreeNodeName(treeData.value)

    // message.success('文件重命名成功')
  } catch (error: any) {
    console.error('重命名文件失败:', error)
    message.error(error.message || '重命名文件失败')

    // 重新加载文件树以恢复原始状态
    await loadFileTree()
  } finally {
    isRenaming.value = false
  }
}

/**
 * 加载工作空间详情
 */
async function loadWorkspaceDetail() {
  try {
    loading.value = true
    const workspaceId = Number(route.params.id)

    const response = await getWorkspaceDetail(workspaceId)
    workspace.value = response.data
  } catch (error) {
    console.error('加载工作空间详情失败:', error)
    message.error('加载工作空间详情失败')
    throw error
  } finally {
    loading.value = false
  }
}

/**
 * 加载工作空间统计数据
 */
async function loadWorkspaceStats() {
  try {
    const workspaceId = Number(route.params.id)
    const response = await getWorkspaceStats(workspaceId)
    stats.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 统计数据加载失败不影响主要功能,只记录错误
  }
}

/**
 * 初始化面包屑路径
 */
function initBreadcrumb() {
  currentPath.value = [{
    id: null,
    name: workspace.value?.workspaceName || '工作空间'
  }]
}

/**
 * 初始化
 */
onMounted(async () => {
  const workspaceId = Number(route.params.id)

  if (!workspaceId) {
    message.error('工作空间ID无效')
    router.push('/')
    return
  }

  try {
    // 并行加载数据
    await Promise.all([
      loadWorkspaceDetail(),
      loadWorkspaceStats(),
      loadFileTree(),
      loadMembers()
    ])

    // 初始化面包屑
    initBreadcrumb()
  } catch (error) {
    message.error('加载工作空间详情失败')
    router.push('/')
  }
})
</script>

<style scoped lang="scss">
.workspace-detail-page {
  height: 100vh;
  overflow: hidden;
  background: #f5f5f5;

  .workspace-layout {
    display: flex;
    height: 100%;

    // 左侧面板
    .left-panel {
      width: 300px;
      background: white;
      border-right: 1px solid #e8e8e8;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      position: relative;
      transition: width 0.3s ease, margin-left 0.3s ease;

      // 折叠按钮
      .collapse-btn {
        position: absolute;
        top: 16px;
        right: 8px;
        z-index: 10;
        font-size: 16px;
        color: #666;
        transition: all 0.3s ease;

        &:hover {
          color: #1890ff;
        }
      }

      // 折叠状态
      &.collapsed {
        width: 48px;
        min-width: 48px;

        // 折叠状态下,按钮居中显示
        .collapse-btn {
          right: 50%;
          transform: translateX(50%);
        }

        // 隐藏所有内容区域
        .top-bar,
        .workspace-info-section,
        .search-section,
        .file-tree-section {
          opacity: 0;
          visibility: hidden;
          pointer-events: none;
        }
      }

      // 顶部操作栏
      .top-bar {
        padding: 16px;
        padding-right: 48px; // 为折叠按钮留出空间
        border-bottom: 1px solid #f0f0f0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: opacity 0.3s ease, visibility 0.3s ease;

        .back-btn {
          padding: 4px 8px;
          font-size: 14px;
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .top-actions {
          display: flex;
          align-items: center;
          gap: 12px;

          .member-count {
            font-size: 14px;
            color: #666;
            display: inline-flex;
            align-items: center;
            gap: 4px;
            line-height: 1;
          }

          .share-btn {
            padding: 4px 8px;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }

      // 工作空间信息区域
      .workspace-info-section {
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        transition: opacity 0.3s ease, visibility 0.3s ease;

        .workspace-cover {
          width: 100%;
          height: 140px;
          border-radius: 8px;
          overflow: hidden;
          margin-bottom: 12px;

          .cover-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .cover-placeholder {
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }

        .workspace-name {
          font-size: 18px;
          font-weight: 600;
          margin: 0 0 8px 0;
          color: #262626;
        }

        .workspace-meta {
          display: flex;
          gap: 16px;
          margin-bottom: 12px;

          .meta-item {
            font-size: 12px;
            color: #8c8c8c;
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }

        .workspace-description {
          .desc-text {
            font-size: 13px;
            color: #595959;
            line-height: 1.6;
            margin: 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .desc-placeholder {
            font-size: 13px;
            color: #bfbfbf;
            font-style: italic;
            margin: 0;
          }
        }
      }

      // 搜索框
      .search-section {
        padding: 12px 16px;
        border-bottom: 1px solid #f0f0f0;
        transition: opacity 0.3s ease, visibility 0.3s ease;

        .search-bar-wrapper {
          display: flex;
          gap: 8px;
          align-items: center;

          :deep(.ant-input-search) {
            flex: 1;
          }

          .create-btn {
            white-space: nowrap;
          }
        }
      }

      // 面包屑导航
      .breadcrumb-section {
        padding: 8px 16px;
        border-bottom: 1px solid #f0f0f0;
        background: #fafafa;
        transition: opacity 0.3s ease, visibility 0.3s ease;

        .breadcrumb-link {
          color: #1890ff;
          cursor: pointer;
          display: inline-flex;
          align-items: center;
          gap: 4px;

          &:hover {
            color: #40a9ff;
          }
        }

        .breadcrumb-current {
          color: #262626;
          display: inline-flex;
          align-items: center;
          gap: 4px;
        }
      }

      // 文件树
      .file-tree-section {
        flex: 1;
        overflow-y: auto;
        padding: 8px;
        transition: opacity 0.3s ease, visibility 0.3s ease;

        :deep(.ant-tree) {
          background: transparent;

          .ant-tree-node-content-wrapper {
            &:hover {
              background-color: #f5f5f5;
            }
          }

          .ant-tree-node-selected {
            .ant-tree-node-content-wrapper {
              background-color: #e6f7ff !important;
            }
          }
        }

        .tree-node-title {
          cursor: pointer;
          user-select: none;
        }
      }
    }

    // 右侧面板
    .right-panel {
      flex: 1;
      overflow-y: auto;
      background: #fafafa;
      display: flex;
      flex-direction: column;

      // 编辑器视图
      .editor-view {
        height: 100%;
        display: flex;
        flex-direction: column;
        background: white;

        .editor-header {
          padding: 16px 24px;
          border-bottom: 1px solid #f0f0f0;
          display: flex;
          align-items: center;
          gap: 4px;
          background: white;

          .editor-file-name {
            margin: 0;

            // 覆盖 Typography.Title 的默认样式
            :deep(.ant-typography) {
              margin-bottom: 0;
              font-size: 18px;
              font-weight: 600;
              color: #262626;
            }

            // 编辑图标样式
            :deep(.ant-typography-edit) {
              margin-left: 8px;
              color: #8c8c8c;

              &:hover {
                color: #1890ff;
              }
            }

            // 编辑状态下的输入框样式
            :deep(.ant-input) {
              font-size: 18px;
              font-weight: 600;
            }
          }

          .file-extension {
            font-size: 18px;
            font-weight: 600;
            color: #8c8c8c;
            user-select: none;
          }
        }

        .editor-container-wrapper {
          flex: 1;
          overflow: hidden;
          background: white;
        }
      }

      // 工作空间详情视图
      .workspace-overview {
        max-width: 1000px;
        margin: 0 auto;
        padding: 32px;

        .overview-header {
          margin-bottom: 24px;

          .overview-title {
            font-size: 28px;
            font-weight: 600;
            color: #262626;
            margin: 0;
          }
        }

        .overview-meta {
          margin-bottom: 32px;
          padding: 16px;
          background: white;
          border-radius: 8px;
        }

        .overview-description {
          margin-bottom: 32px;
          padding: 20px;
          background: white;
          border-radius: 8px;

          h3 {
            font-size: 16px;
            font-weight: 600;
            margin: 0 0 12px 0;
            color: #262626;
          }

          .desc-content {
            font-size: 14px;
            color: #595959;
            line-height: 1.8;
            margin: 0;
            white-space: pre-wrap;
          }

          .desc-empty {
            font-size: 14px;
            color: #bfbfbf;
            font-style: italic;
            margin: 0;
          }
        }

        .stats-cards {
          margin-bottom: 32px;

          .stat-card {
            border-radius: 8px;
            text-align: center;

            :deep(.ant-statistic-title) {
              font-size: 14px;
              color: #8c8c8c;
            }

            :deep(.ant-statistic-content) {
              font-size: 24px;
              font-weight: 600;
            }
          }
        }

        .storage-section {
          margin-bottom: 32px;
          padding: 20px;
          background: white;
          border-radius: 8px;

          h3 {
            font-size: 16px;
            font-weight: 600;
            margin: 0 0 16px 0;
            color: #262626;
          }

          .storage-text {
            margin: 12px 0 0 0;
            font-size: 14px;
            color: #8c8c8c;
          }
        }

        .recent-activity {
          padding: 20px;
          background: white;
          border-radius: 8px;

          h3 {
            font-size: 16px;
            font-weight: 600;
            margin: 0 0 16px 0;
            color: #262626;
          }
        }
      }
    }
  }
}
</style>

