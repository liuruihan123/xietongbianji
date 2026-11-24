<template>
  <div class="share-access-page">
    <div class="access-container">
      <!-- 密码验证 -->
      <a-card v-if="!verified" class="password-card">
        <div class="card-header">
          <FileOutlined class="file-icon" />
          <h2>访问分享文件</h2>
        </div>

        <a-form
          :model="formData"
          @finish="handleVerify"
        >
          <!-- 用户状态显示 -->
          <div class="user-status-section">
            <template v-if="isAuthenticated && userInfo">
              <!-- 已登录用户 -->
              <div class="user-logged-in">
                <UserOutlined class="user-icon" />
                <span class="user-name">当前用户: {{ userInfo.userName || userInfo.nickName || '用户' }}</span>
                <a-tag color="green">已登录</a-tag>
              </div>
            </template>
            <template v-else>
              <!-- 未登录用户 -->
              <a-form-item
                label="访客名称"
                name="visitorName"
              >
                <a-input
                  v-model:value="formData.visitorName"
                  placeholder="请输入您的名称（可选）"
                  size="large"
                >
                  <template #prefix>
                    <UserOutlined />
                  </template>
                </a-input>
              </a-form-item>
              <div class="login-hint">
                <span>已有账号？</span>
                <a-button type="link" @click="handleLogin">
                  <LoginOutlined /> 点击登录
                </a-button>
              </div>
            </template>
          </div>

          <!-- 密码输入 -->
          <a-form-item
            v-if="needPassword"
            name="password"
            :rules="[{ required: needPassword, message: '请输入访问密码' }]"
          >
            <a-input-password
              v-model:value="formData.password"
              placeholder="请输入访问密码"
              size="large"
              @press-enter="handleVerify"
            />
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
            >
              {{ isAuthenticated ? '以用户身份访问' : '以访客身份访问' }}
            </a-button>
          </a-form-item>
        </a-form>

        <div class="tips">
          <InfoCircleOutlined />
          <span v-if="needPassword">此分享受密码保护，请输入正确的访问密码</span>
          <span v-else-if="isAuthenticated">您已登录，将以您的账户身份访问分享文件</span>
          <span v-else>您可以输入名称以访客身份访问，或登录以使用您的账户</span>
        </div>
      </a-card>

      <!-- 文件信息 -->
      <a-card v-else class="file-card">
        <a-spin :spinning="loading">
          <div v-if="shareInfo" class="file-content">
            <div class="file-header">
              <FileOutlined class="file-icon" />
              <div class="file-info">
                <h2>{{ shareInfo.fileName }}</h2>
                <div class="file-meta">
                  <span>大小: {{ formatFileSize(shareInfo.fileSize) }}</span>
                  <a-divider type="vertical" />
                  <span>分享者: {{ shareInfo.creatorName || '未知' }}</span>
                  <a-divider type="vertical" />
                  <span>创建时间: {{ formatTime(shareInfo.createTime) }}</span>
                </div>
              </div>
            </div>

            <a-divider />

            <div class="file-actions">
              <a-space size="large">
                <a-button
                  type="primary"
                  size="large"
                  @click="handleOpenEditor"
                >
                  <EyeOutlined /> 打开文档
                </a-button>
                <a-button
                  v-if="canDownload"
                  size="large"
                  @click="handleDownload"
                >
                  <DownloadOutlined /> 下载文件
                </a-button>
              </a-space>
            </div>

            <div class="file-description">
              <a-alert
                v-if="isExpiringSoon"
                message="提示"
                :description="`此分享将在 ${getExpireText()} 后过期`"
                type="warning"
                show-icon
              />
            </div>
          </div>
        </a-spin>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  FileOutlined,
  InfoCircleOutlined,
  EyeOutlined,
  DownloadOutlined,
  UserOutlined,
  LoginOutlined,
} from '@ant-design/icons-vue'
import type { ShareInfo } from '@/types/share'
import type { ApiResponse } from '@/types/auth'
import { SharePermission } from '@/types/share'
import { getShareByCode } from '@/api/share'
import { useAuth } from '@/hooks/useAuth'
import { useUserStore } from '@/stores/useUserStore'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const { isAuthenticated, userInfo, token, initializeAuth } = useAuth()
const userStore = useUserStore()

