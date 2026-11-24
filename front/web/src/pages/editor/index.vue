<template>
  <div class="editor-page">
    <!-- 顶部工具栏 -->
    <EditorHeader
      v-if="showHeader"
      :document-title="documentTitle"
      :file-id="fileId"
      :workspace-id="workspaceId"
      :auto-save="autoSave"
      :collaboration="collaboration"
      :current-user-id="currentUserId"
      @back="handleBack"
      @share="handleShare"
      @invite-collaboration="handleInviteCollaboration"
      @title-changed="handleTitleChanged"
      @show-version-history="handleShowVersionHistory"
    />

    <!-- 编辑器容器1 -->
    <EditorContent
      :key="editorKey"
      ref="editorContentRef"
      :loading="loading"
      :editor-config="editorConfig"
      @app-ready="handleAppReady"
      @ready="handleEditorReady"
      @save="handleEditorSave"
      @state-change="handleStateChange"
      @error="handleEditorError"
    />

    <!-- 底部区域 -->
    <EditorFooter v-if="showFooter" />

    <!-- 版本历史抽屉 -->
    <a-drawer
      v-model:visible="showVersionDrawer"
      title="版本历史"
      width="500"
      placement="right"
    >
      <VersionHistory
        :file-id="fileId"
        :current-version="currentVersion"
        @restore="handleVersionRestore"
        @refresh="handleRefresh"
      />
    </a-drawer>



    <!-- 创建分享模态框 -->
    <CreateShareModal
      ref="createShareModalRef"
      :file-id="fileId"
      :file-name="documentTitle"
      @success="handleShareSuccess"
    />

    <!-- 分享链接模态框 -->
    <ShareLinkModal
      ref="shareLinkModalRef"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import EditorHeader from './components/EditorHeader.vue'
import EditorContent from './components/EditorContent.vue'
import EditorFooter from './components/EditorFooter.vue'
import VersionHistory from './components/VersionHistory.vue'
import CreateShareModal from '@/components/share/CreateShareModal.vue'
import ShareLinkModal from '@/components/share/ShareLinkModal.vue'
import { getEditorConfig } from '@/api/editor'
import { getFileDetail } from '@/api/file'
import { useAutoSave } from '@/hooks/useAutoSave'
import { useCollaboration } from '@/hooks/useCollaboration'
import { useAuthStore } from '@/stores/modules/auth'
import type { OnlyOfficeConfig } from '@/types/editor'
import type { ShareInfo } from '@/types/share'
import type { VersionInfo } from '@/api/version'

// Props 定义 - 控制组件显示
interface Props {
  showHeader?: boolean
  showFooter?: boolean
}

withDefaults(defineProps<Props>(), {
  showHeader: true,
  showFooter: true
})

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 获取文件ID,支持number和string类型
const fileIdParam = route.params.fileId
console.log('路由参数 fileId:', fileIdParam, '类型:', typeof fileIdParam)

// 如果是字符串类型的长整型ID,保持字符串格式
const fileId = typeof fileIdParam === 'string' && fileIdParam.length > 15
  ? fileIdParam
  : Number(fileIdParam)

console.log('处理后的 fileId:', fileId, '类型:', typeof fileId)

const mode = (route.query.mode as 'edit' | 'view') || 'edit'
const currentUserId = authStore.userInfo?.id || 0
const currentUserName = authStore.userInfo?.userName || '未知用户'

const loading = ref(true)
const editorConfig = ref<OnlyOfficeConfig | null>(null)
const editorContentRef = ref()
const editorKey = ref(0) // 用于强制刷新编辑器组件
const documentTitle = ref('文档加载中...')
const currentVersion = ref<number>()
const showVersionDrawer = ref(false)
const workspaceId = ref<number | string>()

// 分享相关
const createShareModalRef = ref()
const shareLinkModalRef = ref()

// 自动保存
const autoSave = useAutoSave({
  fileId,
  enabled: mode === 'edit',
  onSaveSuccess: () => {
    console.log('自动保存成功')
  },
  onSaveError: (error) => {
    console.error('自动保存失败:', error)
  },
})

