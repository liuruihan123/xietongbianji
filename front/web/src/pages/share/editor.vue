<template>
  <div class="share-editor-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin size="large" tip="正在加载分享文档..." />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <a-result
        status="error"
        :title="error.title || '加载失败'"
        :sub-title="error.message || '无法加载分享文档'"
      >
        <template #extra>
          <a-button type="primary" @click="retry">重试</a-button>
          <a-button @click="goBack">返回</a-button>
        </template>
      </a-result>
    </div>

    <!-- 编辑器内容 -->
    <div v-else-if="editorConfig" class="editor-wrapper">
      <!-- 分享信息头部 -->
      <div class="share-header">
        <div class="share-info">
          <h3>{{ shareInfo?.fileName || '分享文档' }}</h3>
          <div class="share-meta">
            <span v-if="visitorName" class="visitor-info">
              <UserOutlined /> {{ visitorName }}（访客模式）
            </span>
            <span v-else-if="userInfo" class="user-info">
              <UserOutlined /> {{ userInfo.userName }}
            </span>
          </div>
        </div>
      </div>

      <!-- OnlyOffice编辑器 -->
      <div class="editor-container">
        <OnlyOfficeEditor
          :config="editorConfig"
          @app-ready="handleEditorReady"
          @ready="handleDocumentReady"
          @error="handleEditorError"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined } from '@ant-design/icons-vue'
import OnlyOfficeEditor from '@/components/editor/OnlyOfficeEditor.vue'
import { getShareByCode } from '@/api/share'
import { getEditorConfig } from '@/api/editor'
import { useAuthStore } from '@/stores/modules/auth'
import type { ShareInfo } from '@/types/share'
import type { OnlyOfficeConfig } from '@/types/editor'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 状态
const loading = ref(true)
const error = ref<{ title?: string; message?: string } | null>(null)
const shareInfo = ref<ShareInfo>()
const editorConfig = ref<OnlyOfficeConfig>()
const visitorName = ref<string>()

// 用户信息
const userInfo = authStore.userInfo

// 获取路由参数
const shareCode = route.params.code as string
const password = route.query.password as string
const authToken = route.query.authToken as string
const visitorNameParam = route.query.visitorName as string

if (visitorNameParam) {
  visitorName.value = visitorNameParam
}

// 如果有authToken，尝试恢复用户会话
if (authToken && !authStore.isAuthenticated) {
  // 这里可以添加恢复会话的逻辑
  console.log('检测到authToken，尝试恢复用户会话')
}

async function loadShareEditor() {
  loading.value = true
  error.value = null

  try {
    // 1. 获取分享信息
    console.log('获取分享信息:', { shareCode, password })
    const shareResponse = await getShareByCode(shareCode, password)
    shareInfo.value = shareResponse.data || shareResponse

    if (!shareInfo.value) {
      throw new Error('分享信息获取失败')
    }

    // 2. 获取编辑器配置
    console.log('获取编辑器配置:', shareInfo.value.fileId)
    const configResponse = await getEditorConfig(shareInfo.value.fileId, 'edit')
    editorConfig.value = configResponse.data || configResponse

    if (!editorConfig.value) {
      throw new Error('编辑器配置获取失败')
    }

    console.log('分享编辑器加载成功')
  } catch (err: any) {
    console.error('加载分享编辑器失败:', err)
    error.value = {
      title: '加载失败',
      message: err.message || '无法加载分享文档，请检查分享链接是否有效'
    }
  } finally {
    loading.value = false
  }
}

function handleEditorReady() {
  console.log('编辑器应用已加载')
}

function handleDocumentReady() {
  console.log('文档准备就绪')
  message.success('文档加载成功！')
}

function handleEditorError(error: any) {
  console.error('编辑器错误:', error)
  message.error('编辑器加载失败')
}

function retry() {
  loadShareEditor()
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadShareEditor()
})
</script>

<style scoped lang="scss">
.share-editor-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.loading-container,
.error-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.editor-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.share-header {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

  .share-info {
    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
    }

    .share-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 14px;
      color: #666;

      .visitor-info,
      .user-info {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

.editor-container {
  flex: 1;
  background: white;
}
</style>
