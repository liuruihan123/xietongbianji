<template>
  <div class="sso-login-container">
    <div class="sso-login-card">
      <!-- 加载状态 -->
      <div v-if="status === 'loading'" class="sso-status">
        <a-spin size="large" />
        <p class="sso-message">{{ t('sso.verifying') }}</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="status === 'error'" class="sso-status">
        <div class="sso-icon error">
          <CloseCircleOutlined />
        </div>
        <p class="sso-message error">{{ errorMessage }}</p>
        <div class="sso-actions">
          <a-button type="primary" @click="handleRetry" v-if="canRetry">
            {{ t('sso.retry') }}
          </a-button>
          <a-button @click="handleGoLogin">
            {{ t('sso.goLogin') }}
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Spin as ASpin, Button as AButton } from 'ant-design-vue'
import { CloseCircleOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { routeUtils } from '@/router'

type SsoStatus = 'loading' | 'error'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

const status = ref<SsoStatus>('loading')
const errorMessage = ref('')
const ssoToken = ref('')

// 判断是否可以重试（有 token 时才可以重试）
const canRetry = computed(() => !!ssoToken.value)

/**
 * 执行 SSO 登录
 */
const performSsoLogin = async () => {
  // 从 URL 获取 token
  const token = route.query.token as string
  ssoToken.value = token

  if (!token) {
    status.value = 'error'
    errorMessage.value = t('sso.noToken')
    return
  }

  try {
    status.value = 'loading'
    await authStore.ssoLogin(token)

    // 登录成功后直接跳转
    const redirect = route.query.redirect as string
    const languagePrefix = routeUtils.getCurrentLanguagePrefix()
    router.replace(redirect || `${languagePrefix}/`)
  } catch (error: any) {
    status.value = 'error'
    errorMessage.value = error.message || t('sso.failed')
  }
}

/**
 * 重试登录
 */
const handleRetry = () => {
  performSsoLogin()
}

/**
 * 跳转到登录页
 */
const handleGoLogin = () => {
  const languagePrefix = routeUtils.getCurrentLanguagePrefix()
  router.replace(`${languagePrefix}/signin`)
}

onMounted(() => {
  performSsoLogin()
})
</script>

<style scoped lang="scss">
.sso-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.sso-login-card {
  background: white;
  border-radius: 16px;
  padding: 48px;
  min-width: 360px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.sso-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.sso-icon {
  font-size: 64px;
  margin-bottom: 24px;

  &.error { color: #ff4d4f; }
}

.sso-message {
  font-size: 18px;
  color: #333;
  margin: 16px 0 8px;

  &.error { color: #ff4d4f; }
}

.sso-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}
</style>