// 协同编辑
const collaboration = useCollaboration({
  documentId: fileId,
  userId: currentUserId,
  userName: currentUserName,
  autoConnect: true,
  onUserJoin: (user) => {
    console.log('用户加入:', user)
  },
  onUserLeave: (user) => {
    console.log('用户离开:', user)
  },
})

async function loadEditorConfig() {
  loading.value = true
  try {
    console.log('========== 开始加载编辑器配置 ==========')
    console.log('fileId:', fileId, 'mode:', mode)

    // 并行获取编辑器配置和文件详情
    const [configResponse, fileDetailResponse] = await Promise.all([
      getEditorConfig(fileId, mode),
      getFileDetail(fileId)
    ])

    console.log('========== 后端返回数据 ==========')
    console.log('编辑器配置响应:', configResponse)
    console.log('编辑器配置数据:', configResponse.data)
    console.log('document.title:', configResponse.data?.document?.title)
    console.log('🔑 Token:', configResponse.data?.token)
    console.log('文件详情响应:', fileDetailResponse)
    console.log('文件详情数据:', fileDetailResponse.data)
    console.log('文件名 (name):', fileDetailResponse.data?.name)

    editorConfig.value = configResponse.data

    // 获取workspaceId
    if (fileDetailResponse.data?.workspaceId) {
      workspaceId.value = fileDetailResponse.data.workspaceId
      console.log('已获取 workspaceId:', workspaceId.value)
    }

    // 详细输出配置信息用于诊断
    console.log('========== 配置详情 ==========')
    console.log('文件类型:', editorConfig.value?.document.fileType)
    console.log('文档URL:', editorConfig.value?.document.url)
    console.log('回调URL:', editorConfig.value?.editorConfig?.callbackUrl)
    console.log('编辑模式:', editorConfig.value?.editorConfig?.mode)
    console.log('document.info:', editorConfig.value?.document.info)
    console.log('customization:', editorConfig.value?.editorConfig?.customization)

    // 为 TXT 文件添加默认配置，避免弹出选项对话框
    if (editorConfig.value && editorConfig.value.document.fileType === 'txt') {
      console.log('========== 检测到 TXT 文件，应用默认配置 ==========')

      // 确保 editorConfig 存在
      if (!editorConfig.value.editorConfig) {
        editorConfig.value.editorConfig = {} as any
      }

      // 确保 customization 存在
      if (!editorConfig.value.editorConfig.customization) {
        editorConfig.value.editorConfig.customization = {}
      }

      // 设置 TXT 文件的默认配置
      editorConfig.value.editorConfig.customization = {
        ...editorConfig.value.editorConfig.customization,
        // 禁用宏相关功能
        macros: false,
        macrosMode: 'disable',
        // 禁用插件
        plugins: false,
        // 禁用帮助和反馈，减少弹框和404错误
        help: false,
        feedback: false,
        // 禁用评论功能，减少弹框
        comments: false,
      }

      // 如果后端没有提供 document.info，添加默认的编码信息
      if (!editorConfig.value.document.info) {
        (editorConfig.value.document as any).info = {
          // 设置默认编码为 UTF-8 (codePage 65001)
          encoding: 'utf-8',
        }
        console.log('已添加默认编码配置: utf-8')
      } else {
        console.log('后端已提供 document.info:', editorConfig.value.document.info)
      }

      console.log('TXT 文件最终配置:', JSON.stringify(editorConfig.value, null, 2))
    }

    // 检查 callbackUrl 是否存在
    if (!editorConfig.value?.editorConfig?.callbackUrl) {
      console.warn('⚠️ 警告: callbackUrl 未配置，文件可能无法保存！')
      message.warning('编辑器配置缺少回调URL，文件可能无法保存')
    } else {
      console.log('✅ callbackUrl 已配置:', editorConfig.value.editorConfig.callbackUrl)
    }

    // 优先使用文件详情中的文件名，如果没有则使用编辑器配置中的标题
    // 注意：后端返回的字段名是 fileName，不是 name
    const fileDetailName = fileDetailResponse.data?.fileName || fileDetailResponse.data?.name
    const editorConfigTitle = editorConfig.value?.document.title

    console.log('========== 文件名信息 ==========')
    console.log('文件详情中的文件名 (fileName):', fileDetailResponse.data?.fileName)
    console.log('文件详情中的文件名 (name):', fileDetailResponse.data?.name)
    console.log('最终取到的文件名:', fileDetailName)
    console.log('编辑器配置中的标题:', editorConfigTitle)

    // 优先使用文件详情中的文件名
    documentTitle.value = fileDetailName || editorConfigTitle || '未命名文档'

    console.log('最终使用的文档标题:', documentTitle.value)

    // 获取workspaceId - 需要从文件详情中获取
    // 注意:这里需要后端在editorConfig中返回workspaceId,或者调用getFileDetail获取
    // 暂时设置为undefined,需要后端支持
    console.log('========== 配置加载完成 ==========')
  } catch (error: any) {
    console.error('========== 加载编辑器配置失败 ==========')
    console.error('错误信息:', error.message)
    console.error('错误响应:', error.response)
    console.error('错误堆栈:', error.stack)
    message.error(`加载编辑器配置失败: ${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

function handleAppReady() {
  console.log('编辑器应用已加载')
  // 编辑器应用加载完成，可以关闭 loading 了
  loading.value = false
}

function handleEditorReady() {
  console.log('文档准备就绪')
  message.success('文档加载成功')
}

function handleEditorSave() {
  console.log('编辑器请求保存')
  autoSave.markAsSaved()
}

function handleStateChange(hasChanges: boolean) {
  console.log('文档状态改变:', hasChanges)
  if (hasChanges) {
    autoSave.markAsChanged()
  }
}

function handleEditorError(error: any) {
  console.error('编辑器错误:', error)
  // 错误信息已在 OnlyOfficeEditor 组件中显示，这里只记录日志
}

function handleBack() {
  router.back()
}

function handleVersionRestore(version: VersionInfo) {
  message.success(`已恢复到版本 ${version.versionNumber}`)
  handleRefresh()
}

async function handleRefresh() {
  console.log('🔄 [版本恢复] 开始刷新编辑器...')
  await loadEditorConfig()
  // 通过更新 key 强制销毁并重建编辑器组件
  editorKey.value++
  console.log('✅ [版本恢复] 编辑器刷新完成, editorKey:', editorKey.value)
}

// 分享功能
function handleShare() {
  createShareModalRef.value?.open()
}

function handleShareSuccess(shareInfo: ShareInfo) {
  console.log('========== 分享创建成功 ==========')
  console.log('后端返回的完整 ShareInfo 对象:', JSON.stringify(shareInfo, null, 2))
  console.log('ShareInfo 对象的所有字段:')
  console.log('  - id:', shareInfo.id)
  console.log('  - fileId:', shareInfo.fileId)
  console.log('  - fileName:', shareInfo.fileName)
  console.log('  - shareUrl:', shareInfo.shareUrl)
  console.log('  - shareCode:', shareInfo.shareCode)
  console.log('  - password:', shareInfo.sharePassword)
  console.log('  - permissions:', shareInfo.permissions)
  console.log('  - expireType:', shareInfo.expireType)
  console.log('  - status:', shareInfo.status)

  // 检查 shareUrl 是否存在
  if (!shareInfo.shareUrl && shareInfo.shareCode) {
    console.warn('⚠️ 后端未返回 shareUrl，正在前端构造...')
    // 构造完整的分享链接
    const origin = window.location.origin
    shareInfo.shareUrl = `${origin}/s/${shareInfo.shareCode}`
    console.log('✅ 已构造 shareUrl:', shareInfo.shareUrl)
  }

  console.log('========== 准备打开分享链接模态框 ==========')
  // 显示分享链接模态框
  shareLinkModalRef.value?.open(shareInfo)
}

// 邀请协作功能
function handleInviteCollaboration() {
  // 邀请协作可以复用分享功能,创建可编辑权限的分享链接
  // 或者打开一个专门的邀请协作模态框
  message.info('邀请协作功能:建议创建可编辑权限的分享链接')
  handleShare()
}

// 标题更改处理
function handleTitleChanged(newTitle: string) {
  documentTitle.value = newTitle
  console.log('文档标题已更改:', newTitle)
}

// 显示版本历史
function handleShowVersionHistory() {
  showVersionDrawer.value = true
}


onMounted(() => {
  loadEditorConfig()
})
</script>

<style scoped lang="scss">
.editor-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}
</style>