const loading = ref(false)
const verified = ref(false)
const needPassword = ref(false)
const shareInfo = ref<ShareInfo>()
const authInitialized = ref(false)
const formData = ref({
  password: '',
  visitorName: '',
})

const shareCode = route.params.code as string

const canView = computed(() => {
  return shareInfo.value?.permissions?.includes(SharePermission.VIEW) ?? false
})

const canDownload = computed(() => {
  return shareInfo.value?.permissions?.includes(SharePermission.DOWNLOAD) ?? false
})

const canEdit = computed(() => {
  return shareInfo.value?.permissions?.includes(SharePermission.EDIT) ?? false
})

const isExpiringSoon = computed(() => {
  if (!shareInfo.value?.expireTime) return false
  const expireTime = dayjs(shareInfo.value.expireTime)
  const now = dayjs()
  const hoursLeft = expireTime.diff(now, 'hour')
  return hoursLeft > 0 && hoursLeft <= 24
})

async function handleVerify() {
  loading.value = true

  try {
    const response = await getShareByCode(shareCode, formData.value.password)
    // 正确提取 ShareInfo 数据
    const data = (response as any).data || response

    console.log(data)
    // 检查验证是否成功
    const responseObj = data as any
    if (responseObj.isValid === false || !responseObj.shareLink?.fileId) {
      // 验证失败（密码错误或链接无效）
      console.error('密码验证失败:', responseObj.message)
      message.error(responseObj.message || '密码错误或分享已失效')
      return
    }

    // 从嵌套的 shareLink 对象中提取分享信息
    shareInfo.value = responseObj.shareLink as ShareInfo
    console.log('密码验证成功,分享信息:', shareInfo.value)
    message.success('验证成功,正在打开文档...')

    // 验证成功后直接打开编辑器
    openEditor()
  } catch (error: any) {
    console.error('验证失败:', error)
    message.error(error.message || '密码错误或分享已失效')
  } finally {
    loading.value = false
  }
}

/**
 * 打开分享编辑器
 * 检查用户是否已认证，如果已认证则使用用户身份，否则使用访客模式
 */
function openEditor() {
  if (!shareInfo.value) return

  // 构造分享编辑器URL
  const params = new URLSearchParams()
  if (formData.value.password) {
    params.append('password', formData.value.password)
  }

  // 检查用户是否已认证
  if (isAuthenticated.value && userInfo.value && token.value) {
    // 用户已认证，使用用户真实身份
    console.log('用户已认证，使用用户身份访问分享文档')
    console.log('用户信息:', userInfo.value)

    // 将认证Token附加到URL参数中，以便在新窗口中恢复会话
    params.append('authToken', token.value)
    console.log('附加认证Token到URL')

  } else {
    // 用户未认证，使用访客模式
    const visitorName = formData.value.visitorName || '访客'
    params.append('visitorName', visitorName)
    console.log('用户未认证，使用访客模式:', visitorName)
  }

  // 注意：后端接口路径是 /open/ 而不是 /editor/
  const url = `/api/coedit/share/open/${shareCode}?${params.toString()}`
  console.log('========== 打开分享编辑器 ==========')
  console.log('分享码:', shareCode)
  console.log('用户认证状态:', isAuthenticated.value)
  console.log('完整URL:', url)


  // 直接在新窗口打开,避免 iframe 导致的路径解析问题
  window.open(url, '_blank')

  // 提示用户
  if (isAuthenticated.value) {
    message.success('正在以您的身份打开编辑器...')
  } else {
    message.success('正在以访客身份打开编辑器...')
  }
}

/**
 * 手动打开编辑器(从中转页面点击按钮)
 */
