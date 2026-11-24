<template>
  <div class="test-page">
    <div class="test-container">
      <h1>登录弹窗测试页面</h1>
      
      <div class="info-card">
        <h2>当前登录状态</h2>
        <div class="status-item">
          <span class="label">是否已登录:</span>
          <span :class="['value', authStore.isAuthenticated ? 'success' : 'error']">
            {{ authStore.isAuthenticated ? '是' : '否' }}
          </span>
        </div>
        <div class="status-item" v-if="authStore.userInfo">
          <span class="label">用户名:</span>
          <span class="value">{{ authStore.userInfo.userName }}</span>
        </div>
        <div class="status-item" v-if="authStore.userInfo">
          <span class="label">邮箱:</span>
          <span class="value">{{ authStore.userInfo.email }}</span>
        </div>
        <div class="status-item">
          <span class="label">Token:</span>
          <span class="value token">{{ authStore.token ? authStore.token.substring(0, 20) + '...' : '无' }}</span>
        </div>
      </div>

      <div class="action-card">
        <h2>测试操作</h2>
        
        <div class="button-group">
          <a-button 
            type="primary" 
            size="large"
            @click="handleOpenLoginModal"
          >
            手动打开登录弹窗
          </a-button>

          <a-button 
            size="large"
            @click="handleGoToProtectedPage"
          >
            访问需要认证的页面 (/profile)
          </a-button>

          <a-button 
            danger
            size="large"
            @click="handleLogout"
            v-if="authStore.isAuthenticated"
          >
            退出登录
          </a-button>
        </div>
      </div>

      <div class="info-card">
        <h2>测试说明</h2>
        <ul class="test-instructions">
          <li>
            <strong>测试1 - 手动打开登录弹窗:</strong>
            <p>点击"手动打开登录弹窗"按钮,应该弹出登录对话框</p>
          </li>
          <li>
            <strong>测试2 - 路由守卫触发:</strong>
            <p>未登录状态下,点击"访问需要认证的页面",应该自动弹出登录弹窗</p>
          </li>
          <li>
            <strong>测试3 - 登录成功跳转:</strong>
            <p>在弹窗中登录成功后,应该自动跳转到目标页面(/profile)</p>
          </li>
          <li>
            <strong>测试4 - 退出登录:</strong>
            <p>登录后点击"退出登录",应该清除登录状态</p>
          </li>
        </ul>
      </div>

      <div class="info-card">
        <h2>环境配置</h2>
        <div class="status-item">
          <span class="label">后端API地址:</span>
          <span class="value">{{ apiBaseUrl }}</span>
        </div>
        <div class="status-item">
          <span class="label">OnlyOffice地址:</span>
          <span class="value">{{ onlyOfficeUrl }}</span>
        </div>
        <a-alert 
          v-if="!isBackendReady"
          message="后端服务未启动" 
          description="请确保后端服务运行在 http://localhost:8500" 
          type="warning" 
          show-icon 
          style="margin-top: 16px;"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import { useUserStore } from '@/stores/useUserStore'
import { routeUtils } from '@/router'
import { getApiBaseUrl, getOnlyOfficeUrl } from '@/utils/runtimeConfig'

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()

const apiBaseUrl = ref(getApiBaseUrl())
const onlyOfficeUrl = ref(getOnlyOfficeUrl())
const isBackendReady = ref(false)

// 手动打开登录弹窗
const handleOpenLoginModal = () => {
  userStore.openLoginModal()
}

// 访问需要认证的页面
const handleGoToProtectedPage = () => {
  router.push(routeUtils.getLocalizedPath('/profile'))
}

// 退出登录
const handleLogout = async () => {
  await authStore.logout()
}

// 检查后端服务状态(可选)
const checkBackendStatus = async () => {
  try {
    const response = await fetch(`${apiBaseUrl.value}/health`, { method: 'GET' })
    isBackendReady.value = response.ok
  } catch (error) {
    isBackendReady.value = false
  }
}

// checkBackendStatus()
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.test-container {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  color: white;
  text-align: center;
  margin-bottom: 40px;
  font-size: 32px;
  font-weight: bold;
}

.info-card,
.action-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.status-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 0;
}

.label {
  font-weight: 500;
  color: #666;
  min-width: 140px;
}

.value {
  color: #333;
  font-family: monospace;
}

.value.success {
  color: #52c41a;
  font-weight: bold;
}

.value.error {
  color: #ff4d4f;
  font-weight: bold;
}

.value.token {
  font-size: 12px;
  color: #999;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.test-instructions {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-instructions li {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.test-instructions strong {
  color: #667eea;
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
}

.test-instructions p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .test-page {
    padding: 20px 10px;
  }

  h1 {
    font-size: 24px;
  }

  .info-card,
  .action-card {
    padding: 16px;
  }
}
</style>