function handleOpenEditor() {
  openEditor()
}

async function handleDownload() {
  if (!shareInfo.value) return

  try {
    // 构造下载URL，包含必要的参数
    const params = new URLSearchParams()

    // 如果用户已认证，添加authToken
    if (isAuthenticated.value && token.value) {
      params.append('authToken', token.value)
      console.log('用户已认证，添加authToken到下载请求')
    }

    // 如果有分享密码，也添加到参数中（某些情况下可能需要）
    if (formData.value.password) {
      params.append('sharePassword', formData.value.password)
    }

    console.log(166666)
    const downloadUrl = `/api/coedit/file/download/${shareInfo.value.fileId}${params.toString() ? '?' + params.toString() : ''}`

    console.log('下载文件:', {
      fileId: shareInfo.value.fileId,
      fileName: shareInfo.value.fileName,
      isAuthenticated: isAuthenticated.value,
      downloadUrl
    })

    // 使用window.open下载文件
    window.open(downloadUrl, '_blank')

    message.success('正在下载文件...')
  } catch (error: any) {
    console.error('下载文件失败:', error)
    message.error(error.message || '下载失败')
  }
}

function formatFileSize(size?: number): string {
  if (!size || size === 0) return '未知'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(2)} KB`
  if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(2)} MB`
  return `${(size / 1024 / 1024 / 1024).toFixed(2)} GB`
}

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

function getExpireText(): string {
  if (!shareInfo.value?.expireTime) return ''
  const expireTime = dayjs(shareInfo.value.expireTime)
  const now = dayjs()
  const hoursLeft = expireTime.diff(now, 'hour')
  if (hoursLeft < 24) return `${hoursLeft} 小时`
  const daysLeft = expireTime.diff(now, 'day')
  return `${daysLeft} 天`
}

/**
 * 加载分享信息
 */
async function loadShareInfo() {
  loading.value = true
  try {
    const response = await getShareByCode(shareCode)
    // 正确提取 ShareInfo 数据
    const data = (response as any).data || response

    console.log('分享信息加载成功:', data)

    // 检查响应是否表示验证失败（需要密码）
    // 后端在需要密码但未提供时会返回 {isValid: false, message: "..."}
    const responseObj = data as any
    if (responseObj.isValid === false || !responseObj.shareLink?.fileId) {
      // 后端返回验证失败，需要密码验证
      console.log('分享访问需要密码验证:', responseObj.message || '需要输入密码')
      verified.value = false
      needPassword.value = true
      return
    }

    // 从嵌套的 shareLink 对象中提取分享信息
    shareInfo.value = responseObj.shareLink as ShareInfo
    console.log('是否需要密码:', shareInfo.value.hasPassword)

    // 检查是否需要密码（正常的分享信息检查，作为双重保障）
    const requiresPassword = shareInfo.value.hasPassword || !!shareInfo.value.sharePassword

    if (requiresPassword) {
      // 需要密码验证,显示密码输入页面
      console.log('分享需要密码,显示密码输入页面')
      verified.value = false
      needPassword.value = true
    } else {
      // 无需密码,直接打开编辑器
      console.log('分享无需密码,直接打开编辑器')
      verified.value = true
      needPassword.value = false

      // 延迟一下再打开,让用户看到加载过程
      setTimeout(() => {
        message.success('正在打开文档...')
        openEditor()
      }, 500)
    }
  } catch (error: any) {
    console.log('获取分享信息失败,可能需要密码验证:', error)
    // 获取失败,可能需要密码验证
    verified.value = false
    needPassword.value = true
  } finally {
    loading.value = false
  }
}

/**
 * 打开登录弹窗
 */
function handleLogin() {
  const currentPath = route.fullPath
  console.log('========== 打开登录弹窗 ==========')
  console.log('当前路径:', currentPath)
  userStore.openLoginModal(currentPath)
}

// 监听登录状态变化，登录成功后重新加载分享信息
watch(() => isAuthenticated.value, (newVal, oldVal) => {
  console.log('========== 登录状态变化 ==========')
  console.log('旧状态:', oldVal, '新状态:', newVal)
  console.log('认证初始化状态:', authInitialized.value)

  if (newVal && !oldVal && authInitialized.value) {
    // 用户刚刚登录成功，重新加载分享信息
    console.log('用户登录成功，重新加载分享信息')
    message.success('登录成功，正在刷新...')
    loadShareInfo()
  }
})

onMounted(async () => {
  console.log('========== 分享访问页面初始化 ==========')
  console.log('分享码:', shareCode)
  console.log('当前URL:', window.location.href)
  console.log('当前路径:', route.fullPath)

  // 调试 localStorage 状态
  console.log('========== localStorage 调试 ==========')
  console.log('ACCESS_TOKEN:', localStorage.getItem('ACCESS_TOKEN'))
  console.log('USER_INFO:', localStorage.getItem('USER_INFO'))
  console.log('所有 localStorage keys:', Object.keys(localStorage))

  // 首先初始化认证状态，确保从 localStorage 加载用户信息
  console.log('========== 初始化认证状态 ==========')
  console.log('初始化前 - token:', token.value)
  console.log('初始化前 - userInfo:', userInfo.value)
  console.log('初始化前 - isAuthenticated:', isAuthenticated.value)

  try {
    await initializeAuth()
    console.log('认证初始化完成')
  } catch (e) {
    console.log('认证初始化失败:', e)
  }
  authInitialized.value = true

  console.log('========== 认证初始化后状态 ==========')
  console.log('初始化后 - token:', token.value)
  console.log('初始化后 - userInfo:', userInfo.value)
  console.log('初始化后 - isAuthenticated:', isAuthenticated.value)

  if (isAuthenticated.value && userInfo.value) {
    console.log('========== 用户已登录，详细信息 ==========')
    console.log('用户ID:', userInfo.value.userId || userInfo.value.id)
    console.log('用户名:', userInfo.value.userName)
    console.log('邮箱:', userInfo.value.email)
    console.log('头像:', userInfo.value.avatar)
  } else {
    console.log('用户未登录，将显示访客模式')
  }

  // 加载分享信息
  await loadShareInfo()
})
</script>

<style scoped lang="scss">
.share-access-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;

  .access-container {
    width: 100%;
    max-width: 600px;

    .password-card,
    .file-card {
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

      .card-header {
        text-align: center;
        margin-bottom: 24px;

        .file-icon {
          font-size: 48px;
          color: #1890ff;
          margin-bottom: 16px;
        }

        h2 {
          margin: 0;
          font-size: 24px;
        }
      }

      .tips {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px;
        background: #f0f0f0;
        border-radius: 4px;
        font-size: 14px;
        color: #666;
      }

      .user-status-section {
        margin-bottom: 16px;

        .user-logged-in {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 16px;
          background: #f6ffed;
          border: 1px solid #b7eb8f;
          border-radius: 6px;
          margin-bottom: 16px;

          .user-icon {
            font-size: 20px;
            color: #52c41a;
          }

          .user-name {
            flex: 1;
            font-size: 15px;
            color: #333;
            font-weight: 500;
          }
        }

        .login-hint {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 4px;
          margin-top: 8px;
          font-size: 14px;
          color: #666;

          .ant-btn-link {
            padding: 0 4px;
            height: auto;
          }
        }
      }
    }

    .file-card {
      .file-content {
        .file-header {
          display: flex;
          gap: 16px;

          .file-icon {
            font-size: 48px;
            color: #1890ff;
          }

          .file-info {
            flex: 1;

            h2 {
              margin: 0 0 8px 0;
              font-size: 20px;
            }

            .file-meta {
              font-size: 14px;
              color: #666;
            }
          }
        }

        .file-actions {
          display: flex;
          justify-content: center;
          margin: 24px 0;
        }

        .file-description {
          margin-top: 24px;
        }
      }
    }
  }
}
</style>

